package com.jcz.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jcz.myapplication.downloadUtils.DownLoadService;
import com.jcz.myapplication.downloadUtils.DownloadFileUtils;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    public static String downUrl = "http://shouji.360tpcdn.com/160922/9d2726ddae4f1fa6533ccf35d7d5c515/com.moji.mjweather_6000602.apk";
    public static String filename = "coolPacket";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testDownload();
    }

    private void testDownload() {
        Button download = (Button) findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadFileUtils.getInstand().initDownloadFileUtils(MainActivity.this, true, null);
                DownloadFileUtils.getInstand().DownloadFileOnNewThread(downUrl, filename);
            }
        });

        Button downloadInBackground = (Button) findViewById(R.id.downloadInBackground);
        downloadInBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownLoadService.setInitData(downUrl, filename, -1);
                startService(new Intent(MainActivity.this, DownLoadService.class));
            }
        });
    }


}
