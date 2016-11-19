package com.example.testing.downloadutil;

import android.content.Context;

import java.io.File;

/**
 * Created by Administrator on 2016/11/19.
 */

public interface FactoryImpl {

    DownloadThreadImpl create();

    FactoryImpl setUrl(String downUrl);

    FactoryImpl setDownloadFile(File downloadFile);

    FactoryImpl autoInstall(Context context);

    FactoryImpl setDownloadCallback(Callback callback);

    FactoryImpl setFileName(String fileName);

}
