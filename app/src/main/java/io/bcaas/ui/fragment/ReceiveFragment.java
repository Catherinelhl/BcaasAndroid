package io.bcaas.ui.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import com.obt.qrcode.encoding.EncodingUtils;
import io.bcaas.R;
import io.bcaas.base.BCAASApplication;
import io.bcaas.base.BaseFragment;
import io.bcaas.tools.StringTool;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * Fragment:「二維碼」页面
 */
public class ReceiveFragment extends BaseFragment {
    private String TAG = ReceiveFragment.class.getSimpleName();
    @BindView(R.id.iv_qr_code)
    ImageView ivQRCode;
    @BindView(R.id.tv_my_address)
    TextView tvMyAddress;
    //二維碼渲染的前景色
    private int foregroundColorOfQRCode = 0xff000000;
    //二維碼渲染的背景色
    private int backgroundColorOfQRCode = 0x00000000;

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
        String address = BCAASApplication.getWalletAddress();
        if (StringTool.isEmpty(address)) {
            showToast(getResources().getString(R.string.account_data_error));
        } else {
            tvMyAddress.setText(address);
            makeQRCodeByAddress(address);
        }

    }

    private void makeQRCodeByAddress(String address) {
//        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        Bitmap qrCode = EncodingUtils.createQRCode(address, context.getResources().getDimensionPixelOffset(R.dimen.d200),
                context.getResources().getDimensionPixelOffset(R.dimen.d200), null, foregroundColorOfQRCode, backgroundColorOfQRCode);
        ivQRCode.setImageBitmap(qrCode);
    }


    @Override
    public void initListener() {

    }


}
