package io.bcaas.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.adapter.AddressManagerAdapter;
import io.bcaas.base.BaseActivity;
import io.bcaas.db.vo.Address;
import io.bcaas.event.NotifyAddressData;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.presenter.AddressManagerPresenterImp;
import io.bcaas.ui.contracts.AddressManagerContract;
import io.bcaas.view.dialog.BcaasDialog;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 * <p>
 * 地址管理
 */
public class AddressManagerActivity extends BaseActivity
        implements AddressManagerContract.View {

    private String TAG = AddressManagerActivity.class.getSimpleName();

    @BindView(R.id.ib_back)
    ImageButton ibBack;
    @BindView(R.id.ib_close)
    ImageButton ibClose;
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
    private AddressManagerAdapter addressManagerAdapter;
    private AddressManagerContract.Presenter presenter;
    private List<Address> addressBeans;

    @Override
    public int getContentView() {
        return R.layout.activity_address_manager;
    }

    @Override
    public void getArgs(Bundle bundle) {

    }

    @Override
    public void initViews() {
        addressBeans = new ArrayList<>();
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
            public <T> void onItemSelect(T type) {
                if (type == null) {
                    return;
                }
                if (type instanceof Address) {
                    final Address addressBean = (Address) type;
                    showBcaasDialog(getString(R.string.sure_delete) + addressBean.getAddress(), new BcaasDialog.ConfirmClickListener() {
                        @Override
                        public void sure() {
                            presenter.deleteSingleAddress(addressBean);
                            //响应删除事件
                            if (addressBeans != null) {
                                addressBeans.remove(addressBean);
                                addressManagerAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void cancel() {

                        }
                    });
                }
            }
        });
        ibRight.setOnClickListener(v -> intentToActivity(InsertAddressActivity.class));
        ibBack.setOnClickListener(v -> finish());
        btnInsertAddress.setOnClickListener(v -> intentToActivity(InsertAddressActivity.class));

    }

    @Override
    public void getAddresses(List<Address> addresses) {
        addressBeans = addresses;
        rvSetting.setVisibility(View.VISIBLE);
        ivNoAddress.setVisibility(View.GONE);
        if (addressManagerAdapter != null) {
            addressManagerAdapter.addList(addressBeans);
        }
    }

    @Override
    public void noData() {
        rvSetting.setVisibility(View.GONE);
        ivNoAddress.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void notifyAddressData(NotifyAddressData notifyAddressData) {
        boolean isNotify = notifyAddressData.isNotify();
        if (isNotify) {
            presenter.queryAllAddresses();
        }
    }
}
