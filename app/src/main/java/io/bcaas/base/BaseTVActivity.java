package io.bcaas.base;

import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;

import java.util.List;

import io.bcaas.R;
import io.bcaas.bean.LanguageSwitchingBean;
import io.bcaas.constants.Constants;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.tools.ActivityTool;
import io.bcaas.ui.activity.tv.MainActivityTV;
import io.bcaas.view.dialog.TVBcaasDialog;
import io.bcaas.view.dialog.TVLanguageSwitchDialog;
import io.bcaas.view.pop.ListPopWindow;
import io.bcaas.view.pop.TVListPopWindow;
import io.bcaas.vo.PublicUnitVO;

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

    /**
     * 显示当前需要顯示的货币列表
     * 點擊幣種、點擊選擇交互帳戶地址
     *
     * @param onItemSelectListener 通過傳入的回調來得到選擇的值
     * @param list                 需要顯示的列表
     */
    public void showTVCurrencyListPopWindow(OnItemSelectListener onItemSelectListener, List<PublicUnitVO> list) {
        TVListPopWindow tvListPopWindow = new TVListPopWindow(context);
        tvListPopWindow.addCurrencyList(onItemSelectListener, list);
        tvListPopWindow.setOnDismissListener(() -> setBackgroundAlpha(1f));
        //设置layout在PopupWindow中显示的位置
        tvListPopWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        setBackgroundAlpha(0.7f);
    }

}
