package com.jcz.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.testing.downloadutil.Callback;
import com.example.testing.downloadutil.FileDownloadBuilder;
import com.example.testing.downloadutil.InstallUtil;
import com.example.testing.downloadutil.SimpleFileDownload;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public static String downUrl = "http://shouji.360tpcdn.com/160922/9d2726ddae4f1fa6533ccf35d7d5c515/com.moji.mjweather_6000602.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testDownload();
    }

    private void testDownload() {
        final Button download = (Button) findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FileDownloadBuilder(FileDownloadBuilder.File_Type.APK)
                        .setUrl(downUrl)
                        .autoInstall(null)
                        .setDownloadCallback(new Callback() {
                            MaterialDialog dialog;

                            @Override
                            public void beforeDownload(final SimpleFileDownload simpleFileDownload) {
                                dialog = new MaterialDialog.Builder(MainActivity.this)
                                        .title("提示")
                                        .content("正在检查更新")
                                        .negativeText("取消")
                                        .cancelable(false)
                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                simpleFileDownload.close();

                                            }
                                        })
                                        .build();
                                dialog.show();
                            }

                            @Override
                            public void updataDownload(final SimpleFileDownload simpleFileDownload, float progress, boolean isFirstUpdata) {
                                if (isFirstUpdata) {
                                    dialog.dismiss();
                                    dialog = new MaterialDialog.Builder(MainActivity.this)
                                            .title("正在下载新版本")
                                            .negativeText("取消")
                                            .cancelable(false)
                                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    simpleFileDownload.close();
                                                }
                                            })
                                            .progress(false, 100, true)
                                            .build();
                                }
                                dialog.show();
                                dialog.setProgress((int) progress);
                                dialog.getView().invalidate();

                            }

                            @Override
                            public void afterDownload(final SimpleFileDownload simpleFileDownload, final File file) {
                                dialog.setActionButton(DialogAction.NEUTRAL, "安装");
                                dialog.getActionButton(DialogAction.NEUTRAL).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        InstallUtil.installApk(MainActivity.this, file);
                                    }
                                });
                                dialog.getView().invalidate();
                            }

                            @Override
                            public void errorDownload(SimpleFileDownload simpleFileDownload) {
                                dialog.dismiss();
                                Toast.makeText(MainActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .create()
                        .start();

            }
        });

        Button downloadInBackground = (Button) findViewById(R.id.downloadInBackground);
        downloadInBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DownLoadService.class);
                intent.putExtra("downUrl", downUrl);
                startService(intent);
            }
        });
    }


}
