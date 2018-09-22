package io.bcaas.ui.activity.tv;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.obt.qrcode.encoding.EncodingUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.event.VerifyEvent;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.tools.DateFormatTool;
import io.bcaas.tools.OttoTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.ecc.WalletTool;
import io.bcaas.tools.wallet.NumberTool;
import io.bcaas.view.tv.FlyBroadLayout;
import io.bcaas.view.tv.MainUpLayout;
import io.bcaas.vo.PublicUnitVO;
import io.reactivex.disposables.Disposable;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/20
 * TV版發送頁面
 */
public class SendActivityTV extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_current_time)
    TextView tvCurrentTime;
    @BindView(R.id.tv_logout)
    TextView tvLogout;
    @BindView(R.id.rl_header)
    RelativeLayout rlHeader;
    @BindView(R.id.tv_star_need)
    TextView tvStarNeed;
    @BindView(R.id.tv_currency_key)
    TextView tvCurrencyKey;
    @BindView(R.id.tv_currency)
    TextView tvCurrency;
    @BindView(R.id.tv_balance_key)
    TextView tvBalanceKey;
    @BindView(R.id.tv_balance)
    TextView tvBalance;
    @BindView(R.id.pb_balance)
    ProgressBar pbBalance;
    @BindView(R.id.tv_account_address_key)
    TextView tvAccountAddressKey;
    @BindView(R.id.iv_qr_code)
    ImageView ivQrCode;
    @BindView(R.id.tv_my_address)
    TextView tvMyAddress;
    @BindView(R.id.et_input_destination_address)
    EditText etInputDestinationAddress;
    @BindView(R.id.et_transaction_amount)
    EditText etTransactionAmount;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.block_base_mainup)
    FlyBroadLayout blockBaseMainup;
    @BindView(R.id.block_base_content)
    MainUpLayout blockBaseContent;

    // 得到當前的幣種
    List<PublicUnitVO> publicUnitVOList;

    @Override
    public boolean full() {
        return false;
    }

    @Override
    public int getContentView() {
        return R.layout.tv_activity_send;
    }

    @Override
    public void getArgs(Bundle bundle) {

    }

    @Override
    public void initViews() {
        //获取所有的清单
        publicUnitVOList = WalletTool.getPublicUnitVO();
        initData();
    }

    private void initData() {
        tvCurrentTime.setText(DateFormatTool.getCurrentTime());
        tvTitle.setText(getResources().getString(R.string.send));
        setBalance(BcaasApplication.getWalletBalance());
        tvCurrency.setText(BcaasApplication.getBlockService());
        String address = BcaasApplication.getWalletAddress();
        if (StringTool.isEmpty(address)) {
            showToast(getResources().getString(R.string.account_data_error));
        } else {
            tvMyAddress.setText(address);
            makeQRCodeByAddress(address);
        }
    }


    //对当前的余额进行赋值，如果当前没有读取到数据，那么就显示进度条，否则显示余额
    private void setBalance(String balance) {
        if (StringTool.isEmpty(balance)) {
            //隐藏显示余额的文本，展示进度条
            tvBalance.setVisibility(View.INVISIBLE);
            pbBalance.setVisibility(View.VISIBLE);
        } else {
            pbBalance.setVisibility(View.GONE);
            tvBalance.setVisibility(View.VISIBLE);
            tvBalance.setText(NumberTool.formatNumber(balance));
        }
    }

    private void makeQRCodeByAddress(String address) {
        Bitmap qrCode = EncodingUtils.createQRCode(address, context.getResources().getDimensionPixelOffset(R.dimen.d200),
                context.getResources().getDimensionPixelOffset(R.dimen.d200), null);
        ivQrCode.setImageBitmap(qrCode);
    }

    @Override
    public void initListener() {
        blockBaseContent.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                blockBaseMainup.setFocusView(newFocus, oldFocus, 1.2f);
            }
        });
        Disposable subscribe = RxView.clicks(tvCurrency)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    showCurrencyListPopWindow(onItemSelectListener, publicUnitVOList);

                });
    }

    /*币种重新选择返回*/
    private OnItemSelectListener onItemSelectListener = new OnItemSelectListener() {
        @Override
        public <T> void onItemSelect(T type) {
            if (type != null) {
                /*显示币种*/
                tvCurrency.setText(type.toString());
                /*存储币种*/
                BcaasApplication.setBlockService(type.toString());
                /*重新verify，获取新的区块数据*/
                OttoTool.getInstance().post(new VerifyEvent());
                /*重置余额*/
                BcaasApplication.resetWalletBalance();
                tvBalance.setVisibility(View.INVISIBLE);
                pbBalance.setVisibility(View.VISIBLE);
            }
        }
    };

}
