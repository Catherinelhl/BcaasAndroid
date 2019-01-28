package io.bcaas.view.pop;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import io.bcaas.R;
import io.bcaas.base.BCAASApplication;
import io.bcaas.event.RefreshWalletBalanceEvent;
import io.bcaas.tools.OttoTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.decimal.DecimalTool;

/**
 * @author catherine.brainwilliam
 * @since 2018/08/30
 * 自定義PopWindow：用于金额显示不完全，点击显示完全的金额
 */
public class ShowDetailPopWindow extends PopupWindow {

    private TextView textView;

    public ShowDetailPopWindow(Context context, String content) {
        super(context);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View contentView = LayoutInflater.from(context).inflate(R.layout.popwindow_show_amount,
                null, false);
        textView = contentView.findViewById(R.id.tv_amount);
        textView.setText(StringTool.isEmpty(content) ? "0.0000000" : content);
        setContentView(contentView);
        OttoTool.getInstance().register(this);
    }

    /*更新钱包余额*/
    @Subscribe
    public void refreshWalletBalance(RefreshWalletBalanceEvent refreshWalletBalanceEvent) {
        if (refreshWalletBalanceEvent == null) {
            return;
        }
        if (textView != null) {
            textView.setText(DecimalTool.transferDisplay(BCAASApplication.getWalletBalance()));
        }
    }
}