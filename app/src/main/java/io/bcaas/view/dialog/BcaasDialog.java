package io.bcaas.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import io.bcaas.R;
import io.bcaas.tools.StringTool;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/27
 * <p>
 * 提示框
 */
public class BcaasDialog extends Dialog {

    TextView tvTitle;
    TextView tvContent;
    Button btnCancel;
    Button btnSure;
    private ConfirmClickListener confirmClickListener;

    public BcaasDialog(Context context) {
        this(context, 0);
    }


    public BcaasDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_bcaas_dialog, null);
        setContentView(view);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnSure = view.findViewById(R.id.btn_sure);
        tvTitle = view.findViewById(R.id.tv_title);
        tvContent = view.findViewById(R.id.tv_content);
        initListener();
    }

    public BcaasDialog setLeftText(String left) {
        if (StringTool.isEmpty(left)) return this;
        btnSure.setText(left);
        return this;

    }

    public BcaasDialog setRightText(String right) {
        if (StringTool.isEmpty(right)) return this;
        btnCancel.setText(right);
        return this;

    }

    public BcaasDialog setContent(String content) {
        if (StringTool.isEmpty(content)) return this;
        tvContent.setText(content);
        return this;

    }

    public BcaasDialog setTitle(String title) {
        if (StringTool.isEmpty(title)) return this;
        tvTitle.setText(title);
        return this;

    }

    public void initListener() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmClickListener.cancel();

            }
        });
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmClickListener.sure();
            }
        });
    }


    public BcaasDialog setOnConfirmClickListener(ConfirmClickListener confirmClickListener) {
        this.confirmClickListener = confirmClickListener;
        return this;
    }

    public interface ConfirmClickListener {
        void sure();

        void cancel();
    }
}
