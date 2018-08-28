package io.bcaas.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;


import io.bcaas.R;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/11
 *
 * 加载进度的显示框
 */
public class BcaasLoadingDialog extends Dialog {
    Context context;

    public BcaasLoadingDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.layout_loading_dailog, null);
        setContentView(view);

    }

}
