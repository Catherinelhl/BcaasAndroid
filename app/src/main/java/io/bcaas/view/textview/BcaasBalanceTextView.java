package io.bcaas.view.textview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.bcaas.base.BCAASApplication;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.decimal.DecimalTool;
import io.bcaas.view.pop.ShowDetailPopWindow;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/19
 * 自定義TextView：用于顯示界面顯示余额
 */
public class BcaasBalanceTextView extends TextView {

    private Context context;
    //是否显示pop
    private boolean showPop;


    public BcaasBalanceTextView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public BcaasBalanceTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setShowPop(true);
        initView();
    }

    private void initView() {
        this.setOnClickListener(v -> {
            if (isShowPop()) {
                showDetailPop(BcaasBalanceTextView.this, getText().toString());
            }

        });
    }

    public boolean isShowPop() {
        return showPop;
    }

    public void setShowPop(boolean showPop) {
        this.showPop = showPop;
    }

    //对当前的余额进行赋值
    public void setBalance(String balance) {
        if (StringTool.isEmpty(balance)) {
            balance = BCAASApplication.getWalletBalance();
            if (StringTool.isEmpty(balance)) {
                balance = "0";
            }
        }
        setText(DecimalTool.transferDisplay(balance));
        this.setEllipsize(TextUtils.TruncateAt.END);
    }

    /**
     * 顯示完整的信息：金额/地址/私钥
     *
     * @param view 需要依賴的視圖
     */
    public void showDetailPop(View view, String content) {
        ShowDetailPopWindow window = new ShowDetailPopWindow(context, content);
        View contentView = window.getContentView();
        //需要先测量，PopupWindow还未弹出时，宽高为0
        contentView.measure(makeDropDownMeasureSpec(window.getWidth()),
                makeDropDownMeasureSpec(window.getHeight()));
        int offsetX = Math.abs(window.getContentView().getMeasuredWidth() - view.getWidth()) / 2;
        int offsetY = -(window.getContentView().getMeasuredHeight() + view.getHeight());
        window.showAsDropDown(view, offsetX, offsetY, Gravity.START);

    }


    @SuppressWarnings("ResourceType")
    private static int makeDropDownMeasureSpec(int measureSpec) {
        int mode;
        if (measureSpec == ViewGroup.LayoutParams.WRAP_CONTENT) {
            mode = View.MeasureSpec.UNSPECIFIED;
        } else {
            mode = View.MeasureSpec.EXACTLY;
        }
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), mode);
    }
}
