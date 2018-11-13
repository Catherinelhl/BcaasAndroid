package io.bcaas.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.bcaas.R;
import io.bcaas.adapter.TypeSwitchingAdapter;
import io.bcaas.bean.TypeSwitchingBean;
import io.bcaas.constants.Constants;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.tools.StringTool;
import io.bcaas.view.tv.FlyBroadLayout;
import io.bcaas.view.tv.MainUpLayout;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/27
 * <p>
 * 自定義Dialog：TV版「语言切换」彈框
 */
public class TVLanguageSwitchDialog extends Dialog {
    private String TAG = TVLanguageSwitchDialog.class.getSimpleName();
    @BindView(R.id.block_base_mainup)
    FlyBroadLayout blockBaseMainup;
    @BindView(R.id.rv_setting)
    RecyclerView rvSetting;
    @BindView(R.id.block_base_content)
    MainUpLayout blockBaseContent;
    private Unbinder unbinder;

    private Context context;
    private TypeSwitchingAdapter typeSwitchingAdapter;
    private OnItemSelectListener itemSelectListener;
    private String currentLanguage;

    public TVLanguageSwitchDialog(Context context, OnItemSelectListener itemSelectListener, String currentLanguage) {
        this(context, R.style.tv_bcaas_dialog, itemSelectListener, currentLanguage);
    }


    public TVLanguageSwitchDialog(@NonNull Context context, int themeResId, OnItemSelectListener itemSelectListener, String currentLanguage) {
        super(context, themeResId);
        this.context = context;
        this.currentLanguage = currentLanguage;
        this.itemSelectListener = itemSelectListener;
        View view = LayoutInflater.from(context).inflate(R.layout.tv_layout_language_switch_dialog, null);
        setContentView(view);
        unbinder = ButterKnife.bind(this);
        setAdapter();
        initListener();
    }

    private void setAdapter() {
        List<TypeSwitchingBean> typeSwitchingBeans = new ArrayList<>();
        TypeSwitchingBean typeSwitchingBeanCN = new TypeSwitchingBean(context.getResources().getString(R.string.language_chinese_simplified),
                Constants.ValueMaps.CN, StringTool.equals(currentLanguage, Constants.ValueMaps.CN));
        TypeSwitchingBean typeSwitchingBeanEN = new TypeSwitchingBean(context.getResources().getString(R.string.lauguage_english),
                Constants.ValueMaps.EN, StringTool.equals(currentLanguage, Constants.ValueMaps.EN));
        typeSwitchingBeans.add(typeSwitchingBeanCN);
        typeSwitchingBeans.add(typeSwitchingBeanEN);
        typeSwitchingAdapter = new TypeSwitchingAdapter(context, typeSwitchingBeans);
        typeSwitchingAdapter.setSettingItemSelectListener(onItemSelectListener);
        rvSetting.setHasFixedSize(true);
        rvSetting.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rvSetting.setAdapter(typeSwitchingAdapter);
    }

    private OnItemSelectListener onItemSelectListener = new OnItemSelectListener() {
        @Override
        public <T> void onItemSelect(T type, String from) {
            itemSelectListener.onItemSelect(type, Constants.KeyMaps.LANGUAGE_SWITCH);
        }

        @Override
        public void changeItem(boolean isChange) {
            if (!isChange) {
                dismiss();
            }
        }
    };

    public void initListener() {
        blockBaseContent.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                blockBaseMainup.setFocusView(newFocus, oldFocus, 1f);
            }
        });
    }

}
