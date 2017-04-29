package com.example.onotes.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.onotes.App;
import com.example.onotes.R;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cwj on 4/26/17.
 */

public class ScreenShot {
    private static String path;

    public static void sharePhoto(Activity activity, ScrollView scrollView) {
        try{
            File image=createImageFile();

           // path =  image.getAbsolutePath();
           // path=getAlbumStorageDir("cwj").getAbsolutePath();

            path=createImageFile().getAbsolutePath();
          //  ToastUtil.showToast(path, Toast.LENGTH_LONG);

            LogUtil.d("sharephote",path);

           // savePic(getScrollViewBitmap(scrollView,path));
           // getScrollViewBitmap(scrollView,path);
          //  ScreenShot.getScrollViewBitmap(scrollView,path);

            //captureScreen(activity,path);
            savePic(takeScreenShot(activity),path);
        }catch (IOException e){
            e.printStackTrace();
        }


        Intent intent  = new Intent(Intent.ACTION_SEND);

        File file = new File(path);

        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

        intent.setType("image/*");

        Intent chooser = Intent.createChooser(intent, "Share screen shot");
        chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(intent.resolveActivity(App.getContext().getPackageManager()) != null) {
            App.getContext().startActivity(chooser);
        }
    }

    public static Bitmap getViewBitmap(View v) {

        v.clearFocus(); //

        v.setPressed(false); //

        // 能画缓存就返回false

        boolean willNotCache = v.willNotCacheDrawing();

        v.setWillNotCacheDrawing(false);

        int color = v.getDrawingCacheBackgroundColor();

        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {

            v.destroyDrawingCache();

        }

        v.buildDrawingCache();

        Bitmap cacheBitmap = v.getDrawingCache();

        if (cacheBitmap == null) {

            return null;

        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view

        v.destroyDrawingCache();

        v.setWillNotCacheDrawing(willNotCache);

        v.setDrawingCacheBackgroundColor(color);

        return bitmap;

    }

    // 保存到sdcard
    public static void savePic(Bitmap b, String strFileName) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(strFileName);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**

     * 保存Bitmap图片为本地文件

     */

    public static void saveFile(Bitmap bitmap, String filename) {

        FileOutputStream fileOutputStream = null;

        try {

            fileOutputStream = new FileOutputStream(filename);

            if (fileOutputStream != null) {

                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);

                fileOutputStream.flush();

                fileOutputStream.close();

            }

        } catch (FileNotFoundException e) {

            LogUtil.d("a","Exception:FileNotFoundException");

            e.printStackTrace();

        } catch (IOException e) {

            LogUtil.d("a","IOException:IOException");

            e.printStackTrace();

        }

    }

    public static File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
           LogUtil.d("a", "Directory not created");
        }
        return file;
    }




    private static String mCurrentPhotoPath;

    private static File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "_";
       // File storageDir = App.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File storageDir = App.getContext().getExternalCacheDir();
         File storageDir = getAlbumStorageDir("cwj");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }



    /**

     * 截屏

     * @param activity

     * @return

     */

    public static void captureScreen(Activity activity,String path) {

        // 获取屏幕大小：

        DisplayMetrics metrics = new DisplayMetrics();

        WindowManager WM = (WindowManager) activity

                .getSystemService(Context.WINDOW_SERVICE);

        Display display = WM.getDefaultDisplay();

        display.getMetrics(metrics);

        int height = metrics.heightPixels; // 屏幕高

        int width = metrics.widthPixels; // 屏幕的宽

        // 获取显示方式

        int pixelformat = display.getPixelFormat();

        PixelFormat localPixelFormat1 = new PixelFormat();

        PixelFormat.getPixelFormatInfo(pixelformat, localPixelFormat1);

        int deepth = localPixelFormat1.bytesPerPixel;// 位深

        byte[] piex = new byte[height * width * deepth];

        try {

            Runtime.getRuntime().exec(

                    new String[] { "/system/bin/su", "-c",

                            "chmod 777 /dev/graphics/fb0" });

        } catch (IOException e) {

            e.printStackTrace();

        }

        try {

            // 获取fb0数据输入流

            InputStream stream = new FileInputStream(new File(

                    "/dev/graphics/fb0"));

            DataInputStream dStream = new DataInputStream(stream);

            dStream.readFully(piex);

        } catch (Exception e) {

            e.printStackTrace();

        }

        // 保存图片

        int[] colors = new int[height * width];

        for (int m = 0; m < colors.length; m++) {

            int r = (piex[m * 4] & 0xFF);

            int g = (piex[m * 4 + 1] & 0xFF);

            int b = (piex[m * 4 + 2] & 0xFF);

            int a = (piex[m * 4 + 3] & 0xFF);

            colors[m] = (a << 24) + (r << 16) + (g << 8) + b;

        }

        // piex生成Bitmap

        Bitmap bitmap = Bitmap.createBitmap(colors, width, height,

                Bitmap.Config.ARGB_8888);

       // return bitmap;

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            if (null != fos) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //This work
    // 获取指定Activity的截屏，保存到png文件
    public static Bitmap takeScreenShot(Activity activity) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);

        int statusBarHeight = frame.top;

        System.out.println(statusBarHeight);

       /* int contentTop =  activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();

        //statusBarHeight是上面状态栏的高度
        int titleBarHeight = contentTop - statusBarHeight;*/


        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();

        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (App.getContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            //方法一
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, App.getContext().getResources().getDisplayMetrics());
            LogUtil.d("nani PopupListView", "tv.data=" + tv.data + ",actionBarHeight=" + actionBarHeight);
        }

            LogUtil.d("nani", "statusBarHeight " + statusBarHeight + "  width " + width + "   height " + height + "  actionBarHeight " + actionBarHeight);
            // 去掉标题栏
            // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
            Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight+actionBarHeight, width, height - actionBarHeight-statusBarHeight);

            view.destroyDrawingCache();

       return b;
    }



    // 程序入口 截取当前屏幕
    public static void shootLoacleView(Activity a, String picpath) {
        savePic(takeScreenShot(a), picpath);
    }



    /** * 截取scrollview的屏幕 * **/
    public static Bitmap getScrollViewBitmap(ScrollView scrollView, String picpath) {
        int h = 0;
        Bitmap bitmap;
        // 获取listView实际高度
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
        }
        LogUtil.d(TAG, "实际高度:" + h);
        LogUtil.d(TAG, " 高度:" + scrollView.getHeight());
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        // 测试输出
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(picpath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (null != out) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            }
        } catch (IOException e) {
        }
        return bitmap;
    }



    private static String TAG = "Listview and ScrollView item 截图:";

    /** * 截图listview * **/
    public static Bitmap getListViewBitmap(ListView listView, String picpath) {
        int h = 0;
        Bitmap bitmap;
        // 获取listView实际高度
        for (int i = 0; i < listView.getChildCount(); i++) {
            h += listView.getChildAt(i).getHeight();
        }
        LogUtil.d(TAG, "实际高度:" + h);
        LogUtil.d(TAG, "list 高度:" + listView.getHeight());
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(listView.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        listView.draw(canvas);
        // 测试输出
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(picpath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (null != out) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            }
        } catch (IOException e) {
        }
        return bitmap;
    }

    // 程序入口 截取ScrollView
    public static void shootScrollView(ScrollView scrollView, String picpath) {
        savePic(getScrollViewBitmap(scrollView, picpath), picpath);

    }

    // 程序入口 截取ListView
    public static void shootListView(ListView listView, String picpath) {
        savePic(getListViewBitmap(listView,picpath), picpath);
    }

}
