package com.jcz.myapplication.downloadUtils;

import android.content.Context;
import android.os.Build;

import java.lang.reflect.Method;

/**
 * Created by asus on 2016/8/12.
 */
public class StatusBarUtils {

    public static void expandStatusBar(Context context) {
        try {
            Object statusBarManager = context.getSystemService("statusbar");
            Method expand;

            if (Build.VERSION.SDK_INT <= 16) {
                expand = statusBarManager.getClass().getMethod("expand");
            } else {
                expand = statusBarManager.getClass().getMethod("expandNotificationsPanel");
            }
            expand.invoke(statusBarManager);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public static void collapseStatusBar(Context context) {
        try {
            Object statusBarManager = context.getSystemService("statusbar");
            Method collapse;

            if (Build.VERSION.SDK_INT <= 16) {
                collapse = statusBarManager.getClass().getMethod("collapse");
            } else {
                collapse = statusBarManager.getClass().getMethod("collapsePanels");
            }
            collapse.invoke(statusBarManager);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

}
