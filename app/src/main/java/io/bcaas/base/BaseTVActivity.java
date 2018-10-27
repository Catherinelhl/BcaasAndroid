package io.bcaas.base;

import android.os.Bundle;

import io.bcaas.R;
import io.bcaas.bean.TypeSwitchingBean;
import io.bcaas.constants.Constants;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.tools.ActivityTool;
import io.bcaas.ui.activity.tv.MainActivityTV;
import io.bcaas.view.dialog.BcaasSingleDialog;
import io.bcaas.view.dialog.TVBcaasDialog;
import io.bcaas.view.dialog.TVBcaasSingleDialog;
import io.bcaas.view.dialog.TVCurrencySwitchDialog;
import io.bcaas.view.dialog.TVLanguageSwitchDialog;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/27
 * <p>
 * TV版基类
 */
public abstract class BaseTVActivity extends BaseActivity {

    private String TAG = BaseTVActivity.class.getSimpleName();
    /*TV「切換語言」彈框*/
    private TVLanguageSwitchDialog tvLanguageSwitchDialog;
    /*TV 幣種切換 彈框*/
    private TVCurrencySwitchDialog tvCurrencySwitchDialog;
    /*TV 雙按鈕 彈框*/
    private TVBcaasDialog tvBcaasDialog;
    /*TV 單按鈕 彈框*/
    private TVBcaasSingleDialog tvBcaasSingleDialog;

    /**
     * 显示对话框
     *
     * @param message
     * @param listener
     */
    public void showTVBcaasDialog(String message, final TVBcaasDialog.ConfirmClickListener listener) {
        showTVBcaasDialog(getResources().getString(R.string.warning),
                getResources().getString(R.string.confirm),
                getResources().getString(R.string.cancel), message, listener);
    }

    public void showTVBcaasDialog(String title, String left, String right, String message, final TVBcaasDialog.ConfirmClickListener listener) {
        if (tvBcaasDialog != null) {
            tvBcaasDialog.dismiss();
            tvBcaasDialog.cancel();
            tvBcaasDialog = null;
        }
        tvBcaasDialog = new TVBcaasDialog(this);
        /*设置弹框点击周围不予消失*/
        tvBcaasDialog.setCanceledOnTouchOutside(false);
        /*设置弹框背景*/
//        tvBcaasDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_white));
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

    //隐藏TV双按钮对话框
    public void hideTVBcaasDialog() {
        if (tvBcaasDialog != null) {
            tvBcaasDialog.dismiss();
            tvBcaasDialog.cancel();
            tvBcaasDialog = null;
        }
    }

    /*显示TV版「切换语言」弹框 */
    public void showTVLanguageSwitchDialog(OnItemSelectListener onItemSelectListener) {
        if (tvLanguageSwitchDialog != null) {
            tvLanguageSwitchDialog.dismiss();
            tvLanguageSwitchDialog.cancel();
            tvLanguageSwitchDialog = null;
        }
        tvLanguageSwitchDialog = new TVLanguageSwitchDialog(this, onItemSelectListener, getCurrentLanguage());
        /*设置弹框点击周围不予消失*/
        tvLanguageSwitchDialog.setCanceledOnTouchOutside(false);
        /*设置弹框背景*/
        // tvLanguageSwitchDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_white));
        tvLanguageSwitchDialog.show();
    }

    //隐藏「切换语言」对话框
    public void hideTVLanguageSwitchDialog() {
        if (tvLanguageSwitchDialog != null) {
            tvLanguageSwitchDialog.dismiss();
            tvLanguageSwitchDialog.cancel();
            tvLanguageSwitchDialog = null;
        }
    }

    /*显示TV版 幣種切換弹框 */
    public void showTVCurrencySwitchDialog(OnItemSelectListener onItemSelectListener) {
        if (tvCurrencySwitchDialog != null) {
            tvCurrencySwitchDialog.dismiss();
            tvCurrencySwitchDialog.cancel();
            tvCurrencySwitchDialog = null;
        }
        tvCurrencySwitchDialog = new TVCurrencySwitchDialog(this, onItemSelectListener);
        /*设置弹框点击周围不予消失*/
        tvCurrencySwitchDialog.setCanceledOnTouchOutside(false);
        /*设置弹框背景*/
        // tvCurrencySwitchDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_white));
        tvCurrencySwitchDialog.show();
    }

    //隐藏 幣種切換 对话框
    public void hideTVCurrencySwitchDialog() {
        if (tvCurrencySwitchDialog != null) {
            tvCurrencySwitchDialog.dismiss();
            tvCurrencySwitchDialog.cancel();
            tvCurrencySwitchDialog = null;
        }
    }

    /**
     * 切换语言
     *
     * @param type
     * @param <T>
     */
    public <T> void switchLanguage(T type) {
        TypeSwitchingBean typeSwitchingBean = (TypeSwitchingBean) type;
        if (typeSwitchingBean == null) {
            return;
        }
        String languageType = typeSwitchingBean.getType();
        //存儲當前的語言環境
        switchingLanguage(languageType);
        //存儲當前的語言環境
        BCAASApplication.setStringToSP(Constants.Preference.LANGUAGE_TYPE, languageType);
        //如果不重启当前界面，是不会立马修改的
        ActivityTool.getInstance().removeAllActivity();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KeyMaps.From, Constants.ValueMaps.FROM_LANGUAGE_SWITCH);
        intentToActivity(bundle, MainActivityTV.class, true);
    }

    @Override
    protected void onDestroy() {
        hideTVLanguageSwitchDialog();
        super.onDestroy();
    }

    public void showTVBcaasSingleDialog(String message, final BcaasSingleDialog.ConfirmClickListener listener) {
        showTVBcaasSingleDialog(getResources().getString(R.string.warning), message, listener);
    }

    /**
     * 显示单个 按钮对话框
     *
     * @param title
     * @param message
     * @param listener
     */
    public void showTVBcaasSingleDialog(String title, String message, final BcaasSingleDialog.ConfirmClickListener listener) {
        if (tvBcaasSingleDialog != null) {
            tvBcaasSingleDialog.dismiss();
            tvBcaasSingleDialog.cancel();
            tvBcaasSingleDialog = null;
        }
        tvBcaasSingleDialog = new TVBcaasSingleDialog(this);
        /*设置弹框点击周围不予消失*/
        tvBcaasSingleDialog.setCanceledOnTouchOutside(false);
        tvBcaasSingleDialog.setCancelable(false);
        /*设置弹框背景*/
        //tvBcaasSingleDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_white));
        tvBcaasSingleDialog.setContent(message)
                .setTitle(title)
                .setOnConfirmClickListener(() -> {
                    listener.sure();
                    tvBcaasSingleDialog.dismiss();
                }).show();
    }

    public void showTVLogoutSingleDialog() {
        cleanAccountData();
        showTVBcaasSingleDialog(getString(R.string.warning),
                getString(R.string.please_login_again), () -> {
                    intentToLogin();
                });

    }

}
