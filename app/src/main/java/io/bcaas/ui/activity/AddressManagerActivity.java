package io.bcaas.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.adapter.AddressManagerAdapter;
import io.bcaas.base.BaseActivity;
import io.bcaas.database.Address;
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
    @BindView(R.id.rlHeader)
    RelativeLayout rlHeader;
    @BindView(R.id.rv_setting)
    RecyclerView rvSetting;
    private AddressManagerAdapter addressManagerAdapter;
    private AddressManagerContract.Presenter presenter;
    private List<Address> addressBeans;

    @Override
    public int getContentView() {
        return R.layout.aty_address_manager;
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
                if (type == null) return;
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
        ibRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentToActivity(InsertAddressActivity.class);
            }
        });
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnInsertAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentToActivity(InsertAddressActivity.class);

            }
        });

    }

    @Override
    public void getAddresses(List<Address> addresses) {
        addressBeans = addresses;
        if (addressManagerAdapter != null) {
            addressManagerAdapter.addList(addressBeans);
        }
    }

    @Override
    public void noData() {
    }

    @Subscribe
    public void notifyAddressData(NotifyAddressData notifyAddressData) {
        boolean isNotify = notifyAddressData.isNotify();
        if (isNotify) {
            presenter.queryAllAddresses();
        }

    }


}
