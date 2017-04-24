package com.example.onotes.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.onotes.R;
import com.example.onotes.anim.CircularAnim;
import com.example.onotes.bean.Person;
import com.example.onotes.utils.ActivityCollector;
import com.example.onotes.utils.InputUtil;
import com.example.onotes.utils.SendMail;
import com.example.onotes.utils.SharedPreferenesUtil;
import com.example.onotes.utils.ToastUtil;
import com.example.onotes.weather.WeatherActivity;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.SaveListener;


public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText signupusername;
    private EditText signuppassword;
    private EditText signupemail;
    private Button sendverifycode;
    private EditText verifycode;
    private Button signupbutton;
    public int verfiycode;
    private boolean isexist;

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

        signupusername.setFilters(new InputFilter[]{InputUtil.filterspace()});
        signupemail.setFilters(new InputFilter[]{InputUtil.filterspace()});
        signuppassword.setFilters(new InputFilter[]{InputUtil.filterspace()});
        verifycode.setFilters(new InputFilter[]{InputUtil.filterspace()});

        sendverifycode.setOnClickListener(this);
        signupbutton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendverifycode:
                verfiycode = 100000 + (int) (Math.random() * 800000);
                String message = R.string.verify_email_hi + signupusername.getText().toString()
                        + R.string.verify_email_welcome + verfiycode
                        + R.string.verify_email_end;

                SendMail sm = new SendMail(this, signupemail.getText().toString(), message);
                sm.execute();
                break;
            case R.id.signupbutton:

                register();
                break;
        }
    }

    private boolean isexist() {

        BmobQuery<Person> query = new BmobQuery<Person>();
        query.addWhereEqualTo("username", signupusername.getText().toString());
        query.count(Person.class, new CountListener() {
            @Override
            public void done(Integer count, BmobException e) {
                if (e == null) {
                    if (count != 0) {
                       // Toast.makeText(SignUpActivity.this, , Toast.LENGTH_SHORT).show();
                        ToastUtil.showToast(R.string.username_registered,Toast.LENGTH_SHORT);
                        isexist = true;
                    } else isexist = false;

                } else {
                    Log.d("cwj", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
        return isexist;
    }

    private void register() {
        if (verifycode.getText().toString().equals("" + verfiycode)) {
            signupbutton.setClickable(false);
            Person person = new Person();
            person.setUsername(signupusername.getText().toString());
            person.setPassword(signuppassword.getText().toString());
            person.setEmail(signupemail.getText().toString());
            if (isexist()) {
                ToastUtil.showToast(R.string.username_registered,Toast.LENGTH_SHORT);
                signupbutton.setClickable(true);
            } else {
                person.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            Toast.makeText(SignUpActivity.this, "succeed", Toast.LENGTH_SHORT).show();

                            SharedPreferenesUtil.setUsername(signupusername.getText().toString());
                            SharedPreferenesUtil.setPassword(signuppassword.getText().toString());

                            CircularAnim.fullActivity(SignUpActivity.this, signupbutton)
                                    .colorOrImageRes(R.color.accent)
                                    .go(new CircularAnim.OnAnimationEndListener() {
                                        @Override
                                        public void onAnimationEnd() {
                                            startActivity(new Intent(SignUpActivity.this, WeatherActivity.class));
                                        }
                                    });
                            finish();
                        } else {
                            ToastUtil.showToast(R.string.signup_failed,Toast.LENGTH_SHORT);
                        }
                    }
                });
            }

             /*_User user = new _User();
             user.setUsername(signupusername.getText().toString());
             user.setPassword(signuppassword.getText().toString());
             user.setEmail(signupemail.getText().toString());
             user.save(new SaveListener<String>() {
                 @Override
                 public void done(String s, BmobException e) {
                     if (e == null) {
                         Toast.makeText(SignUpActivity.this, "succeed", Toast.LENGTH_SHORT).show();
                         finish();
                     } else {
                         Toast.makeText(SignUpActivity.this, "failed", Toast.LENGTH_SHORT).show();
                     }
                 }
             });*/

            sendverifycode.setClickable(false);

        } else {
            ToastUtil.showToast(R.string.verify_code_wrong,Toast.LENGTH_SHORT);
        }
    }


}
