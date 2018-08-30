package io.bcaas.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.bcaas.BuildConfig;
import io.bcaas.R;
import io.bcaas.base.BaseFragment;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.db.vo.Address;
import io.bcaas.event.UpdateAddressEvent;
import io.bcaas.event.UpdateWalletBalance;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.NumberTool;
import io.bcaas.tools.StringTool;
import io.bcaas.ui.activity.MainActivity;
import io.bcaas.ui.activity.SendConfirmationActivity;
import io.reactivex.disposables.Disposable;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * 「交易发送」一级页面，输入交易的信息
 */
public class SendFragment extends BaseFragment {
    private String TAG = SendFragment.class.getSimpleName();

    @BindView(R.id.btn_select_currency)
    Button btnSelectCurrency;
    @BindView(R.id.tv_transaction_currency)
    TextView tvTransactionCurrency;
    @BindView(R.id.btn_select_transaction_currency)
    Button btnSelectTransactionCurrency;

    @BindView(R.id.tv_address_key)
    TextView tvMyAddressKey;
    @BindView(R.id.tv_balance_key)
    TextView tvBalanceKey;
    @BindView(R.id.v_vertical_line)
    View vVerticalLine;
    @BindView(R.id.tv_currency_key)
    TextView tvCurrencyKey;
    @BindView(R.id.tv_transaction_block_title)
    TextView tvTransactionBlockTitle;
    @BindView(R.id.btn_select_address)
    Button btnSelectAddress;
    @BindView(R.id.et_input_destination_address)
    EditText etInputDestinationAddress;
    @BindView(R.id.v_line_2)
    View vLine2;
    @BindView(R.id.tv_select_currency_key)
    TextView tvSelectCurrencyKey;
    @BindView(R.id.ll_select_currency)
    LinearLayout llSelectCurrency;
    @BindView(R.id.tv_transaction_amount_key)
    TextView tvTransactionAmountKey;
    @BindView(R.id.tv_account_address_value)
    TextView tvMyAccountAddressValue;//我的账户地址显示容器
    @BindView(R.id.tv_balance)
    TextView tvBalance;
    @BindView(R.id.v_line)
    View vLine;
    @BindView(R.id.tv_currency)
    TextView tvCurrency;
    @BindView(R.id.et_transaction_amount)
    EditText etTransactionAmount;//我的交易数额
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.tv_account_address_key)
    TextView tvAccountAddressKey;
    @BindView(R.id.pb_balance)
    ProgressBar progressBar;

    private String destinationWallet;//收款的账户地址
    private String receiveCurrency;//收款的币种

    public static SendFragment newInstance() {
        SendFragment sendFragment = new SendFragment();
        Bundle bundle = new Bundle();
        sendFragment.setArguments(bundle);
        return sendFragment;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.frg_send;
    }

    @Override
    public void getArgs(Bundle bundle) {
        if (bundle == null) {
            return;
        }
    }

    @Override
    public void initViews(View view) {
        tvMyAccountAddressValue.setText(BcaasApplication.getWalletAddress());
        setBalance(BcaasApplication.getWalletBalance());
        initData();

    }

    private void initData() {
        if (ListTool.noEmpty(getCurrency())) {
            tvCurrency.setText(getCurrency().get(0));
            tvTransactionCurrency.setText(getCurrency().get(0));
        }
        if (ListTool.noEmpty(getAddress())) {
            etInputDestinationAddress.setText(getAddress().get(0));

        }
    }

    //解析从数据库得到的存储地址，然后重组为adapter需要的数据
    private List<String> getAddress() {
        List<String> addresses = new ArrayList<>();
        List<Address> addressList = BcaasApplication.bcaasDBHelper.queryAddress();
        for (Address address : addressList) {
            addresses.add(address.getAddress());
        }
        return addresses;
    }

    @Override
    public void initListener() {
        Disposable subscribeSeletAddress = RxView.clicks(btnSelectAddress)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    showListPopWindow(onAddressSelectListener, getAddress());
                });
        Disposable subscribeSelectCurrency = RxView.clicks(btnSelectCurrency)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    showListPopWindow(onCurrencySelectListener, getCurrency());
                });
        Disposable subscribeSelectTransactionCurrency = RxView.clicks(btnSelectTransactionCurrency)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    showListPopWindow(onTransactionCurrencySelectListener, getCurrency());
                });
        Disposable subscribeSend = RxView.clicks(btnSend)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    //将当前页面的数据传输到下一个页面进行失焦显示
                    String amount = etTransactionAmount.getText().toString();
                    if (StringTool.isEmpty(amount)) {
                        showToast(getResources().getString(R.string.please_input_transaction_amount));
                        return;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.KeyMaps.DESTINATION_WALLET, destinationWallet);
                    bundle.putString(Constants.KeyMaps.RECEIVE_CURRENCY, receiveCurrency);
                    bundle.putString(Constants.KeyMaps.TRANSACTION_AMOUNT, amount);
                    intentToActivity(bundle, SendConfirmationActivity.class, false);
                });
        tvAccountAddressKey.setOnLongClickListener(v -> {
            if (BuildConfig.DEBUG) {
                ((MainActivity) activity).intentToCaptureAty();
            }
            return false;
        });
        etTransactionAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //监听当前的输入，如果输入的数额大于当前的余额，提示余额不足？
                String privateKey = s.toString();
                if (StringTool.isEmpty(privateKey)) {
                    return;
                }
                int account = Integer.valueOf(privateKey);

            }
        });
    }

    @Subscribe
    public void updateAddressEvent(UpdateAddressEvent updateAddressEvent) {
        if (updateAddressEvent == null) {
            return;
        }
        String result = updateAddressEvent.getResult();
        destinationWallet = result;
        etInputDestinationAddress.setText(destinationWallet);
    }

    @Subscribe
    public void UpdateWalletBalance(UpdateWalletBalance updateWalletBalance) {
        if (updateWalletBalance == null) {
            return;
        }
        String walletBalance = updateWalletBalance.getWalletBalance();
        setBalance(walletBalance);
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

    private OnItemSelectListener onAddressSelectListener = new OnItemSelectListener() {
        @Override
        public <T> void onItemSelect(T type) {
            etInputDestinationAddress.setText(type.toString());
        }
    };
    private OnItemSelectListener onCurrencySelectListener = new OnItemSelectListener() {
        @Override
        public <T> void onItemSelect(T type) {
            tvCurrency.setText(type.toString());
        }
    };
    private OnItemSelectListener onTransactionCurrencySelectListener = new OnItemSelectListener() {
        @Override
        public <T> void onItemSelect(T type) {
            receiveCurrency = type.toString();
            tvTransactionCurrency.setText(receiveCurrency);
        }
    };

}
