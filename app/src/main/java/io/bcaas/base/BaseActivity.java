package io.bcaas.base;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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
import io.bcaas.event.ToLogin;
import io.bcaas.gson.ResponseJson;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.listener.SoftKeyBroadManager;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.NumberTool;
import io.bcaas.tools.OttoTool;
import io.bcaas.tools.StringTool;
import io.bcaas.ui.contracts.BaseContract;
import io.bcaas.view.dialog.BcaasDialog;
import io.bcaas.view.dialog.BcaasLoadingDialog;
import io.bcaas.view.dialog.BcaasSingleDialog;
import io.bcaas.view.pop.ShowDetailPopWindow;
import io.bcaas.view.pop.ListPopWindow;
import io.bcaas.vo.PublicUnitVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 */
public abstract class BaseActivity extends FragmentActivity implements BaseContract.View {

    private String TAG = BaseActivity.class.getSimpleName();
    private Unbinder unbinder;
    private BcaasDialog bcaasDialog;
    private BcaasSingleDialog bcaasSingleDialog;
    private BcaasLoadingDialog bcaasLoadingDialog;
    private ListPopWindow listPopWindow;
    protected Context context;
    private InputMethodManager inputMethodManager;
    private long lastClickBackTime = 0L;//存儲當前點擊返回按鍵的時間，用於提示連續點擊兩次才能退出
    protected SoftKeyBroadManager softKeyBroadManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getArgs(getIntent().getExtras());
        setContentView(getContentView());
        context = getApplicationContext();
        unbinder = ButterKnife.bind(this);
        OttoTool.getInstance().register(this);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        initViews();
        initListener();
    }

    public abstract int getContentView();

    public abstract void getArgs(Bundle bundle);

    public abstract void initViews();

    public abstract void initListener();

    public void showToast(int res) {
        showToast(String.valueOf(res));
    }

    public void showToast(final String toastInfo) {
        BcaasLog.d(TAG, toastInfo);
        Toast.makeText(BcaasApplication.context(), toastInfo, Toast.LENGTH_SHORT).show();
    }

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
        if (softKeyBroadManager != null && softKeyboardStateListener != null) {
            //注意销毁时，得移除监听
            softKeyBroadManager.removeSoftKeyboardStateListener(softKeyboardStateListener);
        }
        unbinder.unbind();
        OttoTool.getInstance().unregister(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void showLoadingDialog(String loading) {
        // TODO: 2018/8/17 需要自定义一个加载弹框
    }

    @Override
    public void hideLoadingDialog() {

    }

    @Override
    public void success(String message) {
        BcaasLog.d(TAG, message);
    }

    /**
     * 显示对话框
     *
     * @param message
     * @param listener
     */
    public void showBcaasDialog(String message, final BcaasDialog.ConfirmClickListener listener) {
        showBcaasDialog(getResources().getString(R.string.warning),
                getResources().getString(R.string.sure),
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
                getResources().getString(R.string.sure),
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
    private void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = alpha;
        getWindow().setAttributes(lp);
    }

    public void showBalancePop(View view) {
        showDetailPop(view, NumberTool.getBalance(BcaasApplication.getWalletBalance()));
    }

    /**
     * 顯示完整的信息：金额/地址/私钥
     *
     * @param view 需要依賴的視圖
     */
    public void showDetailPop(View view, String content) {
        ShowDetailPopWindow window = new ShowDetailPopWindow(context, content);
        View contentView = window.getContentView();
        //需要先测量，PopupWindow还未弹出时，宽高为0
        contentView.measure(makeDropDownMeasureSpec(window.getWidth()),
                makeDropDownMeasureSpec(window.getHeight()));
        int offsetX = Math.abs(window.getContentView().getMeasuredWidth() - view.getWidth()) / 2;
        int offsetY = -(window.getContentView().getMeasuredHeight() + view.getHeight());
//        PopupWindowCompat.showAsDropDown(window, view, offsetX, offsetY, Gravity.START);
        window.showAsDropDown(view, offsetX, offsetY, Gravity.START);

    }

    @SuppressWarnings("ResourceType")
    private static int makeDropDownMeasureSpec(int measureSpec) {
        int mode;
        if (measureSpec == ViewGroup.LayoutParams.WRAP_CONTENT) {
            mode = View.MeasureSpec.UNSPECIFIED;
        } else {
            mode = View.MeasureSpec.EXACTLY;
        }
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), mode);
    }

    /*獲取當前語言環境*/
    protected String getCurrentLanguage() {
        // 1：檢查應用是否已經有用戶自己存儲的語言種類
        String currentString = BcaasApplication.getStringFromSP(Constants.Preference.LANGUAGE_TYPE);
        BcaasLog.d(TAG, currentString);
        if (StringTool.isEmpty(currentString)) {
            //2:當前的選中為空，那麼就默認讀取當前系統的語言環境
            Locale locale = getResources().getConfiguration().locale;
            //locale.getLanguage();//zh  是中國
            currentString = locale.getCountry();//CN-簡體中文，TW、HK-繁體中文
        }
        //3:匹配當前的語言獲取，返回APP裡面識別的TAG
        if (StringTool.equals(currentString, Constants.ValueMaps.CN)) {
            return currentString;
        } else if (StringTool.equals(currentString, Constants.ValueMaps.TW) || StringTool.equals(currentString, Constants.ValueMaps.HK)) {
            return Constants.ValueMaps.TW;
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
            case Constants.ValueMaps.TW:
                config.locale = Locale.TAIWAN; // 繁體中文
                break;
            case Constants.ValueMaps.EN:
                config.locale = Locale.ENGLISH; // 英文
                break;
        }
        resources.updateConfiguration(config, dm);
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
        if (responseJson == null) {
            return;
        }
        BcaasLog.e(TAG, responseJson.getMessage());
        int code = responseJson.getCode();
        if (code == MessageConstants.CODE_3006 || code == MessageConstants.CODE_3008) {
            showBcaasSingleDialog(getString(R.string.warning),
                    getString(R.string.please_login_again), () -> OttoTool.getInstance().post(new ToLogin()));
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
            BcaasLog.d(TAG, keyboardHeightInPx);
//            int[] location = new int[2];
//            //获取scrollToView在窗体的坐标
//            vSpace.getLocationInWindow(location);
//            //计算root滚动高度，使scrollToView在可见区域
//            int scrollHeight = (location[1] + vSpace.getHeight()) - bottom;
//            llCreateWallet.scrollTo(0, scrollHeight);
        }

        @Override
        public void onSoftKeyboardClosed() {
            //键盘隐藏
//            llCreateWallet.scrollTo(0, 0);

        }
    };

}
