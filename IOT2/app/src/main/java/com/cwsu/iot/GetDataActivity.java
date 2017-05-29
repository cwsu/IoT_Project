package com.cwsu.iot;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
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
 * Created by cwsu on 2017/5/16.
 */

public class GetDataActivity extends Activity {
    private iotDB dbHelper = null;
    private Calculate ca = null;
    private sessionManager sessionHelper= null;
    String Spu = "1000";
    String Ku ="";
    Bundle bundle;
    RequestQueue queue;
    LinearLayout getdataContent;
    Button btnGetRealtime;
    Button btnGetHidtory;
    TextView txtViewData;
    LinearLayout llhTemperature;
    TextView temperature;
    TextView temperatureTxt;
    LinearLayout llhHumidity;
    TextView humidity;
    TextView humidityTxt;
    LinearLayout llhHeartbeat;
    TextView heartbeat;
    TextView heartbeatTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getdata);
        this.initModule();
        this.initLayout();
        this.initListener();
    }

    private void initModule(){
        queue = Volley.newRequestQueue(GetDataActivity.this);
        dbHelper = new iotDB(GetDataActivity.this);
        dbHelper.openDB(); // open db
        ca = new Calculate();
        sessionHelper = new sessionManager(getApplicationContext());
    }

    private void initLayout() {
        getdataContent = (LinearLayout)findViewById(R.id.getdata_content);
        btnGetRealtime = (Button)findViewById(R.id.btn_realtime);
        btnGetHidtory = (Button)findViewById(R.id.btn_history);
        txtViewData = (TextView)findViewById(R.id.txtView_Data);
        llhTemperature = (LinearLayout) findViewById(R.id.llh_temperature);
        temperature = (TextView)findViewById(R.id.temperature);
        temperatureTxt = (TextView)findViewById(R.id.temperatureTxt);
        llhHumidity = (LinearLayout) findViewById(R.id.llh_humidity);
        humidity = (TextView)findViewById(R.id.humidity);
        humidityTxt = (TextView)findViewById(R.id.humidityTxt);
        llhHeartbeat = (LinearLayout) findViewById(R.id.llh_heartbeat);
        heartbeat = (TextView)findViewById(R.id.heartbeat);
        heartbeatTxt = (TextView)findViewById(R.id.heartbeatTxt);

    }

    private void initListener() {
        btnGetRealtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://140.119.164.35:8003/getdata";

                final String signinIdText = sessionHelper.getUserName();
                Ku = sessionHelper.getKu();
                bundle =GetDataActivity.this.getIntent().getExtras();
                String token = bundle.getString("token");

                String getdata = ca.encryptDecrypt(signinIdText+"_"+ca.encryptDecrypt(token,Ku)+"_"+ca.sha3(ca.encryptDecrypt(token,Ku)),Spu);
                JSONObject request = new JSONObject();
                try {
                    request.put("getdata",getdata);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST, url, request,
                        new Response.Listener<

                                JSONObject>() {
                            @Override
                            public void onResponse(JSONObject responseObj) {
                                try {
                                    String response = responseObj.getString("data");
                                    Log.i("response",response);
                                    String de_data_hdata = ca.encryptDecrypt(response,Ku);
                                    Log.i("data_hdata",de_data_hdata);
                                    String[] data_hdata = de_data_hdata.split("_");
                                    Log.i("data",data_hdata[0]);
                                    Log.i("hdata",data_hdata[1]);
                                    String[] dataArray = data_hdata[0].split("_");
                                    for(int i=0;i<dataArray.length;i++){
                                        String[] data = dataArray[i].split(":");
                                        if(data[0]=="temperature"){
                                            temperatureTxt.setText(data[1]);
                                            Log.i("temperature",data[1]);
                                        }
                                        else if (data[0]=="humidity"){
                                            humidityTxt.setText(data[1]);
                                            Log.i("humidity",data[1]);
                                        }
                                        else if (data[0]=="heartbeat"){
                                            heartbeatTxt.setText(data[1]);
                                            Log.i("heartbeat",data[1]);
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


            }
        });
    }



}
