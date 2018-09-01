package io.bcaas.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
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
import io.bcaas.tools.BcaasLog;
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
    @BindView(R.id.tv_select_address)
    TextView tvSelectAddress;
    @BindView(R.id.et_input_destination_address)
    EditText etInputDestinationAddress;
    @BindView(R.id.v_line_2)
    View vLine2;
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
    @BindView(R.id.sv_send)
    ScrollView scrollView;
    private List<Address> addresses;//得到当前所有的地址
    private Address currentAddress;//得到当前选中的address


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
        addresses = new ArrayList<>();
        tvMyAccountAddressValue.setText(BcaasApplication.getWalletAddress());
        setBalance(BcaasApplication.getStringFromSP(Constants.Preference.WALLET_BALANCE));
        getAddress();
        initData();

    }

    private void initData() {
        if (ListTool.noEmpty(getCurrency())) {
            tvCurrency.setText(getCurrency().get(0));
        }
        if (ListTool.noEmpty(addresses)) {
            Address address = addresses.get(0);
            if (address != null) {
                etInputDestinationAddress.setText(addresses.get(0).getAddress());
            }

        }
    }

    private void getAddress() {
        //解析从数据库得到的存储地址，然后重组为adapter需要的数据
        addresses = BcaasApplication.bcaasDBHelper.queryAddress();
    }

    /*获取到当前所有钱包的名字*/
    private List<String> getAddressName() {
        getAddress();
        if (ListTool.isEmpty(addresses)) {
            return null;
        }
        List<String> addressName = new ArrayList<>();
        for (Address address : addresses) {
            addressName.add(address.getAddressName());
        }
        return addressName;
    }

    @Override
    public void initListener() {
        scrollView.setOnTouchListener((v, event) -> {
            hideSoftKeyboard();
            return false;
        });
        etInputDestinationAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String address = s.toString();
                String amount = etTransactionAmount.getText().toString();
                if (StringTool.notEmpty(address) && StringTool.notEmpty(amount)) {
                    btnSend.setEnabled(true);
                }

            }
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
                String amount = s.toString();
                String address = etInputDestinationAddress.getText().toString();
                if (StringTool.notEmpty(address) && StringTool.notEmpty(amount)) {
                    btnSend.setEnabled(true);
                }

            }
        });
        Disposable subscribeSeletAddress = RxView.clicks(tvSelectAddress)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (ListTool.isEmpty(getAddressName())) {
                        showToast(getString(R.string.no_address_to_choose));
                        return;
                    }
                    showAddressListPopWindow(onAddressSelectListener, addresses);
                });
        Disposable subscribeSelectCurrency = RxView.clicks(tvCurrency)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    showCurrencyListPopWindow(onCurrencySelectListener, getCurrency());
                });
        Disposable subscribeSend = RxView.clicks(btnSend)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    //将当前页面的数据传输到下一个页面进行失焦显示
                    String amount = etTransactionAmount.getText().toString();
                    String destinationWallet = etInputDestinationAddress.getText().toString();
                    if (StringTool.isEmpty(amount)) {
                        showToast(getResources().getString(R.string.please_input_transaction_amount));
                        return;
                    }
                    if (StringTool.isEmpty(destinationWallet)) {
                        showToast(getResources().getString(R.string.please_input_account_address));
                        return;
                    }
                    etTransactionAmount.setText("");
                    Bundle bundle = new Bundle();
                    BcaasLog.d(TAG, currentAddress);
                    bundle.putString(Constants.KeyMaps.DESTINATION_WALLET, destinationWallet);
                    if (currentAddress != null) {
                        bundle.putString(Constants.KeyMaps.ADDRESS_NAME, currentAddress.getAddressName());
                    }
                    bundle.putString(Constants.KeyMaps.TRANSACTION_AMOUNT, amount);
                    intentToActivity(bundle, SendConfirmationActivity.class, false);
                });
        tvAccountAddressKey.setOnLongClickListener(v -> {
            if (BuildConfig.DEBUG) {
                ((MainActivity) activity).intentToCaptureAty();
            }
            return false;
        });
        tvBalance.setOnLongClickListener(v -> {
            showBalancePop(tvBalance);
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
                // TODO: 2018/9/1 是否應該做一個交易限額；是否可以輸入小數？-Randy 下週定
//                int account = Integer.valueOf(privateKey);

            }
        });
    }

    @Subscribe
    public void updateAddressEvent(UpdateAddressEvent updateAddressEvent) {
        if (updateAddressEvent == null) {
            return;
        }
        String result = updateAddressEvent.getResult();
        etInputDestinationAddress.setText(result);
        currentAddress = null;
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
            if (type instanceof Address) {
                currentAddress = (Address) type;
                etInputDestinationAddress.setText(currentAddress.getAddress());
            }
        }
    };
    private OnItemSelectListener onCurrencySelectListener = new OnItemSelectListener() {
        @Override
        public <T> void onItemSelect(T type) {
            tvCurrency.setText(type.toString());
        }
    };
}
