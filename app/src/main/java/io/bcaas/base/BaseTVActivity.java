package io.bcaas.base;

import android.os.Bundle;

import io.bcaas.R;
import io.bcaas.bean.LanguageSwitchingBean;
import io.bcaas.constants.Constants;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.tools.ActivityTool;
import io.bcaas.ui.activity.tv.MainActivityTV;
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
    public void showTVLanguageSwitchDialog(OnItemSelectListener onItemSelectListener) {
        TVLanguageSwitchDialog tvLanguageSwitchDialog = new TVLanguageSwitchDialog(this, onItemSelectListener, getCurrentLanguage());
        /*设置弹框点击周围不予消失*/
        tvLanguageSwitchDialog.setCanceledOnTouchOutside(false);
        /*设置弹框背景*/
        tvLanguageSwitchDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_white));
        tvLanguageSwitchDialog.show();
    }

    /**
     * 切换语言
     *
     * @param type
     * @param <T>
     */
    public <T> void switchLanguage(T type) {
        LanguageSwitchingBean languageSwitchingBean = (LanguageSwitchingBean) type;
        if (languageSwitchingBean == null) {
            return;
        }
        String languageType = languageSwitchingBean.getType();
        //存儲當前的語言環境
        switchingLanguage(languageType);
        //存儲當前的語言環境
        BcaasApplication.setStringToSP(Constants.Preference.LANGUAGE_TYPE, languageType);
        //如果不重启当前界面，是不会立马修改的
        ActivityTool.getInstance().removeAllActivity();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KeyMaps.From, Constants.ValueMaps.FROM_LANGUAGESWITCH);
        intentToActivity(bundle, MainActivityTV.class, true);
    }
}
