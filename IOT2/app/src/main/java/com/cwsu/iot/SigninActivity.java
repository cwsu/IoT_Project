package com.cwsu.iot;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by cwsu on 2017/5/10.
 */
public class SigninActivity extends Activity{
    LinearLayout signinContent;
    EditText signinId;
    EditText signinPw;
    Button btnDoSignin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        this.initLayout();
        this.initListener();
    }

    private void initLayout() {
        signinContent = (LinearLayout) findViewById(R.id.signin_content);
        signinId = (EditText) findViewById(R.id.signin_id);
        signinPw = (EditText) findViewById(R.id.signin_pw);
        btnDoSignin = (Button) findViewById(R.id.btn_do_signin);
    }

    private void initListener() {
        btnDoSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idText = signinId.getText().toString();
                String pwText = signinPw.getText().toString();
                if(!idText.equals("")&& !pwText.equals("")){

                }else{
                    Toast.makeText(SigninActivity.this, "please enter your id or password",Toast.LENGTH_SHORT).show();
                    signinId.setText("");
                    signinPw.setText("");
                }
            }
        });
    }
}
