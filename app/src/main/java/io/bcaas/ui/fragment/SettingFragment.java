package io.bcaas.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.adapter.SettingsAdapter;
import io.bcaas.base.BCAASApplication;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BaseFragment;
import io.bcaas.bean.SettingsBean;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.presenter.SettingPresenterImp;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.StringTool;
import io.bcaas.ui.activity.AddressManagerActivity;
import io.bcaas.ui.activity.CheckWalletInfoActivity;
import io.bcaas.ui.activity.LanguageSwitchingActivity;
import io.bcaas.ui.activity.ModifyAuthorizedRepresentativesActivity;
import io.bcaas.ui.contracts.SettingContract;
import io.bcaas.view.dialog.BcaasDialog;
import io.reactivex.disposables.Disposable;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * Fragment：「设置」
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
        return R.layout.fragment_setting;
    }


    @Override
    public void initViews(View view) {
        presenter = new SettingPresenterImp(this);
        List<SettingsBean> settingTypes = initSettingTypes();//得到设置页面需要显示的所有设置选项
        settingTypesAdapter = new SettingsAdapter(context, settingTypes);
        rvSetting.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rvSetting.setAdapter(settingTypesAdapter);
    }

    /**
     * 添加页面数据，实则应该写在presenter里面，但是写在里面在切换语言的时候却不会更新数据
     *
     * @return
     */
    private List<SettingsBean> initSettingTypes() {
        List<SettingsBean> settingTypes = new ArrayList<>();
        SettingsBean settingTypeBean = new SettingsBean(getString(R.string.view_wallet_info), Constants.SettingType.CHECK_WALLET_INFO);
        SettingsBean settingTypeBean3 = new SettingsBean(getString(R.string.change_representatives), Constants.SettingType.MODIFY_AUTH);
        SettingsBean settingTypeBean4 = new SettingsBean(getString(R.string.address_manager), Constants.SettingType.ADDRESS_MANAGE);
        SettingsBean settingTypeBean5 = new SettingsBean(getString(R.string.Language_switching), Constants.SettingType.LANGUAGE_SWITCHING);
        settingTypes.add(settingTypeBean);
        settingTypes.add(settingTypeBean3);
        settingTypes.add(settingTypeBean4);
        settingTypes.add(settingTypeBean5);
        return settingTypes;

    }

    @Override
    public void initListener() {
        settingTypesAdapter.setSettingItemSelectListener(new OnItemSelectListener() {
            @Override
            public <T> void onItemSelect(T type, String from) {
                if (type == null) {
                    return;
                }
                if (type instanceof SettingsBean) {
                    SettingsBean settingTypeBean = (SettingsBean) type;
                    switch (settingTypeBean.getTag()) {
                        case CHECK_WALLET_INFO:
                            intentToActivity(null, CheckWalletInfoActivity.class, false);
                            break;
                        case MODIFY_AUTH:
                            // 判断当前是否选择了积分
                            if (StringTool.isEmpty(BCAASApplication.getBlockService())) {
                                showToast(getResources().getString(R.string.select_token_please));
                                return;
                            }
                            /*请求授权代表*/
                            /*1：获取最新的授权地址*/
                            intentToActivity(null, ModifyAuthorizedRepresentativesActivity.class, false);
                            break;
                        case ADDRESS_MANAGE:
                            intentToActivity(null, AddressManagerActivity.class, false);
                            break;
                        case LANGUAGE_SWITCHING:
                            intentToActivity(null, LanguageSwitchingActivity.class, false);
                            break;

                    }
                }
            }

            @Override
            public void changeItem(boolean isChange) {

            }
        });
        Disposable subscribeLogout = RxView.clicks(btnLogout)
                .throttleFirst(Constants.Time.sleep800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    showBcaasDialog(getResources().getString(R.string.confirm_logout), new BcaasDialog.ConfirmClickListener() {
                        @Override
                        public void sure() {
                            presenter.logout();
                            if (activity != null) {
                                ((BaseActivity) activity).cleanAccountData();
                                ((BaseActivity) activity).cleanQueueTask();
                                ((BaseActivity) activity).intentToLogin();
                            }
                        }

                        @Override
                        public void cancel() {

                        }
                    });
                });
    }

    @Override
    public void logoutFailure(String message) {
        LogTool.d(TAG, message);
        logoutFailure();
    }

    @Override
    public void logoutFailure() {
        if (!checkActivityState()) {
            return;
        }
        showToast(getString(R.string.logout_failure));

    }

    @Override
    public void accountError() {
        showToast(getResources().getString(R.string.account_data_error));
    }

    @Override
    public void logoutSuccess() {
        LogTool.d(TAG, MessageConstants.LogInfo.LOGOUT_TAG, MessageConstants.LOGOUT_SUCCESSFULLY);
    }
}
