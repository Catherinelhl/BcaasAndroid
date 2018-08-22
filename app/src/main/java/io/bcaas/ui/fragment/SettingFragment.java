package io.bcaas.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.adapter.SettingTypesAdapter;
import io.bcaas.base.BaseFragment;
import io.bcaas.base.BcaasApplication;
import io.bcaas.bean.SettingTypeBean;
import io.bcaas.constants.Constants;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.presenter.SettingPresenterImp;
import io.bcaas.ui.activity.AddressManagerActivity;
import io.bcaas.ui.activity.CheckWalletInfoActivity;
import io.bcaas.ui.contracts.SettingContract;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.StringTool;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 设置页面
 */
public class SettingFragment extends BaseFragment implements SettingContract.View {
    @BindView(R.id.rvSetting)
    RecyclerView rvSetting;
    @BindView(R.id.btnLogout)
    Button btnLogout;

    private String TAG = "SettingFragment";

    private SettingContract.Presenter presenter;

    private SettingTypesAdapter settingTypesAdapter;


    public static SettingFragment newInstance() {
        SettingFragment settingFragment = new SettingFragment();
        return settingFragment;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.frg_setting;
    }


    @Override
    public void initViews(View view) {
        presenter = new SettingPresenterImp(this);
        List<SettingTypeBean> settingTypes = presenter.initSettingTypes();//得到设置页面需要显示的所有设置选项
        settingTypesAdapter = new SettingTypesAdapter(context, settingTypes);
        rvSetting.setHasFixedSize(true);
        rvSetting.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rvSetting.setAdapter(settingTypesAdapter);
    }

    @Override
    public void initListener() {
        settingTypesAdapter.setSettingItemSelectListener(new OnItemSelectListener() {
            @Override
            public <T> void onItemSelect(T type) {
                //TODO  到时候这个类目，可以考虑用一个有TAG值的数据类来保存
                if (type == null) return;
                if (type instanceof SettingTypeBean) {
                    SettingTypeBean settingTypeBean = (SettingTypeBean) type;
                    switch (settingTypeBean.getTag()) {
                        case CHECKWALLETINFO:
                            Gson gson = new Gson();
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.KeyMaps.CURRENCY, gson.toJson(getCurrency()));
                            bundle.putString(Constants.KeyMaps.ALLCURRENCY, gson.toJson(getAllTransactionData()));
                            intentToActivity(bundle, CheckWalletInfoActivity.class, false);
                            break;
                        case MODIFYPOSSWORD:
                        case MODIFYAUTH:
                            showToast(settingTypeBean.getType());
                            break;
                        case ADRESSMANNAGE:
                            intentToActivity(null, AddressManagerActivity.class, false);
                            break;
                    }
                }
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BcaasLog.d(TAG, "当前账户地址是=》" + BcaasApplication.getWalletAddress());
                String address = BcaasApplication.getWalletAddress();
                if (StringTool.isEmpty(address)) {
                    showToast(getString(R.string.dataexceptionofaccount));
                    return;
                }

                presenter.logout(BcaasApplication.getWalletAddress());
            }
        });
    }

    @Override
    public void logoutFailure(String message) {
        showToast(getString(R.string.logout_failure));
    }

    @Override
    public void logoutSuccess() {
        logout();
    }


}
