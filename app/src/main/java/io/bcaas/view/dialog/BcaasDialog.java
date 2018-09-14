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

    private TextView tvTitle;
    private TextView tvContent;
    private Button btnLeft;
    private Button btnRight;
    private Context context;
    private ConfirmClickListener confirmClickListener;

    public BcaasDialog(Context context) {
        this(context, 0);
    }


    public BcaasDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_bcaas_dialog, null);
        setContentView(view);
        btnLeft = view.findViewById(R.id.btn_left);
        btnRight = view.findViewById(R.id.btn_right);
        tvTitle = view.findViewById(R.id.tv_title);
        tvContent = view.findViewById(R.id.tv_content);
        initListener();
    }

    public BcaasDialog setLeftText(String left) {
        if (StringTool.isEmpty(left)) return this;
        btnLeft.setText(left);
        return this;

    }

    public BcaasDialog setRightText(String right) {
        if (StringTool.isEmpty(right)) return this;
        btnRight.setText(right);
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

    public BcaasDialog setOnConfirmClickListener(ConfirmClickListener confirmClickListener) {
        this.confirmClickListener = confirmClickListener;
        return this;
    }

    public interface ConfirmClickListener {
        void sure();

        void cancel();
    }
}
