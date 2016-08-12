package com.jcz.myapplication.downloadUtils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by asus on 2016/8/12.
 */
public class DownloadFileUtils {

    private Context context;

    private final int PRE_DOWNLOAD = 3;
    private final int COMPLETE = 2;
    private final int NETWORK_ERROR = 1;
    private final int UPDATA_PROGRESS = 0;
    private boolean cancleAble = false;

    public interface OnDownloadFileProgressUpdataListener {
        void onPreDownload();

        void onProgressUpdata(int i);

        void onNetWorkError();

        void onPreInstall(File downloadFile);
    }

    private OnDownloadFileProgressUpdataListener listener;
    private File downloadFile;
    private Integer mSize = 0;//下载进度(0~100)
    private boolean isShowDialog;//是否展示进度条的Dialog

    private boolean isTimer;

    private String TAG = "DownloadFileUtils";

    private static DownloadFileUtils downloadFileUtils;

    private DownloadFileUtils() {
    }

    public static DownloadFileUtils getInstand() {
        if (downloadFileUtils == null) {
            downloadFileUtils = new DownloadFileUtils();
        }
        return downloadFileUtils;
    }

    /**
     * 必须在UI线程执行。这样handler执行ui操作时才不报错。
     *
     * @param context
     * @param isShowDialog 是否显示在线进度条
     */
    public void initDownloadFileUtils(Context context, boolean isShowDialog) {
        this.isShowDialog = isShowDialog;
        this.context = context;
    }

    /**
     * 这里接收到的listener的方法一定是在UI线程中对调了。
     *
     * @param context
     * @param isShowDialog
     * @param listener
     */
    public void initDownloadFileUtils(Context context, boolean isShowDialog, OnDownloadFileProgressUpdataListener listener) {
        setOnDownloadFileProgressUpdataListener(listener);
        initDownloadFileUtils(context, isShowDialog);
    }

    //主线程为了更新UI
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case UPDATA_PROGRESS:
                    synchronized (mSize) {
                        if (isShowDialog && isTimer) {
                            ProgressDialogUtils.updataHorizontalProgressDialog(mSize + "%", mSize);
                            isTimer = false;
                        }
                        if (listener != null)
                            listener.onProgressUpdata(mSize);
                    }
                    break;
                case NETWORK_ERROR:
                    Toast.makeText(context, "网络连接错误,下载更新失败", Toast.LENGTH_SHORT).show();
                    if (listener != null)
                        listener.onNetWorkError();
                    break;
                case COMPLETE:
                    if (listener != null)
                        listener.onPreInstall(downloadFile);
                    installApk();
                    break;
                case PRE_DOWNLOAD:
                    showProgressDialog();
                    if (listener!=null)
                        listener.onPreDownload();
                    break;
                default:
                    break;
            }
        }
    };


    //设置定时器，固定时间更新一次进度,避免更新进度太快，刷新界面太频繁造成卡顿。
    private void initTimerAndTask() {
        TimerUtils.startTimer(100, 100, new TimerUtils.OnTimerTaskRunning() {
            @Override
            public void onTimerTaskRunning() {
                isTimer = true;
                handler.sendEmptyMessage(UPDATA_PROGRESS);
            }
        });

    }

    /**
     * 更新进度数值
     *
     * @param mSize
     */
    public void setmSize(int mSize) {
        this.mSize = mSize;
    }

    public void showProgressDialog() {
        if (isShowDialog){
            ProgressDialogUtils.showHorizontalProgressDialog(context, "正在下载更新", mSize + "%", new ProgressDialogUtils.onDialogCallback() {
                @Override
                public void ondismissCallback() {
                    cancleDownload();
                }
            });
        }
    }

    //由url获得文件名
    private String getFileName(String string) {
        return string.substring(string.lastIndexOf("/") + 1);
    }

    //下载文件夹路径
    private String getFolderPath() {
        return Environment.getExternalStorageDirectory().toString() + "/AsyncTaskDownload/";
    }

    public void DownloadFileOnNewThread(final String url, final String filename) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DownloadFile(url, filename);
            }
        }).start();
    }

    /**
     * @param url
     * @param filename
     */
    public void DownloadFile(String url, String filename) {
        handler.sendEmptyMessage(PRE_DOWNLOAD);
        initTimerAndTask();
        cancleAble = false;
        try {
            URLConnection connection = new URL(url).openConnection();
            connection.setConnectTimeout(5000);
            long length = connection.getContentLength();

            InputStream input = connection.getInputStream();
            //建立文件夹
            File file = new File(getFolderPath());
            if (!file.exists()) {
                file.mkdirs();
            }
            //获得文件名字
            if (filename == null) {
                filename = getFileName(url);
            }
            downloadFile = new File(getFolderPath() + filename);
            //文件输出流
            FileOutputStream fileOut = new FileOutputStream(downloadFile);
            int len;
            long total = 0;
            byte[] data = new byte[1024];
            while ((len = input.read(data)) != -1) {
                if (cancleAble) {
                    return;
                }
                fileOut.write(data, 0, len);
                total += len;//下载进度叠加
                synchronized (mSize) {
                    mSize = (int) ((total * 100) / length);
                }
                handler.sendEmptyMessage(UPDATA_PROGRESS);
            }
            fileOut.close();
            input.close();
            handler.sendEmptyMessage(COMPLETE);
        } catch (Exception e) {
            e.printStackTrace();
            cancleDownload();
            handler.sendEmptyMessage(NETWORK_ERROR);
        }

    }

    //安装APK
    public void installApk() {
        if (downloadFile == null) {
            return;
        }
        Uri uri = Uri.fromFile(downloadFile);
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        install.setDataAndType(uri, "application/vnd.android.package-archive");
        // 执行意图进行安装
        context.startActivity(install);
        cancleDownload();
    }


    /**
     * 设置进度更新的监听
     *
     * @param listener
     */
    public void setOnDownloadFileProgressUpdataListener(OnDownloadFileProgressUpdataListener listener) {
        this.listener = listener;
    }

    /**
     * 显示进度条的情况下，下载完毕后，要关闭定时器和dialog
     */
    public void cancleDownload() {
        if (isShowDialog) {
            TimerUtils.cancelTimer();
            ProgressDialogUtils.dismissDialog();
            cancleAble = true;
        }
    }

}
