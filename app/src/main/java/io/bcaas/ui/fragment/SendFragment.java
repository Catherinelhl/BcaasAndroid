package io.bcaas.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.base.BaseFragment;
import io.bcaas.base.BCAASApplication;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.db.vo.AddressVO;
import io.bcaas.event.RefreshAddressEvent;
import io.bcaas.event.RefreshBlockServiceEvent;
import io.bcaas.event.RefreshWalletBalanceEvent;
import io.bcaas.http.tcp.TCPThread;
import io.bcaas.listener.AmountEditTextFilter;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.listener.SoftKeyBroadManager;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.TextTool;
import io.bcaas.tools.decimal.DecimalTool;
import io.bcaas.tools.ecc.KeyTool;
import io.bcaas.tools.ecc.WalletTool;
import io.bcaas.tools.regex.RegexTool;
import io.bcaas.ui.activity.MainActivity;
import io.bcaas.ui.activity.SendConfirmationActivity;
import io.bcaas.view.BcaasBalanceTextView;
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
    @BindView(R.id.rl_currency)
    RelativeLayout rlCurrency;
    @BindView(R.id.ll_amount_info)
    LinearLayout llAmountInfo;
    @BindView(R.id.rl_transaction_info)
    RelativeLayout rlTransactionInfo;
    @BindView(R.id.tv_address_key)
    TextView tvMyAddressKey;
    @BindView(R.id.tv_balance_key)
    TextView tvBalanceKey;
    @BindView(R.id.tv_transaction_block_title)
    TextView tvTransactionBlockTitle;
    @BindView(R.id.ib_select_address)
    ImageButton ibSelectAddress;
    @BindView(R.id.ib_scan_address)
    ImageButton ibScanAddress;
    @BindView(R.id.et_input_destination_address)
    EditText etInputDestinationAddress;
    @BindView(R.id.v_line_2)
    View vLine2;
    @BindView(R.id.tv_transaction_amount_key)
    TextView tvTransactionAmountKey;
    //我的账户地址显示容器
    @BindView(R.id.tv_account_address_value)
    TextView tvMyAccountAddressValue;
    @BindView(R.id.bbt_balance)
    BcaasBalanceTextView bbtBalance;
    @BindView(R.id.v_line)
    View vLine;
    @BindView(R.id.tv_currency)
    TextView tvCurrency;
    //我的交易数额
    @BindView(R.id.et_transaction_amount)
    EditText etTransactionAmount;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.tv_account_address_key)
    TextView tvAccountAddressKey;
    @BindView(R.id.pb_balance)
    ProgressBar progressBar;
    @BindView(R.id.sv_send)
    ScrollView scrollView;
    @BindView(R.id.v_space)
    View vSpace;
    @BindView(R.id.tv_amount_hint)
    TextView tvAmountHint;
    @BindView(R.id.ll_send)
    LinearLayout llSend;
    //得到当前所有的地址
    private List<AddressVO> addressVOS;
    //得到当前选中的address
    private AddressVO currentAddressVO;

    public static SendFragment newInstance() {
        SendFragment sendFragment = new SendFragment();
        Bundle bundle = new Bundle();
        sendFragment.setArguments(bundle);
        return sendFragment;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_send;
    }

    @Override
    public void getArgs(Bundle bundle) {
    }

    @Override
    public void initViews(View view) {
        addressVOS = new ArrayList<>();
        //获取当前text view占用的布局
        int widthExceptMargin = (BCAASApplication.getScreenWidth() - getResources().getDimensionPixelOffset(R.dimen.d42));
        LogTool.d(TAG, widthExceptMargin);
        double weightWidth = widthExceptMargin / 3.4;
        LogTool.d(TAG, weightWidth);
        double contentWidth = widthExceptMargin - weightWidth;
        LogTool.d(TAG, contentWidth);
        double width = contentWidth - getResources().getDimensionPixelOffset(R.dimen.d16);
        tvMyAccountAddressValue.setText(
                TextTool.intelligentOmissionText(
                        tvMyAccountAddressValue, (int) width,
                        BCAASApplication.getWalletAddress()));
        setBalance(BCAASApplication.getWalletBalance());
        getAddress();
        setCurrency();
        addSoftKeyBroadManager();
        etTransactionAmount.setFilters(new InputFilter[]{new AmountEditTextFilter().setDigits(8)});
    }

    /*设置输入框的hint的大小而不影响text size*/
    private void setEditHintTextSize() {
        SpannableString spannableString = new SpannableString(getResources().getString(R.string.please_enter_transaction_amount));//定义hint的值
        AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(14, true);//设置字体大小 true表示单位是sp
        spannableString.setSpan(absoluteSizeSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        etTransactionAmount.setHint(new SpannedString(spannableString));
    }

    /**
     * 添加软键盘监听
     */
    private void addSoftKeyBroadManager() {
        softKeyBroadManager = new SoftKeyBroadManager(llSend, vSpace);
        softKeyBroadManager.addSoftKeyboardStateListener(softKeyboardStateListener);
    }

    /*显示默认币种*/
    private void setCurrency() {
        tvCurrency.setText(WalletTool.getDisplayBlockService());
    }

    private void getAddress() {
        //解析从数据库得到的存储地址，然后重组为adapter需要的数据
        addressVOS = BCAASApplication.bcaasDBHelper.queryAddress();
    }

    /*获取到当前所有钱包的名字*/
    private List<String> getAddressName() {
        getAddress();
        if (ListTool.isEmpty(addressVOS)) {
            return null;
        }
        List<String> addressName = new ArrayList<>();
        for (AddressVO addressVO : addressVOS) {
            addressName.add(addressVO.getAddressName());
        }
        return addressName;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initListener() {
        etTransactionAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tvAmountHint.setVisibility(StringTool.notEmpty(s.toString()) ? View.INVISIBLE : View.VISIBLE);
            }
        });
        scrollView.setOnTouchListener((v, event) -> {
            hideSoftKeyboard();
            return false;
        });
        llAmountInfo.setOnTouchListener((v, event) -> true);
        rlTransactionInfo.setOnTouchListener((v, event) -> true);
        Disposable subscribeSeletAddress = RxView.clicks(ibSelectAddress)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (ListTool.isEmpty(getAddressName())) {
                        showToast(getString(R.string.no_account_address_to_choose_from));
                        return;
                    }
                    hideSoftKeyboard();
                    showAddressListPopWindow(onAddressSelectListener, addressVOS);
                });
        Disposable subscribeScanAddress = RxView.clicks(ibScanAddress)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    hideSoftKeyboard();
                    ((MainActivity) activity).intentToCaptureActivity();
                });
        Disposable subscribeSelectCurrency = RxView.clicks(rlCurrency)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    hideSoftKeyboard();
                    showCurrencyListPopWindow(onCurrencySelectListener);
                });
        Disposable subscribeSend = RxView.clicks(btnSend)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    hideSoftKeyboard();
                    /*点击发送，本地做一些网络请求前的规范判断*/
                    String amount = etTransactionAmount.getText().toString();
                    /*去掉地址里面的空格清空，以防在验证地址格式的时候，报异常情况*/
                    String destinationWallet = RegexTool.replaceBlank(etInputDestinationAddress.getText().toString());
                    /*1：检测当前地址长度*/
                    if (StringTool.isEmpty(destinationWallet)) {
                        showToast(getResources().getString(R.string.the_address_of_receiving_account_is_empty));
                        return;
                    }
                    /*2：检测当前地址是否有效*/
                    if (!KeyTool.validateBitcoinAddress(destinationWallet)) {
                        showToast(getResources().getString(R.string.address_format_error));
                        return;
                    }
                    /*3：检测当前输入交易地址是否是自己*/
                    if (StringTool.equals(destinationWallet, BCAASApplication.getWalletAddress())) {
                        showToast(getResources().getString(R.string.sending_wallet_same_as_receiving_wallet));
                        return;
                    }
                    /*4：检测交易数额长度*/
                    if (StringTool.isEmpty(amount)) {
                        showToast(getResources().getString(R.string.please_enter_transaction_amount));
                        return;
                    }
                    /*5：判断余额是否获取成功*/
                    String balance = BCAASApplication.getWalletBalance();
                    if (StringTool.isEmpty(balance)) {
                        showToast(getResources().getString(R.string.unable_to_trade_at_present));
                        return;
                    }
                    /*6：判断余额是否>0*/
                    if (StringTool.equals(balance, "0")) {
                        showToast(getResources().getString(R.string.insufficient_balance));
                        return;
                    }
                    /*7：判断余额是否足够发送*/
                    if (StringTool.equals(DecimalTool.calculateFirstSubtractSecondValue(balance, amount), MessageConstants.NO_ENOUGH_BALANCE)) {
                        showToast(getResources().getString(R.string.insufficient_balance));
                        return;
                    }
                    etTransactionAmount.setText("");
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.KeyMaps.DESTINATION_WALLET, destinationWallet);
                    if (currentAddressVO != null) {
                        bundle.putString(Constants.KeyMaps.ADDRESS_NAME, currentAddressVO.getAddressName());
                    }
                    bundle.putString(Constants.KeyMaps.TRANSACTION_AMOUNT, amount);
                    intentToActivity(bundle, SendConfirmationActivity.class, false);
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
            }
        });
    }

    @Subscribe
    public void updateAddressEvent(RefreshAddressEvent updateAddressEvent) {
        if (updateAddressEvent == null) {
            return;
        }
        String result = updateAddressEvent.getResult();
        etInputDestinationAddress.setText(RegexTool.replaceBlank(result));
        currentAddressVO = null;
    }

    @Subscribe
    public void UpdateWalletBalance(RefreshWalletBalanceEvent updateWalletBalanceEvent) {
        if (updateWalletBalanceEvent == null) {
            return;
        }
        setBalance(BCAASApplication.getWalletBalance());
    }

    //对当前的余额进行赋值，如果当前没有读取到数据，那么就显示进度条，否则显示余额
    private void setBalance(String balance) {
        if (StringTool.isEmpty(balance)) {
            //隐藏显示余额的文本，展示进度条
            bbtBalance.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            bbtBalance.setVisibility(View.VISIBLE);
            bbtBalance.setBalance(balance);
        }
    }

    private OnItemSelectListener onAddressSelectListener = new OnItemSelectListener() {
        @Override
        public <T> void onItemSelect(T type, String from) {
            if (type instanceof AddressVO) {
                currentAddressVO = (AddressVO) type;
                String address = currentAddressVO.getAddress();
                if (StringTool.notEmpty(address)) {
                    etInputDestinationAddress.setText(currentAddressVO.getAddress());
                }

            }
        }

        @Override
        public void changeItem(boolean isChange) {

        }
    };
    private OnItemSelectListener onCurrencySelectListener = new OnItemSelectListener() {
        @Override
        public <T> void onItemSelect(T type, String from) {
            /*显示币种*/
            tvCurrency.setText(type.toString());
            /*存储币种*/
            BCAASApplication.setBlockService(type.toString());
            /*重新verify，获取新的区块数据*/
            if (activity != null) {
                ((MainActivity) activity).verify();
            }
            TCPThread.setActiveDisconnect(true);
            /*重置余额*/
            BCAASApplication.resetWalletBalance();
            bbtBalance.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void changeItem(boolean isChange) {

        }
    };

    @Subscribe
    public void updateBlockService(RefreshBlockServiceEvent updateBlockServiceEvent) {
        if (activity != null) {
            if (tvCurrency != null) {
                tvCurrency.setText(BCAASApplication.getBlockService());
            }
            /*不为用户保留默认地址*/
            if (etInputDestinationAddress != null) {
                etInputDestinationAddress.setText("");
            }
        }
    }

}
