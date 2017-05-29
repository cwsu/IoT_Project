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

import myDB.iotDB;
import util.Calculate;

/**
 * Created by cwsu on 2017/5/10.
 */
public class SignupActivity extends Activity{

    private iotDB dbHelper = null;
    private sessionManager sessionHelper= null;
    private Calculate ca = null;
    String Spu = "1000";
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
//                String url = "http://140.119.164.35:8003/register";
                String url = "http://127.0.0.1:8003/register";
                JSONObject request = new JSONObject();
                final String signupIdText = signupId.getText().toString();
                String signupPwText = signupPw.getText().toString();
                String Ku = ca.sha3(signupIdText+"_"+"gateway"+"_"+signupPwText);
                String PV = ca.encryptDecrypt(ca.sha3(Ku+"_"+signupPwText),Ku);

                try
                {
                    request.put("ID", signupIdText);
                    request.put("PV",PV);
                    request.put("Espu_P",ca.encryptDecrypt(signupPwText,Spu));
                    request.put("Espu_Ku", ca.encryptDecrypt(Ku,Spu));

                    Log.i("PV",PV);
                    Log.i("Espu_P",ca.encryptDecrypt(signupPwText,Spu));
                    Log.i("Espu_Ku",ca.encryptDecrypt(Ku,Spu));
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
                                    String response = responseObj.getString("response");
                                    Log.i("response",response);
                                    if(response =="yes"){
                                        Toast.makeText(getApplicationContext(),"register success", Toast.LENGTH_LONG).show();
                                        SignupActivity.this.finish();
                                    }else{
                                        Toast.makeText(getApplicationContext(),"register fail", Toast.LENGTH_LONG).show();
                                        signupId.setText("");
                                        signupPw.setText("");
                                    }
                                } catch (JSONException e) {
                                    Log.i("err",e.getMessage());
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
            }
        });
    }
}
