package io.bcaas.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.base.BCAASApplication;
import io.bcaas.base.BaseActivity;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.event.RefreshWalletBalanceEvent;
import io.bcaas.event.RequestBlockServiceEvent;
import io.bcaas.event.SwitchBlockServiceAndVerifyEvent;
import io.bcaas.presenter.CheckWalletInfoPresenterImp;
import io.bcaas.tools.FilePathTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.OttoTool;
import io.bcaas.tools.PreferenceTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.TextTool;
import io.bcaas.ui.contracts.CheckWalletInfoContract;
import io.bcaas.view.textview.BcaasBalanceTextView;
import io.reactivex.disposables.Disposable;

import static android.support.v4.content.FileProvider.getUriForFile;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 * <p>
 * Activity：[首頁] -> [设置] -> [钱包信息] -> 检查当前的钱包信息
 */
public class CheckWalletInfoActivity extends BaseActivity implements CheckWalletInfoContract.View {
    private String TAG = CheckWalletInfoActivity.class.getSimpleName();

    @BindView(R.id.tv_currency)
    TextView tvCurrency;
    @BindView(R.id.et_private_key)
    EditText etPrivateKey;
    @BindView(R.id.cb_pwd)
    CheckBox cbPwd;
    @BindView(R.id.rl_private_key)
    RelativeLayout rlPrivateKey;
    @BindView(R.id.ib_back)
    ImageButton ibBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ib_right)
    ImageButton ibRight;
    @BindView(R.id.rl_header)
    RelativeLayout rlHeader;
    @BindView(R.id.tv_address_key)
    TextView tvMyAddressKey;
    @BindView(R.id.ib_copy)
    ImageButton ibCopy;
    @BindView(R.id.tv_account_address_value)
    TextView tvMyAccountAddressValue;
    @BindView(R.id.tv_balance_key)
    TextView tvBalanceKey;
    @BindView(R.id.bbt_balance)
    BcaasBalanceTextView bbtBalance;
    @BindView(R.id.btnSendEmail)
    Button btnSendEmail;
    @BindView(R.id.pb_balance)
    ProgressBar progressBar;
    /*可见的私钥*/
    private String visiblePrivateKey;

    private CheckWalletInfoContract.Presenter presenter;
    //当前发送邮件code
    private static int SEND_EMAIL_OK = 0x11;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    //得到需要存储当前keystore信息的文件
    private File file;


    @Override
    public int getContentView() {
        return R.layout.activity_check_wallet_info;
    }

    @Override
    public void getArgs(Bundle bundle) {
        if (bundle == null) {
            return;
        }
    }

    @Override
    public boolean full() {
        return false;
    }


    @Override
    public void initViews() {
        presenter = new CheckWalletInfoPresenterImp(this);
        setTitle();
        ibBack.setVisibility(View.VISIBLE);
        //获取当前text view占用的布局
        //1:获取屏幕的宽度
        int screenWidth = BCAASApplication.getScreenWidth();
        // 得到除去边距
        int widthExceptMargin = screenWidth - getResources().getDimensionPixelOffset(R.dimen.d44);
        LogTool.d(TAG, widthExceptMargin);
        //获取左边固定显示文本的大小
        String content = BCAASApplication.isIsZH() ? context.getResources().getString(R.string.account_address_en) : context.getResources().getString(R.string.my_account_address_en);
        float textPaintWidth = TextTool.getViewWidth(tvMyAddressKey, content);
        LogTool.d(TAG, textPaintWidth);
        float rightWidth = widthExceptMargin - textPaintWidth;
        LogTool.d(TAG, rightWidth);
        double width = rightWidth - getResources().getDimensionPixelOffset(R.dimen.d32);
        tvMyAccountAddressValue.setText(
                TextTool.intelligentOmissionText(
                        tvMyAccountAddressValue, (int) width,
                        BCAASApplication.getWalletAddress()));
        visiblePrivateKey = PreferenceTool.getInstance().getString(Constants.Preference.PRIVATE_KEY);
        if (StringTool.notEmpty(visiblePrivateKey)) {
            etPrivateKey.setText(Constants.ValueMaps.DEFAULT_PRIVATE_KEY);
            //设置editText不可编辑，但是可以复制
            etPrivateKey.setKeyListener(null);
            etPrivateKey.setSelection(visiblePrivateKey.length());
        }
        String blockService = BCAASApplication.getBlockService();
        if (StringTool.notEmpty(blockService)) {
            setBalance(BCAASApplication.getWalletBalance());
            tvCurrency.setText(blockService);
        }
    }

    /**
     * 「余额」和「加载余额」的控件默认是不显示的，进入此界面进行判断是否已经有币种，
     * 如果没有，就「币种」控件显示三个点，其他的按兵不动，等待用户选择
     * <p>
     * 对当前的余额进行赋值，如果当前没有读取到数据，那么就显示进度条，否则显示余额
     */
    private void setBalance(String balance) {
        LogTool.d(TAG, BCAASApplication.getWalletBalance());
        if (StringTool.isEmpty(balance)) {
            //隐藏显示余额的文本，展示进度条
            bbtBalance.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            bbtBalance.setVisibility(View.VISIBLE);
            bbtBalance.setBalance(balance);
        }
    }


    private void setTitle() {
        tvTitle.setText(R.string.wallet_info);
        tvTitle.setTextColor(getResources().getColor(R.color.black));
        tvTitle.setBackgroundColor(getResources().getColor(R.color.transparent));

    }

    @Override
    public void initListener() {
        ibCopy.setOnClickListener(v -> {
            //获取剪贴板管理器：
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText(Constants.KeyMaps.COPY_ADDRESS, BCAASApplication.getWalletAddress());
            // 将ClipData内容放到系统剪贴板里。
            if (cm != null) {
                cm.setPrimaryClip(mClipData);
                showToast(getString(R.string.successfully_copied));
            }
        });
        cbPwd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String text = etPrivateKey.getText().toString();
            if (StringTool.isEmpty(text)) {
                return;
            }
            if (isChecked) {
                etPrivateKey.setText(visiblePrivateKey);
            } else {
                etPrivateKey.setText(Constants.ValueMaps.DEFAULT_PRIVATE_KEY);
            }
        });
        ibBack.setOnClickListener(v -> finish());
        Disposable subscribeSendEmail = RxView.clicks(btnSendEmail)
                .throttleFirst(Constants.Time.sleep800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    getExternalFile();
                    if (file != null) {
                        if (BCAASApplication.isRealNet()) {
                            checkWriteStoragePermission(CheckWalletInfoActivity.this);
                        } else {
                            showToast(getResources().getString(R.string.network_not_reachable));
                        }
                    } else {
                        // 如果当前文件夹返回的文件信息为空，提示当前不能发送邮件
                        showToast(getResources().getString(R.string.cannot_send_email));
                    }

                });
        Disposable subscribeCurrency = RxView.clicks(tvCurrency)
                .throttleFirst(Constants.Time.sleep800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    //重新请求币种信息
                    OttoTool.getInstance().post(new RequestBlockServiceEvent(Constants.From.CHECK_WALLET_INFO));
                    showCurrencyListPopWindow(Constants.From.CHECK_WALLET_INFO);
                });
    }

    /*得到一个存储当前keystore的文件地址*/
    private void getExternalFile() {
        //得到需要创建的文件夹
        File rootFile = new File(getExternalFilesDir(Constants.KeyMaps.BCAAS_DIR_NAME).getAbsolutePath());
        if (!rootFile.exists()) {
            rootFile.mkdir();
        }
        //得到需要创建用来存储信息的文件,文件夹是一个以当前钱包地址命名的txt
        file = new File(rootFile, FilePathTool.getKeyStoreFileName(BCAASApplication.getWalletAddress()));
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                LogTool.e(TAG, e.getMessage());
            }
        }
    }


    @Override
    public void getWalletFileSuccess() {
        sendEmail();
    }

    @Override
    public void getWalletFileFailed() {
        showToast(getResources().getString(R.string.account_data_error));
    }


    /**
     * 检查当前读写权限
     *
     * @param activity
     */
    public void checkWriteStoragePermission(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            } else {
                presenter.getWalletFileFromDB(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogTool.d(TAG, e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //这里已经获取到了摄像头的权限，想干嘛干嘛了可以
                    LogTool.d(TAG, MessageConstants.HAD_WRITE_PERMISSION);
                    presenter.getWalletFileFromDB(file);
                } else {
                    //这里是拒绝给APP摄像头权限，给个提示什么的说明一下都可以。
                    LogTool.d(TAG, MessageConstants.WRITE_PERMISSION_REFUSED);
                }
                break;
        }
    }

    /**
     * 发送邮件
     */
    private void sendEmail() {
        //如果当前手机版本7.0以上，需要根据规则利用fileprovider来send
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri uri = getUriForFile(this, getPackageName() + Constants.ValueMaps.FILE_PROVIDER, file);
            Intent intent = new Intent(Intent.ACTION_SEND);
            //当无法确认发送类型的时候使用如下语句
            intent.setType(Constants.ValueMaps.EMAIL_TYPE);
            LogTool.d(TAG, uri);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivityForResult(intent, SEND_EMAIL_OK);
        } else {
            Intent intent = new Intent(Intent.ACTION_SEND);
            //当无法确认发送类型的时候使用如下语句
            intent.setType(Constants.ValueMaps.EMAIL_TYPE);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            startActivityForResult(intent, SEND_EMAIL_OK);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEND_EMAIL_OK) {
            switch (resultCode) {
                case RESULT_OK:
                    showToast(getResources().getString(R.string.send_success));
                    break;
            }
        } else {
            showToast(getResources().getString(R.string.send_fail));
        }
    }

    @Override
    public void showLoading() {
        if (!checkActivityState()) {
            return;
        }
        showLoadingDialog();
    }

    @Override
    public void hideLoading() {
        if (!checkActivityState()) {
            return;
        }
        hideLoadingDialog();
    }

    @Subscribe
    public void UpdateWalletBalance(RefreshWalletBalanceEvent updateWalletBalanceEvent) {
        if (updateWalletBalanceEvent == null) {
            return;
        }
        Constants.EventSubscriber subscriber = updateWalletBalanceEvent.getSubscriber();
        if (subscriber == Constants.EventSubscriber.CHECK_WALLET_INFO || subscriber == Constants.EventSubscriber.ALL) {
            setBalance(BCAASApplication.getWalletBalance());
        }
    }

    @Subscribe
    public void UpdateBlockService(SwitchBlockServiceAndVerifyEvent switchBlockServiceAndVerifyEvent) {
        if (switchBlockServiceAndVerifyEvent != null) {
            String blockService = BCAASApplication.getBlockService();
            if (StringTool.notEmpty(blockService) && tvCurrency != null) {
                tvCurrency.setText(blockService);
                bbtBalance.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

            }
        }
    }
}
