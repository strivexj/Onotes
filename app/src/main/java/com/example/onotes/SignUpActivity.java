package com.example.onotes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.onotes.bean.Person;

import java.util.Random;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText signupusername;
    private EditText signuppassword;
    private EditText signupemail;
    private Button sendverifycode;
    private EditText verifycode;
    private Button signupbutton;
    public int verfiycode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initView();
        //signupemail.setText("1003214597@qq.com");
    }

    private void initView() {
        signupusername = (EditText) findViewById(R.id.signupusername);
        signuppassword = (EditText) findViewById(R.id.signuppassword);
        signupemail = (EditText) findViewById(R.id.signupemail);
        sendverifycode = (Button) findViewById(R.id.sendverifycode);
        verifycode = (EditText) findViewById(R.id.verifycode);
        signupbutton = (Button) findViewById(R.id.signupbutton);


        sendverifycode.setOnClickListener(this);
        signupbutton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendverifycode:
                verfiycode=100000+(int)(Math.random()*800000);
                String message="Your verify code is:"+verfiycode;
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                SendMail sm = new SendMail(this, signupemail.getText().toString(), message);
                sm.execute();


                break;
            case R.id.signupbutton:
               if(verifycode.getText().toString().equals(""+verfiycode))
                {
                    Person person = new Person();
                    person.setUsername(signupusername.getText().toString());
                    person.setPassword(signuppassword.getText().toString());
                    person.setEmail(signupemail.getText().toString());
                    person.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                Toast.makeText(SignUpActivity.this, "succeed", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignUpActivity.this, "failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    sendverifycode.setClickable(false);

              /*  if(verifycode.getText().toString().equals(""+verfiycode)){
                    b=true;
                }
                if(b){
                    Log.d("cwj","equal");
                }*/
                   }
                    else {
                   Toast.makeText(this, "your verify code is wrong", Toast.LENGTH_SHORT).show();
               }
                break;
        }
    }


}
