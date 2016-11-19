package com.example.testing.downloadutil;

import android.content.Context;

import java.io.File;

/**
 * Created by Administrator on 2016/11/19.
 */

public class FileDownloadBuilder implements FactoryImpl {

    public enum File_Type {
        APK,
        Nurmal
    }

    private SimpleFileDownload fileDownload;

    public FileDownloadBuilder(File_Type file_type) {
        if (file_type == File_Type.APK) {
            fileDownload = new SimpleFileDownload();
        }
    }

    @Override
    public DownloadThreadImpl create() {
        return fileDownload;
    }

    @Override
    public FactoryImpl setUrl(String downUrl) {
        fileDownload.setDownloadUrl(downUrl);
        return this;
    }

    @Override
    public FactoryImpl setDownloadFile(File downloadFile) {
        fileDownload.setFile(downloadFile);
        return this;
    }

    @Override
    public FactoryImpl autoInstall(Context context) {
        if (context == null) {
            fileDownload.setAutoInstall(false, null);
        } else {
            fileDownload.setAutoInstall(true, context);
        }
        return this;
    }

    @Override
    public FactoryImpl setDownloadCallback(Callback callback) {
        fileDownload.setCallback(callback);
        return this;
    }

    @Override
    public FactoryImpl setFileName(String fileName) {
        fileDownload.setFileName(fileName);
        return this;
    }

}
