package io.bcaas.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.view.View;
import android.widget.*;
import butterknife.BindView;
import com.jakewharton.rxbinding2.view.RxView;
import com.obt.qrcode.activity.CaptureActivity;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.constants.Constants;
import io.bcaas.db.vo.AddressVO;
import io.bcaas.listener.AliasRuleEditTextFilter;
import io.bcaas.listener.SoftKeyBroadManager;
import io.bcaas.presenter.InsertAddressPresenterImp;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.ecc.KeyTool;
import io.bcaas.tools.regex.RegexTool;
import io.bcaas.ui.contracts.InsertAddressContract;
import io.reactivex.disposables.Disposable;

import java.util.concurrent.TimeUnit;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 * <p>
 * Activity：「新增地址」
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
    @BindView(R.id.rl_content)
    RelativeLayout rlContent;
    @BindView(R.id.v_space)
    View vSpace;
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
        addSoftKeyBroadManager();
    }


    /**
     * 添加软键盘监听
     */
    private void addSoftKeyBroadManager() {
        softKeyBroadManager = new SoftKeyBroadManager(llInsertAddress, vSpace);
        softKeyBroadManager.addSoftKeyboardStateListener(softKeyboardStateListener);
    }

    @Override
    public void initListener() {
        etAddressName.setFilters(new InputFilter[]{new AliasRuleEditTextFilter()});
        llInsertAddress.setOnTouchListener((v, event) -> {
            hideSoftKeyboard();
            return false;
        });
        rlContent.setOnTouchListener((v, event) -> true);
        Disposable subscribeScan = RxView.clicks(ibScan)
                .throttleFirst(Constants.Time.sleep800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    startActivityForResult(new Intent(context, CaptureActivity.class), 0);
                });
        ibBack.setOnClickListener(v -> setResult(true));
        Disposable subscribeSave = RxView.clicks(btnSave)
                .throttleFirst(Constants.Time.sleep800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    hideSoftKeyboard();
                    String alias = etAddressName.getText().toString();
                    String address = RegexTool.replaceBlank(etAddress.getText().toString());
                    AddressVO addressVOBean = new AddressVO();
                    addressVOBean.setAddressName(alias);
                    addressVOBean.setAddress(address);
                    if (StringTool.isEmpty(alias)) {
                        showToast(getResources().getString(R.string.please_enter_address_name));
                        return;
                    } else if (StringTool.isEmpty(address)) {
                        showToast(getResources().getString(R.string.please_enter_receive_account));
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

    private void setResult(boolean isBack) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.KeyMaps.From, isBack);
        intent.putExtras(bundle);
        this.setResult(RESULT_OK, intent);
        this.finish();
    }

    @Override
    public void saveDataSuccess() {
        setResult(false);
    }

    @Override
    public void saveDataFailure() {
        showToast(getString(R.string.save_data_failure));

    }

    @Override
    public void addressRepeat() {
        showToast(getResources().getString(R.string.address_repeat));
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
