package com.jcz.myapplication.downloadUtils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jcz.myapplication.R;

/**
 * Created by asus on 2016/8/11.
 */
public class ProgressDialogUtils {

    interface onDialogCallback{
        void ondismissCallback();
//        void onCompleteCallback();
    }

    private static MaterialDialog progressDialog;
    private static ProgressBar progressBar;
    private static TextView textView;

    public static void showHorizontalProgressDialog(Context context, String title, String text, final onDialogCallback callback){

        dismissDialog();
        View view = LayoutInflater.from(context).inflate(R.layout.progressdialog,null,false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        textView = (TextView) view.findViewById(R.id.textView);

        progressBar.setProgress(0);
        textView.setText(0+"%");

        progressDialog = new MaterialDialog.Builder(context)
                .cancelable(false)
                .negativeText("取消")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (callback!=null){
                            callback.ondismissCallback();
                        }
                        dialog.dismiss();
                    }
                })
                .customView(view,false)
                .title(title)
                .build();
        progressDialog.show();
    }

    public static void updataHorizontalProgressDialog(String text,int size){
        if (progressDialog!=null&&progressDialog.isShowing()){
            //增加进度
            if(size<100){
                progressBar.setProgress(size);
                textView.setText(size+"%");
                progressDialog.getView().invalidate();
            }
        }
    }

    public static void dismissDialog() {
        if (progressDialog!=null&&progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }


}
