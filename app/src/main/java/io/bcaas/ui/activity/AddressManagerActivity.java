package io.bcaas.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.*;
import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.adapter.AddressManagerAdapter;
import io.bcaas.base.BaseActivity;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.db.vo.AddressVO;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.presenter.AddressManagerPresenterImp;
import io.bcaas.ui.contracts.AddressManagerContract;
import io.bcaas.view.dialog.BcaasDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 * <p>
 * Activity：[首頁] -> [设置] -> [地址管理] 界面
 */
public class AddressManagerActivity extends BaseActivity
        implements AddressManagerContract.View {
    @BindView(R.id.ib_back)
    ImageButton ibBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ib_right)
    ImageButton ibRight;
    @BindView(R.id.btn_insert_address)
    Button btnInsertAddress;
    @BindView(R.id.rl_header)
    RelativeLayout rlHeader;
    @BindView(R.id.rv_setting)
    RecyclerView rvSetting;
    @BindView(R.id.iv_no_address)
    ImageView ivNoAddress;
    @BindView(R.id.tv_no_address)
    TextView tvNoAddress;
    private AddressManagerAdapter addressManagerAdapter;
    private AddressManagerContract.Presenter presenter;
    private List<AddressVO> addressVOBeans;

    @Override
    public int getContentView() {
        return R.layout.activity_address_manager;
    }

    @Override
    public void getArgs(Bundle bundle) {

    }

    @Override
    public boolean full() {
        return false;
    }

    @Override
    public void initViews() {
        addressVOBeans = new ArrayList<>();
        presenter = new AddressManagerPresenterImp(this);
        ibBack.setVisibility(View.VISIBLE);
        ibRight.setVisibility(View.VISIBLE);
        tvTitle.setText(R.string.address_manager);
        initAdapter();
        presenter.queryAllAddresses();
    }

    private void initAdapter() {
        addressManagerAdapter = new AddressManagerAdapter(this);
        rvSetting.setHasFixedSize(true);
        rvSetting.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvSetting.setAdapter(addressManagerAdapter);
    }

    @Override
    public void initListener() {
        addressManagerAdapter.setItemSelectListener(new OnItemSelectListener() {
            @Override
            public <T> void onItemSelect(T type, String from) {
                if (type == null) {
                    return;
                }
                if (type instanceof AddressVO) {
                    final AddressVO addressVOBean = (AddressVO) type;
                    showBcaasDialog(getString(R.string.confirm_delete),
                            getResources().getString(R.string.cancel),
                            getResources().getString(R.string.confirm),
                            addressVOBean.getAddress(), new BcaasDialog.ConfirmClickListener() {
                                @Override
                                public void sure() {
                                    presenter.deleteSingleAddress(addressVOBean);
                                    //响应删除事件
                                    if (addressVOBeans != null) {
                                        addressVOBeans.remove(addressVOBean);
                                        addressManagerAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void cancel() {

                                }
                            });
                }
            }

            @Override
            public void changeItem(boolean isChange) {

            }
        });
        ibRight.setOnClickListener(v -> intentToInsertAddress());
        ibBack.setOnClickListener(v -> finish());
        btnInsertAddress.setOnClickListener(v -> intentToInsertAddress());

    }

    private void intentToInsertAddress() {
        //1：检测当前数据库地址数据的总条数，暂时限定为100条，如果已达上限，进行弹框提示
        if (addressVOBeans.size() >= MessageConstants.ADDRESS_LIMIT) {
            showToast(getResources().getString(R.string.address_over_quantity));
        } else {
            Intent intent = new Intent();
            intent.setClass(this, InsertAddressActivity.class);
            startActivityForResult(intent, Constants.KeyMaps.REQUEST_CODE_INSERT_ADDRESS_ACTIVITY);
        }
    }

    @Override
    public void getAddresses(List<AddressVO> addressVOS) {
        addressVOBeans = addressVOS;
        rvSetting.setVisibility(View.VISIBLE);
        ivNoAddress.setVisibility(View.GONE);
        tvNoAddress.setVisibility(View.GONE);
        if (addressManagerAdapter != null) {
            addressManagerAdapter.addList(addressVOBeans);
        }
    }

    @Override
    public void noAddress() {
        rvSetting.setVisibility(View.GONE);
        ivNoAddress.setVisibility(View.VISIBLE);
        tvNoAddress.setVisibility(View.VISIBLE);
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            //如果等於「InsertAddress」
            if (requestCode == Constants.KeyMaps.REQUEST_CODE_INSERT_ADDRESS_ACTIVITY) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    boolean isBack = bundle.getBoolean(Constants.KeyMaps.From);
                    if (!isBack) {
                        presenter.queryAllAddresses();
                    }
                }
            }
        }
    }

}
