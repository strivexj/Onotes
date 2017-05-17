
package com.example.onotes.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.onotes.R;
import com.example.onotes.bean.MyUser;
import com.example.onotes.utils.LogUtil;
import com.example.onotes.utils.SharedPreferenesUtil;
import com.example.onotes.utils.ToastUtil;
import java.io.File;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 用户个人信息
 */
public class UserDetailActivity extends PickPictureActivity implements View.OnClickListener {

    private static final int TAKE_PHOTO_REQUEST_CODE = 3; // 拍照返回的 requestCode
    private static final int CHOICE_FROM_ALBUM_REQUEST_CODE = 4; // 相册选取返回的 requestCode
    private static final int CROP_PHOTO_REQUEST_CODE = 5; // 裁剪图片返回的 requestCode

    private Toolbar toolbar;
    private CircleImageView setuserpicture;
    private EditText nickname;
    private EditText personalizedSignatures;
    private EditText self_introduction;
    private EditText wrok;
    private Button save_information;
    private CheckBox male;
    private CheckBox female;
    private  MyUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
    private String avatarUrl;

    private static final int PICK_PICTURE_FOR_AVATOR=6;
    private static final String avatarName="avatar.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_detail);
        initView();
        setType(PICK_PICTURE_FOR_AVATOR);//设置选取图片类型

    }


    private void initView() {

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

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.arrowleft);
        }

        toolbar.setTitle(R.string.private_information);

        final File file = new File(getAlbumStorageDir(), avatarName);

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

                    SharedPreferenesUtil.setAvatarUrl(avatarUrl);
                    LogUtil.d("url before",avatarUrl);
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

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

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
                break;
            case R.id.female:
                if(male.isChecked()){
                    male.setChecked(false);
                }
                female.setChecked(true);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // 通过返回码判断是哪个应用返回的数据
            switch (requestCode) {
                // 拍照
                case TAKE_PHOTO_REQUEST_CODE:

                    cropPhoto(getPhotoUri());


                    break;
                // 相册选择
                case CHOICE_FROM_ALBUM_REQUEST_CODE:

                    cropPhoto(data.getData());
                    LogUtil.d("path",data.getData().toString());

                   /*  ContentResolver resolver = getContentResolver();
                    //照片的原始资源地址
                    Uri originalUri = data.getData();
                    cropPhoto(originalUri);

                   try {
                        //使用ContentProvider通过URI获取原始图片
                        Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);

                        savePhotoToSDCard("avatar.jpg",photo);

                        setuserpicture.setImageBitmap(photo);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/

                    break;
                // 裁剪图片
                case CROP_PHOTO_REQUEST_CODE:

                    String path=getPhotoOutputUri().getPath();
                    File file = new File(path);
                    if (file.exists()) {
                        LogUtil.d("path output",path);
                        Bitmap bitmap = BitmapFactory.decodeFile(path);

                        setuserpicture.setImageBitmap(bitmap);
                        SharedPreferenesUtil.setIsNeedUploadAvatar(true);
                    } else {
                        ToastUtil.showToast(getString(R.string.withoutpicture));
                    }
                    break;
            }
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
        myUser.update(bmobUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    LogUtil.d("uploadinfo", "succeed ");

                } else {
                    LogUtil.d("uploadinfo", "failed ");
                }
                closeProgressDialog();
            }
        });

        if(SharedPreferenesUtil.isNeedUploadAvatar()){
            upLoadAvatar();
            LogUtil.d("isneed","yes");
        }
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