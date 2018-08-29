package io.bcaas.base;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.bcaas.bean.TransactionsBean;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.OttoTool;
import io.bcaas.ui.activity.MainActivity;
import io.bcaas.ui.contracts.BaseContract;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 */
public abstract class BaseFragment extends Fragment implements BaseContract.View {
    private String TAG = "BaseFragment";
    private View rootView;
    protected Context context;
    protected Activity activity;
    private List<String> currency;
    private List<TransactionsBean> allTransactionData;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(getLayoutRes(), container, false);
        }
        unbinder = ButterKnife.bind(this, rootView);
        OttoTool.getInstance().register(this);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        activity = getActivity();
        assert activity != null;
        getArgs(activity.getIntent().getExtras());
        currency = ((MainActivity) activity).getCurrency();
        allTransactionData = ((MainActivity) activity).getAllCurrencyData();
        initViews(view);
        initListener();
    }

    protected List<String> getCurrency() {
        currency.add("BCC");
        currency.add("TCC");
        return currency;
    }

    protected List<TransactionsBean> getAllTransactionData() {
        return allTransactionData;
    }

    public abstract int getLayoutRes();//得到当前的layoutRes

    public abstract void initViews(View view);

    public abstract void getArgs(Bundle bundle);

    public abstract void initListener();


    public void showToast(String info) {
        if (activity == null) return;
        ((BaseActivity) activity).showToast(info);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        OttoTool.getInstance().unregister(this);
    }

    public void intentToActivity(Bundle bundle, Class classTo, Boolean finishFrom) {//跳转到另外一个界面
        if (activity == null) return;
        ((BaseActivity) activity).intentToActivity(bundle, classTo, finishFrom);
    }

    public String getAddressOfUser() {//获取用户的账户地址
        return BcaasApplication.getWalletAddress();
    }

    public void logout() {
        ((MainActivity) activity).logout();
    }

    @Override
    public void showLoadingDialog(String loading) {

    }

    @Override
    public void hideLoadingDialog() {

    }

    @Override
    public void success(String message) {
        showToast(message);
    }

    @Override
    public void failure(String message) {
        BcaasLog.d(TAG, message);
    }

    @Override
    public void onTip(String message) {
        showToast(message);
    }


//    // 处理事件的方法
//    protected <T> Observable.Transformer<T, T> timer() {
//        return observable -> observable.subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread());
//    }

}
