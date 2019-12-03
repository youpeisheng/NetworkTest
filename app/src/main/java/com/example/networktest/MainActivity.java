package com.example.networktest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.SAXParserFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
//
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView responseText;
    private String TAG="MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button sendRequest=(Button) findViewById(R.id.send_request);
        responseText=(TextView) findViewById(R.id.response_text);
        sendRequest.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.send_request){
//           sendRequesWithHttpURLConnection();
            sendRequestWithokHttp(); //OKHttp
        }
    }
    private void sendRequestWithokHttp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
            try {
                OkHttpClient client=new OkHttpClient();
                Request request=new Request.Builder()
                        .url("http://192.168.31.225/get_data.json") //url("http://10.0.2.2/get_data.xml") http://127.0.0.1/get_data.xml
                        .build();
                Response response=client.newCall(request).execute();
                String responseData=response.body().string();
//                parseXMLWithPull(responseData);
//                parseXMLWithSAX(responseData);
                showResponse(responseData);
//                parseJSONWithJSONobject(responseData);//使用官方JSONObject 解析
                parseJSONWithGSON(responseData); //使用 GSON 库解析
            }catch (Exception e){
                e.printStackTrace();
            }
            }
        }).start();
    }
    private void parseJSONWithGSON(String jsonData){ //使用 GSON 库解析
        Gson gson=new Gson();
        List<App> appList=gson.fromJson(jsonData,new TypeToken<List<App>>(){}.getType());
        for (App app :appList){
            Log.d(TAG,"id is "+app.getId());
            Log.d(TAG,"name is "+app.getName());
            Log.d(TAG,"Version is "+app.getVersion());
        }
    }
    private void parseJSONWithJSONobject(String jsonData){ //使用官方JSONObject 解析
        try {
            JSONArray jsonArray=new JSONArray(jsonData);
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(1);
                String id=jsonObject.getString("id");
                String name=jsonObject.getString("name");
                String version =jsonObject.getString("version");
                Log.d("MainActivity","id is"+id);
                Log.d("MainActivity","name is"+name);
                Log.d("MainActivity","version is"+version);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void parseXMLWithSAX(String xmlData){
        try {
            SAXParserFactory factory=SAXParserFactory.newInstance();
            XMLReader xmlReader=factory.newSAXParser().getXMLReader();
            ContentHandler handler=new ContentHandler();
            //将ContentHandle 的实例设置到XMLReader 中
            xmlReader.setContentHandler(handler);
            //开始执行解析
            xmlReader.parse(new InputSource(new StringReader(xmlData)));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void parseXMLWithPull(String xmlData){
        try {
            XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser=factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType=xmlPullParser.getEventType();
            String id="";
            String name="";
            String version="";
            while (eventType!=XmlPullParser.END_DOCUMENT){
                String nodeName=xmlPullParser.getName();
                switch (eventType){
                    //开始解析某个节点
                    case XmlPullParser.START_TAG:{
                        if("id".equals(nodeName)){
                            id=xmlPullParser.nextText();
                        }else if("name".equals(nodeName)){
                            name=xmlPullParser.nextText();
                        }else if("version".equals(nodeName)){
                            version=xmlPullParser.nextText();
                        }
                        break;
                    }
                    case XmlPullParser.END_TAG:{
                        if("app".equals(nodeName)){
                            Log.d(TAG,"id is "+id);
                            Log.d(TAG,"name is "+name);
                            Log.d(TAG,"version is "+version);
                        }
                        break;
                    }
                    default:
                        break;
                }
                eventType=xmlPullParser.next();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void sendRequesWithHttpURLConnection(){
        //开启线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection=null;
                BufferedReader reader=null;
                try {
                    URL url=new URL("http://192.168.31.225/get_data.xml");
                    connection=(HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoInput(true);
                    InputStream in=connection.getInputStream();

                    //下面对获取到的输入流进行读取
                    reader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder response=new StringBuilder();
                    String line;
                    while ((line=reader.readLine())!=null){
                        response.append(line);
                    }
                    response.append(String.valueOf(connection.getResponseCode()));
                    response.append(connection.getResponseMessage());
                    showResponse(response.toString());
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(reader!=null){
                        try {
                            reader.close();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    if(connection!=null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
    private void showResponse(final String response){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //在这里进行UI操作 将结果显示到界面上
                responseText.setText(response);
            }
        });
    }
}
