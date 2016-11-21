package com.example.testing.downloadutil;

import java.io.File;

/**
 * Created by Administrator on 2016/11/19.
 */

public interface Callback {

    void beforeDownload(DownloadThreadImpl downloadThread);

    void updataDownload(DownloadThreadImpl downloadThread, float progress, boolean isFirstUpdata);

    void afterDownload(DownloadThreadImpl downloadThread, File file);

    void errorDownload(DownloadThreadImpl downloadThread);

}
