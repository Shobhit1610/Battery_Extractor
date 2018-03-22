package com.example.shobh.battery_extractor;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity
{
    TextView batteryShow;
    Button submit;
    String batteryLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        batteryShow = findViewById(R.id.batteryshow);
        submit = findViewById(R.id.submit);

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getApplicationContext().registerReceiver(null,intentFilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE,-1);
        float l = (level/(float)scale)*100;
        this.batteryLevel = String.valueOf(l);

        batteryShow.setText(this.batteryLevel);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendData sendData = new SendData();
                sendData.execute(batteryLevel);
            }
        });
    }

    private class SendData extends AsyncTask<String,String,String>
    {

        @Override
        protected String doInBackground(String... strings)
        {
            batteryLevel = strings[0];


            try {
                URL url = new URL("http://www.aptronnoida.com/batterylevel/batterylevelinsert.php");
                HttpURLConnection h = (HttpURLConnection)url.openConnection();
                h.setRequestMethod("POST");

                OutputStream outputStream = h.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"utf-8"));
                String data = URLEncoder.encode("batterylevel_id","utf-8")+"="+URLEncoder.encode(batteryLevel,"utf-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = h.getInputStream();
                inputStream.close();
                h.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(),"Battery Percentage Sent",Toast.LENGTH_SHORT).show();
        }
    }
}