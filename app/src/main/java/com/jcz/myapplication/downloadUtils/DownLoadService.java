package com.jcz.myapplication.downloadUtils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.jcz.myapplication.MainActivity;
import com.jcz.myapplication.R;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by asus on 2016/8/10.
 */
public class DownLoadService extends Service {

    private Notification.Builder builder;
    private NotificationManager notificationManager;
    private final int NOTIFYID = 1000;
    private int preprogress = 0;
    private Notification notification;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        loadFile();
        return super.onStartCommand(intent, flags, startId);
    }

    //下载文件
    private void loadFile() {

        DownloadFileUtils.getInstand().initDownloadFileUtils(this, false, new DownloadFileUtils.OnDownloadFileProgressUpdataListener() {
            @Override
            public void onPreDownload() {
                initNotification();
                StatusBarUtils.expandStatusBar(DownLoadService.this);
            }

            @Override
            public void onProgressUpdata(int i) {
                updataNotification(i);
            }

            @Override
            public void onNetWorkError() {
                cancleNotification();
                StatusBarUtils.collapseStatusBar(DownLoadService.this);
            }

            @Override
            public void onPreInstall(File downloadFile) {
                cancleNotification();
                StatusBarUtils.collapseStatusBar(DownLoadService.this);
            }
        });

        DownloadFileUtils.getInstand().DownloadFileOnNewThread(MainActivity.downUrl,MainActivity.filename);


    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initNotification() {

        builder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("下载中")
                .setProgress(100, 0, false);
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notification = builder.build();
        notificationManager.notify(NOTIFYID, builder.build());
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void updataNotification(long progress) {
        int curProgress = (int) progress;
        if (curProgress > preprogress) {
            builder.setContentText(curProgress + "%");
            builder.setProgress(100, curProgress, false);
            notification = builder.build();
            notificationManager.notify(NOTIFYID, notification);
        }
        preprogress = curProgress;
    }

    public void cancleNotification() {
        notificationManager.cancel(NOTIFYID);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
