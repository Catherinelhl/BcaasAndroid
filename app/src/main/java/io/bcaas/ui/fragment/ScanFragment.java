package io.bcaas.ui.fragment;

import android.view.View;

import io.bcaas.R;
import io.bcaas.base.BaseFragment;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * 扫描页面
 */
public class ScanFragment extends BaseFragment {

    public static ScanFragment newInstance() {
        ScanFragment scanFragment = new ScanFragment();
        return scanFragment;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.frg_scan;
    }


    @Override
    public void initViews(View view) {


    }

    @Override
    public void initListener() {

    }


}
