package com.example.testing.downloadutil;

import java.io.File;

/**
 * Created by Administrator on 2016/11/19.
 */

public interface DownloadThreadImpl {

    void start();

    void close();

    Callback getCallback();

    void setCallback(Callback callback);

    String getDownloadUrl();

    void setDownloadUrl(String downloadUrl);

    File getFile();

    void setFile(File file);

    void setFileName(String fileName);

    String getFileName();

}
