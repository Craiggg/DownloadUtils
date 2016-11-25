package com.example.testing.downloadutil;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.os.Build;

import com.example.testing.downloadutil.inter.Callback;
import com.example.testing.downloadutil.inter.DownloadThreadImpl;
import com.example.testing.downloadutil.util.InstallUtil;
import com.example.testing.downloadutil.util.StatusBarUtils;

import java.io.File;


/**
 * Created by Administrator on 2016/11/19.
 */

public class ServiceDownloadManager {

    private final int NOTIFY_ID = 1000;
    private int iconRes = android.R.drawable.sym_def_app_icon;
    private int preprogress = 0;
    private Notification.Builder builder;
    private NotificationManager notificationManager;
    private Notification notification;
    private Service context;//Service Context

    public ServiceDownloadManager(Service context) {
        this.context = context;
    }

    public void autoDownloadFile(String downUrl) {
        autoDownloadFile(downUrl, null);
    }

    public void autoDownloadFile(String downUrl, String fileName) {
        autoDownloadFile(downUrl, fileName, -1);
    }

    public void autoDownloadFile(String downUrl, String fileName, int iconRes) {
        if (iconRes != -1) {
            this.iconRes = iconRes;
        }
        new FileDownloadBuilder(FileDownloadBuilder.File_Type.APK)
                .setFileName(fileName)
                .setUrl(downUrl)
                .setDownloadCallback(new Callback() {
                    boolean isFirstTime = true;//当网络连接错误时，没有加判断的话通知栏会上下抖动一次。

                    @Override
                    public void beforeDownload(DownloadThreadImpl simpleFileDownload) {
                        initNotification();
                    }

                    @Override
                    public void updataDownload(DownloadThreadImpl simpleFileDownload, float progress) {
                        if (isFirstTime) {
                            isFirstTime = false;
                            StatusBarUtils.expandStatusBar(context);
                        }
                        updataNotification((int) progress);
                    }

                    @Override
                    public void afterDownload(DownloadThreadImpl simpleFileDownload, File file) {
                        cancel();
                        simpleFileDownload.close();
                        InstallUtil.installApk(context,file);
                    }

                    @Override
                    public void errorDownload(DownloadThreadImpl simpleFileDownload) {
                        cancel();
                        simpleFileDownload.close();
                    }
                })
                .create().start();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initNotification() {
        builder = new Notification.Builder(context)
                .setSmallIcon(iconRes)
                .setContentTitle("下载中")
                .setProgress(100, 0, false);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notification = builder.build();
        notificationManager.notify(NOTIFY_ID, builder.build());
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void updataNotification(int progress) {
        if (progress > preprogress && builder != null) {
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
            notification = builder.build();
            notificationManager.notify(NOTIFY_ID, notification);
        }
        preprogress = progress;
    }

    private void cancel() {
        if (notificationManager != null) {
            notificationManager.cancel(NOTIFY_ID);
        }
        StatusBarUtils.collapseStatusBar(context);
        context.stopSelf();
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public Notification.Builder getBuilder() {
        return builder;
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public Notification getNotification() {
        return notification;
    }

    public int getNOTIFY_ID() {
        return NOTIFY_ID;
    }
}
