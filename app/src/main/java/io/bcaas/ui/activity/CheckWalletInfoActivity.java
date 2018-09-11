package io.bcaas.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.event.CheckVerifyEvent;
import io.bcaas.event.UpdateWalletBalanceEvent;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.presenter.CheckWalletInfoPresenterImp;
import io.bcaas.tools.FilePathTool;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.NumberTool;
import io.bcaas.tools.OttoTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.TextTool;
import io.bcaas.tools.ecc.WalletTool;
import io.bcaas.ui.contracts.CheckWalletInfoContract;
import io.bcaas.vo.PublicUnitVO;
import io.reactivex.disposables.Disposable;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 * <p>
 * [设置] -> [钱包信息] -> 检查当前的钱包信息
 */
public class CheckWalletInfoActivity extends BaseActivity implements CheckWalletInfoContract.View {

    @BindView(R.id.tv_currency)
    TextView tvCurrency;
    @BindView(R.id.ll_currency)
    LinearLayout llCurrency;
    @BindView(R.id.et_private_key)
    EditText etPrivateKey;
    @BindView(R.id.cbPwd)
    CheckBox cbPwd;
    @BindView(R.id.rl_private_key)
    RelativeLayout rlPrivateKey;
    private String TAG = CheckWalletInfoActivity.class.getSimpleName();
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
    @BindView(R.id.btn_copy)
    Button btnCopy;
    @BindView(R.id.tv_account_address_value)
    TextView tvMyAccountAddressValue;
    @BindView(R.id.tv_balance_key)
    TextView tvBalanceKey;
    @BindView(R.id.tv_balance)
    TextView tvBalance;
    @BindView(R.id.btnSendEmail)
    Button btnSendEmail;
    @BindView(R.id.pb_balance)
    ProgressBar progressBar;
    private List<PublicUnitVO> publicUnitVOS;
    /*可见的私钥*/
    private String visiblePrivateKey;

    private CheckWalletInfoContract.Presenter presenter;
    //当前发送邮件code
    private static int SEND_EMAIL_OK = 0x11;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

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
        publicUnitVOS = new ArrayList<>();
        setTitle();
        ibBack.setVisibility(View.VISIBLE);
        //获取当前text view占用的布局
        double width = BcaasApplication.getScreenWidth() - (BcaasApplication.getScreenWidth() - getResources().getDimensionPixelOffset(R.dimen.d42)) / 2 - getResources().getDimensionPixelOffset(R.dimen.d36);
        tvMyAccountAddressValue.setText(TextTool.intelligentOmissionText(tvMyAccountAddressValue, (int) width, BcaasApplication.getWalletAddress()));
        visiblePrivateKey = BcaasApplication.getStringFromSP(Constants.Preference.PRIVATE_KEY);
        etPrivateKey.setFocusable(false);
        if (StringTool.notEmpty(visiblePrivateKey)) {
            etPrivateKey.setText(Constants.ValueMaps.PRIVATE_KEY);
            etPrivateKey.setSelection(visiblePrivateKey.length());
        }
        LogTool.d(TAG, BcaasApplication.getWalletBalance());
        setBalance(BcaasApplication.getWalletBalance());
        setCurrency();
    }

    /*显示默认币种*/
    private void setCurrency() {
        publicUnitVOS = BcaasApplication.getPublicUnitVO();
        //1:检测历史选中币种，如果没有，默认显示币种的第一条数据
        String blockService = BcaasApplication.getBlockService();
        if (ListTool.noEmpty(publicUnitVOS)) {
            if (StringTool.isEmpty(blockService)) {
                tvCurrency.setText(publicUnitVOS.get(0).getBlockService());
            } else {
                //2:是否应该去比对获取的到币种是否关闭，否则重新赋值
                String isStartUp = Constants.BlockService.CLOSE;
                for (PublicUnitVO publicUnitVO : publicUnitVOS) {
                    if (StringTool.equals(blockService, publicUnitVO.getBlockService())) {
                        isStartUp = publicUnitVO.isStartup();
                        break;
                    }
                }
                if (StringTool.equals(isStartUp, Constants.BlockService.OPEN)) {
                    tvCurrency.setText(blockService);
                } else {
                    tvCurrency.setText(publicUnitVOS.get(0).getBlockService());

                }
            }
        } else {
            //当前币种信息为空时，显示默认blockService
            tvCurrency.setText(Constants.BLOCKSERVICE_BCC);
        }

    }

    //对当前的余额进行赋值，如果当前没有读取到数据，那么就显示进度条，否则显示余额
    private void setBalance(String balance) {
        if (StringTool.isEmpty(balance)) {
            //隐藏显示余额的文本，展示进度条
            tvBalance.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            tvBalance.setVisibility(View.VISIBLE);
            tvBalance.setText(NumberTool.formatNumber(balance));
        }
    }


    private void setTitle() {
        tvTitle.setText(R.string.wallet_info);
        tvTitle.setTextColor(getResources().getColor(R.color.black));
        tvTitle.setBackgroundColor(getResources().getColor(R.color.transparent));

    }

    @Override
    public void initListener() {
        btnCopy.setOnClickListener(v -> {
            //获取剪贴板管理器：
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText(Constants.KeyMaps.COPY_ADDRESS, tvMyAccountAddressValue.getText());
            // 将ClipData内容放到系统剪贴板里。
            if (cm == null) return;
            cm.setPrimaryClip(mClipData);
            showToast(getString(R.string.successfully_copied));

        });
        etPrivateKey.setOnLongClickListener(view -> {
            String privateKey = etPrivateKey.getText().toString();
            if (cbPwd.isChecked()) {
                if (StringTool.notEmpty(privateKey)) {
                    showDetailPop(etPrivateKey, privateKey);
                }
            }
            return false;
        });
        cbPwd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String text = etPrivateKey.getText().toString();
            if (StringTool.isEmpty(text)) {
                return;
            }
            if (isChecked) {
                etPrivateKey.setText(visiblePrivateKey);
            } else {
                etPrivateKey.setText(Constants.ValueMaps.PRIVATE_KEY);
            }
        });
        ibBack.setOnClickListener(v -> finish());
        Disposable subscribeSendEmail = RxView.clicks(btnSendEmail)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> checkWriteStoragePermission(CheckWalletInfoActivity.this));
        Disposable subscribeCurrency = RxView.clicks(tvCurrency)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (ListTool.isEmpty(publicUnitVOS)) {
                        publicUnitVOS.add(WalletTool.getDefaultBlockService());
                        return;
                    } else {
                        showCurrencyListPopWindow(onItemSelectListener, publicUnitVOS);
                    }
                });
        tvBalance.setOnLongClickListener(v -> {
            showBalancePop(tvBalance);
            return false;
        });

    }

    /*重新选择币种返回监听*/
    private OnItemSelectListener onItemSelectListener = new OnItemSelectListener() {
        @Override
        public <T> void onItemSelect(T type) {
            if (type != null) {
                /*设置当前选择的币种*/
                tvCurrency.setText(type.toString());
                /*存储币种*/
                BcaasApplication.setBlockService(type.toString());
                /*重新verify，获取新的区块数据*/
                OttoTool.getInstance().post(new CheckVerifyEvent());
                /*重置余额*/
                BcaasApplication.setWalletBalance("");
                tvBalance.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }
        }
    };

    @Subscribe
    public void UpdateWalletBalance(UpdateWalletBalanceEvent updateWalletBalanceEvent) {
        if (updateWalletBalanceEvent == null) {
            return;
        }
        String walletBalance = updateWalletBalanceEvent.getWalletBalance();
        setBalance(BcaasApplication.getWalletBalance());
    }

    @Override
    public void getWalletFileSuccess() {
        sendEmail();
    }

    @Override
    public void getWalletFileFailed() {
        showToast(getResources().getString(R.string.account_data_error));
    }

    @Override
    public void walletDamage() {
        showToast(getResources().getString(R.string.keystore_is_damaged));
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
                presenter.getWalletFileFromDB();
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
                    LogTool.d(TAG, "我已经获取权限了");
                    presenter.getWalletFileFromDB();
                } else {
                    //这里是拒绝给APP摄像头权限，给个提示什么的说明一下都可以。
                    LogTool.d(TAG, "我被拒绝获取权限了");
                }
                break;
        }
    }

    /**
     * 发送邮件
     */
    private void sendEmail() {
        Uri uri = FileProvider.getUriForFile(this,
                getPackageName() + Constants.ValueMaps.FILEPROVIDER,
                new File(FilePathTool.getKeyStoreFileName(BcaasApplication.getWalletAddress())));

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Bcaas钱包文件");
        intent.putExtra(Intent.EXTRA_TEXT, "请妥善保存");
        intent.setType(Constants.ValueMaps.EMAIL_TYPE);
//        intent.setType(“*/*”);
        System.out.println(uri);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivityForResult(intent, SEND_EMAIL_OK);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEND_EMAIL_OK) {
            showToast(getResources().getString(R.string.send_success));
        } else {
            showToast(getResources().getString(R.string.send_fail));

        }
    }
}
