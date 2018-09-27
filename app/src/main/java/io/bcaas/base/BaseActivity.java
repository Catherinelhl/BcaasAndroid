package io.bcaas.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.bcaas.R;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.db.vo.AddressVO;
import io.bcaas.gson.ResponseJson;
import io.bcaas.http.tcp.TCPThread;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.listener.SoftKeyBroadManager;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.OttoTool;
import io.bcaas.tools.StringTool;
import io.bcaas.ui.activity.LoginActivity;
import io.bcaas.ui.contracts.BaseContract;
import io.bcaas.view.dialog.BcaasDialog;
import io.bcaas.view.dialog.BcaasLoadingDialog;
import io.bcaas.view.dialog.BcaasSingleDialog;
import io.bcaas.view.dialog.TVBcaasDialog;
import io.bcaas.view.pop.ListPopWindow;
import io.bcaas.vo.PublicUnitVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 */
public abstract class BaseActivity extends FragmentActivity implements BaseContract.View, BaseContract.HttpView {

    private String TAG = BaseActivity.class.getSimpleName();
    private Unbinder unbinder;
    protected Context context;
    /*双按钮弹框*/
    private BcaasDialog bcaasDialog;
    /*单按钮弹框*/
    private BcaasSingleDialog bcaasSingleDialog;
    private BcaasLoadingDialog bcaasLoadingDialog;
    /*显示列表的Pop Window*/
    private ListPopWindow listPopWindow;
    /*键盘输入管理*/
    private InputMethodManager inputMethodManager;
    /*存儲當前點擊返回按鍵的時間，用於提示連續點擊兩次才能退出*/
    private long lastClickBackTime = 0L;
    /*软键盘管理*/
    protected SoftKeyBroadManager softKeyBroadManager;
    private Activity activity;
    private BaseContract.HttpPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getArgs(getIntent().getExtras());
        setFullScreen(full());
        setContentView(getContentView());
        activity = this;
        context = getApplicationContext();
        unbinder = ButterKnife.bind(this);
        OttoTool.getInstance().register(this);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        initViews();
        initListener();
        checkNetState();
        presenter = new BaseHttpPresenterImp(this);
    }

    private void setFullScreen(boolean isFull) {
        if (isFull) {
            //去除标题栏
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            //去除状态栏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

    }

    public abstract boolean full();

    public abstract int getContentView();

    public abstract void getArgs(Bundle bundle);

    public abstract void initViews();

    public abstract void initListener();

    public void showToast(String toastInfo) {
        showToast(toastInfo, Constants.ValueMaps.TOAST_SHORT);
    }

    /**
     * @param toastInfo    提示信息
     * @param durationMode 提示展示时间长短的模式
     */
    public void showToast(String toastInfo, int durationMode) {
        Message message = new Message();
        message.obj = toastInfo;
        message.what = durationMode;//0：short；1：Long
        handler.sendMessage(message);

    }

    /*Looper: Could not create epoll instance: Too many open files*/
    /*在一些类中的子线程中使用Toast，可以发到这个主线程的Handler解决，防止子线程Loop太多带来的各种问题*/
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!checkActivityState()) {
                return;
            }
            /*1:取出当前需要显示的信息*/
            String toastInfo = (String) msg.obj;
            /*2：得到当前Toast需要展现的时间长短*/
            int what = msg.what;
            /*3:根据取值得到时间段*/
            int duration = what == 0 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG;
            LogTool.d(TAG, toastInfo);
            Toast toast = Toast.makeText(context, "", duration);
            /*解决小米手机toast自带包名的问题*/
            toast.setText(toastInfo);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    };

    /**
     * 从当前页面跳转到另一个页面
     *
     * @param classTo
     */
    public void intentToActivity(Class classTo) {
        intentToActivity(null, classTo);
    }

    /**
     * @param finishFrom 是否关闭上一个activity，默认是不关闭 false
     */
    public void intentToActivity(Class classTo, boolean finishFrom) {
        intentToActivity(null, classTo, finishFrom);
    }

    /**
     * @param bundle 存储当前页面需要传递的数据
     */
    public void intentToActivity(Bundle bundle, Class classTo) {
        intentToActivity(bundle, classTo, false);
    }

    public void intentToActivity(Bundle bundle, Class classTo, Boolean finishFrom) {
        Intent intent = new Intent();
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.setClass(this, classTo);
        startActivity(intent);
        if (finishFrom) {
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*解绑注解*/
        unbinder.unbind();
        /*移除键盘监听*/
        if (softKeyBroadManager != null && softKeyboardStateListener != null) {
            softKeyBroadManager.removeSoftKeyboardStateListener(softKeyboardStateListener);
        }
        /*关闭未关闭的弹框*/
        hideLoadingDialog();
        if (bcaasSingleDialog != null) {
            bcaasSingleDialog.dismiss();
            bcaasSingleDialog.cancel();
            bcaasSingleDialog = null;
        }
        if (bcaasDialog != null) {
            bcaasDialog.dismiss();
            bcaasDialog.cancel();
            bcaasDialog = null;
        }
        /*注销事件分发*/
        OttoTool.getInstance().unregister(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void showLoadingDialog() {
        showLoadingDialog(getResources().getColor(R.color.red));
    }

    public void showLoadingDialog(int color) {
        if (!checkActivityState()) {
            return;
        }
        if (bcaasLoadingDialog == null) {
            bcaasLoadingDialog = new BcaasLoadingDialog(activity);
            bcaasLoadingDialog.setProgressBarColor(color);
        }
        bcaasLoadingDialog.show();
    }

    public void hideLoadingDialog() {
        if (!checkActivityState()) {
            return;
        }
        if (bcaasLoadingDialog != null) {
            bcaasLoadingDialog.dismiss();
            bcaasLoadingDialog.cancel();
            bcaasLoadingDialog = null;

        }
    }

    @Override
    public void success(String message) {
        LogTool.d(TAG, message);
    }

    /**
     * 显示对话框
     *
     * @param message
     * @param listener
     */
    public void showBcaasDialog(String message, final BcaasDialog.ConfirmClickListener listener) {
        showBcaasDialog(getResources().getString(R.string.warning),
                getResources().getString(R.string.confirm),
                getResources().getString(R.string.cancel), message, listener);
    }

    /**
     * 显示对话框
     *
     * @param title
     * @param message
     * @param listener
     */
    public void showBcaasDialog(String title, String message, final BcaasDialog.ConfirmClickListener listener) {
        showBcaasDialog(title,
                getResources().getString(R.string.confirm),
                getResources().getString(R.string.cancel), message, listener);
    }

    /**
     * 显示对话框
     *
     * @param title
     * @param left
     * @param right
     * @param message
     * @param listener
     */
    public void showBcaasDialog(String title, String left, String right, String message, final BcaasDialog.ConfirmClickListener listener) {
        if (bcaasDialog == null) {
            bcaasDialog = new BcaasDialog(this);
        }
        /*设置弹框点击周围不予消失*/
        bcaasDialog.setCanceledOnTouchOutside(false);
        /*设置弹框背景*/
        bcaasDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_white));
        bcaasDialog.setLeftText(left)
                .setRightText(right)
                .setContent(message)
                .setTitle(title)
                .setOnConfirmClickListener(new BcaasDialog.ConfirmClickListener() {
                    @Override
                    public void sure() {
                        listener.sure();
                        bcaasDialog.dismiss();
                    }

                    @Override
                    public void cancel() {
                        listener.cancel();
                        bcaasDialog.dismiss();

                    }
                }).show();
    }

    public void showBcaasSingleDialog(String message, final BcaasSingleDialog.ConfirmClickListener listener) {
        showBcaasSingleDialog(getResources().getString(R.string.warning), message, listener);
    }

    /**
     * 显示单个 按钮对话框
     *
     * @param title
     * @param message
     * @param listener
     */
    public void showBcaasSingleDialog(String title, String message, final BcaasSingleDialog.ConfirmClickListener listener) {
        if (bcaasSingleDialog == null) {
            bcaasSingleDialog = new BcaasSingleDialog(this);
        }
        /*设置弹框点击周围不予消失*/
        bcaasSingleDialog.setCanceledOnTouchOutside(false);
        /*设置弹框背景*/
        bcaasSingleDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_white));
        bcaasSingleDialog.setContent(message)
                .setTitle(title)
                .setOnConfirmClickListener(() -> {
                    listener.sure();
                    bcaasSingleDialog.dismiss();
                }).show();
    }

    /**
     * 显示当前需要顯示的地址列表
     * 點擊幣種、點擊選擇交互帳戶地址
     *
     * @param onItemSelectListener 通過傳入的回調來得到選擇的值
     * @param list                 需要顯示的列表
     */
    public void showAddressListPopWindow(OnItemSelectListener onItemSelectListener, List<AddressVO> list) {
        listPopWindow = new ListPopWindow(context);
        listPopWindow.addListAddress(onItemSelectListener, list);
        listPopWindow.setOnDismissListener(() -> setBackgroundAlpha(1f));
        //设置layout在PopupWindow中显示的位置
        listPopWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        setBackgroundAlpha(0.7f);
    }

    /**
     * 显示当前需要顯示的货币列表
     * 點擊幣種、點擊選擇交互帳戶地址
     *
     * @param onItemSelectListener 通過傳入的回調來得到選擇的值
     * @param list                 需要顯示的列表
     */
    public void showCurrencyListPopWindow(OnItemSelectListener onItemSelectListener, List<PublicUnitVO> list) {
        listPopWindow = new ListPopWindow(context);
        listPopWindow.addCurrencyList(onItemSelectListener, list);
        listPopWindow.setOnDismissListener(() -> setBackgroundAlpha(1f));
        //设置layout在PopupWindow中显示的位置
        listPopWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        setBackgroundAlpha(0.7f);
    }

    //设置屏幕背景透明效果
    protected void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = alpha;
        getWindow().setAttributes(lp);
    }

    /*隱藏當前軟鍵盤*/
    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 連續點擊兩次退出
     *
     * @return
     */
    protected boolean doubleClickForExit() {
        if ((System.currentTimeMillis() - lastClickBackTime) > Constants.ValueMaps.sleepTime2000) {
            lastClickBackTime = System.currentTimeMillis();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void httpExceptionStatus(ResponseJson responseJson) {
        String message = responseJson.getMessage();
        LogTool.e(TAG, message);
        int code = responseJson.getCode();
        if (code == MessageConstants.CODE_3003) {
            // TODO: 2018/9/6 remember to delete
            failure(message);
        } else if (code == MessageConstants.CODE_2035) {
            //代表TCP没有连接上，这个时候应该停止socket请求，重新请求新的AN
            presenter.stopTCP();
            BcaasApplication.setKeepHttpRequest(true);
            presenter.onResetAuthNodeInfo();
        } else {
            failure(getResources().getString(R.string.data_acquisition_error));
        }

    }


    @Override
    public void failure(String message) {
        showToast(message);
    }

    protected SoftKeyBroadManager.SoftKeyboardStateListener softKeyboardStateListener = new SoftKeyBroadManager.SoftKeyboardStateListener() {
        @Override
        public void onSoftKeyboardOpened(int keyboardHeightInPx, int bottom) {
        }

        @Override
        public void onSoftKeyboardClosed() {
            //键盘隐藏
        }
    };


    /**
     * 获取当前手机的网络状态
     *
     * @return
     */
    private boolean checkNetState() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                BcaasApplication.setRealNet(true);
                return true;
            } else {
                BcaasApplication.setRealNet(false);
                showToast(getResources().getString(R.string.network_not_reachable));
                return false;
            }
        }
        return false;

    }

    @Override
    public void noNetWork() {
        showToast(getResources().getString(R.string.network_not_reachable));
    }

    @Override
    public void httpGetWalletWaitingToReceiveBlockSuccess() {
        LogTool.d(TAG, MessageConstants.SUCCESS_GET_WALLET_RECEIVE_BLOCK);

    }

    @Override
    public void httpGetWalletWaitingToReceiveBlockFailure() {
        LogTool.d(TAG, MessageConstants.FAILURE_GET_WALLET_RECEIVE_BLOCK);
    }

    @Override
    public void httpGetLastestBlockAndBalanceSuccess() {
        LogTool.d(TAG, MessageConstants.SUCCESS_GET_LATESTBLOCK_AND_BALANCE);

    }

    @Override
    public void httpGetLastestBlockAndBalanceFailure() {
        LogTool.d(TAG, MessageConstants.FAILURE_GET_LATESTBLOCK_AND_BALANCE);

    }

    @Override
    public void verifySuccess(boolean isReset) {
    }

    @Override
    public void resetAuthNodeFailure(String message) {
        presenter.onResetAuthNodeInfo();

    }

    @Override
    public void resetAuthNodeSuccess() {

    }

    @Override
    public void responseDataError() {

    }

    @Override
    public void noData() {

    }

    @Override
    public void passwordError() {

    }

    @Override
    public void verifyFailure() {

    }

    public boolean checkActivityState() {
        return activity != null && !activity.isFinishing();
    }

    public void logout() {
        BcaasApplication.setKeepHttpRequest(false);
        TCPThread.kill(true);
        BcaasApplication.clearAccessToken();
        intentToActivity(LoginActivity.class, true);
    }


    /*獲取當前語言環境*/
    protected String getCurrentLanguage() {
        // 1：檢查應用是否已經有用戶自己存儲的語言種類
        String currentString = BcaasApplication.getStringFromSP(Constants.Preference.LANGUAGE_TYPE);
        if (StringTool.isEmpty(currentString)) {
            //2:當前的選中為空，那麼就默認讀取當前系統的語言環境
            Locale locale = getResources().getConfiguration().locale;
            //locale.getLanguage();//zh  是中國
            currentString = locale.getCountry();//CN-簡體中文，TW、HK-繁體中文
        }
        //3:匹配當前的語言獲取，返回APP裡面識別的TAG
        if (StringTool.equals(currentString, Constants.ValueMaps.CN)) {
            return currentString;
        } else {
            return Constants.ValueMaps.EN;

        }
    }

    /**
     * 切換語言
     *
     * @param type
     */
    protected void switchingLanguage(String type) {
        // 1：获得res资源对象
        Resources resources = getResources();
        //2： 获得设置对象
        Configuration config = resources.getConfiguration();
        //3： 获得屏幕参数：主要是分辨率，像素等。
        DisplayMetrics dm = resources.getDisplayMetrics();
        switch (type) {
            case Constants.ValueMaps.CN:
                config.locale = Locale.CHINA; // 简体中文
                break;
            case Constants.ValueMaps.EN:
                config.locale = Locale.ENGLISH; // 英文
                break;
        }
        resources.updateConfiguration(config, dm);
    }
}
