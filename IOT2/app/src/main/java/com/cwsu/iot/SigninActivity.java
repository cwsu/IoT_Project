package com.cwsu.iot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
public class SigninActivity extends Activity{
    private dbUser dbuser = null;
    private iotDB dbHelper = null;
    private sessionManager sessionHelper;
    private Calculate ca = null;

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
                String idText = signinId.getText().toString();
                String pwText = signinPw.getText().toString();
//                User result = dbuser.checkUser(idText,pwText);
//                if(result!=null){
                    sessionHelper.setLogin(true);
                    sessionHelper.setUserName(idText);
                    sessionHelper.setKey("1000000000011000010000111011101000001100110111001101011000000011100111010011001000010100101111100011111110010000000111101111001100001011101101001011001100000001100001110011010011001101101000001110011010110010100110010010110100111000111011110111111011011101");
//                    sessionHelper.setUserName(result.getUsername());
//                    sessionHelper.setKey(result.getKey());
                    Intent intent = new Intent(SigninActivity.this,GetDataActivity.class);
                    startActivity(intent);
                    finish();
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
