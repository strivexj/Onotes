package com.example.onotes.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.onotes.R;
import com.example.onotes.anim.CircularAnim;
import com.example.onotes.bean.MyUser;
import com.example.onotes.bean.QqUser;
import com.example.onotes.utils.ActivityCollector;
import com.example.onotes.utils.InputUtil;
import com.example.onotes.utils.KeyboardUtil;
import com.example.onotes.utils.LogUtil;
import com.example.onotes.utils.SharedPreferenesUtil;
import com.example.onotes.utils.ToastUtil;
import com.example.onotes.view.NotelistActivity;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.onotes.utils.ScreenShot.getAlbumStorageDir;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog progressDialog;
    private EditText username;
    private EditText password;
    private Button sign_in_button;
    private TextView signup;
    private TextView forgetpassword;
    private CheckBox rememeberpassword;

    private static final String TAG = "LoginActivity";
    private static final String APP_ID = "1106087728";//官方获取的APPID

    private Tencent mTencent;
    private BaseUiListener mIUiListener;
    private UserInfo mUserInfo;
    private TextView qq;
    private ProgressBar progressBar;
    private ImageView backgroud;
    private CircleImageView userpicture;
    private CircleImageView qqpicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCollector.addActivity(this);

        //传入参数APPID和全局Context上下文
        mTencent = Tencent.createInstance(APP_ID, LoginActivity.this.getApplicationContext());
        //如果已经登录，就直接跳转至notelistActivity
        if (SharedPreferenesUtil.issignin()) {
            MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
            if (userInfo != null) {
                startActivity(new Intent(LoginActivity.this, NotelistActivity.class));
            }

        }
        setContentView(R.layout.activity_login);

        initView();
    }

    private void initView() {

        backgroud = (ImageView) findViewById(R.id.backgroud);
        username = (EditText) findViewById(R.id.signinusername);
        password = (EditText) findViewById(R.id.password);
        sign_in_button = (Button) findViewById(R.id.sign_in_button);
        signup = (TextView) findViewById(R.id.signup);
        forgetpassword = (TextView) findViewById(R.id.forgetpassword);
        rememeberpassword = (CheckBox) findViewById(R.id.rememeberpassword);
        qq = (TextView) findViewById(R.id.qq);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        userpicture = (CircleImageView) findViewById(R.id.userpicture);
        signup.setOnClickListener(this);
        sign_in_button.setOnClickListener(this);
        forgetpassword.setOnClickListener(this);
        qq.setOnClickListener(this);
        userpicture.setOnClickListener(this);
        qqpicture = (CircleImageView) findViewById(R.id.qqpicture);
        qqpicture.setOnClickListener(this);

        //过滤空格
        username.setFilters(new InputFilter[]{InputUtil.filterspace()});
        password.setFilters(new InputFilter[]{InputUtil.filterspace()});

        //默认弹出英文输入法
        username.setInputType(EditorInfo.TYPE_TEXT_VARIATION_URI);

        password.setImeOptions(EditorInfo.IME_ACTION_DONE);


        /**
         *
         * IME_ACTION_SEARCH 搜索
         * IME_ACTION_SEND 发送
         * IME_ACTION_NEXT 下一步
         * IME_ACTION_DONE 完成
         */


        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {


                    View view = LoginActivity.this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    sign_in_button.callOnClick();
                }
                return true;
            }
        });

        String pictureurl = SharedPreferenesUtil.getFigureurl_qq_2();

        File file = new File(getAlbumStorageDir("Onotes"), "avatar.jpg");
        if (file.exists()) {
            Glide.with(this)
                    .load(file)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .load(file).into(userpicture);
        } else if (!TextUtils.isEmpty(pictureurl)) {
            Glide.with(this).load(pictureurl).into(userpicture);
        }

        String susername = SharedPreferenesUtil.getUsername();
        final String spassword = SharedPreferenesUtil.getPassword();

        if (!TextUtils.isEmpty(susername)) {
            username.setText(susername);
            password.requestFocus();
        }

        //Search from stackoverflow
        //You may have to use et.post( new Runnable({... et.setSel... to get in the queue.
        // This is because android waits to do some layout stuff until a better time by posting so if you try to
        // setSelection before the system is finished it will undo your work.
        // – MinceMan Dec 7 '13 at 18:16
        if (!TextUtils.isEmpty(spassword)) {
            password.setText(spassword);
            password.post(new Runnable() {
                @Override
                public void run() {
                    password.setSelection(spassword.length());
                }
            });

        }

        //检查记住密码是够勾选
        if (SharedPreferenesUtil.isRemember_password_checkbox()) {
            rememeberpassword.setChecked(true);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button: {

                KeyboardUtil.hideSoftInput(this);

                progressBar.setVisibility(View.VISIBLE);

                // 收缩按钮
                CircularAnim.hide(sign_in_button).go();

                MyUser.loginByAccount(username.getText().toString(), password.getText().toString(), new LogInListener<MyUser>() {

                    @Override
                    public void done(MyUser user, BmobException e) {
                        if (user != null) {
                            ToastUtil.showToast(getString(R.string.sign_in_successfully));
                            Log.d("cwj", "Sign in succeed");

                            SharedPreferenesUtil.setUsername(username.getText().toString());

                            if (rememeberpassword.isChecked()) {
                                SharedPreferenesUtil.setPassword(password.getText().toString());
                                SharedPreferenesUtil.setRemember_password_checkbox(true);
                            } else {
                                SharedPreferenesUtil.setRemember_password_checkbox(false);
                                SharedPreferenesUtil.setPassword("");
                            }
                            CircularAnim.fullActivity(LoginActivity.this, sign_in_button)
                                    .colorOrImageRes(R.color.primary)
                                    .go(new CircularAnim.OnAnimationEndListener() {
                                        @Override
                                        public void onAnimationEnd() {
                                            startActivity(new Intent(LoginActivity.this, NotelistActivity.class));
                                        }
                                    });
                            SharedPreferenesUtil.setIssignin(true);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            CircularAnim.show(sign_in_button).go();
                            KeyboardUtil.showSoftInput(password);
                            ToastUtil.showToast(getString(R.string.username_or_password_wrong));
                        }
                    }

                });
            }
            break;
            case R.id.signup:
                CircularAnim.fullActivity(LoginActivity.this, signup)
                        .colorOrImageRes(R.color.primary)
                        .go(new CircularAnim.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                            }
                        });
                break;
            case R.id.forgetpassword:
                CircularAnim.fullActivity(LoginActivity.this, forgetpassword)
                        .colorOrImageRes(R.color.primary)
                        .go(new CircularAnim.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
                            }
                        });


                break;

            case R.id.qq:
            case R.id.qqpicture: {
                /**通过这句代码，SDK实现了QQ的登录，这个方法有三个参数，第一个参数是context上下文，第二个参数SCOPO 是一个String类型的字符串，表示一些权限
                 官方文档中的说明：应用需要获得哪些API的权限，由“，”分隔。例如：SCOPE = “get_user_info,add_t”；所有权限用“all”
                 第三个参数，是一个事件监听器，IUiListener接口的实例，这里用的是该接口的实现类 */
                showProgressDialog();
                mIUiListener = new BaseUiListener();
                //all表示获取所有权限
                mTencent.login(LoginActivity.this, "all", mIUiListener);
                break;
            }

        }
    }


    /**
     * 自定义监听器实现IUiListener接口后，需要实现的3个方法
     * onComplete完成 onError错误 onCancel取消
     */
    private class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object response) {
            ToastUtil.showToast(getString(R.string.authorize_successfully));

            Log.e(TAG, "response:" + response);

            JSONObject obj = (JSONObject) response;
            try {

                final String openID = obj.getString("openid");
                final String accessToken = obj.getString("access_token");
                final String expires = obj.getString("expires_in");

                mTencent.setOpenId(openID);
                mTencent.setAccessToken(accessToken, expires);
                final QQToken qqToken = mTencent.getQQToken();
                mUserInfo = new UserInfo(getApplicationContext(), qqToken);

                mUserInfo.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(Object response) {

                        Log.d("cwj", response.toString());

                        try {
                            JSONObject jo = (JSONObject) response;
                            final String nickname = jo.getString("nickname");
                            final String gender = jo.getString("gender");
                            final String province = jo.getString("province");
                            final String city = jo.getString("city");
                            final String figureurl = jo.getString("figureurl");
                            final String figureurl_1 = jo.getString("figureurl_1");
                            final String figureurl_2 = jo.getString("figureurl_2");
                            final String figureurl_qq_1 = jo.getString("figureurl_qq_1");
                            final String figureurl_qq_2 = jo.getString("figureurl_qq_2");

                            SharedPreferenesUtil.setQqInfo(nickname, gender, province, city, figureurl,
                                    figureurl_1, figureurl_2, figureurl_qq_1, figureurl_qq_2, openID,
                                    accessToken, expires);

                            BmobQuery<QqUser> query = new BmobQuery<QqUser>();
                            query.addWhereEqualTo("accessToken", accessToken);
                            query.count(QqUser.class, new CountListener() {
                                @Override
                                public void done(Integer count, BmobException e) {
                                    if (e == null) {
                                        if (count == 0) {

                                            QqUser user = new QqUser();
                                            user.setNickname(nickname);
                                            user.setGender(gender);
                                            user.setProvince(province);
                                            user.setCity(city);
                                            user.setFigureurl(figureurl);
                                            user.setFigureurl_1(figureurl_1);
                                            user.setFigureurl_2(figureurl_2);
                                            user.setFigureurl_qq_1(figureurl_qq_1);
                                            user.setFigureurl_2(figureurl_qq_2);
                                            user.setAccessToken(accessToken);
                                            user.setOpenID(openID);
                                            user.setExpires(expires);
                                            user.save(new SaveListener<String>() {
                                                @Override
                                                public void done(String s, BmobException e) {
                                                    if (e == null) {
                                                        LogUtil.d("cwj", "register");
                                                    } else {
                                                        LogUtil.d("cwj", "register fail");
                                                    }
                                                }
                                            });
                                        }


                                    } else {
                                        LogUtil.d("cwj", "失败：" + e.getMessage() + "," + e.getErrorCode());
                                    }
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        BmobUser.BmobThirdUserAuth authInfo = new BmobUser.BmobThirdUserAuth("qq", accessToken, expires, openID);
                        BmobUser.loginWithAuthData(authInfo, new LogInListener<JSONObject>() {

                            @Override
                            public void done(JSONObject userAuth, BmobException e) {
                                if (e == null) {

                                    CircularAnim.fullActivity(LoginActivity.this, sign_in_button)
                                            .colorOrImageRes(R.color.colorAccent)
                                            .go(new CircularAnim.OnAnimationEndListener() {
                                                @Override
                                                public void onAnimationEnd() {
                                                    startActivity(new Intent(LoginActivity.this, NotelistActivity.class));
                                                }
                                            });

                                    SharedPreferenesUtil.setIssignin(true);
                                }
                            }
                        });
                        Log.e(TAG, "登录成功" + response.toString());
                    }

                    @Override
                    public void onError(UiError uiError) {
                        Log.e(TAG, "登录失败" + uiError.toString());
                    }

                    @Override
                    public void onCancel() {
                        Log.e(TAG, "登录取消");

                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                closeProgressDialog();
            }

        }

        @Override
        public void onError(UiError uiError) {
            ToastUtil.showToast(getString((R.string.authorize_failed)));
            closeProgressDialog();
        }

        @Override
        public void onCancel() {
            ToastUtil.showToast(getString(R.string.authorize_canceled));
            closeProgressDialog();
        }

    }

    /**
     * 在调用Login的Activity或者Fragment中重写onActivityResult方法
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, mIUiListener);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onPause() {
        if (SharedPreferenesUtil.issignin())
            finish();
        Log.d("cwja", "onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d("cwja", "onresume");

        final String susername = SharedPreferenesUtil.getUsername();
        final String spassword = SharedPreferenesUtil.getPassword();

        if (!TextUtils.isEmpty(susername)) {
            username.setText(susername);
            password.requestFocus();
        }
        ;
        //Search from stackoverflow
        //You may have to use et.post( new Runnable({... et.setSel... to get in the queue.
        // This is because android waits to do some layout stuff until a better time by posting so if you try to
        // setSelection before the system is finished it will undo your work.
        // – MinceMan Dec 7 '13 at 18:16

        if (!TextUtils.isEmpty(spassword)) {
            password.setText(spassword);
            password.post(new Runnable() {
                @Override
                public void run() {
                    password.setSelection(spassword.length());
                }
            });

        }
        if (SharedPreferenesUtil.isRemember_password_checkbox()) rememeberpassword.setChecked(true);
        super.onResume();
    }

    /**
     * show progress dialog
     */
    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(this.getString(R.string.sign_in_progress));
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ActivityCollector.finishAll();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        Log.d("cwja", "ondestroy");
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        Log.d("cwja", "onstart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d("cwja", "onstop");
        super.onStop();
    }

    @Override
    public void finish() {
        Log.d("cwja", "onsfinish");
        super.finish();
    }


}

