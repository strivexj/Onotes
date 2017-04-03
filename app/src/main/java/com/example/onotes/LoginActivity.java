package com.example.onotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onotes.anim.CircularAnim;
import com.example.onotes.bean.Person;
import com.example.onotes.weatheractivity.WeatherMainActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Bmob.initialize(this, "9114a2d5e04f0ff10206a7efb408e11e");
        initView();

    }

    private void initView() {
        login_progress = (ProgressBar) findViewById(R.id.login_progress);
        username = (AutoCompleteTextView) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        sign_in_button = (Button) findViewById(R.id.sign_in_button);
        signup = (TextView) findViewById(R.id.signup);
        forgetpassword = (TextView) findViewById(R.id.forgetpassword);
        email_login_form = (LinearLayout) findViewById(R.id.email_login_form);
        rememeberpassword=(CheckBox) findViewById(R.id.rememeberpassword);
        SharedPreferences pref=getSharedPreferences("account",MODE_PRIVATE);
        String susername=pref.getString("username","");
        String spassword=pref.getString("password","");

        if(!TextUtils.isEmpty(susername))username.setText(susername);
        if(!TextUtils.isEmpty(spassword))password.setText(spassword);
        if(pref.getBoolean("checkbox",false))rememeberpassword.setChecked(true);
        signup.setOnClickListener(this);
        sign_in_button.setOnClickListener(this);
        forgetpassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:{

                BmobQuery<Person> query = new BmobQuery<Person>();
                query.addWhereEqualTo("username",username.getText().toString());
                query.findObjects(new FindListener<Person>() {
                    @Override
                    public void done(List<Person> object, BmobException e) {
                        if(e==null) {
                            Person person = object.get(0);
                            if (password.getText().toString().equals(person.getPassword())) {
                                Toast.makeText(LoginActivity.this, "Sign in succeed", Toast.LENGTH_SHORT).show();
                                Log.d("cwj", "Sign in succeed");
                                SharedPreferences.Editor editor = getSharedPreferences("account", MODE_PRIVATE).edit();
                                editor.putString("username", password.getText().toString());
                                if (rememeberpassword.isChecked()) {
                                    Log.d("cwj","2");

                                    //Toast.makeText(LoginActivity.this, password.getText().toString(), Toast.LENGTH_SHORT).show();
                                    editor.putString("password", password.getText().toString());
                                    editor.putBoolean("checkbox",true);
                                    editor.apply();
                                }else{
                                    Log.d("cwj","1");
                                    editor.putBoolean("checkbox",false);
                                    editor.putString("password", "");
                                    editor.apply();
                                }
                                CircularAnim.fullActivity(LoginActivity.this,sign_in_button)
                                        .colorOrImageRes(R.color.colorPrimary)
                                        .go(new CircularAnim.OnAnimationEndListener() {
                                            @Override
                                            public void onAnimationEnd() {
                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            }
                                        });
                                /*Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);*/
                            }

                            else{
                                Toast.makeText(LoginActivity.this, "Your username or password maybe wrong.", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(LoginActivity.this, "Your username or password maybe wrong.", Toast.LENGTH_SHORT).show();
                            Log.d("cwj","query failed");
                        }
                    }
                });
            }

            break;
            case R.id.signup:
                Intent intent=new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.forgetpassword:

                break;

        }
    }

    private void submit() {
        // validate
        String passwordString = password.getText().toString().trim();
        if (TextUtils.isEmpty(passwordString)) {
            Toast.makeText(this, "passwordString不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO validate success, do something


    }
}
