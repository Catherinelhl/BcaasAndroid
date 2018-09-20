package io.bcaas.ui.activity.tv;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.tools.DateFormatTool;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/20
 * <p>
 * TV版總攬
 */
public class HomeActivityTV extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_current_time)
    TextView tvCurrentTime;
    @BindView(R.id.btn_logout)
    Button btnLogout;
    @BindView(R.id.rl_header)
    RelativeLayout rlHeader;

    @Override
    public boolean full() {
        return false;
    }

    @Override
    public int getContentView() {
        return R.layout.tv_activity_home;
    }

    @Override
    public void getArgs(Bundle bundle) {

    }

    @Override
    public void initViews() {
        tvCurrentTime.setText(DateFormatTool.getCurrentTime());
        tvTitle.setText(getResources().getString(R.string.home));
    }

    @Override
    public void initListener() {

    }

}
