package com.jcz.myapplication.downloadUtils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jcz.myapplication.DialogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by asus on 2016/8/12.
 */
public class DownloadFileUtils {

    private static DownloadFileUtils downloadFileUtils;
    private Context context;
    private final int CREATE_PROGRESS = 4;
    private final int PRE_DOWNLOAD = 3;
    private final int COMPLETE = 2;
    private final int NETWORK_ERROR = 1;
    private final int UPDATA_PROGRESS = 0;
    private boolean cancleAble = false;
    private boolean isShowDialog;//是否展示进度条的Dialog
    private boolean isTimer;
    private boolean isFirstIn = true;
    private String TAG = "DownloadFileUtils";
    private OnDownloadFileProgressUpdataListener listener;
    private File downloadFile;
    private Integer mSize = 0;//下载进度(0~100)
    private Thread downloadThread;

    private DownloadFileUtils() {
    }

    public interface OnDownloadFileProgressUpdataListener {
        void onPreDownload();

        void onProgressUpdata(int i);

        void onNetWorkError();

        void onPreInstall(File downloadFile);


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
     * 这里接收到的listener的方法一定是在UI线程中回调了。
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
                        if (isTimer) {
                            showProgressDialog(UPDATA_PROGRESS);
                            isTimer = false;
                        }
                        if (listener != null)
                            listener.onProgressUpdata(mSize);
                    }
                    break;
                case NETWORK_ERROR:
                    Toast.makeText(context, "网络连接错误,下载更新失败", Toast.LENGTH_SHORT).show();
                    showProgressDialog(NETWORK_ERROR);
                    if (listener != null)
                        listener.onNetWorkError();
                    break;
                case COMPLETE:
                    if (listener != null)
                        listener.onPreInstall(downloadFile);
                    installApk();
                    break;
                case PRE_DOWNLOAD:
                    showProgressDialog(PRE_DOWNLOAD);
                    if (listener != null)
                        listener.onPreDownload();
                    break;
                case CREATE_PROGRESS:
                    Log.d(TAG, "Create_progress");
                    showProgressDialog(CREATE_PROGRESS);
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
                Log.d(TAG, "Updata_progress");
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

    public void showProgressDialog(int status) {
        if (isShowDialog) {
            switch (status) {
                case PRE_DOWNLOAD:
                    DialogUtils.showProgressDialog(context, "提示", "正在检查更新","取消", false, new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            cancleDownload();
                        }
                    },null);
                    break;
                case CREATE_PROGRESS:
                    DialogUtils.showHorizontalProgressDialog(context, "正在下载更新", mSize + "%", new DialogUtils.onNormalDialogCallback() {
                        @Override
                        public void onNegativeCallback() {
                            cancleDownload();
                        }

                        @Override
                        public void onPositiveCallback() {
                        }
                    });
                    break;
                case UPDATA_PROGRESS:
                    DialogUtils.updataHorizontalProgressDialog(mSize);
                    break;
                case COMPLETE:
                    break;
                case NETWORK_ERROR:
                    DialogUtils.showTextDialog(context, "下载失败，网络错误。", "提示", null, "确定", true, new DialogUtils.onNormalDialogCallback() {
                        @Override
                        public void onNegativeCallback() {
                            cancleDownload();
                        }

                        @Override
                        public void onPositiveCallback() {
                        }
                    });
                    break;
                default:
                    break;
            }
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
        downloadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                DownloadFile(url, filename);
            }
        });
        downloadThread.start();
    }

    /**
     * @param url
     * @param filename
     */
    public void DownloadFile(String url, String filename) {
        cancleAble = false;
        isFirstIn = true;
        InputStream input = null;
        FileOutputStream fileOut = null;
        handler.sendEmptyMessage(PRE_DOWNLOAD);
        Log.d(TAG, "Pre_download");
        try {
            URLConnection connection = new URL(url).openConnection();
            connection.setConnectTimeout(5000);
            long length = connection.getContentLength();

            input = connection.getInputStream();
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
            fileOut = new FileOutputStream(downloadFile);
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
                if (isFirstIn) {
                    handler.sendEmptyMessage(CREATE_PROGRESS);

                    initTimerAndTask();
                    Log.d(TAG, "initTimerAndTask");
                    isFirstIn = false;
                } else {
//                    handler.sendEmptyMessage(UPDATA_PROGRESS);
                }
            }
            handler.sendEmptyMessage(COMPLETE);
        } catch (IOException e) {
            e.printStackTrace();
            if (!cancleAble){
                handler.sendEmptyMessage(NETWORK_ERROR);
            }
            cancleDownload();
        } finally {
            try {
                fileOut.close();
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

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
            DialogUtils.dismissDialog();
        }
        TimerUtils.cancelTimer();
        cancleAble = true;
        if (downloadThread != null)
            downloadThread.interrupt();
    }

}
