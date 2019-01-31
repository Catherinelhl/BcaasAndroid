package io.bcaas.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.obt.qrcode.activity.CaptureActivity;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.base.BCAASApplication;
import io.bcaas.base.BaseActivity;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.db.vo.AddressVO;
import io.bcaas.event.RefreshWalletBalanceEvent;
import io.bcaas.listener.AmountEditTextFilter;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.listener.SoftKeyBroadManager;
import io.bcaas.tools.DensityTool;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.TextTool;
import io.bcaas.tools.decimal.DecimalTool;
import io.bcaas.tools.ecc.KeyTool;
import io.bcaas.tools.regex.RegexTool;
import io.bcaas.view.guide.GuideView;
import io.bcaas.view.textview.BcaasBalanceTextView;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * Activity：「交易发送」二级页面，输入交易的信息
 */
public class SendInfoFillInActivity extends BaseActivity {
    @BindView(R.id.ib_back)
    ImageButton ibBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ib_right)
    ImageButton ibRight;
    @BindView(R.id.rl_header)
    RelativeLayout rlHeader;
    //我的账户地址显示容器
    @BindView(R.id.tv_account_address_value)
    TextView tvAccountAddressValue;
    @BindView(R.id.rl_address)
    RelativeLayout rlAddress;
    @BindView(R.id.et_transaction_amount)
    EditText etTransactionAmount;
    private String TAG = SendInfoFillInActivity.class.getSimpleName();
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
    @BindView(R.id.bbt_balance)
    BcaasBalanceTextView bbtBalance;
    @BindView(R.id.v_line)
    View vLine;
    @BindView(R.id.tv_currency)
    TextView tvCurrency;
    //我的交易数额
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
    //得到当前所有的地址
    private List<AddressVO> addressVOS;
    //得到当前选中的address
    private AddressVO currentAddressVO;

    private GuideView guideViewSelectAddress;
    private GuideView guideViewScanAddress;

    private String destinationAddress;

    @Override
    public boolean full() {
        return false;
    }

    @Override
    public int getContentView() {
        return R.layout.activity_send_fill_in;
    }

    @Override
    public void getArgs(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        destinationAddress = bundle.getString(Constants.KeyMaps.SCAN_ADDRESS);
        LogTool.d(TAG, "scanAddress:" + destinationAddress);

    }

    @Override
    public void initViews() {
        addressVOS = new ArrayList<>();
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(getResources().getString(R.string.send));
        //显示返回按钮
        ibBack.setVisibility(View.VISIBLE);
        //显示币种
        tvCurrency.setText(BCAASApplication.getBlockService());
        //获取当前text view占用的布局
        int widthExceptMargin = (BCAASApplication.getScreenWidth() - getResources().getDimensionPixelOffset(R.dimen.d42));
        LogTool.d(TAG, widthExceptMargin);
        double weightWidth = widthExceptMargin / 3.4;
        LogTool.d(TAG, weightWidth);
        double contentWidth = widthExceptMargin - weightWidth;
        LogTool.d(TAG, contentWidth);
        double width = contentWidth - getResources().getDimensionPixelOffset(R.dimen.d16);
        tvAccountAddressValue.setText(
                TextTool.intelligentOmissionText(
                        tvAccountAddressValue, (int) width,
                        BCAASApplication.getWalletAddress()));
        setBalance(BCAASApplication.getWalletBalance());
        getAddress();
        addSoftKeyBroadManager();
        etTransactionAmount.setFilters(new InputFilter[]{new AmountEditTextFilter().setDigits(8)});

        if (!isShowGuide) {
            isShowGuide = true;
            initSelectAddressGuideView();
            initScanAddressGuideView();
        }
        setDestinationAddress();
    }

    /**
     * 设置接收方的账户地址
     */
    private void setDestinationAddress() {
        if (StringTool.notEmpty(destinationAddress)) {
            etInputDestinationAddress.setText(RegexTool.replaceBlank(destinationAddress));
            getFocus();
            currentAddressVO = null;
        }
    }

    /**
     * 扫描地址
     */
    private void initScanAddressGuideView() {
        View view = LayoutInflater.from(this).inflate(R.layout.help_view_main, null);
        LinearLayout linearLayout = view.findViewById(R.id.ll_guide);
        linearLayout.setGravity(Gravity.RIGHT);
        TextView textView = view.findViewById(R.id.tv_content);
        textView.setText(context.getResources().getString(R.string.scan_account_address));
        ImageView imageView = view.findViewById(R.id.iv_gesture);
        imageView.setImageResource(R.drawable.icon_help_arrow_one);
        Button button = view.findViewById(R.id.btn_next);
        button.setText(context.getResources().getString(R.string.yes));

        guideViewScanAddress = GuideView.Builder
                .newInstance(this)
                .setTargetView(ibScanAddress)//设置目标
                .setIsDraw(true)
                .setCustomGuideView(view)
                .setDirction(GuideView.Direction.RIGHT_ALIGN_BOTTOM)
                .setShape(GuideView.MyShape.RECTANGULAR)
                .setRadius(DensityTool.dip2px(BCAASApplication.context(), 6))
                .setBgColor(getResources().getColor(R.color.black80))
                .build();
        guideViewScanAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guideViewScanAddress.hide();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guideViewScanAddress.hide();
            }
        });
    }

    /**
     * 选择地址
     */
    private void initSelectAddressGuideView() {
        View view = LayoutInflater.from(this).inflate(R.layout.help_view_main, null);
        LinearLayout linearLayout = view.findViewById(R.id.ll_guide);
        linearLayout.setGravity(Gravity.RIGHT);
        TextView textView = view.findViewById(R.id.tv_content);
        textView.setText(context.getResources().getString(R.string.choose_account_address));
        ImageView imageView = view.findViewById(R.id.iv_gesture);
        imageView.setImageResource(R.drawable.icon_help_arrow_one);
        Button button = view.findViewById(R.id.btn_next);
        button.setText(context.getResources().getString(R.string.next));
        guideViewSelectAddress = GuideView.Builder
                .newInstance(this)
                .setTargetView(ibSelectAddress)//设置目标
                .setIsDraw(true)
                .setCustomGuideView(view)
                .setDirction(GuideView.Direction.RIGHT_ALIGN_BOTTOM)
                .setShape(GuideView.MyShape.RECTANGULAR)
                .setRadius(DensityTool.dip2px(BCAASApplication.context(), 6))
                .setBgColor(getResources().getColor(R.color.black80))
                .build();
        guideViewSelectAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guideViewSelectAddress.hide();
                guideViewScanAddress.show(Constants.Preference.GUIDE_SCAN_ADDRESS);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guideViewSelectAddress.hide();
                guideViewScanAddress.show(Constants.Preference.GUIDE_SCAN_ADDRESS);
            }
        });
        guideViewSelectAddress.show(Constants.Preference.GUIDE_SEND_ADDRESS);
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
        softKeyBroadManager = new SoftKeyBroadManager(scrollView, vSpace);
        softKeyBroadManager.addSoftKeyboardStateListener(softKeyboardStateListener);
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
        RxView.clicks(ibBack).throttleFirst(Constants.Time.sleep800, TimeUnit.MILLISECONDS)
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        setResult(Constants.ValueMaps.ACTIVITY_STATUS_TODO);

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogTool.e(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {

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
                .throttleFirst(Constants.Time.sleep800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (ListTool.isEmpty(getAddressName())) {
                        showToast(getString(R.string.no_account_address_to_choose_from));
                        return;
                    }
                    hideSoftKeyboard();
                    showAddressListPopWindow(onAddressSelectListener, addressVOS);
                });
        Disposable subscribeScanAddress = RxView.clicks(ibScanAddress)
                .throttleFirst(Constants.Time.sleep800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    hideSoftKeyboard();
                    //跳转打开相机进行扫描
                    startActivityForResult(new Intent(this, CaptureActivity.class), Constants.REQUEST_CODE_CAMERA_OK);
                });
        Disposable subscribeSend = RxView.clicks(btnSend)
                .throttleFirst(Constants.Time.sleep800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    hideSoftKeyboard();
                    //判断当前是否有交易还未完成
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
                    if (StringTool.equals(balance, "0") || DecimalTool.compareFirstEqualSecondValue(amount, "0")) {
                        showToast(getResources().getString(R.string.transaction_cannot_be_zero));
                        return;
                    }
                    /*7：判断余额是否足够发送*/
                    if (StringTool.equals(DecimalTool.calculateFirstSubtractSecondValue(balance, amount), MessageConstants.NO_ENOUGH_BALANCE)) {
                        showToast(getResources().getString(R.string.insufficient_balance));
                        return;
                    }
                    etTransactionAmount.setText(MessageConstants.Empty);
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.KeyMaps.DESTINATION_WALLET, destinationWallet);
                    if (currentAddressVO != null) {
                        bundle.putString(Constants.KeyMaps.ADDRESS_NAME, currentAddressVO.getAddressName());
                    }
                    bundle.putString(Constants.KeyMaps.TRANSACTION_AMOUNT, amount);
                    intent.putExtras(bundle);
                    intent.setClass(this.activity, SendInfoConfirmationActivity.class);
                    startActivityForResult(intent, Constants.REQUEST_CODE_SEND_CONFIRM_ACTIVITY);
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

    private void getFocus() {
        //将焦点放在输入金额输入框上面
        etTransactionAmount.setFocusable(true);
        etTransactionAmount.setFocusableInTouchMode(true);
        etTransactionAmount.requestFocus();
//        if (activity != null) {
//            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST);
//        }
    }

    //因为首页有一个连续两次发出的消息，所以为了避免出现两层引导页面，定义此变量来约束
    private boolean isShowGuide;

    /**
     * 通知更新当前余额，自然也要更新当前的币种
     *
     * @param updateWalletBalanceEvent
     */
    @Subscribe
    public void UpdateWalletBalance(RefreshWalletBalanceEvent updateWalletBalanceEvent) {
        if (updateWalletBalanceEvent == null) {
            return;
        }
        LogTool.d(TAG, "RefreshWalletBalanceEvent");
        //更新余额
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
                    getFocus();
                }

            }
        }

        @Override
        public void changeItem(boolean isChange) {

        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            if (requestCode == Constants.REQUEST_CODE_SEND_CONFIRM_ACTIVITY) {
                //判断当前是发送页面进行返回的
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    setResult(bundle.getString(Constants.KeyMaps.ACTIVITY_STATUS));
                }
            } else if (requestCode == Constants.REQUEST_CODE_CAMERA_OK) {
                // 如果当前是照相机扫描回来
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    destinationAddress = bundle.getString(Constants.RESULT);

                    setDestinationAddress();

                }
            }

        }

    }

    /**
     * 退出当前界面
     *
     * @param status
     */
    private void setResult(String status) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KeyMaps.ACTIVITY_STATUS, status);
        intent.putExtras(bundle);
        this.setResult(RESULT_OK, intent);
        this.finish();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }


}
