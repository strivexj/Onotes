package com.example.onotes.view;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import com.example.onotes.R;
import com.example.onotes.bean.MyUser;
import com.example.onotes.utils.LogUtil;
import com.example.onotes.utils.SharedPreferenesUtil;
import com.example.onotes.utils.ToastUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * 选取图片的父类
 */
public class PickPictureActivity extends AppCompatActivity {
    private static final int TAKE_PHOTO_PERMISSION_REQUEST_CODE = 0; // 拍照的权限处理返回码
    private static final int WRITE_SDCARD_PERMISSION_REQUEST_CODE = 1; // 读储存卡内容的权限处理返回码
    private static final int TAKE_PHOTO_REQUEST_CODE = 3; // 拍照返回的 requestCode
    private static final int CHOICE_FROM_ALBUM_REQUEST_CODE = 4; // 相册选取返回的 requestCode
    private static final int CROP_PHOTO_REQUEST_CODE = 5; // 裁剪图片返回的 requestCode

    private static final String onotesPictureStoreDirectory="Onotes";
    private static final String avatarName="avatar.jpg";
    private static final String backGroundName="bg.jpg";

    private static final int PICK_PICTURE_FOR_AVATOR=6;
    private static final int PICK_PICTURE_FOR_BG=7;
    private static final int PICK_PICTURE_FOR_NOTES=8;

    public Uri photoUri = null;
    private Uri photoOutputUri = null; // 图片最终的输出文件的 Uri
    private ProgressDialog progressDialog;

    public int type;

    private MyUser bmobUser = BmobUser.getCurrentUser(MyUser.class);

    private String photoname=null;
    private File file=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 得到输出图片路径Uri
     * @return
     */
    public Uri getPhotoOutputUri(){
        return photoOutputUri;
    }


    public Uri getPhotoUri(){
        return photoUri;
    }

    public File getFile(){ return file;}

    public int getType(){
        return type;
    }
    public void setType(int type){
        this.type=type;
    }

    private boolean isHavePermission() {
        boolean a=true;
        LogUtil.d("detail","check");

        if (ContextCompat.checkSelfPermission(PickPictureActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(PickPictureActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_SDCARD_PERMISSION_REQUEST_CODE);
            a=false;
        }
        if (ContextCompat.checkSelfPermission(PickPictureActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(PickPictureActivity.this,
                    new String[]{Manifest.permission.CAMERA,}, TAKE_PHOTO_PERMISSION_REQUEST_CODE);
            a=false;
        }
        return a;
    }


    public void showListDialog() {
        final String[] items = {getString(R.string.takepicture), getString(R.string.choosepicture)};
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(PickPictureActivity.this);
        listDialog.setTitle(R.string.setavatar);
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (ContextCompat.checkSelfPermission(PickPictureActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                    /* * 下面是对调用相机拍照权限进行申请 */
                        ActivityCompat.requestPermissions(PickPictureActivity.this,
                                new String[]{Manifest.permission.CAMERA,}, TAKE_PHOTO_PERMISSION_REQUEST_CODE);
                    } else {

                        startCamera();
                    }
                } else if (which == 1) {
                    choiceFromAlbum();
                }
            }
        });
        listDialog.show();
    }


    public File getAlbumStorageDir() {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), onotesPictureStoreDirectory);
        if (!file.mkdirs()) {
            LogUtil.e(this, "Directory not created");
        }
        return file;
    }


    /**
     * 拍照
     */
    public void startCamera() {

        if(!isHavePermission()){
            return;
        }

        LogUtil.d("detail","checkfinish");


        /** * 设置拍照得到的照片的储存目录*/
        if(type==PICK_PICTURE_FOR_AVATOR){
           file = new File(getAlbumStorageDir(), avatarName);
        }else if(type==PICK_PICTURE_FOR_BG){
            file = new File(getAlbumStorageDir(), backGroundName);
        }else if(type==PICK_PICTURE_FOR_NOTES){
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            file = new File(getAlbumStorageDir(),"notesPhoto_"+timeStamp+".jpg");
        }

            if (file.exists()) {
                file.delete();
            }


        /** * 因 Android 7.0 开始，不能使用 file:// 类型的 Uri 访问跨应用文件，否则报异常， * 因此这里需要使用内容提供器，FileProvider 是 ContentProvider 的一个子类， * 我们可以轻松的使用 FileProvider 来在不同程序之间分享数据(相对于 ContentProvider 来说) */
        if (Build.VERSION.SDK_INT >= 24) {
            photoUri = FileProvider.getUriForFile(this, "com.example.onotes.provider", file);
        } else {
            photoUri = Uri.fromFile(file); // Android 7.0 以前使用原来的方法来获取文件的 Uri
        }

        // 打开系统相机的 Action，等同于："android.media.action.IMAGE_CAPTURE"
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 设置拍照所得照片的输出目录
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST_CODE);
    }

    /**
     * 从相册选取
     */
    public void choiceFromAlbum() {
        // 打开系统图库的 Action，等同于: "android.intent.action.GET_CONTENT"
        Intent choiceFromAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
        // 设置数据类型为图片类型
        choiceFromAlbumIntent.setType("image/*");
        startActivityForResult(choiceFromAlbumIntent, CHOICE_FROM_ALBUM_REQUEST_CODE);
    }

    /**
     * 裁剪图片
     */
   public void cropPhoto(Uri inputUri) {

           // 调用系统裁剪图片的 Action
           Intent cropPhotoIntent = new Intent("com.android.camera.action.CROP");
           // 设置数据Uri 和类型
           cropPhotoIntent.setDataAndType(inputUri, "image/*");
           // 授权应用读取 Uri，这一步要有，不然裁剪程序会崩溃
           cropPhotoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

           // 设置图片的最终输出目录
           if(type==PICK_PICTURE_FOR_AVATOR){
               cropPhotoIntent.putExtra("aspectX", 1);
               cropPhotoIntent.putExtra("aspectY", 1);
               cropPhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                       photoOutputUri = Uri.fromFile(new File(getAlbumStorageDir(), avatarName)));

               LogUtil.d("crop","cropavatar");
           }

           else if(type==PICK_PICTURE_FOR_BG) {
               cropPhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                       photoOutputUri = Uri.fromFile(new File(getAlbumStorageDir(), backGroundName)));
               LogUtil.d("crop","bg");
           }else if(type==PICK_PICTURE_FOR_NOTES){
               String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
               cropPhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                       photoOutputUri = Uri.fromFile(new File(getAlbumStorageDir(), "notesPhoto_"+timeStamp+".jpg")));
               LogUtil.d("crop","notes");
           }

           startActivityForResult(cropPhotoIntent, CROP_PHOTO_REQUEST_CODE);



    }

    /**
     * 在这里进行用户权限授予结果处理 * @param requestCode 权限要求码，即我们申请权限时传入的常量 * @param permissions 保存权限名称的 String 数组，可以同时申请一个以上的权限 * @param grantResults 每一个申请的权限的用户处理结果数组(是否授权)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            // 调用相机拍照：
            case TAKE_PHOTO_PERMISSION_REQUEST_CODE:
                // 如果用户授予权限，那么打开相机拍照
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera();
                } else {
                    // Toast.makeText(this, "拍照权限被拒绝").show();
                    ToastUtil.showToast(getString(R.string.camerapermissionrefuse));
                }
                break;
            // 打开相册选取：
            case WRITE_SDCARD_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    //Toast.makeText(this, "读写内存卡内容权限被拒绝").show();
                    ToastUtil.showToast(getString(R.string.SDpermissionrefused));
                }
                break;
        }
    }

    /**
     * 上传用户头像
     */
    public void upLoadAvatar() {
                final BmobFile bmobFile=new BmobFile(new File(getAlbumStorageDir(),avatarName));

                bmobFile.upload(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            LogUtil.d("upload","succeed");

                            SharedPreferenesUtil.setIsNeedUploadAvatar(false);

                            MyUser myUser = new MyUser();
                            myUser.setAvatarUrl(bmobFile.getUrl());
                            myUser.update(bmobUser.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        LogUtil.d("uploadurl", "succeed ");

                                    } else {

                                    }
                                }
                            });

                            BmobFile delete=new BmobFile();
                            delete.setUrl(SharedPreferenesUtil.getAvatarUrl());
                            LogUtil.d("url delete",SharedPreferenesUtil.getAvatarUrl());
                            delete.delete(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                        LogUtil.d("delete","succeed");
                                    }else{
                                        LogUtil.d("delete","failed");
                                    }
                                }
                            });
                        }   else {
                            LogUtil.d("upload","failed "+e.getMessage()+e.getErrorCode());
                        }
                    }
                });
    }



    /**
     * 保存照片到SDCard
     *
     *            需要保存的路径
     * @param photoName
     *            保存的相片名字
     * @param photoBitmap
     *            照片的Bitmap对象
     */
    public void savePhotoToSDCard(String photoName, Bitmap photoBitmap) {
        FileOutputStream fileOutputStream = null;

        File photoFile =  new File(getAlbumStorageDir(), photoName);;
        try {
            fileOutputStream = new FileOutputStream(photoFile);
            if (photoBitmap != null) {
                if (photoBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fileOutputStream)) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            }
        } catch (FileNotFoundException e) {
            photoFile.delete();
            e.printStackTrace();
        } catch (IOException e) {
            photoFile.delete();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    /**
     * show progress dialog
     */
    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.save_information));
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * close progress dialog
     */
    public void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d("upload","activityOnDestroy");
    }
}
