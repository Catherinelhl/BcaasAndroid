package io.bcaas.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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
import io.bcaas.db.vo.AddressVO;
import io.bcaas.event.UpdateBlockServiceEvent;
import io.bcaas.listener.SoftKeyBroadManager;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.ecc.KeyTool;
import io.bcaas.event.UpdateAddressEvent;
import io.bcaas.event.UpdateWalletBalanceEvent;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.NumberTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.ecc.WalletTool;
import io.bcaas.ui.activity.MainActivity;
import io.bcaas.ui.activity.SendConfirmationActivity;
import io.bcaas.vo.PublicUnitVO;
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
    @BindView(R.id.v_space)
    View vSpace;
    @BindView(R.id.ll_send)
    LinearLayout llSend;
    private List<AddressVO> addressVOS;//得到当前所有的地址
    private AddressVO currentAddressVO;//得到当前选中的address
    private List<PublicUnitVO> publicUnitVOS;

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
        if (bundle == null) {
            return;
        }
    }

    @Override
    public void initViews(View view) {
        publicUnitVOS = new ArrayList<>();
        addressVOS = new ArrayList<>();
        tvMyAccountAddressValue.setText(BcaasApplication.getWalletAddress());
        setBalance(BcaasApplication.getWalletBalance());
        getAddress();
        setAddresses();
        setCurrency();
        addSoftKeyBroadManager();

    }

    /**
     * 添加软键盘监听
     */
    private void addSoftKeyBroadManager() {
        softKeyBroadManager = new SoftKeyBroadManager(llSend, vSpace);
        softKeyBroadManager.addSoftKeyboardStateListener(softKeyboardStateListener);
    }

    /*显示默认的账户地址*/
    private void setAddresses() {
        if (ListTool.noEmpty(addressVOS)) {
            AddressVO addressVO = addressVOS.get(0);
            if (addressVO != null) {
                etInputDestinationAddress.setText(addressVOS.get(0).getAddress());
            }

        }
    }

    /*显示默认币种*/
    private void setCurrency() {
        publicUnitVOS = BcaasApplication.getPublicUnitVO();
        //1:检测历史选中币种，如果没有，默认显示币种的第一条数据
        String blockService = BcaasApplication.getBlockService();
        if (ListTool.noEmpty(publicUnitVOS)) {
            if (StringTool.isEmpty(blockService)) {
                tvCurrency.setText(publicUnitVOS.get(0).getBlockService());
            } else {
                //2:是否应该去比对获取的到币种是否关闭，否则重新赋值
                String isStartUp = Constants.BlockService.CLOSE;
                for (PublicUnitVO publicUnitVO : publicUnitVOS) {
                    if (StringTool.equals(blockService, publicUnitVO.getBlockService())) {
                        isStartUp = publicUnitVO.isStartup();
                        break;
                    }
                }
                if (StringTool.equals(isStartUp, Constants.BlockService.OPEN)) {
                    tvCurrency.setText(blockService);
                } else {
                    tvCurrency.setText(publicUnitVOS.get(0).getBlockService());

                }
            }
        } else {
            tvCurrency.setText(Constants.BLOCKSERVICE_BCC);
        }

    }

    private void getAddress() {
        //解析从数据库得到的存储地址，然后重组为adapter需要的数据
        addressVOS = BcaasApplication.bcaasDBHelper.queryAddress();
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
                        showToast(getString(R.string.no_account_address_to_choose_from));
                        return;
                    }
                    showAddressListPopWindow(onAddressSelectListener, addressVOS);
                });
        Disposable subscribeSelectCurrency = RxView.clicks(tvCurrency)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (ListTool.isEmpty(publicUnitVOS)) {
                        publicUnitVOS.add(WalletTool.getDefaultBlockService());
                    }
                    showCurrencyListPopWindow(onCurrencySelectListener, publicUnitVOS);
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
                        showToast(getResources().getString(R.string.the_address_of_receiving_account_is_empty));
                        return;
                    }
                    /*检测当前地址格式*/
                    if (!KeyTool.validateBitcoinAddress(destinationWallet)) {
                        showToast(getResources().getString(R.string.address_format_error));
                        return;
                    }
                    etTransactionAmount.setText("");
                    Bundle bundle = new Bundle();
                    LogTool.d(TAG, currentAddressVO);
                    bundle.putString(Constants.KeyMaps.DESTINATION_WALLET, destinationWallet);
                    if (currentAddressVO != null) {
                        bundle.putString(Constants.KeyMaps.ADDRESS_NAME, currentAddressVO.getAddressName());
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
        currentAddressVO = null;
    }

    @Subscribe
    public void UpdateWalletBalance(UpdateWalletBalanceEvent updateWalletBalanceEvent) {
        if (updateWalletBalanceEvent == null) {
            return;
        }
        String walletBalance = updateWalletBalanceEvent.getWalletBalance();
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

    private OnItemSelectListener onAddressSelectListener = new OnItemSelectListener() {
        @Override
        public <T> void onItemSelect(T type) {
            if (type instanceof AddressVO) {
                currentAddressVO = (AddressVO) type;
                etInputDestinationAddress.setText(currentAddressVO.getAddress());
            }
        }
    };
    private OnItemSelectListener onCurrencySelectListener = new OnItemSelectListener() {
        @Override
        public <T> void onItemSelect(T type) {
            /*显示币种*/
            tvCurrency.setText(type.toString());
            /*存储币种*/
            BcaasApplication.setBlockService(type.toString());
            /*重新verify，获取新的区块数据*/
            if (activity != null) {
                ((MainActivity) activity).verify();
            }

            /*重置余额*/
            BcaasApplication.setWalletBalance("");
            tvBalance.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    };

    @Subscribe
    public void updateBlockService(UpdateBlockServiceEvent updateBlockServiceEvent) {
        if (activity != null && tvCurrency != null) {
            tvCurrency.setText(BcaasApplication.getBlockService());

        }
    }
}
