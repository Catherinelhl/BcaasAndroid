package io.bcaas.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;
import io.bcaas.BuildConfig;
import io.bcaas.R;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.StringTool;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/11
 * <p>
 * 加载进度的显示框
 */
public class BcaasLoadingDialog extends Dialog implements DialogInterface {
    private String TAG = BcaasLoadingDialog.class.getSimpleName();
    Dialog dlg;
    Context context;
    @BindView(R.id.loadView)
    LinearLayout loadView;
    @BindView(R.id.tipTextView)
    TextView tipTextView;
    @BindView(R.id.pb_loading)
    ProgressBar progressBar;


//    private Effectstype type = null;


    private View mDialogView;

    private int mDuration = -1;

    private static int mOrientation = 1;

    private boolean isCancelable = false;

    private String msg;
    Animation hyperspaceJumpAnimation;


    public BcaasLoadingDialog(Context context) {
        super(context, R.style.dialog_loading);
        init(context);

    }

    public BcaasLoadingDialog(Context context, String msg) {
        super(context, R.style.dialog_loading);
        init(context, msg);

    }

    private void init(Context context) {
        dlg = this;
        this.context = context;
        hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.loading_animation);
        Runnable runnable = this::initView;
        runnable.run();
    }

    private void init(Context context, String msg) {
        dlg = this;
        this.context = context;
        this.msg = msg;
        hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.loading_animation);
        Runnable runnable = this::initView;
        runnable.run();
    }

    public void initView() {
        mDialogView = View.inflate(context, R.layout.layout_loading_dailog, null);
        setContentView(mDialogView);
        ButterKnife.bind(this, mDialogView);
        loadView.setOnClickListener(null);
        loadView.setClickable(false);
        isCancelable(false);
        isCancelableOnTouchOutside(false);
        mDialogView.setOnClickListener(null);
        if (!StringTool.isEmpty(msg)) {
            tipTextView.setVisibility(View.VISIBLE);
            tipTextView.setText(msg);// 设置加载信息
        } else {
            tipTextView.setVisibility(View.GONE);
        }

    }

    public void message(String msg) {
        if (!StringTool.isEmpty(msg)) {
            tipTextView.setVisibility(View.VISIBLE);
            tipTextView.setText(msg);// 设置加载信息
        } else {
            tipTextView.setVisibility(View.GONE);
        }
    }

    public BcaasLoadingDialog isCancelableOnTouchOutside(boolean cancelable) {
        this.isCancelable = cancelable;
        if (BuildConfig.DEBUG)
            this.setCanceledOnTouchOutside(true);
        else
            this.setCanceledOnTouchOutside(cancelable);
        return this;
    }

    public BcaasLoadingDialog isCancelable(boolean cancelable) {
        this.isCancelable = cancelable;
        if (BuildConfig.DEBUG)
            this.setCancelable(true);
        else
            this.setCancelable(cancelable);
        return this;
    }


    @Override
    public void show() {
        if (isShowing()) return;
        super.show();
    }

    public void show(String message) {
        this.message(message);
        this.show();
    }

    @Override
    public void dismiss() {
        if (isShowing()) {
            try {
                super.dismiss();
            } catch (Exception e) {
                LogTool.d(TAG, e.getMessage());
            }
        }
    }


}
