package com.jcz.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.testing.downloadutil.ServiceDownloadManager;

/**
 * Created by Administrator on 2016/11/19.
 */
public class DownLoadService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ServiceDownloadManager downloadManager = new ServiceDownloadManager(this);
        downloadManager.autoDownloadFile(intent.getStringExtra("downUrl"));
        return super.onStartCommand(intent, flags, startId);
    }
}
