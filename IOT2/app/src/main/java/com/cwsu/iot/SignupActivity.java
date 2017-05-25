package com.cwsu.iot;

import android.app.Activity;
import android.os.Bundle;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import myDB.dbUser;
import myDB.iotDB;
import util.Calculate;

/**
 * Created by cwsu on 2017/5/10.
 */
public class SignupActivity extends Activity{
    private dbUser dbuser = null;
    private iotDB dbHelper = null;
    private sessionManager sessionHelper= null;
    private Calculate ca = null;
    String key ="";
    String AuthQ = "";
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
        this.initModule();
        this.initLayout();
        this.initListener();
    }

    private void initModule(){
        queue = Volley.newRequestQueue(SignupActivity.this);
        dbHelper = new iotDB(SignupActivity.this);
        dbHelper.openDB(); // open db
        dbuser = new dbUser(dbHelper);
        sessionHelper = new sessionManager(getApplicationContext());
        ca = new Calculate();
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

            @Override
            public void onClick(View v) {
                String url = "http://140.119.164.35:8000/register/";
                JSONObject request = new JSONObject();
                String signupIdText = signupId.getText().toString();
                String signupPwText = signupPw.getText().toString();
//                String Ku = ca.generateKey();
//                String PV = ca.encryptDecrypt(ca.sha3(Ku+signupPwText),Ku);
                try
                {
                    request.put("username", signupIdText);
//                    request.put("PV",PV);
                    request.put("password", signupPwText);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST, url, request,
                        new Response.Listener<

                                JSONObject>() {
                            @Override
                            public void onResponse(JSONObject responseObj) {
                                try {
                                    key = responseObj.getString("key");
                                    Log.i("key",key);
//                                    String url2 = "http://140.119.164.35:8000/register/";
//                                    AuthQ = responseObj.getString("AuthQ");
//                                    String AuthA = ca.sha3(AuthQ);
//                                    JSONObject request2 = new JSONObject();
//                                    request2.put("AuthA",AuthA);
//                                    JsonObjectRequest strReq2 = new JsonObjectRequest(Request.Method.POST,url2,request2,
//                                            new Response.Listener<
//
//                                                    JSONObject>() {
//                                                @Override
//                                                public void onResponse(JSONObject responseObj) {
//                                                    try {
//
//                                                    } catch (JSONException e) {
//                                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//                                                    }
//                                                }
//                                            },
//                                            new Response.ErrorListener() {
//                                                @Override
//                                                public void onErrorResponse(VolleyError error) {
//                                                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
//                                                }
//                                            }
//                                    );
                                } catch (JSONException e) {
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                        }

                );
                queue.add(strReq);
                String tempKey = ca.encryptDecrypt(key,signupPwText);
                //Key 為用pw加密過的 key
                if(dbuser.addUser(signupPwText,signupPwText,tempKey)){
                    Toast.makeText(getApplicationContext(), "register success", Toast.LENGTH_LONG).show();
                    SignupActivity.this.finish();
                } else{
                    Toast.makeText(getApplicationContext(), "register fail", Toast.LENGTH_LONG).show();
                }


            }
        });
    }



}
