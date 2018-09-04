package io.bcaas.ui.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.obt.qrcode.encoding.EncodingUtils;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.base.BaseFragment;
import io.bcaas.base.BcaasApplication;
import io.bcaas.tools.StringTool;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * 「交易接收」页面
 */
public class ReceiveFragment extends BaseFragment {
    private String TAG = ReceiveFragment.class.getSimpleName();
    @BindView(R.id.ivQRCode)
    ImageView ivQRCode;
    @BindView(R.id.tvMyAddress)
    TextView tvMyAddress;

    public static ReceiveFragment newInstance() {
        ReceiveFragment receiveFragment = new ReceiveFragment();
        return receiveFragment;
    }

    @Override
    public void getArgs(Bundle bundle) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_receive;
    }


    @Override
    public void initViews(View view) {
        String address = BcaasApplication.getWalletAddress();
        if (StringTool.isEmpty(address)) {
            showToast(getResources().getString(R.string.data_exception_of_account));
        } else {
            tvMyAddress.setText(address);
            makeQRCodeByAddress(address);
        }

    }

    private void makeQRCodeByAddress(String address) {
        Bitmap qrCode = EncodingUtils.createQRCode(address, context.getResources().getDimensionPixelOffset(R.dimen.d200),
                context.getResources().getDimensionPixelOffset(R.dimen.d200), null);
        ivQRCode.setImageBitmap(qrCode);
    }


    @Override
    public void initListener() {

    }


}
