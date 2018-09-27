package io.bcaas.base;

import io.bcaas.R;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.view.dialog.TVBcaasDialog;
import io.bcaas.view.dialog.TVLanguageSwitchDialog;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/27
 * <p>
 * TV版基类
 */
public abstract class BaseTVActivity extends BaseActivity {

    /**
     * 显示TV版弹框
     *
     * @param title
     * @param left
     * @param right
     * @param message
     * @param listener
     */
    public void showTVBcaasDialog(String title, String left, String right, String message, final TVBcaasDialog.ConfirmClickListener listener) {
        TVBcaasDialog tvBcaasDialog = new TVBcaasDialog(this);
        /*设置弹框点击周围不予消失*/
        tvBcaasDialog.setCanceledOnTouchOutside(false);
        /*设置弹框背景*/
        tvBcaasDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_white));
        tvBcaasDialog.setLeftText(left)
                .setRightText(right)
                .setContent(message)
                .setTitle(title)
                .setOnConfirmClickListener(new TVBcaasDialog.ConfirmClickListener() {
                    @Override
                    public void sure() {
                        listener.sure();
                        tvBcaasDialog.dismiss();
                        tvBcaasDialog.cancel();
                    }

                    @Override
                    public void cancel() {
                        listener.cancel();
                        tvBcaasDialog.dismiss();
                        tvBcaasDialog.cancel();

                    }
                }).show();
    }

    /**
     * 显示TV版「切换语言」弹框
     */
    public void  showTVLanguageSwitchDialog(OnItemSelectListener onItemSelectListener) {
        TVLanguageSwitchDialog tvLanguageSwitchDialog = new TVLanguageSwitchDialog(this, onItemSelectListener);
        /*设置弹框点击周围不予消失*/
        tvLanguageSwitchDialog.setCanceledOnTouchOutside(false);
        /*设置弹框背景*/
        tvLanguageSwitchDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_white));
        tvLanguageSwitchDialog.show();
    }
}
