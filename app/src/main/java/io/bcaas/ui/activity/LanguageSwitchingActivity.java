package io.bcaas.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.adapter.LanguageSwitchingAdapter;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BcaasApplication;
import io.bcaas.bean.LanguageSwitchingBean;
import io.bcaas.constants.Constants;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.tools.ActivityTool;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.StringTool;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/31
 * <p>
 * 「切換語言」
 */
public class LanguageSwitchingActivity extends BaseActivity {
    private String TAG = LanguageSwitchingActivity.class.getSimpleName();

    @BindView(R.id.ib_back)
    ImageButton ibBack;
    @BindView(R.id.ib_close)
    ImageButton ibClose;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ib_right)
    ImageButton ibRight;
    @BindView(R.id.rl_header)
    RelativeLayout rlHeader;
    @BindView(R.id.rv_setting)
    RecyclerView rvSetting;

    private LanguageSwitchingAdapter languageSwitchingAdapter;

    @Override
    public int getContentView() {
        return R.layout.aty_language_switch;
    }

    @Override
    public void getArgs(Bundle bundle) {

    }

    @Override
    public void initViews() {
        tvTitle.setText(R.string.Language_switching);
        ibBack.setVisibility(View.VISIBLE);
        setAdapter();
    }

    @Override
    public void initListener() {
        ibBack.setOnClickListener(v -> {
            finish();
        });
    }


    private void setAdapter() {
        String currentLanguage = getCurrentLanguage();
        List<LanguageSwitchingBean> languageSwitchingBeans = new ArrayList<>();
        LanguageSwitchingBean languageSwitchingBeanCN = new LanguageSwitchingBean(Constants.KeyMaps.CN, Constants.ValueMaps.CN, StringTool.equals(currentLanguage, Constants.ValueMaps.CN));
        LanguageSwitchingBean languageSwitchingBeanTW = new LanguageSwitchingBean(Constants.KeyMaps.TW, Constants.ValueMaps.TW, StringTool.equals(currentLanguage, Constants.ValueMaps.TW));
        LanguageSwitchingBean languageSwitchingBeanEN = new LanguageSwitchingBean(Constants.KeyMaps.EN, Constants.ValueMaps.EN, StringTool.equals(currentLanguage, Constants.ValueMaps.EN));
        languageSwitchingBeans.add(languageSwitchingBeanCN);
        languageSwitchingBeans.add(languageSwitchingBeanEN);
        languageSwitchingBeans.add(languageSwitchingBeanTW);
        languageSwitchingAdapter = new LanguageSwitchingAdapter(this, languageSwitchingBeans);
        languageSwitchingAdapter.setSettingItemSelectListener(onItemSelectListener);
        rvSetting.setHasFixedSize(true);
        rvSetting.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rvSetting.setAdapter(languageSwitchingAdapter);

    }

    /*獲取當前語言環境*/
    private String getCurrentLanguage() {
        // 1：檢查應用是否已經有用戶自己存儲的語言種類
        String currentString = BcaasApplication.getLanguageType();
        BcaasLog.d(TAG, currentString);
        if (StringTool.isEmpty(currentString)) {
            //zh -中文
            //當前的選中為空，那麼就默認讀取當前系統的語言環境
            Locale locale = getResources().getConfiguration().locale;
//            locale.getLanguage();//zh  是中國
            currentString = locale.getCountry();//CN-簡體中文，TW、HK-繁體中文
        }

        if (StringTool.equals(currentString, Constants.ValueMaps.CN)) {
            return currentString;
        } else if (StringTool.equals(currentString, Constants.ValueMaps.TW) || StringTool.equals(currentString, Constants.ValueMaps.HK)) {
            return Constants.ValueMaps.TW;
        } else {
            return Constants.ValueMaps.EN;

        }
    }

    private OnItemSelectListener onItemSelectListener = new OnItemSelectListener() {
        @Override
        public <T> void onItemSelect(T type) {
            if (type == null) {
                return;
            }
            LanguageSwitchingBean languageSwitchingBean = (LanguageSwitchingBean) type;
            if (languageSwitchingBean == null) {
                return;
            }

            //存儲當前的語言環境
            switchingLanguage(languageSwitchingBean.getType());
        }
    };


    /**
     * 切换英文
     */
    public void switchingLanguage(String type) {
        Resources resources = getResources();// 获得res资源对象
        Configuration config = resources.getConfiguration();// 获得设置对象
        DisplayMetrics dm = resources.getDisplayMetrics();// 获得屏幕参数：主要是分辨率，像素等。
        switch (type) {
            case Constants.ValueMaps.CN:
                config.locale = Locale.CHINA; // 简体中文
                break;
            case Constants.ValueMaps.TW:
                config.locale = Locale.TAIWAN; // 繁體中文
                break;
            case Constants.ValueMaps.EN:
                config.locale = Locale.ENGLISH; // 英文
                break;
        }
        BcaasApplication.setLanguageType(type);
        resources.updateConfiguration(config, dm);
        //如果不重启当前界面，是不会立马修改的
        ActivityTool.getInstance().removeAllActivity();
        intentToActivity(MainActivity.class,true);
    }
}
