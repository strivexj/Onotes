package com.example.onotes.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import com.example.onotes.App;
import com.example.onotes.R;
import com.example.onotes.bean.Chat;
import com.example.onotes.datebase.ChatDbHelper;
import com.example.onotes.utils.LogUtil;
import com.example.onotes.view.Main2Activity;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.listener.ValueEventListener;

import static android.support.v4.app.NotificationCompat.PRIORITY_MAX;


public class ChatService extends Service {

    private static final int NOTIFICATION_ID =2148;
    private static final int messagenumber=0;
    private final IBinder iBinder = new LocalBinder();//Android的远程调用（就是跨进程调用）就是通过IBinder实现的
    private List<Chat> data = new ArrayList<>();
    private int initial;
    public static final int TYPE_MSG_RIGHT = 0;
    public static final int TYPE_MSG_LEFT = 1;
    public static final int TYPE_PICTURE_LEFT = 2;
    public static final int TYPE_PICTURE_RIGHT = 3;
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

       // buildNotification();
        initchat();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public class LocalBinder extends Binder {
        public ChatService getService() {
            return ChatService.this;
        }
    }

    private void initchat() {

        final BmobRealTimeData rtd = new BmobRealTimeData();
        rtd.start(new ValueEventListener() {
            @Override
            public void onDataChange(JSONObject data) {
                if(BmobRealTimeData.ACTION_UPDATETABLE.equals(data.optString("action"))){
                    JSONObject contentdata = data.optJSONObject("data");

                    adddata(contentdata.optString("content"),contentdata.optString("pictureurl"));
                    buildNotification(contentdata.optString("name")+": "+contentdata.optString("content"));
                    Log.d("servicebmob",contentdata.optString("name")+" "+contentdata.optString("content")+ contentdata.optString("pictureurl"));
                }
            }

            @Override
            public void onConnectCompleted(Exception ex) {
                Log.d("bmob", "连接成功:"+rtd.isConnected());
                if(rtd.isConnected())
                    rtd.subTableUpdate("Chat");
            }
        });
    }
    private void adddata(String content,String pictureurl) {
        LogUtil.d("service", "adddata");

            Chat chat = new Chat();
            chat.setContent(content);
            chat.setType(TYPE_MSG_LEFT);
            chat.setPictureurl(pictureurl);

            data.add(chat);
            LogUtil.d("serviceadddata",chat.getContent()+chat.getPictureurl()+chat.getName());
        savedata();
    }
    private void buildNotification(String content) {

        Intent intent=new Intent(this,Main2Activity.class);
        TaskStackBuilder taskStackBuilder= TaskStackBuilder.create(this);
        taskStackBuilder.addParentStack(Main2Activity.class);
        taskStackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent=taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.icon);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setShowWhen(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setLargeIcon(largeIcon)
                .setPriority(PRIORITY_MAX)
                //.setUsesChronometer(true)
                .setSmallIcon(R.drawable.icon)
                //.setContentText("You have received a new messages~!")
                .setContentText(content)
                .setContentTitle("A new message")
                //.setContentInfo(activeAudio.getTitle())
                .setContentIntent(pendingIntent);
            final Notification notification = notificationBuilder.build();
            //notification.flags= Notification.FLAG_NO_CLEAR;
            startForeground(NOTIFICATION_ID, notification);
    }

    private void initdata() {
        LogUtil.d("service", "initdata");
        ChatDbHelper chatDbHelper = new ChatDbHelper(App.getContext());
        SQLiteDatabase db = chatDbHelper.getWritableDatabase();
        Cursor cursor = db.query("Chat", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String content = cursor.getString(cursor.getColumnIndex("content"));
                int type = cursor.getInt(cursor.getColumnIndex("type"));
                String pictureurl=cursor.getString(cursor.getColumnIndex("pictureurl"));
                Chat chat = new Chat();
                chat.setType(type);
                chat.setContent(content);
                chat.setPictureurl(pictureurl);
                data.add(chat);
            } while (cursor.moveToNext());
        }
        initial = data.size();
        LogUtil.d("service","init"+initial);
        cursor.close();
        db.close();
    }
    private void savedata() {
        initdata();

        ChatDbHelper chatDbHelper = new ChatDbHelper(App.getContext());
        SQLiteDatabase db = chatDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        for (int i = initial; i < data.size(); i++) {
            String content = data.get(i).getContent();
            int type = data.get(i).getType();
            String pictureurl=data.get(i).getPictureurl();
            values.put("type", type);
            values.put("pictureurl",pictureurl);
            values.put("content", content);
            db.insert("Chat", null, values);
            LogUtil.d("service", "savedata "+content+type+pictureurl);
        }
        db.close();

        LogUtil.d("service", "savedata");
    }
    private void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

}
