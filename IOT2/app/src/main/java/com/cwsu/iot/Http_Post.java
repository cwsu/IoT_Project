package com.cwsu.iot;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by cwsu on 2017/5/14.
 */

public class Http_Post extends Service {
    String strTxt=null;
    String postUrl=null;


    public void Post(final String strTxt, final String PostUrl){
        this.strTxt = strTxt;
        this.postUrl = PostUrl;

        new Thread(new Runnable() {

            @Override
            public void run() {
                String strResult = "";
                InputStream inputStream = null;

                //建立HttpClient物件
                HttpClient httpClient = new DefaultHttpClient();
                //建立一個Post物件，並給予要連線的Url
                HttpPost httpPost = new HttpPost(postUrl);
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", strTxt));
                try{
                    //發送Http Request，內容為params，且為UTF8格式
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                    //接收Http Server的回應
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    HttpEntity resEntity = httpResponse.getEntity();

                    if (resEntity != null) {
                        strResult = EntityUtils.toString(resEntity);
                    }
//                    inputStream = httpResponse.getEntity().getContent();
//                    if(inputStream != null){
//                        strResult = convertInputStreamToString(inputStream);
//                    }else{
//                        strResult = "No";
//                    }
                    Log.i("Http",strResult);
                }catch (IOException e) {
                    // Log exception
                    e.printStackTrace();
                }

            }}).start();

    }
    private  String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
