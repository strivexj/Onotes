package com.example.onotes.login;

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
import com.example.onotes.R;
import com.example.onotes.anim.CircularAnim;
import com.example.onotes.bean.Person;
import com.example.onotes.utils.InputUtil;
import com.example.onotes.utils.LogUtil;
import com.example.onotes.view.EditTextActivity;
import com.example.onotes.weather.WeatherMainActivity;
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
import cn.bmob.v3.listener.FindListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


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
    private static final String APP_ID = "**";//官方获取的APPID
    private Tencent mTencent;
    private BaseUiListener mIUiListener;
    private UserInfo mUserInfo;

    private TextView qq;
    private TextView addgroup;
    private ProgressBar progressBar;
    private ImageView backgroud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        LogUtil.v(this,"aaa");
        Bmob.initialize(this, "9114a2d5e04f0ff10206a7efb408e11e");

        //传入参数APPID和全局Context上下文
        mTencent = Tencent.createInstance(APP_ID, LoginActivity.this.getApplicationContext());

        initView();

    }

    private void initView() {
        backgroud=(ImageView)findViewById(R.id.backgroud) ;
        username = (AutoCompleteTextView) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        sign_in_button = (Button) findViewById(R.id.sign_in_button);
        signup = (TextView) findViewById(R.id.signup);
        forgetpassword = (TextView) findViewById(R.id.forgetpassword);
        rememeberpassword = (CheckBox) findViewById(R.id.rememeberpassword);
        qq = (TextView) findViewById(R.id.qq);
        addgroup = (TextView) findViewById(R.id.addgroup);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);


        username.setFilters(new InputFilter[]{InputUtil.filterspace()});
        password.setFilters(new InputFilter[]{InputUtil.filterspace()});

        password.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    sign_in_button.callOnClick();
                }
                return false;
            }
        });

       // Glide.with(this).load(R.drawable.backgroud).into(backgroud);
        SharedPreferences pref = getSharedPreferences("account", MODE_PRIVATE);
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
                                SharedPreferences.Editor editor = getSharedPreferences("account", MODE_PRIVATE).edit();
                                editor.putString("username", username.getText().toString());
                                if (rememeberpassword.isChecked()) {

                                    editor.putString("password", password.getText().toString());
                                    editor.putBoolean("checkbox", true);
                                    editor.apply();
                                } else {
                                    Log.d("cwj", "1");
                                    editor.putBoolean("checkbox", false);
                                    editor.putString("password", "");
                                    editor.apply();
                                }
                                CircularAnim.show(sign_in_button).go();
                                CircularAnim.fullActivity(LoginActivity.this, sign_in_button)
                                        .colorOrImageRes(R.color.primary)
                                        .go(new CircularAnim.OnAnimationEndListener() {
                                            @Override
                                            public void onAnimationEnd() {
                                                startActivity(new Intent(LoginActivity.this, EditTextActivity.class));
                                            }
                                        });
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
                /**通过这句代码，SDK实现了QQ的登录，这个方法有三个参数，第一个参数是context上下文，第二个参数SCOPO 是一个String类型的字符串，表示一些权限
                 官方文档中的说明：应用需要获得哪些API的权限，由“，”分隔。例如：SCOPE = “get_user_info,add_t”；所有权限用“all”
                 第三个参数，是一个事件监听器，IUiListener接口的实例，这里用的是该接口的实现类 */
                mIUiListener = new BaseUiListener();
                //all表示获取所有权限
                mTencent.login(LoginActivity.this, "all", mIUiListener);
                //Intent intent=new Intent(LoginActivity.this,ForgetPasswordActivity.class);
                // startActivity(intent);
                break;
            case R.id.addgroup:
                joinQQGroup();
                break;

        }
    }

    /****************
     * 发起添加群流程。群号：pdf(311357701) 的 key 为： _pTMqAXJrpUUk0t86WRYonnbx-axNgWb
     * 调用 joinQQGroup(_pTMqAXJrpUUk0t86WRYonnbx-axNgWb) 即可发起手Q客户端申请加群 pdf(311357701)
     *
     * @param
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    public boolean joinQQGroup() {
        String key = "_pTMqAXJrpUUk0t86WRYonnbx-axNgWb";
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivityForResult(intent, 0);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
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
                String openID = obj.getString("openid");
                String accessToken = obj.getString("access_token");
                String expires = obj.getString("expires_in");
                mTencent.setOpenId(openID);
                mTencent.setAccessToken(accessToken, expires);
                QQToken qqToken = mTencent.getQQToken();
                mUserInfo = new UserInfo(getApplicationContext(), qqToken);
                mUserInfo.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(Object response) {
                        CircularAnim.fullActivity(LoginActivity.this, sign_in_button)
                                .colorOrImageRes(R.color.accent)
                                .go(new CircularAnim.OnAnimationEndListener() {
                                    @Override
                                    public void onAnimationEnd() {
                                        startActivity(new Intent(LoginActivity.this, EditTextActivity.class));
                                    }
                                });
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
            }
        }

        @Override
        public void onError(UiError uiError) {
            Toast.makeText(LoginActivity.this, "授权失败", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCancel() {
            Toast.makeText(LoginActivity.this, "授权取消", Toast.LENGTH_SHORT).show();

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
        SharedPreferences pref = getSharedPreferences("account", MODE_PRIVATE);
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

