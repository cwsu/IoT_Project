package com.cwsu.iot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import myDB.dbUser;
import myDB.iotDB;


public class MainActivity extends AppCompatActivity {

    RelativeLayout activityMain;
    LinearLayout mainContent;
    Button btnSignin;
    Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.initLayout();
        this.initListener();
    }

    private void initLayout() {
        activityMain = (RelativeLayout) findViewById(R.id.activity_main);
        mainContent = (LinearLayout) findViewById(R.id.main_content);
        btnSignin = (Button) findViewById(R.id.btn_signin);
        btnSignup = (Button) findViewById(R.id.btn_signup);
    }

    private void initListener() {

        btnSignin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,SigninActivity.class);
                startActivity(intent);
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,SignupActivity.class);
                startActivity(intent);
            }

        });
    }
}
