package io.bcaas.ui.activity;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BcaasApplication;
import io.bcaas.bean.TransactionsBean;
import io.bcaas.constants.Constants;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.NumberTool;
import io.bcaas.tools.StringTool;
import io.bcaas.view.LineEditText;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 * <p>
 * [设置] -> [钱包信息] -> 检查当前的钱包信息
 */
public class CheckWalletInfoActivity extends BaseActivity {

    private String TAG = CheckWalletInfoActivity.class.getSimpleName();
    @BindView(R.id.ib_back)
    ImageButton ibBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ib_right)
    ImageButton ibRight;
    @BindView(R.id.rl_header)
    RelativeLayout rlHeader;
    @BindView(R.id.tv_address_key)
    TextView tvMyAddressKey;
    @BindView(R.id.ib_copy)
    ImageButton ibCopy;
    @BindView(R.id.tv_account_address_value)
    TextView tvMyAccountAddressValue;
    @BindView(R.id.tv_currency_key)
    TextView tvCurrencyKey;
    @BindView(R.id.sp_select)
    Spinner spSelect;
    @BindView(R.id.tv_balance_key)
    TextView tvBalanceKey;
    @BindView(R.id.tv_balance)
    TextView tvBalance;
    @BindView(R.id.let_private_key)
    LineEditText letPrivateKey;
    @BindView(R.id.cbPwd)
    CheckBox cbPwd;
    @BindView(R.id.rl_private_key)
    RelativeLayout rlPrivateKey;
    @BindView(R.id.btnSendEmail)
    Button btnSendEmail;
    @BindView(R.id.pb_balance)
    ProgressBar progressBar;
    private List<String> currency;
    private List<TransactionsBean> allTransactionData;
    private ArrayAdapter adapter;

    @Override
    public int getContentView() {
        return R.layout.aty_check_wallet_info;
    }

    @Override
    public void getArgs(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        String currencyStr = bundle.getString(Constants.KeyMaps.CURRENCY);
        String allCurrencyStr = bundle.getString(Constants.KeyMaps.ALL_CURRENCY);
        Gson gson = new Gson();
        if (StringTool.notEmpty(currencyStr)) {
            currency = gson.fromJson(currencyStr, new TypeToken<List<String>>() {
            }.getType());
        }
        if (StringTool.notEmpty(allCurrencyStr)) {
            allTransactionData = gson.fromJson(allCurrencyStr, new TypeToken<List<TransactionsBean>>() {
            }.getType());
        }

    }

    @Override
    public void initViews() {
        setTitle();
        ibBack.setVisibility(View.VISIBLE);
        tvMyAccountAddressValue.setEnabled(false);
        letPrivateKey.setEnabled(false);
        tvMyAccountAddressValue.setText(BcaasApplication.getWalletAddress());
        letPrivateKey.setText(BcaasApplication.getPrivateKeyFromSP());
        initSpinnerAdapter();
        BcaasLog.d(TAG, BcaasApplication.getWalletBalance());
        setBalance(BcaasApplication.getWalletBalance());
    }

    //对当前的余额进行赋值，如果当前没有读取到数据，那么就显示进度条，否则显示余额
    private void setBalance(String balance) {
        if (StringTool.isEmpty(balance)) {
            //隐藏显示余额的文本，展示进度条
            tvBalance.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            tvBalance.setVisibility(View.VISIBLE);
            tvBalance.setText(NumberTool.getBalance(balance));
        }
    }


    private void setTitle() {
        tvTitle.setText(R.string.wallet_info);
        tvTitle.setTextColor(getResources().getColor(R.color.black));
        tvTitle.setBackgroundColor(getResources().getColor(R.color.transparent));

    }

    private void initSpinnerAdapter() {
        //将可选内容与ArrayAdapter连接起来
        adapter = new ArrayAdapter<>(this, R.layout.spinner_item, currency);
        //设置下拉列表的风格
        adapter.setDropDownViewResource(R.layout.dropdown_style);
        //将adapter 添加到spinner中
        spSelect.setAdapter(adapter);
    }

    @Override
    public void initListener() {
        cbPwd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            letPrivateKey.setInputType(isChecked ?
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);//设置当前私钥显示不可见
        });
        ibBack.setOnClickListener(v -> finish());
        //添加事件Spinner事件监听
        spSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                tv.setText(String.valueOf(adapter.getItem(position)));
                if (allTransactionData == null) {
                    return;
                }
//                tvBalance.setText(BcaasApplication.getWalletBalance());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnSendEmail.setOnClickListener(v -> {
            //TODO  这里应该有一个请求网络的操作,当结果返回的时候，是否会关闭当前页面，暂时关闭当前页面
            finish();
        });

    }
}
