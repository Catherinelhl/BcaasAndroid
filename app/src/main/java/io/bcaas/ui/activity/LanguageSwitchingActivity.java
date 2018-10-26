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
import io.bcaas.adapter.TypeSwitchingAdapter;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BCAASApplication;
import io.bcaas.bean.TypeSwitchingBean;
import io.bcaas.constants.Constants;
import io.bcaas.event.UnBindServiceEvent;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.tools.ActivityTool;
import io.bcaas.tools.OttoTool;
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

    private TypeSwitchingAdapter typeSwitchingAdapter;

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
        List<TypeSwitchingBean> typeSwitchingBeans = new ArrayList<>();
        TypeSwitchingBean typeSwitchingBeanCN = new TypeSwitchingBean(getResources().getString(R.string.language_chinese_simplified), Constants.ValueMaps.CN, StringTool.equals(currentLanguage, Constants.ValueMaps.CN));
        TypeSwitchingBean typeSwitchingBeanEN = new TypeSwitchingBean(getResources().getString(R.string.lauguage_english), Constants.ValueMaps.EN, StringTool.equals(currentLanguage, Constants.ValueMaps.EN));
        typeSwitchingBeans.add(typeSwitchingBeanCN);
        typeSwitchingBeans.add(typeSwitchingBeanEN);
        typeSwitchingAdapter = new TypeSwitchingAdapter(this, typeSwitchingBeans);
        typeSwitchingAdapter.setSettingItemSelectListener(onItemSelectListener);
        rvSetting.setHasFixedSize(true);
        rvSetting.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rvSetting.setAdapter(typeSwitchingAdapter);

    }

    private OnItemSelectListener onItemSelectListener = new OnItemSelectListener() {
        @Override
        public <T> void onItemSelect(T type, String from) {
            if (type == null) {
                return;
            }
            if (type instanceof TypeSwitchingBean) {
                TypeSwitchingBean typeSwitchingBean = (TypeSwitchingBean) type;
                if (typeSwitchingBean == null) {
                    return;
                }
                OttoTool.getInstance().post(new UnBindServiceEvent());
                String languageType = typeSwitchingBean.getType();
                //存儲當前的語言環境
                switchingLanguage(languageType);
                //存儲當前的語言環境
                BCAASApplication.setStringToSP(Constants.Preference.LANGUAGE_TYPE, languageType);
                //如果不重启当前界面，是不会立马修改的
                ActivityTool.getInstance().removeAllActivity();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.KeyMaps.From, Constants.ValueMaps.FROM_LANGUAGE_SWITCH);
                intentToActivity(bundle, MainActivity.class, true);
            }

        }

        @Override
        public void changeItem(boolean isChange) {
        }
    };

    @Override
    public void showLoading() {
        if (!checkActivityState()) {
            return;
        }
        showLoadingDialog();
    }

    @Override
    public void hideLoading() {
        if (!checkActivityState()) {
            return;
        }
        hideLoadingDialog();
    }

}
