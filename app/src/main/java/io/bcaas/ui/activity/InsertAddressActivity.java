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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.obt.qrcode.activity.CaptureActivity;

import butterknife.BindView;
import io.bcaas.BuildConfig;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.db.vo.Address;
import io.bcaas.event.NotifyAddressData;
import io.bcaas.presenter.InsertAddressPresenterImp;
import io.bcaas.tools.OttoTool;
import io.bcaas.tools.StringTool;
import io.bcaas.ui.contracts.InsertAddressContract;

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
    @BindView(R.id.ib_close)
    ImageButton ibClose;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ib_right)
    ImageButton ibRight;
    @BindView(R.id.rlHeader)
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
    private InsertAddressContract.Presenter presenter;

    @Override
    public int getContentView() {
        return R.layout.aty_insert_address;
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
        tvTitle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (BuildConfig.DEBUG) {
                    startActivityForResult(new Intent(context, CaptureActivity.class), 0);

                }
                return false;
            }
        });
        etAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String address = s.toString();
                String addressName = etAddressName.getText().toString();
                boolean hasPressed = StringTool.notEmpty(address) && StringTool.notEmpty(addressName);
                btnSave.setPressed(hasPressed);
            }
        });
        etAddressName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String addressName = s.toString();
                String address = etAddress.getText().toString();
                boolean hasPressed = StringTool.notEmpty(address) && StringTool.notEmpty(addressName);
                btnSave.setPressed(hasPressed);


            }
        });
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String alias = etAddressName.getText().toString();
                String address = etAddress.getText().toString();
                Address addressBean = new Address();
                addressBean.setAddressName(alias);
                addressBean.setAddress(address);
                if (StringTool.isEmpty(alias) || StringTool.isEmpty(address)) {
                    showToast("请输入地址的相关信息。");
                    return;
                } else {
                    //TODO 保存时需要查看账户名称
                    presenter.saveData(addressBean);
                }
            }
        });

    }

    @Override
    public void saveDataSuccess() {
        OttoTool.getInstance().post(new NotifyAddressData(true));
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
            if (data == null) return;
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String result = bundle.getString(Constants.RESULT);
                etAddress.setText(result);
            }
        }
    }
}
