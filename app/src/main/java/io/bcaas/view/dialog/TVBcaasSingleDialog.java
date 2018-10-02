package io.bcaas.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.bcaas.R;
import io.bcaas.tools.StringTool;
import io.bcaas.view.tv.FlyBroadLayout;
import io.bcaas.view.tv.MainUpLayout;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/27
 * <p>
 * TV提示框单个按钮
 */
public class TVBcaasSingleDialog extends Dialog {

    @BindView(R.id.block_base_mainup)
    FlyBroadLayout blockBaseMainup;
    @BindView(R.id.block_base_content)
    MainUpLayout blockBaseContent;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.btn_sure)
    Button btnSure;
    private Unbinder unbinder;

    private ConfirmClickListener confirmClickListener;

    public TVBcaasSingleDialog(Context context) {
        this(context, R.style.tv_bcaas_dialog);
    }


    public TVBcaasSingleDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        View view = LayoutInflater.from(context).inflate(R.layout.tv_layout_bcaas_single_dialog, null);
        setContentView(view);
        unbinder = ButterKnife.bind(this);

        initListener();
    }

    public TVBcaasSingleDialog setLeftText(String left) {
        if (StringTool.isEmpty(left)) return this;
        btnSure.setText(left);
        return this;

    }

    public TVBcaasSingleDialog setContent(String content) {
        if (StringTool.isEmpty(content)) return this;
        tvContent.setText(content);
        return this;

    }

    public TVBcaasSingleDialog setTitle(String title) {
        if (StringTool.isEmpty(title)) return this;
        tvTitle.setText(title);
        return this;

    }

    public void initListener() {
        btnSure.setOnClickListener(v -> confirmClickListener.sure());
        blockBaseContent.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                blockBaseMainup.setFocusView(newFocus, oldFocus, 1f);
            }
        });
    }


    public TVBcaasSingleDialog setOnConfirmClickListener(ConfirmClickListener confirmClickListener) {
        this.confirmClickListener = confirmClickListener;
        return this;
    }

    public interface ConfirmClickListener {
        void sure();
    }
}
