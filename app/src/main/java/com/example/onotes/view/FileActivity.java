package com.example.onotes.view;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.onotes.R;
import com.example.onotes.utils.ToastUtil;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;


    private Uri imageUri;
    private File outputImage1;

    private final File outputImage2 = new File(Environment.
            getExternalStorageDirectory(), "tempImage2.jpg");
    private ImageView imageview;
    private Button takephote;
    private Button choosephoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        initView();

        if (ContextCompat.checkSelfPermission(FileActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
            //请求权限
            ActivityCompat.requestPermissions(FileActivity.this,
                    new String[]{Manifest.permission.CAMERA},1);
        }
    }

    private void initView() {
        imageview = (ImageView) findViewById(R.id.imageview);
        takephote = (Button) findViewById(R.id.takephote);
        choosephoto = (Button) findViewById(R.id.choosephoto);

        takephote.setOnClickListener(this);
        choosephoto.setOnClickListener(this);


        BottomSheetBehavior behavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
        if(behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }else {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

       ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose color")
                .initialColor(R.color.light_blue_500)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                //.wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        Toast.makeText(FileActivity.this, Integer.toHexString(selectedColor), Toast.LENGTH_SHORT).show();

                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        //changeBackgroundColor(selectedColor);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.takephote:
                outputImage1 = new File(Environment.
                        getExternalStorageDirectory(), "tempImage1.jpg");
                try {
                    if (outputImage1.exists()) {
                        imageUri = Uri.fromFile(outputImage1);
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream
                                    (getContentResolver()
                                            .openInputStream(imageUri));
                           // Glide.with(this).load(bitmap).into(imageview);
                          imageview.setImageBitmap(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        outputImage1.delete();
                    }
                    outputImage1.createNewFile();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageUri = Uri.fromFile(outputImage1);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO); // 启动相机程序
                break;
            case R.id.choosephoto:
                File outputImage = new File(Environment.
                        getExternalStorageDirectory(), "output_image.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageUri = Uri.fromFile(outputImage);
               // Intent intent2 = new Intent(Intent.ACTION_PICK);
                Intent intent2=new Intent("android.intent.action.GET_CONTENT");
                intent2.setType("image/*");//相片类型
                intent2.putExtra("crop", true);
                intent2.putExtra("scale", true);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent2, 3);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:

                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CROP_PHOTO); // 启动裁剪程序
                }
                break;
            case CROP_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream
                                (getContentResolver()
                                        .openInputStream(imageUri));
                       imageview.setImageBitmap(bitmap); // 将裁剪后的照片显示出来
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 3:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri uri = data.getData();
                        Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        imageview.setImageBitmap(bit);
                       // Glide.with(this).load(bit).into(imageview);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }
}
// 监听表更新

// 监听表删除
// rtd.subTableDelete("Person");
// 监听行更新
// rtd.subRowUpdate(tableName, objectId);
// 监听行删除
// rtd.subRowDelete(tableName, objectId);




       /* String picPath = "sdcard/Onotes.apk";
        final BmobFile bmobFile = new BmobFile(new File(picPath));
        bmobFile.uploadblock(new UploadFileListener() {

            @Override
            public void done(BmobException e) {
                if(e==null){
                    //bmobFile.getFileUrl()--返回的上传文件的完整地址
                    Toast.makeText(FileActivity.this, "上传文件成功:"+ bmobFile.getFileUrl(), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(FileActivity.this, "上传文件失败:"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
            }
        });*/
//sendMsg("cwj","qunimade");


   /* private void initchat() {
        final BmobRealTimeData rtd = new BmobRealTimeData();
        rtd.start(new ValueEventListener() {
            @Override
            public void onDataChange(JSONObject data) {
                if(BmobRealTimeData.ACTION_UPDATETABLE.equals(data.optString("action"))){
                    JSONObject contentdata = data.optJSONObject("data");
                   // messages.add(new Chat(data.optString("name"), data.optString("content")));
                   // myAdapter.notifyDataSetChanged();
                    Log.d("bmob",contentdata.optString("name")+" "+contentdata.optString("content"));
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

    private void sendMsg(String name, String msg){
        Chat chat = new Chat();
        chat.setName(name);
        chat.setContent(msg);
        chat.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    Toast.makeText(FileActivity.this, "succeed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
*/

