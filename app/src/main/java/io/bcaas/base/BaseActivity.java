package io.bcaas.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.bcaas.R;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.db.vo.AddressVO;
import io.bcaas.event.RefreshWalletBalanceEvent;
import io.bcaas.event.SwitchBlockServiceAndVerifyEvent;
import io.bcaas.event.VerifySuccessAndResetAuthNodeEvent;
import io.bcaas.gson.ResponseJson;
import io.bcaas.http.requester.HttpIntervalRequester;
import io.bcaas.http.tcp.TCPThread;
import io.bcaas.listener.ObservableTimerListener;
import io.bcaas.listener.OnCurrencyItemSelectListener;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.listener.SoftKeyBroadManager;
import io.bcaas.service.DownloadService;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.ObservableTimerTool;
import io.bcaas.tools.OttoTool;
import io.bcaas.tools.PreferenceTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.gson.JsonTool;
import io.bcaas.tools.language.LanguageTool;
import io.bcaas.ui.activity.LoginActivity;
import io.bcaas.ui.activity.tv.LoginActivityTV;
import io.bcaas.ui.contracts.BaseContract;
import io.bcaas.view.dialog.BcaasDialog;
import io.bcaas.view.dialog.BcaasDownloadDialog;
import io.bcaas.view.dialog.BcaasLoadingDialog;
import io.bcaas.view.dialog.BcaasSingleDialog;
import io.bcaas.view.pop.BlockServicesPopWindow;
import io.bcaas.view.pop.ListPopWindow;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 所有Phone's Activity 的基類
 */
public abstract class BaseActivity extends FragmentActivity
        implements BaseContract.View, BaseContract.HttpView {

    private String TAG = BaseActivity.class.getSimpleName();
    private Unbinder unbinder;
    protected Context context;
    /*双按钮弹框*/
    private BcaasDialog bcaasDialog;
    /*单按钮弹框*/
    private BcaasSingleDialog bcaasSingleDialog;
    private BcaasLoadingDialog bcaasLoadingDialog;
    /*显示地址列表的Pop Window*/
    private ListPopWindow listPopWindow;
    /*显示币种列表的Pop Window*/
    private BlockServicesPopWindow blockServicesPopWindow;
    /*键盘输入管理*/
    private InputMethodManager inputMethodManager;
    /*存儲當前點擊返回按鍵的時間，用於提示連續點擊兩次才能退出*/
    private long lastClickBackTime = 0L;
    /*软键盘管理*/
    protected SoftKeyBroadManager softKeyBroadManager;
    protected Activity activity;
    private BaseContract.HttpPresenter presenter;
    // 下载更新的dialog
    protected BcaasDownloadDialog bcaasDownloadDialog;


    //下载版本的服务绑定
    private DownloadService.DownloadBinder mDownloadBinder;
    //可以取消观察者
    private Disposable mDisposable;

    //读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    //安装权限
    private static String[] PERMISSIONS_INSTALL = {
            Manifest.permission.REQUEST_INSTALL_PACKAGES};
    //存储当前需要更新的Android APk路径
    protected String updateAndroidAPKURL;

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
        showToast(toastInfo, Constants.Time.TOAST_SHORT);
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
            if (activity == null) {
                return;
            }
            /*1:取出当前需要显示的信息*/
            String toastInfo = (String) msg.obj;
            /*2：得到当前Toast需要展现的时间长短*/
            int what = msg.what;
            /*3:根据取值得到时间段*/
            int duration = what == 0 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG;
            LogTool.d(TAG, toastInfo);
            Toast toast = Toast.makeText(activity, "", duration);
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

    /**
     * 頁面跳轉
     *
     * @param bundle
     * @param classTo
     * @param finishFrom 是否
     */
    public void intentToActivity(Bundle bundle, Class classTo, boolean finishFrom) {
        this.intentToActivity(bundle, classTo, finishFrom, false);
    }

    /**
     * 頁面跳轉
     *
     * @param bundle
     * @param classTo
     * @param finishFrom
     * @param isClearTask 是否清空任務
     */
    public void intentToActivity(Bundle bundle, Class classTo, boolean finishFrom, boolean isClearTask) {
        Intent intent = new Intent();
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        if (isClearTask) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setClass(this, classTo);
        startActivity(intent);
        if (finishFrom) {
            this.finish();
        }
        overridePendingTransition(R.anim.slide_in_alpha, R.anim.slide_exit_alpha);
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
        if (mDisposable != null) {
            //取消监听
            mDisposable.dispose();
        }
        if (blockServicesPopWindow != null) {
            blockServicesPopWindow.dismiss();
            blockServicesPopWindow = null;
        }

        /*注销事件分发*/
        OttoTool.getInstance().unregister(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void showLoadingDialog() {
        showLoadingDialog(getResources().getColor(R.color.button_right_color));
    }

    public void showLoadingDialog(int color) {
        if (!checkActivityState()) {
            return;
        }
        hideLoadingDialog();
        bcaasLoadingDialog = new BcaasLoadingDialog(this);
        bcaasLoadingDialog.setProgressBarColor(color);
        bcaasLoadingDialog.show();
    }

    public void hideLoadingDialog() {
        if (bcaasLoadingDialog != null) {
            bcaasLoadingDialog.dismiss();
            bcaasLoadingDialog.cancel();
            bcaasLoadingDialog = null;

        }
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
        if (!checkActivityState()) {
            return;
        }
        if (bcaasSingleDialog == null) {
            bcaasSingleDialog = new BcaasSingleDialog(this);
        }
        /*设置弹框点击周围不予消失*/
        bcaasSingleDialog.setCanceledOnTouchOutside(false);
        bcaasSingleDialog.setCancelable(false);
        /*设置弹框背景*/
        bcaasSingleDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_white));
        bcaasSingleDialog.setContent(message)
                .setTitle(title)
                .setOnConfirmClickListener(() -> {
                    listener.sure();
                    bcaasSingleDialog.dismiss();
                }).show();
    }

    public void showLogoutSingleDialog() {
        cleanQueueTask();
        //清除账户信息
        cleanAccountData();
        showBcaasSingleDialog(getString(R.string.warning),
                getString(R.string.please_login_again), () -> {
                    intentToLogin();
                });
    }

    /**
     * 显示当前需要顯示的地址列表
     * 點擊幣種、點擊選擇交互帳戶地址
     *
     * @param onItemSelectListener 通過傳入的回調來得到選擇的值
     * @param list                 需要顯示的列表
     */
    public void showAddressListPopWindow(OnItemSelectListener onItemSelectListener, List<AddressVO> list) {
        // 對當前pop window進行置空
        if (listPopWindow != null) {
            listPopWindow.dismiss();
            listPopWindow = null;
        }
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
     */
    public void showCurrencyListPopWindow(String from) {
        // 對當前pop window進行置空
        if (blockServicesPopWindow != null) {
            if (blockServicesPopWindow.isShowing()) {
                blockServicesPopWindow.dismiss();
            }
        }
        if (blockServicesPopWindow == null) {
            blockServicesPopWindow = BlockServicesPopWindow.getInstance(context);
        }
        blockServicesPopWindow.addCurrencyList(onCurrencyItemSelectListener, from);
        blockServicesPopWindow.setOnDismissListener(() -> {
            setBackgroundAlpha(1f);
            blockServicesPopWindow.dismiss();
        });
        //设置layout在PopupWindow中显示的位置
        blockServicesPopWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        setBackgroundAlpha(0.7f);
    }

    protected OnCurrencyItemSelectListener onCurrencyItemSelectListener = new OnCurrencyItemSelectListener() {
        @Override
        public <T> void onItemSelect(T type, String from) {
            if (type == null) {
                return;
            }
            //比较当前的币种是否一致,是否需要验证
            boolean isVerify = !StringTool.equals(type.toString(), BCAASApplication.getBlockService());
            /*存储币种*/
            BCAASApplication.setBlockService(type.toString());
            switch (from) {
                case Constants.From.INIT_VIEW:
                    break;
                case Constants.From.CHECK_BALANCE:
                case Constants.From.CHECK_WALLET_INFO:
                case Constants.From.SELECT_CURRENCY:
                    // 清除当前余额
                    BCAASApplication.setWalletBalance(MessageConstants.Empty);
                    OttoTool.getInstance().post(new RefreshWalletBalanceEvent(Constants.EventSubscriber.ALL));
                    if (isVerify) {
                        VerifyAfterSwitchBlockService(from);
                    } else {
                        //避免SAN同步资料或者用户想要重新切换SAN位置，所以，当前币种一致，也需要重新去ResetSAN信息然后getBalance
                        TCPThread.closeSocket(false, MessageConstants.socket.TCP_NOT_CONNECT);
                        BCAASApplication.setKeepHttpRequest(true);
                        presenter.onResetAuthNodeInfo(Constants.Reset.RESET_SAN);
                    }
                    break;
                case Constants.From.SEND://为了保证Send页面发送不影响体验，如果币种一致，那么就不重新拿去SAN
                    if (isVerify) {
                        VerifyAfterSwitchBlockService(from);
                    }

                    break;
            }

        }
    };

    /**
     * 在切换币种之后重新验证
     *
     * @param from
     */
    private void VerifyAfterSwitchBlockService(String from) {

        /*重置余额*/
        BCAASApplication.resetWalletBalance();
        /*切换当前的区块服务并且更新；重新verify，获取新的区块数据*/
        OttoTool.getInstance().post(new SwitchBlockServiceAndVerifyEvent(true, true, from));
        TCPThread.setActiveDisconnect(true);
        if (presenter != null) {
            presenter.checkVerify(from);
        }
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
     * 根据传入的参数来捕捉用户点击的此时
     *
     * @param times 次数
     * @return
     */
    private int clickTimes = 0;

    public boolean multipleClickToDo(int times) {
        if ((System.currentTimeMillis() - lastClickBackTime) > Constants.Time.sleep2000) {
            clickTimes = 1;
            lastClickBackTime = System.currentTimeMillis();
            return false;
        } else {
            clickTimes++;
            if (clickTimes == times) {
                lastClickBackTime = 0;
                return true;
            } else if (clickTimes < times) {
                lastClickBackTime = System.currentTimeMillis();
                return false;
            }
            clickTimes = 0;
            return false;
        }
    }

    @Override
    public void httpExceptionStatus(ResponseJson responseJson) {
        String message = responseJson.getMessage();
        LogTool.e(TAG, message);
        int code = responseJson.getCode();
        if (code == MessageConstants.CODE_3003
                || code == MessageConstants.CODE_2012
                // 2012： public static final String ERROR_WALLET_ADDRESS_INVALID = "Wallet address invalid error.";
                || code == MessageConstants.CODE_2026) {
            //  2026：public static final String ERROR_API_ACCOUNT = "Account is empty.";
            showToast(getResources().getString(R.string.data_acquisition_error));
        } else if (JsonTool.isTokenInvalid(code)) {
            LogTool.d(TAG, message);
        } else if (code == MessageConstants.CODE_2035
                || code == MessageConstants.CODE_2034) {
            //代表TCP没有连接上，这个时候应该停止socket请求，重新请求新的AN
            TCPThread.closeSocket(false, MessageConstants.socket.TCP_NOT_CONNECT);
            BCAASApplication.setKeepHttpRequest(true);
            presenter.onResetAuthNodeInfo(Constants.Reset.TCP_NOT_CONNECT);
        }
//        else if (code == MessageConstants.CODE_2001) {
//            // {"success":false,"code":2001,"message":"Lost parameters.","size":0}
//        }
        else {
            showToast(getResources().getString(R.string.data_acquisition_error));
        }

    }

    @Override
    public void connectFailure() {

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
                BCAASApplication.setRealNet(true);
                return true;
            } else {
                BCAASApplication.setRealNet(false);
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
    public void httpGetLastestBlockAndBalanceSuccess() {
        LogTool.d(TAG, MessageConstants.SUCCESS_GET_LATEST_BLOCK_AND_BALANCE);

    }

    @Override
    public void httpGetLastestBlockAndBalanceFailure() {
        LogTool.d(TAG, MessageConstants.FAILURE_GET_LATEST_BLOCK_AND_BALANCE);
        if (!checkActivityState()) {
            return;
        }
        showToast(context.getResources().getString(R.string.send_fail));

    }

    @Override
    public void verifySuccess(String from) {
    }

    @Override
    public void resetAuthNodeFailure(String message, String from) {
        LogTool.d(TAG, MessageConstants.RESET_SAN_FAILURE);
    }

    @Override
    public void resetAuthNodeSuccess(String from) {

    }

    @Override
    public void verifySuccessAndResetAuthNode(String from) {
        LogTool.d(TAG, from);
        OttoTool.getInstance().post(new VerifySuccessAndResetAuthNodeEvent(from));
    }

    @Override
    public void verifyFailure(String from) {

    }

    public boolean checkActivityState() {
        return activity != null && !activity.isFinishing();
    }

    public void cleanAccountData() {
        BCAASApplication.setKeepHttpRequest(false);
        TCPThread.closeSocket(true, "cleanAccountData");
        //清空当前Token信息
        PreferenceTool.getInstance().clear(Constants.Preference.ACCESS_TOKEN);
        BCAASApplication.setWalletExternalIp("");
    }

    /**
     * 跳转登录页面
     */
    public void intentToLogin() {
        //如果當前是phone，那麼就跳轉到手機的登錄頁面，否則跳轉到TV的登錄頁面
        intentToActivity(BCAASApplication.isIsPhone() ? LoginActivity.class : LoginActivityTV.class, true);
    }

    public void showDownloadDialog() {
        hideDownloadDialog();
        bcaasDownloadDialog = new BcaasDownloadDialog(this);

        /*设置弹框点击周围不予消失*/
        bcaasDownloadDialog.setCanceledOnTouchOutside(false);
        bcaasDownloadDialog.setCancelable(false);
        /*设置弹框背景*/
//        bcaasDownloadDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_white));
        bcaasDownloadDialog.show();

    }

    public void hideDownloadDialog() {
        if (bcaasDownloadDialog != null) {
            bcaasDownloadDialog.dismiss();
            bcaasDownloadDialog.cancel();
            bcaasDownloadDialog = null;
        }

    }

    /**
     * 开始应用内下载
     */
    protected void startAppSYNCDownload() {
        LogTool.d(TAG, MessageConstants.startAppSYNCDownload);
        //检查Binder不为空的情况，开始检查读写权限
        if (mDownloadBinder != null) {
            checkWriteStoragePermission(this);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mDownloadBinder = (DownloadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mDownloadBinder = null;
        }
    };

    /**
     * 绑定下载的服务
     */
    public void bindDownloadService() {
        Intent intent = new Intent(this, DownloadService.class);
        startService(intent);
        bindService(intent, mConnection, BIND_AUTO_CREATE);//绑定服务
    }

    /**
     * 开始应用开启下载应用更新
     */
    private void startDownloadAndroidAPk() {
        showDownloadDialog();
        LogTool.d(TAG, MessageConstants.START_DOWNLOAD_ANDROID_APK + updateAndroidAPKURL);
        long downloadId = mDownloadBinder.startDownload(updateAndroidAPKURL);
        startCheckProgress(downloadId);
    }

    /**
     * 检查当前读写权限
     *
     * @param activity
     */
    public void checkInstallPermission(Activity activity) {
        try {
            //1:判断是否是8.0系统,是的话需要获取此权限，判断开没开，没开的话处理未知应用来源权限问题,否则直接安装
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //2:检测是否有写的权限
                boolean permission = getPackageManager().canRequestPackageInstalls();
                if (permission) {
                    startDownloadAndroidAPk();
                } else {
//                    //3:获取
//                    int permission = ActivityCompat.checkSelfPermission(activity,
//                            Manifest.permission.REQUEST_INSTALL_PACKAGES);
//                    if (permission != PackageManager.PERMISSION_GRANTED) {
//                        // 3：没有写的权限，去申请写的权限，会弹出对话框
                    ActivityCompat.requestPermissions(activity, PERMISSIONS_INSTALL,
                            Constants.REQUEST_CODE_INSTALL);
//                    } else {
//                        startDownloadAndroidAPk();
//
//                    }
                }

            } else {
                startDownloadAndroidAPk();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogTool.e(TAG, e.getMessage());
        }
    }

    /**
     * 检查当前读写权限
     *
     * @param activity
     */
    public void checkWriteStoragePermission(Activity activity) {
        LogTool.d(TAG, MessageConstants.CHECK_WRITE_STORAGE_PERMISSION);
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                        Constants.REQUEST_CODE_EXTERNAL_STORAGE);
            } else {
                checkInstallPermission(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogTool.e(TAG, e.getMessage());
        }
    }

    //开始监听进度
    private void startCheckProgress(final long downloadId) {
        LogTool.d(TAG, MessageConstants.DOWNLOAD_ID + downloadId);
        Observable
                //无限轮询,准备查询进度,在io线程执行
                .interval(100, 200, TimeUnit.MILLISECONDS, Schedulers.io())
                .filter(aLong -> mDownloadBinder != null)
                .map(aLong -> mDownloadBinder.getProgress(downloadId))//获得下载进度
                .takeUntil(progress -> progress >= 100)//返回true就停止了,当进度>=100就是下载完成了
                .distinct()//去重复
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ProgressObserver());
    }


    //观察者
    private class ProgressObserver implements Observer<Integer> {

        @Override
        public void onSubscribe(Disposable d) {
            mDisposable = d;
        }

        @Override
        public void onNext(Integer progress) {
            //设置进度
            if (bcaasDownloadDialog != null) {
                bcaasDownloadDialog.setProgress(progress);
            }
        }

        @Override
        public void onError(Throwable throwable) {
            throwable.printStackTrace();
            showToast(getString(R.string.install_failed));
        }

        @Override
        public void onComplete() {
            //设置进度
            if (bcaasDownloadDialog != null) {
                bcaasDownloadDialog.setProgress(100);
                hideDownloadDialog();
            }
            LogTool.d(TAG, MessageConstants.FINISH_DOWNLOAD);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.REQUEST_CODE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //这里已经获取到了摄像头的权限，想干嘛干嘛了可以
                    checkInstallPermission(this);
                } else {
                    //这里是拒绝给APP摄像头权限，给个提示什么的说明一下都可以。
                    showToast(context.getResources().getString(R.string.to_setting_grant_permission));
                }
                break;
            case Constants.REQUEST_CODE_INSTALL:
                startDownloadAndroidAPk();
                break;
        }
    }

    //显示「Receive」区块成功之后的通知
    private Toast toastNotification;

    public void showNotificationToast(String blockService, String amount) {
        if (activity == null) {
            return;
        }
        if (toastNotification != null) {
            toastNotification.cancel();
        }
        toastNotification = Toast.makeText(activity, "", Toast.LENGTH_SHORT);
        /*解决小米手机toast自带包名的问题*/
        toastNotification.setText(blockService);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_notification_toast, null);
        toastNotification.setView(view);
        TextView toastTitle = view.findViewById(R.id.toast_title);
        RelativeLayout relativeLayout = view.findViewById(R.id.rl_toast);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(BCAASApplication.getScreenWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
        relativeLayout.setLayoutParams(layoutParams);
        TextView toastContent = view.findViewById(R.id.toast_content);
        toastTitle.setText(blockService);
        toastContent.setText(amount);
        toastNotification.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);
        toastNotification.show();

        ObservableTimerTool.countDownTimerBySetTime(Constants.Time.COUNT_DOWN_NOTIFICATION, new ObservableTimerListener() {
            @Override
            public void timeUp(String from) {
                if (StringTool.equals(from, Constants.TimerType.COUNT_DOWN_NOTIFICATION))
                    if (context != null) {
                        toastNotification.cancel();
                    }
            }
        });
    }

    public void cleanQueueTask() {
        HttpIntervalRequester.disposeRequest(HttpIntervalRequester.getBalanceIntervalDisposable);
        HttpIntervalRequester.disposeRequest(HttpIntervalRequester.getReceiveBlockByIntervalDisposable);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LanguageTool.setLocal(newBase));
    }
}
