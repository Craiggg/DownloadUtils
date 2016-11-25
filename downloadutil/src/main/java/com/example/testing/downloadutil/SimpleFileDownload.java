package com.example.testing.downloadutil;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.example.testing.downloadutil.inter.Callback;
import com.example.testing.downloadutil.inter.DownloadThreadImpl;
import com.example.testing.downloadutil.util.TimerUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Administrator on 2016/11/19.
 */
public class SimpleFileDownload implements DownloadThreadImpl {

    private static final int PRE_DOWNLOAD = 3;
    private static final int COMPLETE = 2;
    private static final int NETWORK_ERROR = 1;
    private static final int UPDATA_DOWNLOAD = 0;
    private final String TAG = "SimpleFileDownload";
    private Thread downloadThread;
    private Callback callback;
    private File downFile;
    private String downloadUrl, filename;
    private float mSize;//下载进度(0~100%)
    private boolean cancleAble = false;

    public SimpleFileDownload() {
        downloadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                downloadFile();
            }
        });
    }

    //由url获得文件名
    private String getFileName(String string) {
        return string.substring(string.lastIndexOf("/") + 1);
    }

    //下载文件夹路径
    private String getFolderPath() {
        return Environment.getExternalStorageDirectory().toString() + "/SimpleFileDownload/";
    }

    //设置定时器，固定时间更新一次进度,避免更新进度太快，刷新界面太频繁造成卡顿。
    private void initTimerAndTask() {
        TimerUtils.startTimer(100, 100, new TimerUtils.OnTimerTaskRunning() {
            @Override
            public void onTimerTaskRunning() {
                handler.sendEmptyMessage(UPDATA_DOWNLOAD);
            }
        });

    }

    //主线程为了更新UI
    private Handler handler = new Handler(Looper.getMainLooper()) {
        boolean isFirstUpdata = true;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PRE_DOWNLOAD:
                    initTimerAndTask();
                    if (callback != null)
                        callback.beforeDownload(SimpleFileDownload.this);
                    break;
                case UPDATA_DOWNLOAD:
                    synchronized (TAG) {
                        if (callback != null) {
                            if (isFirstUpdata) {
                                callback.updataDownload(SimpleFileDownload.this, 0);
                                isFirstUpdata = false;
                            } else {
                                callback.updataDownload(SimpleFileDownload.this, mSize);
                            }
                        }
                    }
                    break;
                case COMPLETE:
                    if (callback != null && downFile != null) {
                        callback.afterDownload(SimpleFileDownload.this, downFile);
                    }
                    cancleDownload();
                    break;
                case NETWORK_ERROR:
                    if (callback != null) {
                        callback.errorDownload(SimpleFileDownload.this);
                    }
                    cancleDownload();
                    break;
                default:
                    break;
            }
        }
    };

    private void downloadFile() {
        if (TextUtils.isEmpty(downloadUrl))
            throw new NullPointerException("DownloadUrl is null.");
        if (downFile == null) {
            //建立文件夹
            File file = new File(getFolderPath());
            if (!file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.mkdirs();
            }

            //获得文件名字
            if (filename == null) {
                filename = getFileName(downloadUrl);
            }
            if (!filename.substring(filename.length() - 5, filename.length()).contains(".apk")) {
                filename = filename + ".apk";
            }
            file = new File(getFolderPath() + filename);
            downFile = file;
        }
        boolean isFirstIn = true;
        cancleAble = false;
        int len;
        long total = 0;
        byte[] data = new byte[1024];
        try {
            URLConnection connection = new URL(downloadUrl).openConnection();
            connection.setConnectTimeout(5000);
            long length = connection.getContentLength();
            InputStream input = connection.getInputStream();
            FileOutputStream fileOut = new FileOutputStream(downFile);
            while ((len = input.read(data)) != -1) {
                if (cancleAble) {
                    return;
                }
                fileOut.write(data, 0, len);
                total += len;//下载进度叠加
                synchronized (TAG) {
                    mSize = (total * 100) / length;
                }
                if (isFirstIn) {
                    handler.sendEmptyMessage(PRE_DOWNLOAD);
                    isFirstIn = false;
                }
            }
            handler.sendEmptyMessage(UPDATA_DOWNLOAD);
            handler.sendEmptyMessage(COMPLETE);
            fileOut.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
            if (!cancleAble) {
                handler.sendEmptyMessage(NETWORK_ERROR);
            }
        }

    }

    private void cancleDownload() {
        TimerUtils.cancelTimer();
        cancleAble = true;
        if (downloadThread != null) {
            downloadThread.interrupt();
            downloadThread = null;
        }
    }

    @Override
    public void start() {
        if (downloadThread != null) {
            downloadThread.start();
        }
    }

    @Override
    public void close() {
        cancleDownload();
    }

    @Override
    public Callback getCallback() {
        return callback;
    }

    @Override
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public String getDownloadUrl() {
        return downloadUrl;
    }

    @Override
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    @Override
    public File getFile() {
        return downFile;
    }

    @Override
    public void setFile(File downFile) {
        this.downFile = downFile;
    }

    @Override
    public void setFileName(String fileName) {
        this.filename = fileName;
    }

    @Override
    public String getFileName() {
        return filename;
    }

}
