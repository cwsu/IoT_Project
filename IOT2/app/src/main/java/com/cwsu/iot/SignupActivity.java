package com.cwsu.iot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cwsu on 2017/5/10.
 */
public class SignupActivity extends Activity{
    private String postUrl = "http://127.0.0.1:8000/register/";
    Http_Post HP;
    static Handler handler;
    LinearLayout signupContent;
    EditText signupId;
    EditText signupPw;
    EditText signupConfirmPw;
    Button btnDoSignup;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        this.initLayout();
        this.initListener();
        HP = new Http_Post();
        //接收service傳出Post的到的回傳訊息，並透過Toast顯示出來
        handler = new Handler(){
            public void handleMessage(Message msg){
                switch (msg.what){
                    case 0:
                        Toast.makeText(SignupActivity.this, "register fail", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Toast.makeText(SignupActivity.this, "register seccess", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };
    }

    private void initLayout() {
        signupContent = (LinearLayout) findViewById(R.id.signup_content);
        signupId = (EditText) findViewById(R.id.signup_id);
        signupPw = (EditText) findViewById(R.id.signup_pw);
        signupConfirmPw = (EditText) findViewById(R.id.signup_confirm_pw);
        btnDoSignup = (Button) findViewById(R.id.btn_do_signup);
    }

    private void initListener() {
        btnDoSignup.setOnClickListener(new View.OnClickListener() {
            Intent intent;
            @Override
            public void onClick(View v) {
                String url = "http://10.0.2.2:8000/register/";

                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // response
                                Log.d("Response", response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Log.d("Error.Response", String.valueOf(error));
                            }
                        }
                ) {

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("username", "1234");
                        params.put("password", "12");
                        return params;
                    }
                };

                queue.add(postRequest);
                intent = new Intent(SignupActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }


}
