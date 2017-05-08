package com.example.onotes.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.onotes.R;
import com.example.onotes.utils.ActivityCollector;
import com.example.onotes.utils.SendMail;
import com.example.onotes.anim.CircularAnim;
import com.example.onotes.bean.Person;
import com.example.onotes.utils.InputUtil;
import com.example.onotes.utils.SharedPreferenesUtil;
import com.example.onotes.utils.ToastUtil;
import com.example.onotes.weather.WeatherActivity;
import java.util.List;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText forgetpasswordemail;
    private EditText forgetverifycode;
    private Button forgetbutton;
    private Button forgetsendcodebutton;
    private int verfiycode;
    private EditText resetpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        ActivityCollector.addActivity(this);
        initView();
    }

    private void initView() {
        forgetpasswordemail = (EditText) findViewById(R.id.forgetpasswordemail);
        forgetverifycode = (EditText) findViewById(R.id.forgetverifycode);
        forgetbutton = (Button) findViewById(R.id.forgetbutton);
        forgetsendcodebutton = (Button) findViewById(R.id.forgetsendcodebutton);
        resetpassword = (EditText) findViewById(R.id.resetpassword);

        forgetbutton.setOnClickListener(this);
        forgetsendcodebutton.setOnClickListener(this);
        forgetverifycode.setFilters(new InputFilter[]{InputUtil.filterspace()});
        forgetpasswordemail.setFilters(new InputFilter[]{InputUtil.filterspace()});
        resetpassword.setFilters(new InputFilter[]{InputUtil.filterspace()});

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forgetbutton:
                resetPassword();
                break;
            case R.id.forgetsendcodebutton:
                verfiycode = 100000 + (int) (Math.random() * 800000);
                String message = "Your verify code is:" + verfiycode;
                SendMail sm = new SendMail(this, forgetpasswordemail.getText().toString(), message);
                sm.execute();

                break;
        }
    }

    private void resetPassword() {

        if (forgetverifycode.getText().toString().equals("" + verfiycode)) {

            BmobQuery<Person> query = new BmobQuery<Person>();
            query.addWhereEqualTo("email", forgetpasswordemail.getText().toString());
            query.findObjects(new FindListener<Person>() {
                @Override
                public void done(List<Person> object, BmobException e) {
                    if (e == null) {
                        final Person person = object.get(0);
                        person.setPassword(resetpassword.getText().toString());
                        person.update(person.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    ToastUtil.showToast(R.string.reset_password_sussesfully, Toast.LENGTH_SHORT);

                                    SharedPreferenesUtil.setPassword(resetpassword.getText().toString());
                                    SharedPreferenesUtil.setUsername(person.getUsername());
                                    CircularAnim.fullActivity(ForgetPasswordActivity.this, forgetbutton)
                                            .colorOrImageRes(R.color.colorAccent)
                                            .go(new CircularAnim.OnAnimationEndListener() {
                                                @Override
                                                public void onAnimationEnd() {
                                                    startActivity(new Intent(ForgetPasswordActivity.this, WeatherActivity.class));
                                                }
                                            });
                                    finish();
                                } else {
                                    ToastUtil.showToast(R.string.reset_password_failed, Toast.LENGTH_SHORT);
                                }
                            }
                        });
                    }
                }
            });
        } else {
            ToastUtil.showToast(R.string.verify_code_wrong, Toast.LENGTH_SHORT);
        }
    }
}
