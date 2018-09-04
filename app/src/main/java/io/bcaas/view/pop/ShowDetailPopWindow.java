package io.bcaas.view.pop;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;


import io.bcaas.R;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.tools.NumberTool;

/**
 * @author catherine.brainwilliam
 * @since 2018/08/30
 * 用于金额显示不完全，点击显示完全的金额
 */
public class ShowDetailPopWindow extends PopupWindow {
    public ShowDetailPopWindow(Context context, String content) {
        super(context);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View contentView = LayoutInflater.from(context).inflate(R.layout.popwindow_show_amount,
                null, false);
        TextView textView = contentView.findViewById(R.id.tv_amount);
        textView.setText(content);
        setContentView(contentView);
    }
}