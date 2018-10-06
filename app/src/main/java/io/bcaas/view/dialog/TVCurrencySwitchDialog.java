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
import io.bcaas.base.BCAASApplication;
import io.bcaas.bean.TypeSwitchingBean;
import io.bcaas.constants.Constants;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.ecc.WalletTool;
import io.bcaas.view.tv.FlyBroadLayout;
import io.bcaas.view.tv.MainUpLayout;
import io.bcaas.vo.PublicUnitVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/10/6
 * <p>
 * TV  幣種切換彈框
 */
public class TVCurrencySwitchDialog extends Dialog {
    private String TAG = TVCurrencySwitchDialog.class.getSimpleName();
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

    public TVCurrencySwitchDialog(Context context, OnItemSelectListener itemSelectListener) {
        this(context, R.style.tv_bcaas_dialog, itemSelectListener);
    }


    public TVCurrencySwitchDialog(@NonNull Context context, int themeResId, OnItemSelectListener itemSelectListener) {
        super(context, themeResId);
        this.context = context;
        this.itemSelectListener = itemSelectListener;
        View view = LayoutInflater.from(context).inflate(R.layout.tv_layout_language_switch_dialog, null);
        setContentView(view);
        unbinder = ButterKnife.bind(this);
        setAdapter();
        initListener();
    }

    private void setAdapter() {
        //得到當前所有的幣種
        List<PublicUnitVO> publicUnitVOList = WalletTool.getPublicUnitVO();
        if (ListTool.isEmpty(publicUnitVOList)) {
            return;
        }
        List<TypeSwitchingBean> typeSwitchingBeans = new ArrayList<>();
        String currentBlockService = BCAASApplication.getBlockService();
        //重新組裝成新的數據類，用於適配器
        for (PublicUnitVO publicUnitVO : publicUnitVOList) {
            String blockService = publicUnitVO.getBlockService();
            TypeSwitchingBean typeSwitchingBean = new TypeSwitchingBean(blockService, StringTool.equals(blockService, currentBlockService));
            typeSwitchingBeans.add(typeSwitchingBean);
        }
        typeSwitchingAdapter = new TypeSwitchingAdapter(context, typeSwitchingBeans);
        typeSwitchingAdapter.setSettingItemSelectListener(onItemSelectListener);
        rvSetting.setHasFixedSize(true);
        rvSetting.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rvSetting.setAdapter(typeSwitchingAdapter);
    }

    private OnItemSelectListener onItemSelectListener = new OnItemSelectListener() {
        @Override
        public <T> void onItemSelect(T type, String from) {
            itemSelectListener.onItemSelect(type, Constants.KeyMaps.CURRENCY_SWITCH);
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
