package io.bcaas.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.bcaas.BuildConfig;
import io.bcaas.R;
import io.bcaas.base.BaseFragment;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.db.vo.Address;
import io.bcaas.event.UpdateAddressEvent;
import io.bcaas.event.UpdateWalletBalance;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.StringTool;
import io.bcaas.ui.activity.MainActivity;
import io.bcaas.ui.activity.SendConfirmationActivity;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * 「交易发送」一级页面，输入交易的信息
 */
public class SendFragment extends BaseFragment {
    private String TAG = SendFragment.class.getSimpleName();

    @BindView(R.id.tvMyAddressKey)
    TextView tvMyAddressKey;
    @BindView(R.id.tvBalanceKey)
    TextView tvBalanceKey;
    @BindView(R.id.v_vertical_line)
    View vVerticalLine;
    @BindView(R.id.tvCurrencyKey)
    TextView tvCurrencyKey;
    @BindView(R.id.tvTransactionBlockTitle)
    TextView tvTransactionBlockTitle;
    @BindView(R.id.btn_select_address)
    Button btnSelectAddress;
    @BindView(R.id.et_input_destination_address)
    EditText etInputDestinationAddress;
    @BindView(R.id.v_line_2)
    View vLine2;
    @BindView(R.id.tvSelectCurrencyKey)
    TextView tvSelectCurrencyKey;
    @BindView(R.id.llSelectCurrency)
    LinearLayout llSelectCurrency;
    @BindView(R.id.tvTransactionAmountKey)
    TextView tvTransactionAmountKey;
    @BindView(R.id.tvMyAccountAddressValue)
    TextView tvMyAccountAddressValue;//我的账户地址显示容器
    @BindView(R.id.tvBalance)
    TextView tvBalance;
    @BindView(R.id.sp_select)
    Spinner spSelect;//选择当前查询显示的币种
    @BindView(R.id.v_line)
    View vLine;
    @BindView(R.id.sp_select_account_address)
    Spinner spSelectAccountAddress;//选择收款账户地址
    @BindView(R.id.spSelectReceiveCurrency)
    Spinner spSelectReceiveCurrency;//选择交易发送的币种
    @BindView(R.id.etTransactionAmount)
    EditText etTransactionAmount;//我的交易数额
    @BindView(R.id.btnSend)
    Button btnSend;
    @BindView(R.id.tv_account_address_key)
    TextView tvAccountAddressKey;


    private ArrayAdapter currencyAdapter;//声明用于填充币种的适配
    private ArrayAdapter allAccountAddressAdapter;//声明用于填充所有可选账户的地址

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
        if (bundle == null) return;
    }

    @Override
    public void initViews(View view) {
        tvMyAccountAddressValue.setText(BcaasApplication.getWalletAddress());
        initData();
        tvBalance.setText(BcaasApplication.getWalletBalance());

    }

    private void initData() {
        initSelectDisplaySpinnerAdapter();
        initReceiveAccountAddressSpinnerAdapter();

    }

    /**
     * 初始化选择显示当前想要发送的账户的数据；这里有三种方式
     * ：1：可以手动输入；
     * 2：通过扫描对方的code；
     * 3：通过选择自己本地的交易过的账户列表
     */
    private void initReceiveAccountAddressSpinnerAdapter() {

        //将可选内容与ArrayAdapter连接起来
        allAccountAddressAdapter = new ArrayAdapter<>(this.context, R.layout.spinner_item, getAddress());
        //设置下拉列表的风格
        allAccountAddressAdapter.setDropDownViewResource(R.layout.dropdown_style);
        //将adapter 添加到spinner中
        spSelectAccountAddress.setAdapter(allAccountAddressAdapter);
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

    /*初始化选择显示当前币种的数据*/
    private void initSelectDisplaySpinnerAdapter() {
        //将可选内容与ArrayAdapter连接起来
        currencyAdapter = new ArrayAdapter<>(this.context, R.layout.spinner_item, getCurrency());
        //设置下拉列表的风格
        currencyAdapter.setDropDownViewResource(R.layout.dropdown_style);
        //将adapter 添加到spinner中
        spSelect.setAdapter(currencyAdapter);
        spSelectReceiveCurrency.setAdapter(currencyAdapter);
    }


    @Override
    public void initListener() {
        btnSelectAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spSelectAccountAddress.performClick();
            }
        });
        tvAccountAddressKey.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (BuildConfig.DEBUG) {
                    ((MainActivity) activity).intentToCaptureAty();
                }
                return false;
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
                //监听当前的输入，如果输入的数额大于当前的余额，提示余额不足？
                String privateKeuy = s.toString();
                if (StringTool.isEmpty(privateKeuy)) return;
                int account = Integer.valueOf(privateKeuy);

            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
        spSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //TODO  餘額顯示保留6個精度，如果當前顯示不下文本的長度，長按文本彈出浮窗進行顯示
//                tvBalance.setText(getAllTransactionData().get(position).getBalance());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spSelectReceiveCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //存储当前选中的用于交易的币种信息
                receiveCurrency = String.valueOf(currencyAdapter.getItem(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spSelectAccountAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                destinationWallet = String.valueOf(allAccountAddressAdapter.getItem(position));
                etInputDestinationAddress.setText(destinationWallet);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Subscribe
    public void updateAddressEvent(UpdateAddressEvent updateAddressEvent) {
        if (updateAddressEvent == null) return;
        String result = updateAddressEvent.getResult();
        destinationWallet = result;
        etInputDestinationAddress.setText(destinationWallet);
    }

    @Subscribe
    public void UpdateWalletBalance(UpdateWalletBalance updateWalletBalance) {
        if (updateWalletBalance == null) return;
        String walletBalance = updateWalletBalance.getWalletBalance();
        tvBalance.setText(walletBalance);
    }

}
