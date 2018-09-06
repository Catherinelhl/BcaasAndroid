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
import io.bcaas.db.vo.AddressVO;
import io.bcaas.gson.ResponseJson;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.listener.SoftKeyBroadManager;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.OttoTool;
import io.bcaas.ui.activity.MainActivity;
import io.bcaas.ui.contracts.BaseContract;
import io.bcaas.view.dialog.BcaasDialog;
import io.bcaas.vo.PublicUnitVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 */
public abstract class BaseFragment extends Fragment implements BaseContract.View {
    private String TAG = BaseFragment.class.getSimpleName();
    private View rootView;
    protected Context context;
    protected Activity activity;
    private Unbinder unbinder;
    protected SoftKeyBroadManager softKeyBroadManager;

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
        if (activity != null) {
            getArgs(activity.getIntent().getExtras());
        }
        initViews(view);
        initListener();
    }

    public abstract int getLayoutRes();//得到当前的layoutRes

    public abstract void initViews(View view);

    public abstract void getArgs(Bundle bundle);

    public abstract void initListener();


    public void showToast(String info) {
        if (activity == null) {
            return;
        }
        ((BaseActivity) activity).showToast(info);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        OttoTool.getInstance().unregister(this);
    }

    public void intentToActivity(Bundle bundle, Class classTo, Boolean finishFrom) {//跳转到另外一个界面
        if (activity == null) {
            return;
        }
        ((BaseActivity) activity).intentToActivity(bundle, classTo, finishFrom);
    }

    public void logout() {
        if (activity == null) {
            return;
        }
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
    public void httpExceptionStatus(ResponseJson responseJson) {
        if (activity != null) {
            ((BaseActivity) activity).httpExceptionStatus(responseJson);
        }
    }

    @Override
    public void failure(String message) {
        LogTool.d(TAG, message);
    }

    /**
     * 显示当前需要顯示的列表
     * 點擊幣種、點擊選擇交互帳戶地址
     *
     * @param onItemSelectListener 通過傳入的回調來得到選擇的值
     * @param list                 需要顯示的列表
     */
    public void showAddressListPopWindow(OnItemSelectListener onItemSelectListener, List<AddressVO> list) {
        if (activity != null) {
            ((BaseActivity) activity).showAddressListPopWindow(onItemSelectListener, list);
        }
    }

    /**
     * 显示当前需要顯示的列表
     * 點擊幣種、點擊選擇交互帳戶地址
     *
     * @param onItemSelectListener 通過傳入的回調來得到選擇的值
     * @param list                 需要顯示的列表
     */
    public void showCurrencyListPopWindow(OnItemSelectListener onItemSelectListener, List<PublicUnitVO> list) {
        if (activity != null) {
            ((BaseActivity) activity).showCurrencyListPopWindow(onItemSelectListener, list);
        }
    }


    /**
     * 显示更详细的信息
     *
     * @param view
     */
    public void showDetailPop(View view, String content) {
        if (activity != null) {
            ((BaseActivity) activity).showDetailPop(view, content);
        }
    }

    public void showBalancePop(View view) {
        if (activity != null) {
            ((BaseActivity) activity).showBalancePop(view);
        }
    }

    /*隐藏当前键盘*/
    public void hideSoftKeyboard() {
        if (activity != null) {
            ((BaseActivity) activity).hideSoftKeyboard();
        }
    }

    public void showBcaasDialog(String message, BcaasDialog.ConfirmClickListener confirmClickListener) {
        if (activity != null) {
            ((BaseActivity) activity).showBcaasDialog(message, confirmClickListener);
        }
    }
//    // 处理事件的方法
//    protected <T> Observable.Transformer<T, T> timer() {
//        return observable -> observable.subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread());
//    }


    protected SoftKeyBroadManager.SoftKeyboardStateListener softKeyboardStateListener = new SoftKeyBroadManager.SoftKeyboardStateListener() {
        @Override
        public void onSoftKeyboardOpened(int keyboardHeightInPx, int bottom) {
            LogTool.d(TAG, keyboardHeightInPx);
        }

        @Override
        public void onSoftKeyboardClosed() {

        }
    };

    @Override
    public void onDestroy() {
        if (softKeyBroadManager != null && softKeyboardStateListener != null) {
            softKeyBroadManager.removeSoftKeyboardStateListener(softKeyboardStateListener);
        }
        super.onDestroy();
    }
}
