package com.jcz.myapplication.downloadUtils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;


import java.io.File;


/**
 * Created by asus on 2016/8/10.
 */
public class DownLoadService extends Service {

    public static String downUrl;//一定要先填写url
    public static String filename;//这个文件名可有可无
    private Notification.Builder builder;
    private NotificationManager notificationManager;
    private final int NOTIFYID = 1000;
    private int preprogress = 0;
    private Notification notification;
    public static int ResIcon = android.R.drawable.sym_def_app_icon;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        loadFile();
        return super.onStartCommand(intent, flags, startId);
    }

    //下载文件
    private void loadFile() {
        if (downUrl == null) {
            throw new NullPointerException("downUrl is null.");
        }

        DownloadFileUtils.getInstand().initDownloadFileUtils(this, false, new DownloadFileUtils.OnDownloadFileProgressUpdataListener() {
            boolean isFirstTime = true;//当网络连接错误时，没有加判断的话通知栏会上下抖动一次。

            @Override
            public void onPreDownload() {
                initNotification();
            }

            @Override
            public void onProgressUpdata(int i) {
                if (isFirstTime) {
                    isFirstTime = false;
                    StatusBarUtils.expandStatusBar(DownLoadService.this);
                }
                updataNotification(i);
            }

            @Override
            public void onNetWorkError() {
                cancleNotification();
                StatusBarUtils.collapseStatusBar(DownLoadService.this);
                stopSelf();
            }

            @Override
            public void onPreInstall(File downloadFile) {
                cancleNotification();
                StatusBarUtils.collapseStatusBar(DownLoadService.this);
                DownloadFileUtils.getInstand().cancleDownload();
                stopSelf();
            }
        });
        DownloadFileUtils.getInstand().DownloadFileOnNewThread(downUrl, filename);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initNotification() {

        builder = new Notification.Builder(this)
                .setSmallIcon(ResIcon)
                .setContentTitle("下载中")
                .setProgress(100, 0, false);
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notification = builder.build();
        notificationManager.notify(NOTIFYID, builder.build());
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void updataNotification(long progress) {
        int curProgress = (int) progress;
        if (curProgress > preprogress && builder != null) {
            builder.setContentText(curProgress + "%");
            builder.setProgress(100, curProgress, false);
            notification = builder.build();
            notificationManager.notify(NOTIFYID, notification);
        }
        preprogress = curProgress;
    }

    public void cancleNotification() {
        if (notificationManager != null) {
            notificationManager.cancel(NOTIFYID);
        }
    }

    /**
     * 初始化配置,一开始startService时必须提前设置好,不然会一直报错链接不上
     *
     * @param downUrl
     * @param filename
     * @param iconRes
     */
    public static void setInitData(@Nullable String downUrl, String filename, int iconRes) {
        DownLoadService.downUrl = downUrl;
        if (filename != null) {
            DownLoadService.filename = filename;
        }
        if (iconRes != -1) {
            DownLoadService.ResIcon = iconRes;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
