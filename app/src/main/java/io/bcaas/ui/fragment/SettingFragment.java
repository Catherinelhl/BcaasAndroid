package io.bcaas.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.adapter.SettingsAdapter;
import io.bcaas.base.BaseFragment;
import io.bcaas.base.BcaasApplication;
import io.bcaas.bean.SettingsBean;
import io.bcaas.constants.Constants;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.presenter.SettingPresenterImp;
import io.bcaas.ui.activity.AddressManagerActivity;
import io.bcaas.ui.activity.CheckWalletInfoActivity;
import io.bcaas.ui.contracts.SettingContract;
import io.bcaas.tools.StringTool;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 「设置」
 */
public class SettingFragment extends BaseFragment implements SettingContract.View {
    private String TAG = SettingFragment.class.getSimpleName();

    @BindView(R.id.rv_setting)
    RecyclerView rvSetting;
    @BindView(R.id.btnLogout)
    Button btnLogout;

    private SettingContract.Presenter presenter;

    private SettingsAdapter settingTypesAdapter;

    public static SettingFragment newInstance() {
        SettingFragment settingFragment = new SettingFragment();
        return settingFragment;
    }

    @Override
    public void getArgs(Bundle bundle) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.frg_setting;
    }


    @Override
    public void initViews(View view) {
        presenter = new SettingPresenterImp(this);
        List<SettingsBean> settingTypes = presenter.initSettingTypes();//得到设置页面需要显示的所有设置选项
        settingTypesAdapter = new SettingsAdapter(context, settingTypes);
        rvSetting.setHasFixedSize(true);
        rvSetting.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rvSetting.setAdapter(settingTypesAdapter);
    }

    @Override
    public void initListener() {
        settingTypesAdapter.setSettingItemSelectListener(new OnItemSelectListener() {
            @Override
            public <T> void onItemSelect(T type) {
                if (type == null) {
                    return;
                }
                if (type instanceof SettingsBean) {
                    SettingsBean settingTypeBean = (SettingsBean) type;
                    switch (settingTypeBean.getTag()) {
                        case CHECK_WALLET_INFO:
                            Gson gson = new Gson();
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.KeyMaps.CURRENCY, gson.toJson(getCurrency()));
                            bundle.putString(Constants.KeyMaps.ALL_CURRENCY, gson.toJson(getAllTransactionData()));
                            intentToActivity(bundle, CheckWalletInfoActivity.class, false);
                            break;
                        case MODIFY_PASSWORD:
                        case MODIFY_AUTH:
                            showToast(settingTypeBean.getType());
                            break;
                        case ADDRESS_MANAGE:
                            intentToActivity(null, AddressManagerActivity.class, false);
                            break;
                        case LANGUAGE_SWITCHING:
                            showToast(settingTypeBean.getType());
                            break;

                    }
                }
            }
        });
        Disposable subscribeLogout = RxView.clicks(btnLogout)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    String address = BcaasApplication.getWalletAddress();
                    if (StringTool.isEmpty(address)) {
                        showToast(getString(R.string.dataexceptionofaccount));
                        return;
                    }
                    presenter.logout(address);
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
