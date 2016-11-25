package com.example.testing.downloadutil;

import com.example.testing.downloadutil.inter.Callback;
import com.example.testing.downloadutil.inter.DownloadThreadImpl;
import com.example.testing.downloadutil.inter.FactoryImpl;

import java.io.File;

/**
 * Created by Administrator on 2016/11/19.
 */

public class FileDownloadBuilder implements FactoryImpl {

    public enum File_Type {
        APK,
        Nurmal
    }

    private DownloadThreadImpl fileDownload;

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
