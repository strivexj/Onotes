package com.example.onotes.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.onotes.App;
import com.example.onotes.R;
import com.example.onotes.view.AvatorActivity;

import java.io.File;
import java.io.IOException;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by cwj on 5/6/17.
 */

public class TakeOrChoosePhoto {

        private static final int TAKE_PHOTO_PERMISSION_REQUEST_CODE = 0; // 拍照的权限处理返回码
        private static final int WRITE_SDCARD_PERMISSION_REQUEST_CODE = 1; // 读储存卡内容的权限处理返回码

        private static final int TAKE_PHOTO_REQUEST_CODE = 3; // 拍照返回的 requestCode
        private static final int CHOICE_FROM_ALBUM_REQUEST_CODE = 4; // 相册选取返回的 requestCode
        private static final int CROP_PHOTO_REQUEST_CODE = 5; // 裁剪图片返回的 requestCode

        private static Uri photoUri = null;
        private Uri photoOutputUri = null; // 图片最终的输出文件的 Uri







        public File getAlbumStorageDir(String albumName) {
            // Get the directory for the user's public pictures directory.
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), albumName);
            if (!file.mkdirs()) {
               // LogUtil.e(this, "Directory not created");
            }
            return file;
        }

        /** * 拍照 */
        public static void startCamera(Activity activity) {

            /** * 设置拍照得到的照片的储存目录，因为我们访问应用的缓存路径并不需要读写内存卡的申请权限， * 因此，这里为了方便，将拍照得到的照片存在这个缓存目录中 */
        //    File file = new File(getAlbumStorageDir("cwj"), "avator.jpg");

           /* try {
                if(file.exists()) {
                    file.delete();
                }
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            /** * 因 Android 7.0 开始，不能使用 file:// 类型的 Uri 访问跨应用文件，否则报异常， * 因此我们这里需要使用内容提供器，FileProvider 是 ContentProvider 的一个子类， * 我们可以轻松的使用 FileProvider 来在不同程序之间分享数据(相对于 ContentProvider 来说) */
            if(Build.VERSION.SDK_INT >= 24) {
               // photoUri = FileProvider.getUriForFile(activity, "com.example.onotes.provider", file);
            } else {
             //   photoUri = Uri.fromFile(file); // Android 7.0 以前使用原来的方法来获取文件的 Uri
            }
            // 打开系统相机的 Action，等同于："android.media.action.IMAGE_CAPTURE"
            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // 设置拍照所得照片的输出目录
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            activity.startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST_CODE);
        }

        /** * 从相册选取 */
        public static void choiceFromAlbum() {
            // 打开系统图库的 Action，等同于: "android.intent.action.GET_CONTENT"
            Intent choiceFromAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
            // 设置数据类型为图片类型
            choiceFromAlbumIntent.setType("image/*");
          //  startActivityForResult(choiceFromAlbumIntent, CHOICE_FROM_ALBUM_REQUEST_CODE);
        }

        /** * 裁剪图片 */
        private void cropPhoto(Uri inputUri) {
            // 调用系统裁剪图片的 Action
            Intent cropPhotoIntent = new Intent("com.android.camera.action.CROP");
            // 设置数据Uri 和类型
            cropPhotoIntent.setDataAndType(inputUri, "image/*");
            // 授权应用读取 Uri，这一步要有，不然裁剪程序会崩溃
            cropPhotoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            cropPhotoIntent.putExtra("aspectX", 1);
            cropPhotoIntent.putExtra("aspectY", 1);

            // 设置图片的最终输出目录
            cropPhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    // photoOutputUri = Uri.parse("file:////sdcard/image_output.jpg"));
                    photoOutputUri = Uri.fromFile(new File(getAlbumStorageDir("cwj"), "avator.jpg")));
           // startActivityForResult(cropPhotoIntent, CROP_PHOTO_REQUEST_CODE);
        }

        /** * 在这里进行用户权限授予结果处理 * @param requestCode 权限要求码，即我们申请权限时传入的常量 * @param permissions 保存权限名称的 String 数组，可以同时申请一个以上的权限 * @param grantResults 每一个申请的权限的用户处理结果数组(是否授权) */

        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            switch (requestCode) {
                // 调用相机拍照：
                case TAKE_PHOTO_PERMISSION_REQUEST_CODE:
                    // 如果用户授予权限，那么打开相机拍照
                    if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                       // startCamera();
                    } else {
                        //Toast.makeText(this, "拍照权限被拒绝", Toast.LENGTH_SHORT).show();
                    }
                    break;
                // 打开相册选取：
                case WRITE_SDCARD_PERMISSION_REQUEST_CODE:
                    if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    } else {
                       // Toast.makeText(this, "读写内存卡内容权限被拒绝", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }

        /** * 通过这个 activity 启动的其他 Activity 返回的结果在这个方法进行处理 * 我们在这里对拍照、相册选择图片、裁剪图片的返回结果进行处理 * @param requestCode 返回码，用于确定是哪个 Activity 返回的数据 * @param resultCode 返回结果，一般如果操作成功返回的是 RESULT_OK * @param data 返回对应 activity 返回的数据 */

        /*protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if(resultCode == RESULT_OK) {
                // 通过返回码判断是哪个应用返回的数据
                switch (requestCode) {
                    // 拍照
                    case TAKE_PHOTO_REQUEST_CODE:
                        cropPhoto(photoUri);
                        break;
                    // 相册选择
                    case CHOICE_FROM_ALBUM_REQUEST_CODE:
                        cropPhoto(data.getData());
                        break;
                    // 裁剪图片
                    case CROP_PHOTO_REQUEST_CODE:
                        File file = new File(photoOutputUri.getPath());
                        if(file.exists()) {
                            Bitmap bitmap = BitmapFactory.decodeFile(photoOutputUri.getPath());
                            LogUtil.d("avator",photoOutputUri.getPath());
                            pictureImageView.setImageBitmap(bitmap);

*/
                            BmobFile bmobFile = new BmobFile(new File(getAlbumStorageDir("cwj"), "avator.jpg"));
                       /* bmobFile.delete(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    LogUtil.d("delete","succeed");
                                }else{
                                    LogUtil.d("delete","failed");
                                }
                            }
                        });*/
                        /*    bmobFile.upload(new UploadFileListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        LogUtil.d("upload","succeed");
                                    } else {
                                        LogUtil.d("upload","failed");
                                    }
                                }
                            });

                            // file.delete(); // 选取完后删除照片
                        } else {
                            Toast.makeText(this, "找不到照片", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        }
    }*/

}
