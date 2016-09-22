package com.jcz.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Layout;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;


/**
 * Created by asus on 2016/6/28.
 */
public class DialogUtils {

    private static MaterialDialog dialog;
    private static ProgressBar progressBar;
    private static TextView textView;
    private static boolean stillShowDialog = false;
    private static boolean isHorizontalProgressDialog = false;

    public interface onNormalDialogCallback {
        void onNegativeCallback();

        void onPositiveCallback();
    }


    public interface onEditDialogCallback {

        /**
         * 输入窗口的回调
         *
         * @param text   输入的内容
         * @param dialog 窗口实例
         * @return 返回true时，点击确定后窗口自动消失，返回false时，窗口不会消失
         */
        boolean onResult(String text, MaterialDialog dialog);
    }

    public interface onTwiceEditDialogCallback {

        boolean onResult(String ssid, String password, MaterialDialog dialog);
    }

    public interface OnListDialogClickCallBack {

        /**
         * @param item     被点击的子项中String内容
         * @param position
         */
        void OnItemClick(String item, int position);
    }

    /**
     * 用于输入密码的弹窗
     *
     * @param context
     * @param Hinttext   要显示的hint字符内容
     * @param title      要显示的标题内容
     * @param cancelAble 窗口是否允许点击空白区域自动退出
     * @param MaxLength  输入字符限制最长的长度
     * @param callback   回调 返回true时，点击确定后窗口自动消失，返回false时，窗口不会消失
     */
    public static void showPasswordEditDialog(Context context, String Hinttext, String title, boolean cancelAble, int MaxLength, final onEditDialogCallback callback) {
        dismissDialog();//新建窗口之前，检查并先退出当前窗口。

        View view = LayoutInflater.from(context).inflate(R.layout.password_dialog, null);
        final EditText et = (EditText) view.findViewById(R.id.user_password);
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.isVisiable);
        TextView textView = (TextView) view.findViewById(R.id.isVisiable_textV);
        LinearLayout isPasswordView = (LinearLayout) view.findViewById(R.id.isPasswordView);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.performClick();
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
            }
        });
        isPasswordView.setVisibility(View.VISIBLE);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MaxLength)});
        et.setSingleLine();
        if (!TextUtils.isEmpty(Hinttext)) {
            et.setHint(Hinttext);
        }

        dialog = new MaterialDialog.Builder(context)
                .title(title)
                .cancelable(cancelAble)
                .customView(view, false)
                .negativeText("取消")
                .positiveText("确定")
                .build();

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stillShowDialog = false;
                if (callback != null) {
                    stillShowDialog = callback.onResult(et.getText().toString().trim(), dialog);
                }
                if (!stillShowDialog) {
                    dismissDialog();
                }
            }
        });
        dialog.show();
    }

    /**
     * 普通的输入窗口
     *
     * @param context
     * @param text       默认提前输入的内容
     * @param title      标题
     * @param isHint     text是否为hint
     * @param cancelAble 点击其他区域时，窗口是否可以退出
     * @param MaxLength  输入内容的最大长度
     * @param callBack   回调 返回true时，点击确定后窗口自动消失，返回false时，窗口不会消失
     */
    public static void showNormalEditDialog(Context context, String text, String title, int inputType, boolean isHint, boolean cancelAble, int MaxLength, final onEditDialogCallback callBack) {
        dismissDialog();
        View view = LayoutInflater.from(context).inflate(R.layout.password_dialog, null);
        final EditText et = (EditText) view.findViewById(R.id.user_password);

        et.setInputType(inputType);
        et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MaxLength)});
        et.setSingleLine();
        if (!TextUtils.isEmpty(text)) {
            if (isHint) {
                et.setHint(text);
            } else {
                et.setText(text);
            }
        }
        dialog = new MaterialDialog.Builder(context)
                .title(title)
                .cancelable(cancelAble)
                .customView(view, false)
                .negativeText("取消")
                .positiveText("确定")
                .build();

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stillShowDialog = false;
                if (callBack != null) {
                    stillShowDialog = callBack.onResult(et.getText().toString().trim(), dialog);
                }
                if (!stillShowDialog) {
                    dismissDialog();
                }
            }
        });
        dialog.show();

    }

    public static void showTwiceEdittextDialog(final Context context, String title, String textWiFi, String textPassword, boolean cancelAble, boolean isPassword, int SSIDMaxLength, int PasswordMaxLength, final onTwiceEditDialogCallback callback) {
        dismissDialog();//新建窗口之前，检查并先退出当前窗口。

        View view = LayoutInflater.from(context).inflate(R.layout.twice_edittextdialog, null);
        final EditText et = (EditText) view.findViewById(R.id.password);
        final EditText et1 = (EditText) view.findViewById(R.id.SSid);
        TextView textViewWiFi = (TextView) view.findViewById(R.id.testWiFi);
        TextView textViewPassword = (TextView) view.findViewById(R.id.testPassword);
        if (!TextUtils.isEmpty(textWiFi)) {
            textViewWiFi.setText(textWiFi);
        }
        if (!TextUtils.isEmpty(textPassword)) {
            textViewPassword.setText(textPassword);
        }
        et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(PasswordMaxLength)});
        et1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(SSIDMaxLength)});
        et.setSingleLine();
        et1.setSingleLine();
        LinearLayout isPasswordView = (LinearLayout) view.findViewById(R.id.isPasswordView);
        if (isPassword) {
            final CheckBox checkBox = (CheckBox) view.findViewById(R.id.isVisiable);
            TextView textView = (TextView) view.findViewById(R.id.isVisiable_textV);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox.performClick();
                }
            });
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isChecked) {
                        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        et.setKeyListener(DigitsKeyListener.getInstance(context.getResources().getString(R.string.password_digits)));
                    } else {
                        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        et.setKeyListener(DigitsKeyListener.getInstance(context.getResources().getString(R.string.password_digits)));

                    }
                }
            });
            isPasswordView.setVisibility(View.VISIBLE);
            et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            et.setKeyListener(DigitsKeyListener.getInstance(context.getResources().getString(R.string.password_digits)));
        } else {
            isPasswordView.setVisibility(View.GONE);
            et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }

        dialog = new MaterialDialog.Builder(context)
                .title(title)
                .cancelable(cancelAble)
                .customView(view, false)
                .negativeText("取消")
                .positiveText("确定")
                .build();

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stillShowDialog = false;
                if (callback != null) {
                    stillShowDialog = callback.onResult(et1.getText().toString().trim(), et.getText().toString().trim(), dialog);
                }
                if (!stillShowDialog) {
                    dismissDialog();
                }
            }
        });
        dialog.show();
    }

    /**
     * 只包含text，仅用于显示告知的窗口
     *
     * @param context
     * @param text         显示内容
     * @param title        显示标题
     * @param positiveText 确定按键的内容
     * @param negativeText 取消按键的内容
     * @param cancelable   是否可点击其他区域取消窗口
     * @param callback     回调
     */
    public static void showTextDialog(Context context, final String text, String title, String positiveText, String negativeText, boolean cancelable, final onNormalDialogCallback callback) {
        dismissDialog();

        View v = LayoutInflater.from(context).inflate(R.layout.textview_dialog, null);
        TextView textView = (TextView) v.findViewById(R.id.text);
        textView.setText(text);

        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title(title)
                .cancelable(cancelable)
                .customView(v, false);

        if (!TextUtils.isEmpty(positiveText)) {
            builder.positiveText(positiveText)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (callback != null) {
                                callback.onPositiveCallback();
                            }
                        }
                    });
        }

        if (!TextUtils.isEmpty(negativeText)) {
            builder.negativeText(negativeText)
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (callback != null) {
                                callback.onNegativeCallback();
                            }
                        }
                    });
        }
        dialog = builder.build();
        dialog.show();
    }

    public static void showHorizontalProgressDialog(Context context, String title, String text, final onNormalDialogCallback callback) {
        dismissDialog();

        View view = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        textView = (TextView) view.findViewById(R.id.textView);

        progressBar.setProgress(0);
        textView.setText(text);

        dialog = new MaterialDialog.Builder(context)
                .cancelable(false)
                .negativeText("取消")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (callback != null) {
                            callback.onNegativeCallback();
                        }
                        dialog.dismiss();
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        isHorizontalProgressDialog = false;
                    }
                })
                .customView(view, false)
                .title(title)
                .build();
        dialog.show();
        isHorizontalProgressDialog = true;
    }

    public static boolean isHorizontalProgressDialog() {
        return isHorizontalProgressDialog;
    }

    public static void updataHorizontalProgressDialog(int size) {

        if (dialog != null && dialog.isShowing() && isHorizontalProgressDialog) {
            //增加进度
            if (size < 100) {
                progressBar.setProgress(size);
                textView.setText(size + "%");
                dialog.getView().invalidate();
            }
        }
    }

    public static <VH extends RecyclerView.ViewHolder> void showListTextDialog(Context context, String title, boolean isCancelAble, String positiveText, RecyclerView.Adapter<VH> adapter, RecyclerView.LayoutManager layoutManager, final OnListDialogClickCallBack clickCallBack, MaterialDialog.SingleButtonCallback positiveCallback) {
        dismissDialog();
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        if (!TextUtils.isEmpty(title)) {
            builder.title(title);
        }
        builder.adapter(adapter, layoutManager)
                .cancelable(isCancelAble)
                .dividerColorRes(R.color.graylight);
        if (clickCallBack != null) {
            builder.itemsCallback(new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                    clickCallBack.OnItemClick((String) text, position);
                }
            });
        }
        if (!TextUtils.isEmpty(positiveText)) {
            builder.positiveText(positiveText)
                    .onPositive(positiveCallback);

        }
        dialog = builder.build();
        dialog.getView().setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(context, 350)));
        dialog.show();
    }

    public static void showListTextDialog(Context context, String title, String[] texts, @NonNull final OnListDialogClickCallBack clickCallBack) {
        dismissDialog();
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        if (!TextUtils.isEmpty(title)) {
            builder.title(title);
        }
        builder.items(texts)
                .dividerColorRes(R.color.graylight)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        clickCallBack.OnItemClick((String) text, position);

                    }
                });
        dialog = builder.build();
        dialog.getView().setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(context, 350)));
        dialog.show();
    }

    public static void showProgressDialog(Context context, String title, String text, String nevigative, boolean cancelable, MaterialDialog.SingleButtonCallback callback, DialogInterface.OnDismissListener listener) {
        dismissDialog();
        MaterialDialog.Builder build = new MaterialDialog.Builder(context)
                .title(title)
                .content(text)
                .progress(true, 100)
                .cancelable(cancelable);
        if (listener != null) {
            build.dismissListener(listener);
        }
        if (nevigative != null) {
            build.negativeText(nevigative);
            build.onNegative(callback);
        }

        dialog = build.build();
        dialog.show();
    }


    public static void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * dp转px
     *
     * @param context 上下文
     * @param dpValue dp值
     * @return px值
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
