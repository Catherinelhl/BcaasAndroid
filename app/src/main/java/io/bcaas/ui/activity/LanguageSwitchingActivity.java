package io.bcaas.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.adapter.LanguageSwitchingAdapter;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BcaasApplication;
import io.bcaas.bean.LanguageSwitchingBean;
import io.bcaas.constants.Constants;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.tools.ActivityTool;
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
        return R.layout.activity_language_switch;
    }

    @Override
    public boolean full() {
        return false;
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
        LanguageSwitchingBean languageSwitchingBeanCN = new LanguageSwitchingBean(getResources().getString(R.string.language_chinese_simplified), Constants.ValueMaps.CN, StringTool.equals(currentLanguage, Constants.ValueMaps.CN));
        LanguageSwitchingBean languageSwitchingBeanEN = new LanguageSwitchingBean(getResources().getString(R.string.lauguage_english), Constants.ValueMaps.EN, StringTool.equals(currentLanguage, Constants.ValueMaps.EN));
        languageSwitchingBeans.add(languageSwitchingBeanCN);
        languageSwitchingBeans.add(languageSwitchingBeanEN);
        languageSwitchingAdapter = new LanguageSwitchingAdapter(this, languageSwitchingBeans);
        languageSwitchingAdapter.setSettingItemSelectListener(onItemSelectListener);
        rvSetting.setHasFixedSize(true);
        rvSetting.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rvSetting.setAdapter(languageSwitchingAdapter);

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

            String languageType = languageSwitchingBean.getType();
            //存儲當前的語言環境
            switchingLanguage(languageType);
            //存儲當前的語言環境
            BcaasApplication.setStringToSP(Constants.Preference.LANGUAGE_TYPE, languageType);
            //如果不重启当前界面，是不会立马修改的
            ActivityTool.getInstance().removeAllActivity();
            intentToActivity(MainActivity.class, true);
        }
    };
}
