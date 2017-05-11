package com.example.onotes.view;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.onotes.R;
import com.example.onotes.bean.MyUser;
import com.example.onotes.utils.LogUtil;
import com.example.onotes.utils.SharedPreferenesUtil;
import com.example.onotes.utils.ToastUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private CheckBox checkBox;
    private LinearLayout activity_file;
    private NestedScrollView bottom_sheet;


    private Button startCameraButton = null;
    private Button choiceFromAlbumButton = null;

    private ImageView pictureImageView = null;


    private static final int TAKE_PHOTO_PERMISSION_REQUEST_CODE = 0; // 拍照的权限处理返回码
    private static final int WRITE_SDCARD_PERMISSION_REQUEST_CODE = 1; // 读储存卡内容的权限处理返回码

    private static final int TAKE_PHOTO_REQUEST_CODE = 3; // 拍照返回的 requestCode
    private static final int CHOICE_FROM_ALBUM_REQUEST_CODE = 4; // 相册选取返回的 requestCode
    private static final int CROP_PHOTO_REQUEST_CODE = 5; // 裁剪图片返回的 requestCode

    private Uri photoUri = null;
    private Uri photoOutputUri = null; // 图片最终的输出文件的 Uri
    private Toolbar toolbar;
    private CircleImageView setuserpicture;
    private EditText nickname;
    private EditText personalizedSignatures;
    private EditText self_introduction;
    private EditText wrok;
    private ProgressDialog progressDialog;
    private Button save_information;
    private CheckBox male;
    private CheckBox female;

    private boolean sex;
    private  MyUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
    private String avatarUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_file);
        setContentView(R.layout.user_detail);

        //checkPermission();

        initView();

    }

    private boolean isHavePermission() {
        boolean a=true;
        LogUtil.d("detail","check");
    /* * 先判断用户以前有没有对我们的应用程序允许过读写内存卡内容的权限， * 用户处理的结果在 onRequestPermissionResult 中进行处理 */
        if (ContextCompat.checkSelfPermission(UserDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 申请读写内存卡内容的权限
            ActivityCompat.requestPermissions(UserDetailActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_SDCARD_PERMISSION_REQUEST_CODE);
            a=false;
        }
        if (ContextCompat.checkSelfPermission(UserDetailActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                    /* * 下面是对调用相机拍照权限进行申请 */
            ActivityCompat.requestPermissions(UserDetailActivity.this,
                    new String[]{Manifest.permission.CAMERA,}, TAKE_PHOTO_PERMISSION_REQUEST_CODE);
            a=false;
        }
        return a;
    }

    private void initView() {

        pictureImageView = (ImageView) findViewById(R.id.imageview);
        startCameraButton = (Button) findViewById(R.id.takephoto);
        choiceFromAlbumButton = (Button) findViewById(R.id.choosephoto);
        //checkBox = (CheckBox) findViewById(R.id.checkBox);
        activity_file = (LinearLayout) findViewById(R.id.activity_file);
        bottom_sheet = (NestedScrollView) findViewById(R.id.bottom_sheet);


        // startCameraButton.setOnClickListener(this);
        //choiceFromAlbumButton .setOnClickListener(this);


        toolbar = (Toolbar) findViewById(R.id.detailtoolbar);
        toolbar.setOnClickListener(this);
        setuserpicture = (CircleImageView) findViewById(R.id.setuserpicture);
        setuserpicture.setOnClickListener(this);
        nickname = (EditText) findViewById(R.id.nickname);
        nickname.setOnClickListener(this);
        personalizedSignatures = (EditText) findViewById(R.id.personalizedSignatures);
        personalizedSignatures.setOnClickListener(this);
        self_introduction = (EditText) findViewById(R.id.self_introduction);
        self_introduction.setOnClickListener(this);
        wrok = (EditText) findViewById(R.id.wrok);
        wrok.setOnClickListener(this);


        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.arrowleft);
        }

        toolbar.setTitle(R.string.private_information);

        final File file = new File(getAlbumStorageDir("cwj"), "avatar.jpg");
        if (file.exists()) {
            Glide.with(this)
                    .load(file)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(setuserpicture);
        } else if (!TextUtils.isEmpty(SharedPreferenesUtil.getFigureurl_qq_2())) {
            Glide.with(this).load(SharedPreferenesUtil.getFigureurl_qq_2()).into(setuserpicture);
        }

        save_information = (Button) findViewById(R.id.save_information);
        save_information.setOnClickListener(this);
        male = (CheckBox) findViewById(R.id.male);
        male.setOnClickListener(this);
        female = (CheckBox) findViewById(R.id.female);
        female.setOnClickListener(this);

        BmobQuery<MyUser> query = new BmobQuery<MyUser>();
        query.getObject(bmobUser.getObjectId(), new QueryListener<MyUser>() {
            @Override
            public void done(MyUser user, BmobException e) {
                if(e==null){
                    nickname.setText(user.getNickname());
                    personalizedSignatures.setText(user.getPersonalizeSignature());
                    self_introduction.setText(user.getIntroduction());
                    wrok.setText(user.getWrok());
                    avatarUrl=user.getAvatarUrl();
                    if(user.getSex()){
                        male.setChecked(true);
                        female.setChecked(false);
                    }else {
                        female.setChecked(true);
                        male.setChecked(false);
                    }
                }

            }
        });

      /*  myUser.setNickname(nicknameString);
        myUser.setPersonalizeSignature(personalizedSignaturesString);
        myUser.setIntroduction(introduction);
        myUser.setWrok(wrokString);
        myUser.setSex(sex);*/
    }


    private void showListDialog() {
        final String[] items = {getString(R.string.takepicture), getString(R.string.choosepicture)};
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(UserDetailActivity.this);
        listDialog.setTitle(R.string.setavatar);
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {

                    if (ContextCompat.checkSelfPermission(UserDetailActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                    /* * 下面是对调用相机拍照权限进行申请 */
                        ActivityCompat.requestPermissions(UserDetailActivity.this,
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
           /* case R.id.takephoto:

                if (ContextCompat.checkSelfPermission(UserDetailActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    /* * 下面是对调用相机拍照权限进行申请 */
                  /*  ActivityCompat.requestPermissions(UserDetailActivity.this,
                            new String[]{Manifest.permission.CAMERA,}, TAKE_PHOTO_PERMISSION_REQUEST_CODE);
                } else {
                    startCamera();
                }
                break;
            case R.id.choosephoto:
                choiceFromAlbum();
                break;*/

            case R.id.setuserpicture:
                showListDialog();
                break;
            case R.id.save_information:
                submit();
                break;
            case R.id.male:
                if(female.isChecked()){
                    female.setChecked(false);
                }
                male.setChecked(true);
                sex=true;
                break;
            case R.id.female:
                if(male.isChecked()){
                    male.setChecked(false);
                }
                female.setChecked(true);
                sex=false;
                break;


        }
    }


    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
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
        /** * 设置拍照得到的照片的储存目录，因为我们访问应用的缓存路径并不需要读写内存卡的申请权限， * 因此，这里为了方便，将拍照得到的照片存在这个缓存目录中 */
        File file = new File(getAlbumStorageDir("cwj"), "avatar.jpg");

        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /** * 因 Android 7.0 开始，不能使用 file:// 类型的 Uri 访问跨应用文件，否则报异常， * 因此我们这里需要使用内容提供器，FileProvider 是 ContentProvider 的一个子类， * 我们可以轻松的使用 FileProvider 来在不同程序之间分享数据(相对于 ContentProvider 来说) */
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
    private void choiceFromAlbum() {
        // 打开系统图库的 Action，等同于: "android.intent.action.GET_CONTENT"
        Intent choiceFromAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
        // 设置数据类型为图片类型
        choiceFromAlbumIntent.setType("image/*");
        startActivityForResult(choiceFromAlbumIntent, CHOICE_FROM_ALBUM_REQUEST_CODE);
    }

    /**
     * 裁剪图片
     */
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
                photoOutputUri = Uri.fromFile(new File(getAlbumStorageDir("cwj"), "avatar.jpg")));
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
     * 通过这个 activity 启动的其他 Activity 返回的结果在这个方法进行处理 * 我们在这里对拍照、相册选择图片、裁剪图片的返回结果进行处理 * @param requestCode 返回码，用于确定是哪个 Activity 返回的数据 * @param resultCode 返回结果，一般如果操作成功返回的是 RESULT_OK * @param data 返回对应 activity 返回的数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // 通过返回码判断是哪个应用返回的数据
            switch (requestCode) {
                // 拍照
                case TAKE_PHOTO_REQUEST_CODE:
                    cropPhoto(photoUri);
                    break;
                // 相册选择
                case CHOICE_FROM_ALBUM_REQUEST_CODE:
                    //cropPhoto(data.getData());
                    LogUtil.d("path",data.getData().toString());

                    ContentResolver resolver = getContentResolver();
                    //照片的原始资源地址
                    Uri originalUri = data.getData();
                    try {
                        //使用ContentProvider通过URI获取原始图片
                        Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);

                        savePhotoToSDCard("ablum4.jpg",photo);
                        setuserpicture.setImageBitmap(photo);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                break;
                // 裁剪图片
                case CROP_PHOTO_REQUEST_CODE:
                    File file = new File(photoOutputUri.getPath());
                    if (file.exists()) {
                        LogUtil.d("path output",photoOutputUri.getPath());
                        Bitmap bitmap = BitmapFactory.decodeFile(photoOutputUri.getPath());

                        //pictureImageView.setImageBitmap(bitmap);
                        setuserpicture.setImageBitmap(bitmap);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final BmobFile bmobFile = new BmobFile(new File(getAlbumStorageDir("cwj"), "avatar.jpg"));
                        bmobFile.upload(new UploadFileListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    MyUser myUser = new MyUser();

                                    myUser.setAvatarUrl(bmobFile.getUrl());
                                    myUser.update(bmobUser.getObjectId(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {

                                            } else {

                                            }
                                        }
                                    });

                                    BmobFile delete=new BmobFile();
                                    delete.setUrl(avatarUrl);
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

                                } else {

                                }
                            }
                        });
                    }
                }).start();

                        // file.delete(); // 选取完后删除照片
                    } else {
                        //Toast.makeText(this, "找不到照片").show();
                        ToastUtil.showToast(getString(R.string.withoutpicture));
                    }
                    break;
            }
        }
    }

    /**
     * 保存照片到SDCard
     *
     *
     *            需要保存的路径
     * @param photoName
     *            保存的相片名字
     * @param photoBitmap
     *            照片的Bitmap对象
     */
    private void savePhotoToSDCard( String photoName, Bitmap photoBitmap) {
        FileOutputStream fileOutputStream = null;
        /*if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }*/
            File photoFile =  new File(getAlbumStorageDir("cwj"), photoName);;
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
// TODO Auto-generated catch block
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

    private void submit() {

        showProgressDialog();

        MyUser myUser = new MyUser();
        MyUser bmobUser = MyUser.getCurrentUser(MyUser.class);

        String nicknameString = nickname.getText().toString();
        String personalizedSignaturesString = personalizedSignatures.getText().toString();
        String introduction = self_introduction.getText().toString();
        String wrokString = wrok.getText().toString();

        // myUser.setAvatarUrl(bmobFile.getUrl());
        myUser.setNickname(nicknameString);
        myUser.setPersonalizeSignature(personalizedSignaturesString);
        myUser.setIntroduction(introduction);
        myUser.setWrok(wrokString);
        myUser.setSex(male.isChecked());

        SharedPreferenesUtil.setPersonalizeSignature(personalizedSignaturesString);
        SharedPreferenesUtil.setNickname(nicknameString);
        //myUser.save(bmobUser.getObjectId(),)

        myUser.update(bmobUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    LogUtil.d("uploadurl", "succeed ");

                } else {
                    LogUtil.d("uploadurl", "failed ");
                }
                closeProgressDialog();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            submit();
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            submit();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
