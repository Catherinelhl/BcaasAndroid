package io.bcaas.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.obt.qrcode.encoding.EncodingUtils;


import butterknife.BindView;
import butterknife.ButterKnife;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.bean.TransactionDetailBean;
import io.bcaas.constants.Constants;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.view.textview.LeftAndRightTextView;

/**
 * @author catherine.brainwilliam
 * @since 2018/12/10
 * <p>
 * 顯示交易詳情
 */
public class TransactionDetailActivity extends BaseActivity {
    @BindView(R.id.lar_tv)
    LeftAndRightTextView larTv;
    @BindView(R.id.tv_send_destination_wallet)
    TextView tvSendDestinationWallet;
    private String TAG = TransactionDetailActivity.class.getSimpleName();


    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;

    @BindView(R.id.ib_back)
    ImageButton ibBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ib_status)
    ImageButton ibStatus;
    @BindView(R.id.tv_receive_destination_wallet)
    TextView tvReceiveDestinationWallet;
    @BindView(R.id.tv_hash)
    TextView tvHash;
    @BindView(R.id.tv_height)
    TextView tvHeight;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.iv_qr_code)
    ImageView ivQrCode;
    @BindView(R.id.tv_copy)
    TextView tvCopy;

    //得到当前交易记录的字符串信息
    private TransactionDetailBean transactionDetailBean;

    //二維碼渲染的前景色
    private int foregroundColorOfQRCode = 0xff000000;
    //二維碼渲染的背景色
    private int backgroundColorOfQRCode = 0x00000000;

    @Override
    public boolean full() {
        return false;
    }

    @Override
    public int getContentView() {
        return R.layout.activity_transaction_detail;
    }

    @Override
    public void getArgs(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        String transactionDetailStr = bundle.getString(Constants.TRANSACTION_STR);
        LogTool.d(TAG, transactionDetailStr);
        if (StringTool.notEmpty(transactionDetailStr)) {
            transactionDetailBean = GsonTool.convert(transactionDetailStr, TransactionDetailBean.class);
        }

    }

    @Override
    public void initViews() {
        if (transactionDetailBean != null) {
            String txHash = transactionDetailBean.getTxHash();
            tvSendDestinationWallet.setText(transactionDetailBean.getSendAccount());
            tvReceiveDestinationWallet.setText(transactionDetailBean.getReceiveAccount());
            tvHash.setText(txHash);
            larTv.setLeftAndRight(transactionDetailBean.getBalance(), transactionDetailBean.getBlockService(), transactionDetailBean.isSend());
//            tvBalance.setText(transactionDetailBean.getBalance());
            tvHeight.setText(transactionDetailBean.getHeight());
            tvTime.setText(transactionDetailBean.getTransactionTime());
            Bitmap qrCode = EncodingUtils.createQRCode(txHash, context.getResources().getDimensionPixelOffset(R.dimen.d200),
                    context.getResources().getDimensionPixelOffset(R.dimen.d200), null, foregroundColorOfQRCode, backgroundColorOfQRCode);
            ivQrCode.setImageBitmap(qrCode);
        }
    }

    @Override
    public void initListener() {
        tvCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取剪贴板管理器：
                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText(Constants.KeyMaps.COPY_ADDRESS, tvHash.getText().toString());
                // 将ClipData内容放到系统剪贴板里。
                if (cm != null) {
                    cm.setPrimaryClip(mClipData);
                    showToast(getString(R.string.successfully_copied));
                }
            }
        });
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }
}
