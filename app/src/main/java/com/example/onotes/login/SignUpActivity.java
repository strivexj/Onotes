package com.example.onotes.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.onotes.R;
import com.example.onotes.anim.CircularAnim;
import com.example.onotes.bean.MyUser;
import com.example.onotes.utils.ActivityCollector;
import com.example.onotes.utils.InputUtil;
import com.example.onotes.utils.LogUtil;
import com.example.onotes.utils.SendMail;
import com.example.onotes.utils.SharedPreferenesUtil;
import com.example.onotes.utils.ToastUtil;
import com.example.onotes.view.NotelistActivity;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText signupusername;
    private EditText signuppassword;
    private EditText signupemail;
    private Button sendverifycode;
    private EditText verifycode;
    private Button signupbutton;
    public int verfiycode;
    private boolean isexist;
    private EditText confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ActivityCollector.addActivity(this);
        initView();
    }

    private void initView() {
        signupusername = (EditText) findViewById(R.id.signupusername);
        signuppassword = (EditText) findViewById(R.id.signuppassword);
        signupemail = (EditText) findViewById(R.id.signupemail);
        sendverifycode = (Button) findViewById(R.id.sendverifycode);
        verifycode = (EditText) findViewById(R.id.verifycode);
        signupbutton = (Button) findViewById(R.id.signupbutton);
        confirmPassword=(EditText)findViewById(R.id.confirmPassword);

        //过滤空格
        signupusername.setFilters(new InputFilter[]{InputUtil.filterspace()});
        signupemail.setFilters(new InputFilter[]{InputUtil.filterspace()});
        signuppassword.setFilters(new InputFilter[]{InputUtil.filterspace()});
        confirmPassword.setFilters(new InputFilter[]{InputUtil.filterspace()});
        verifycode.setFilters(new InputFilter[]{InputUtil.filterspace()});
        sendverifycode.setOnClickListener(this);
        signupbutton.setOnClickListener(this);

        sendverifycode.setVisibility(View.GONE);
        verifycode.setVisibility(View.GONE);

        signupemail.setImeOptions(EditorInfo.IME_ACTION_DONE);
        signupemail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {


                    View view =  SignUpActivity.this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    signupbutton.callOnClick();
                }
                return true;
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendverifycode:
                //刚开始做了邮箱验证功能。。。然后发现bmob也有。。割了。。
                //获得一个六位随机数
                verfiycode = 100000 + (int) (Math.random() * 800000);

                String message = getResources().getString(R.string.verify_email_hi) + signupusername.getText().toString()
                        + getResources().getString(R.string.verify_email_welcome) + verfiycode
                        + getResources().getString(R.string.verify_email_end);

                SendMail sm = new SendMail(this, signupemail.getText().toString(), message);
                sm.execute();
                break;
            case R.id.signupbutton:
                register();
                break;
        }
    }

    private void register() {
        LogUtil.d("register","bbb");
            if(!confirmPassword.getText().toString().equals(signuppassword.getText().toString())){
                ToastUtil.showToast(getString(R.string.passwordCantMatch));
                return;
            }

            signupbutton.setClickable(false);

            final MyUser user = new MyUser();
            user.setUsername(signupusername.getText().toString());
            user.setPassword(signuppassword.getText().toString());
            user.setEmail(signupemail.getText().toString());

            user.signUp(new SaveListener<Object>() {
                @Override
                public void done(Object o, BmobException e) {
                    if (e == null) {
                        if(!TextUtils.isEmpty(signupemail.getText().toString())){
                           MyUser.requestEmailVerify(signupemail.getText().toString(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                      LogUtil.d("emailVerify","successfully");
                                    }else{
                                        LogUtil.d("emailVerify","failed");
                                    }
                                }
                            });
                        }

                        ToastUtil.showToast(getString(R.string.registerSuccessfully));

                        SharedPreferenesUtil.setUsername(signupusername.getText().toString());
                        SharedPreferenesUtil.setPassword(signuppassword.getText().toString());

                        user.login(new SaveListener<Object>() {
                            @Override
                            public void done(Object o, BmobException e) {
                                if(e==null){
                                    CircularAnim.fullActivity(SignUpActivity.this, signupbutton)
                                            .colorOrImageRes(R.color.accent)
                                            .go(new CircularAnim.OnAnimationEndListener() {
                                                @Override
                                                public void onAnimationEnd() {
                                                    startActivity(new Intent(SignUpActivity.this, NotelistActivity.class));
                                                }
                                            });
                                }
                            }
                        });
                    } else {
                       switch (e.getErrorCode()){
                               case 202:
                                   ToastUtil.showToast(getString(R.string.username_existed));
                                   break;
                               case 203:
                                   ToastUtil.showToast(getString(R.string.email_registered));
                                   break;
                               case 301:
                                   ToastUtil.showToast(getString(R.string.email_invaild));
                                   break;
                                case 304:
                                    ToastUtil.showToast(getString(R.string.usernameOrPasswordNull));
                                    break;
                               default:
                                    ToastUtil.showToast(getString(R.string.signup_failed));
                                   break;
                           }
                            signupbutton.setClickable(true);
                            LogUtil.d("login",e+" "+e.getErrorCode());
                        }
                    }
            });
    }
}

