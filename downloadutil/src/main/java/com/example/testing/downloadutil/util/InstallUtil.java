package com.example.testing.downloadutil.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * Created by Administrator on 2016/11/19.
 */

public class InstallUtil {

    //安装APK
    public static void installApk(Context context, File downFile) {
        if (downFile == null) {
            throw new NullPointerException("downfile is null.");
        }
        Uri uri = Uri.fromFile(downFile);
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        install.setDataAndType(uri, "application/vnd.android.package-archive");
        // 执行意图进行安装
        context.startActivity(install);
    }

}
