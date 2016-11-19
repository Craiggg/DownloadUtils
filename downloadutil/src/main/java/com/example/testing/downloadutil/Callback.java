package com.example.testing.downloadutil;

import java.io.File;

/**
 * Created by Administrator on 2016/11/19.
 */

public interface Callback {

    void beforeDownload(SimpleFileDownload simpleFileDownload);

    void updataDownload(SimpleFileDownload simpleFileDownload, float progress, boolean isFirstUpdata);

    void afterDownload(SimpleFileDownload simpleFileDownload, File file);

    void errorDownload(SimpleFileDownload simpleFileDownload);

}
