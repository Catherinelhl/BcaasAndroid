package io.bcaas.view.textview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.bcaas.R;
import io.bcaas.base.BCAASApplication;
import io.bcaas.tools.DensityTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.TextTool;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/28
 * 自定義LinearLayout
 */
public class LeftAndRightTextView extends RelativeLayout {

    private String TAG = LeftAndRightTextView.class.getSimpleName();

    private Context context;
    @BindView(R.id.tv_balance)
    TextView tvBalance;
    @BindView(R.id.tv_block_service)
    TextView tvBlockService;

    public LeftAndRightTextView(Context context) {
        super(context);
    }

    public LeftAndRightTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_left_and_right, this, true);
        ButterKnife.bind(view);
    }

    public void setLeftAndRight(String left, String right, boolean isSend) {
        if (StringTool.notEmpty(left) && tvBalance != null) {
            tvBalance.setText(left);
            tvBalance.setTextColor(context.getResources().getColor(isSend ? R.color.red70_da261f : R.color.green70_18ac22));
        }
        if (StringTool.notEmpty(right) && tvBlockService != null) {
            tvBlockService.setText(right);
            // 得到当前币种的宽度
            float textPaintWidth = TextTool.getViewWidth(tvBlockService, right);
            //然后根据当前屏幕得到剩余可以显示余额的宽度 ,这里边距只有10，但是不知道为什么需要减去50才行
            float width = BCAASApplication.getScreenWidth() - textPaintWidth - DensityTool.dip2px(context, 50);
            tvBalance.setMaxWidth((int) width);
        }


    }
}
