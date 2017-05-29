package com.cwsu.iot;

import android.app.Activity;
import android.content.Intent;
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

import java.security.NoSuchAlgorithmException;

import myDB.dbUser;
import myDB.iotDB;
import util.Calculate;

/**
 * Created by cwsu on 2017/5/10.
 */
public class SigninActivity extends Activity{
    private dbUser dbuser = null;
    private iotDB dbHelper = null;
    private sessionManager sessionHelper;
    private Calculate ca = null;
    String Spu = "1000";
    String r1="";
    String token = "";
    LinearLayout signinContent;
    EditText signinId;
    EditText signinPw;
    Button btnDoSignin;
    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        this.initModule();
        this.initLayout();
        this.initListener();
    }

    private void initModule(){
        queue = Volley.newRequestQueue(SigninActivity.this);
        dbHelper = new iotDB(SigninActivity.this);
        dbHelper.openDB(); // open db
        dbuser = new dbUser(dbHelper);
        sessionHelper = new sessionManager(getApplicationContext());
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
                final String idText = signinId.getText().toString();
                String authUrl = "http://140.119.164.35:8003/auth_1";
                String pwText = signinPw.getText().toString();
                String XP = ca.encryptDecrypt(ca.sha3(idText+"_"+"gateway"),pwText);
                final String Ku = ca.sha3(idText+"_"+"gateway"+"_"+pwText);
                final String PV = ca.encryptDecrypt(ca.sha3(Ku+"_"+pwText),Ku);

                try {
                    r1 = ca.generateRandom();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                JSONObject request = new JSONObject();
                try {
                    request.put("X1",ca.encryptDecrypt(ca.sha3(XP+"_"+idText+"_"+r1),Spu));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST, authUrl, request,
                        new Response.Listener<

                                JSONObject>() {
                            @Override
                            public void onResponse(JSONObject responseObj) {
                                try {
                                    String authUrl2 = "http://140.119.164.35:8003/auth_2";
                                    String CA1 = responseObj.getString("CA1");
                                    String RAND = responseObj.getString("RAND");
                                    String SQN_xor_AK = responseObj.getString("SQN_xor_AK");
                                    String AMF = responseObj.getString("AMF");
                                    String MAC_A1 = responseObj.getString("MAC_A");
                                    Log.i("CA1",CA1);
                                    Log.i("RAND",RAND);
                                    Log.i("SQN_xor_AK",SQN_xor_AK);
                                    Log.i("AMF",AMF);
                                    Log.i("MAC_A1",MAC_A1);

                                    String AK = ca.sha3(Ku);
                                    String SQN = ca.encryptDecrypt(SQN_xor_AK,AK);
                                    String MAC_A2 = ca.sha3(Ku+"_"+AMF+"_"+SQN);
                                    if(MAC_A1 == MAC_A2){
                                        String CA2 = ca.encryptDecrypt(ca.sha3(idText),r1);
                                        if(CA1 == CA2){
                                            String RES = ca.sha3(Ku+"_"+RAND);
                                            String L = ca.encryptDecrypt(ca.encryptDecrypt(ca.sha3(ca.encryptDecrypt(ca.sha3(PV+"_"+Ku),RES)),ca.sha3(PV+Ku)),RES);
                                            JSONObject request2 = new JSONObject();
                                            try
                                            {
                                                request2.put("L", L);
                                            }
                                            catch(Exception e)
                                            {
                                                e.printStackTrace();
                                            }
                                            JsonObjectRequest strReq2 = new JsonObjectRequest(Request.Method.POST, authUrl2, request2,
                                                    new Response.Listener<JSONObject>() {
                                                        @Override
                                                        public void onResponse(JSONObject responseObj) {
                                                            try {
                                                                token = responseObj.getString("token");
                                                                Log.i("token",token);
                                                                sessionHelper.setKu(Ku);
                                                                Intent intent = new Intent();
                                                                intent.setClass(SigninActivity.this, GetDataActivity.class);
                                                                Bundle bundle = new Bundle();
                                                                bundle.putString("token",token );
                                                                intent.putExtras(bundle);
                                                                startActivity(intent);
                                                                SigninActivity.this.finish();

                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
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
                                            queue.add(strReq2);
                                            Intent intent = new Intent(SigninActivity.this,GetDataActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

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
//                User result = dbuser.checkUser(idText,pwText);
//                if(result!=null){
//                    sessionHelper.setLogin(true);
//                    sessionHelper.setUserName(idText);
//                    sessionHelper.setKey("1000000000011000010000111011101000001100110111001101011000000011100111010011001000010100101111100011111110010000000111101111001100001011101101001011001100000001100001110011010011001101101000001110011010110010100110010010110100111000111011110111111011011101");
//                    sessionHelper.setUserName(result.getUsername());
//                    sessionHelper.setKey(result.getKey());
//                    Intent intent = new Intent(SigninActivity.this,GetDataActivity.class);
//                    startActivity(intent);
//                    finish();
//                }else{
//                    Toast.makeText(getApplicationContext(), "Signin fail", Toast.LENGTH_SHORT).show();
//                    signinId.setText("");
//                    signinPw.setText("");
//                }

//                Intent intent = new Intent(SigninActivity.this,GetDataActivity.class);
//                startActivity(intent);
            }
        });
    }
}
