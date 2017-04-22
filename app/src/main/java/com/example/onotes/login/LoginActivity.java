package com.example.onotes.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.onotes.App;
import com.example.onotes.R;
import com.example.onotes.anim.CircularAnim;
import com.example.onotes.bean.Person;
import com.example.onotes.bean.QqUser;
import com.example.onotes.service.CityDownloadSerivce;
import com.example.onotes.utils.ActivityCollector;
import com.example.onotes.utils.InputUtil;
import com.example.onotes.utils.LogUtil;
import com.example.onotes.view.NotelistActivity;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog progressDialog;
    private ProgressBar login_progress;
    private AutoCompleteTextView username;
    private EditText password;
    private Button sign_in_button;
    private TextView signup;
    private TextView forgetpassword;
    private LinearLayout email_login_form;
    private ScrollView login_form;
    private CheckBox rememeberpassword;
    private boolean issignin = false;

    private static final String TAG = "LoginActivity";
    private static final String APP_ID = "1106087728";//官方获取的APPID
    private Tencent mTencent;
    private BaseUiListener mIUiListener;
    private UserInfo mUserInfo;

    private TextView qq;
    private TextView addgroup;
    private ProgressBar progressBar;
    private ImageView backgroud;
    private CircleImageView userpicture;
    private CircleImageView qqpicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Bmob.initialize(this, "9114a2d5e04f0ff10206a7efb408e11e");
        ActivityCollector.addActivity(this);
        //传入参数APPID和全局Context上下文
        mTencent = Tencent.createInstance(APP_ID, LoginActivity.this.getApplicationContext());

        SharedPreferences preferences = App.getContext().getSharedPreferences("account", MODE_PRIVATE);
        issignin = preferences.getBoolean("issignin", false);

        SharedPreferences pref = App.getContext().getSharedPreferences("weather", MODE_PRIVATE);
        if (!pref.getBoolean("cityadded", false)) {
            Intent intentService = new Intent(this, CityDownloadSerivce.class);
            startService(intentService);
        }

        if (issignin) {
            startActivity(new Intent(LoginActivity.this, NotelistActivity.class));
        }

        setContentView(R.layout.activity_login);

        initView();


    }

    private void initView() {
        Log.d("cwj", "oasfdds");
        backgroud = (ImageView) findViewById(R.id.backgroud);
        username = (AutoCompleteTextView) findViewById(R.id.signinusername);
        password = (EditText) findViewById(R.id.password);
        sign_in_button = (Button) findViewById(R.id.sign_in_button);
        signup = (TextView) findViewById(R.id.signup);
        forgetpassword = (TextView) findViewById(R.id.forgetpassword);
        rememeberpassword = (CheckBox) findViewById(R.id.rememeberpassword);
        qq = (TextView) findViewById(R.id.qq);
        addgroup = (TextView) findViewById(R.id.addgroup);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        userpicture = (CircleImageView) findViewById(R.id.userpicture);

        username.setFilters(new InputFilter[]{InputUtil.filterspace()});
        password.setFilters(new InputFilter[]{InputUtil.filterspace()});

        //默认弹出英文输入法
        password.setInputType(EditorInfo.TYPE_TEXT_VARIATION_URI);
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

                   // LoginActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    View view =  LoginActivity.this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }


                    /* 打开输入法
                        InputMethodManagerinputMethodManager=(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(editText,0);
                     */
                    sign_in_button.callOnClick();
                }
                return true;
            }

        });


     /*   password.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    sign_in_button.callOnClick();
                }
                return false;
            }
        });*/

        SharedPreferences qqsp = App.getContext().getSharedPreferences("qqaccount", MODE_PRIVATE);
        String pictureurl = qqsp.getString("figureurl_qq_2", "");
        if (!TextUtils.isEmpty(pictureurl)) {
            Glide.with(this).load(pictureurl).into(userpicture);
        }

        SharedPreferences pref = App.getContext().getSharedPreferences("account", MODE_PRIVATE);
        String susername = pref.getString("username", "");
        final String spassword = pref.getString("password", "");

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

            Log.d("aa", "sd");
            //password.setSelection(password.getSelectionEnd());
        }
        if (pref.getBoolean("checkbox", false)) rememeberpassword.setChecked(true);
        signup.setOnClickListener(this);
        sign_in_button.setOnClickListener(this);
        forgetpassword.setOnClickListener(this);
        qq.setOnClickListener(this);
        addgroup.setOnClickListener(this);

        userpicture.setOnClickListener(this);
        qqpicture = (CircleImageView) findViewById(R.id.qqpicture);
        qqpicture.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button: {

                progressBar.setVisibility(View.VISIBLE);
                // 收缩按钮
                CircularAnim.hide(sign_in_button).go();

                BmobQuery<Person> query = new BmobQuery<Person>();
                query.addWhereEqualTo("username", username.getText().toString());
                query.findObjects(new FindListener<Person>() {

                    @Override
                    public void done(List<Person> object, BmobException e) {
                        if (e == null) {
                            Person person = object.get(0);
                            if (password.getText().toString().equals(person.getPassword())) {
                                Toast.makeText(LoginActivity.this, "Sign in succeeded", Toast.LENGTH_SHORT).show();
                                Log.d("cwj", "Sign in succeed");
                                SharedPreferences.Editor editor = App.getContext().getSharedPreferences("account", MODE_PRIVATE).edit();
                                editor.putString("username", username.getText().toString());
                                if (rememeberpassword.isChecked()) {

                                    editor.putString("password", password.getText().toString());
                                    editor.putBoolean("checkbox", true);
                                    //editor.apply();
                                } else {
                                    Log.d("cwj", "1");
                                    editor.putBoolean("checkbox", false);
                                    editor.putString("password", "");
                                    //editor.apply();
                                }
                                CircularAnim.show(sign_in_button).go();
                                CircularAnim.fullActivity(LoginActivity.this, sign_in_button)
                                        .colorOrImageRes(R.color.primary)
                                        .go(new CircularAnim.OnAnimationEndListener() {
                                            @Override
                                            public void onAnimationEnd() {
                                                startActivity(new Intent(LoginActivity.this, NotelistActivity.class));
                                            }
                                        });
                                editor.putBoolean("issignin", true);
                                editor.apply();

                                issignin = true;

                            } else {

                                progressBar.setVisibility(View.GONE);
                                CircularAnim.show(sign_in_button).go();

                                Toast.makeText(LoginActivity.this, "Your username or password maybe wrong.", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            progressBar.setVisibility(View.GONE);
                            CircularAnim.show(sign_in_button).go();

                            Toast.makeText(LoginActivity.this, "Your username or password maybe wrong.", Toast.LENGTH_SHORT).show();
                            Log.d("cwj", "query failed");
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

                break;
            case R.id.qqpicture: {
                /**通过这句代码，SDK实现了QQ的登录，这个方法有三个参数，第一个参数是context上下文，第二个参数SCOPO 是一个String类型的字符串，表示一些权限
                 官方文档中的说明：应用需要获得哪些API的权限，由“，”分隔。例如：SCOPE = “get_user_info,add_t”；所有权限用“all”
                 第三个参数，是一个事件监听器，IUiListener接口的实例，这里用的是该接口的实现类 */
                showProgressDialog();

                mIUiListener = new BaseUiListener();
                //all表示获取所有权限
                mTencent.login(LoginActivity.this, "all", mIUiListener);
                //Intent intent=new Intent(LoginActivity.this,ForgetPasswordActivity.class);
                // startActivity(intent);
                //closeProgressDialog();
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
            Toast.makeText(LoginActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
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
                            SharedPreferences.Editor editor = App.getContext().getSharedPreferences("qqaccount", MODE_PRIVATE).edit();
                            final String nickname = jo.getString("nickname");
                            final String gender = jo.getString("gender");
                            final String province = jo.getString("province");
                            final String city = jo.getString("city");
                            final String figureurl = jo.getString("figureurl");
                            final String figureurl_1 = jo.getString("figureurl_1");
                            final String figureurl_2 = jo.getString("figureurl_2");
                            final String figureurl_qq_1 = jo.getString("figureurl_qq_1");
                            final String figureurl_qq_2 = jo.getString("figureurl_qq_2");

                            editor.putString("nickname", nickname);
                            editor.putString("gender", gender);
                            editor.putString("province", province);
                            editor.putString("city", city);
                            editor.putString("figureurl", figureurl);
                            editor.putString("figureurl_1", figureurl_1);
                            editor.putString("figureurl_2", figureurl_2);
                            editor.putString("figureurl_qq_1", figureurl_qq_1);
                            editor.putString("figureurl_qq_2", figureurl_qq_2);
                            editor.putString("openID", openID);
                            editor.putString("accessToken", accessToken);
                            editor.putString("expires", expires);
                            editor.apply();

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
                        CircularAnim.fullActivity(LoginActivity.this, sign_in_button)
                                .colorOrImageRes(R.color.accent)
                                .go(new CircularAnim.OnAnimationEndListener() {
                                    @Override
                                    public void onAnimationEnd() {
                                        startActivity(new Intent(LoginActivity.this, NotelistActivity.class));
                                    }
                                });

                        SharedPreferences.Editor editor = App.getContext().getSharedPreferences("account", MODE_PRIVATE).edit();
                        editor.putBoolean("issignin", true);
                        editor.apply();

                        issignin = true;
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
            Toast.makeText(LoginActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
            closeProgressDialog();
        }

        @Override
        public void onCancel() {
            Toast.makeText(LoginActivity.this, "授权取消", Toast.LENGTH_SHORT).show();
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
        if (issignin)
            finish();

        Log.d("cwja", "onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d("cwja", "onresume");
        SharedPreferences pref = App.getContext().getSharedPreferences("account", MODE_PRIVATE);
        final String susername = pref.getString("username", "");
        final String spassword = pref.getString("password", "");
        //if(!TextUtils.isEmpty(susername))username.setText(susername);
        // if(!TextUtils.isEmpty(spassword))password.setText(spassword);
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

            Log.d("aa", "sd");
            //password.setSelection(password.getSelectionEnd());
        }
        if (pref.getBoolean("checkbox", false)) rememeberpassword.setChecked(true);
        super.onResume();
    }

    /**
     * show progress dialog
     */
    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait...");
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ActivityCollector.finishAll();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

