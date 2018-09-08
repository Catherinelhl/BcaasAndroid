package io.bcaas.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.obt.qrcode.activity.CaptureActivity;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.bcaas.BuildConfig;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.constants.Constants;
import io.bcaas.db.vo.AddressVO;
import io.bcaas.tools.ecc.KeyTool;
import io.bcaas.event.NotifyAddressDataEvent;
import io.bcaas.presenter.InsertAddressPresenterImp;
import io.bcaas.tools.OttoTool;
import io.bcaas.tools.StringTool;
import io.bcaas.ui.contracts.InsertAddressContract;
import io.reactivex.disposables.Disposable;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 * <p>
 * 新增地址
 */
public class InsertAddressActivity extends BaseActivity
        implements InsertAddressContract.View {


    @BindView(R.id.ib_back)
    ImageButton ibBack;
    @BindView(R.id.ib_scan)
    ImageButton ibScan;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ib_right)
    ImageButton ibRight;
    @BindView(R.id.rl_header)
    RelativeLayout rlHeader;
    @BindView(R.id.tv_account_address_key)
    TextView tvAccountAddressKey;
    @BindView(R.id.et_address_name)
    EditText etAddressName;
    @BindView(R.id.tv_address_key)
    TextView tvAddressKey;
    @BindView(R.id.et_address)
    EditText etAddress;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.ll_insert_address)
    LinearLayout llInsertAddress;
    private InsertAddressContract.Presenter presenter;

    @Override
    public int getContentView() {
        return R.layout.activity_insert_address;
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
        presenter = new InsertAddressPresenterImp(this);
        ibBack.setVisibility(View.VISIBLE);
        tvTitle.setText(R.string.insert_address);

    }

    @Override
    public void initListener() {
        Disposable subscribeScan = RxView.clicks(ibScan)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    startActivityForResult(new Intent(context, CaptureActivity.class), 0);
                });
        ibBack.setOnClickListener(v -> finish());
        Disposable subscribeSave = RxView.clicks(btnSave)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    String alias = etAddressName.getText().toString();
                    String address = etAddress.getText().toString();
                    AddressVO addressVOBean = new AddressVO();
                    addressVOBean.setAddressName(alias);
                    addressVOBean.setAddress(address);
                    if (StringTool.isEmpty(alias)) {
                        showToast(getResources().getString(R.string.please_input_address_name));
                        return;
                    } else if (StringTool.isEmpty(address)) {
                        showToast(getResources().getString(R.string.please_input_receive_account));
                        return;
                    } else {
                        /*检测别名不能超过10个字符*/
                        if (alias.length() > Constants.ValueMaps.ALIAS_LENGTH) {
                        } else {
                            /*检测当前地址格式*/
                            if (KeyTool.validateBitcoinAddress(address)) {
                                /*保存当前数据*/
                                presenter.saveData(addressVOBean);
                            } else {
                                showToast(getResources().getString(R.string.address_format_error));
                            }
                        }


                    }
                });
    }

    @Override
    public void saveDataSuccess() {
        OttoTool.getInstance().post(new NotifyAddressDataEvent(true));
        finish();
    }

    @Override
    public void saveDataFailure() {
        showToast(getString(R.string.save_data_failure));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String result = bundle.getString(Constants.RESULT);
                etAddress.setText(result);
            }
        }
    }
}
