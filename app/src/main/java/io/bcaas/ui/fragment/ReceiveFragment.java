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
import io.bcaas.tools.StringTool;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * 「交易接收」页面
 */
public class ReceiveFragment extends BaseFragment {
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
        return R.layout.frg_receive;
    }


    @Override
    public void initViews(View view) {
        String addressOfUser = getAddressOfUser();
        if (StringTool.isEmpty(addressOfUser)) {
            showToast("获取账户地址出现异常。");
        } else {
            tvMyAddress.setText(addressOfUser);
            makeQRCodeByAddress(addressOfUser);
        }

    }

    private void makeQRCodeByAddress(String address) {
        Bitmap qrCode = EncodingUtils.createQRCode(address, context.getResources().getDimensionPixelOffset(R.dimen.d200),
                context.getResources().getDimensionPixelOffset(R.dimen.d200), null);
        ;//mCheckBox.isChecked() ? BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher) :
        ivQRCode.setImageBitmap(qrCode);
    }


    @Override
    public void initListener() {

    }


}
