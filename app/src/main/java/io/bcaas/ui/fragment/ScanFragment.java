package io.bcaas.ui.fragment;

import android.os.Bundle;
import android.view.View;

import io.bcaas.R;
import io.bcaas.base.BaseFragment;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * 「扫描」对方Wallet 地址
 */
public class ScanFragment extends BaseFragment {

    public static ScanFragment newInstance() {
        ScanFragment scanFragment = new ScanFragment();
        return scanFragment;
    }

    @Override
    public void getArgs(Bundle bundle) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_scan;
    }


    @Override
    public void initViews(View view) {


    }

    @Override
    public void initListener() {

    }


}
