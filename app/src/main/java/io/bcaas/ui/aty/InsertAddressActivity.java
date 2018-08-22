package io.bcaas.ui.aty;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.database.Address;
import io.bcaas.event.NotifyAddressData;
import io.bcaas.presenter.InsertAddressPresenterImp;
import io.bcaas.ui.contracts.InsertAddressContract;
import io.bcaas.utils.OttoU;
import io.bcaas.utils.StringU;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 * <p>
 * 新增地址
 */
public class InsertAddressActivity extends BaseActivity
        implements InsertAddressContract.View {
    @BindView(R.id.ibBack)
    ImageButton ibBack;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ibRight)
    ImageButton ibRight;
    @BindView(R.id.etAddressName)
    EditText etAddressName;
    @BindView(R.id.etAddress)
    EditText etAddress;
    @BindView(R.id.btnSave)
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
                boolean hasPressed = StringU.notEmpty(address) && StringU.notEmpty(addressName);
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
                boolean hasPressed = StringU.notEmpty(address) && StringU.notEmpty(addressName);
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
                addressBean.setAlias(alias);
                addressBean.setAddress(address);
                if (StringU.isEmpty(alias) || StringU.isEmpty(address)) {
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
        OttoU.getInstance().post(new NotifyAddressData(true));
        finish();
    }

    @Override
    public void saveDataFailure() {
        showToast(getString(R.string.save_data_failure));

    }

}
