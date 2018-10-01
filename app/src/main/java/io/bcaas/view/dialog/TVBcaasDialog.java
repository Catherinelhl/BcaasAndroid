package io.bcaas.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import io.bcaas.R;
import io.bcaas.tools.StringTool;
import io.bcaas.view.tv.FlyBroadLayout;
import io.bcaas.view.tv.MainUpLayout;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/27
 * <p>
 * TV提示框
 */
public class TVBcaasDialog extends Dialog {

    private TextView tvTitle;
    private TextView tvContent;
    private Button btnLeft;
    private Button btnRight;
    private Context context;
    FlyBroadLayout blockBaseMainup;
    MainUpLayout blockBaseContent;
    private ConfirmClickListener confirmClickListener;

    public TVBcaasDialog(Context context) {
        this(context, R.style.tv_bcaas_dialog);
    }


    public TVBcaasDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.tv_layout_bcaas_dialog, null);
        setContentView(view);
        btnLeft = view.findViewById(R.id.btn_left);
        btnRight = view.findViewById(R.id.btn_right);
        tvTitle = view.findViewById(R.id.tv_title);
        tvContent = view.findViewById(R.id.tv_content);
        blockBaseMainup = view.findViewById(R.id.block_base_mainup);
        blockBaseContent = view.findViewById(R.id.block_base_content);
        initListener();
    }

    public TVBcaasDialog setLeftText(String left) {
        if (StringTool.isEmpty(left)) return this;
        btnLeft.setText(left);
        return this;

    }

    public TVBcaasDialog setRightText(String right) {
        if (StringTool.isEmpty(right)) return this;
        btnRight.setText(right);
        return this;

    }

    public TVBcaasDialog setContent(String content) {
        if (StringTool.isEmpty(content)) return this;
        tvContent.setText(content);
        return this;

    }

    public TVBcaasDialog setTitle(String title) {
        if (StringTool.isEmpty(title)) return this;
        tvTitle.setText(title);
        return this;

    }

    public void initListener() {
        blockBaseContent.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                blockBaseMainup.setFocusView(newFocus, oldFocus, 1f);
            }
        });
        btnLeft.setOnClickListener(v -> judgeBtnLeftContentToCallBack());
        btnRight.setOnClickListener(v -> judgeBtnRightContentToCallBack());
    }

    /*根据内容来判断*/
    private void judgeBtnLeftContentToCallBack() {
        if (StringTool.equals(btnLeft.getText().toString(), context.getResources().getString(R.string.cancel))) {
            confirmClickListener.cancel();
        } else {
            confirmClickListener.sure();
        }

    }

    private void judgeBtnRightContentToCallBack() {

        if (StringTool.equals(btnRight.getText().toString(), context.getResources().getString(R.string.confirm))) {
            confirmClickListener.sure();
        } else {
            confirmClickListener.cancel();
        }
    }

    public TVBcaasDialog setOnConfirmClickListener(ConfirmClickListener confirmClickListener) {
        this.confirmClickListener = confirmClickListener;
        return this;
    }

    public interface ConfirmClickListener {
        void sure();

        void cancel();
    }
}
