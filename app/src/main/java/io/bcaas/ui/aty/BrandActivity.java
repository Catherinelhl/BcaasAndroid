package io.bcaas.ui.aty;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.constants.Constants;
import io.bcaas.presenter.BrandPresenterImp;
import io.bcaas.ui.contracts.BrandContracts;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 */
public class BrandActivity extends BaseActivity
        implements BrandContracts.View {


    private BrandContracts.Presenter presenter;

    @Override
    public void getArgs(Bundle bundle) {

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int code = msg.what;
            if (code == 1) {
                intentToActivity(LoginActivity.class, true);
            } else {
                Bundle bundle=new Bundle();
                bundle.putString(Constants.KeyMaps.From,Constants.ValueMaps.FROM_BRAND);
                intentToActivity(bundle,MainActivity.class, true);

            }
        }
    };

    @Override
    public int getContentView() {
        return R.layout.aty_brand;
    }

    @Override
    public void initViews() {
        presenter = new BrandPresenterImp(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                presenter.queryWalletInfo();
//                handler.sendEmptyMessageDelayed(1, Constants.ValueMaps.brandSleepTime);
            }
        }).start();
    }

    @Override
    public void initListener() {

    }

    @Override
    public void noWalletInfo() {
        handler.sendEmptyMessage(1);

    }

    @Override
    public void online() {
        handler.sendEmptyMessage(2);

    }

    @Override
    public void offline() {
        handler.sendEmptyMessage(1);

    }
}