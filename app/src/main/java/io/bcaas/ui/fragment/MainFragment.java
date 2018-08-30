package io.bcaas.ui.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.bcaas.R;
import io.bcaas.adapter.PendingTransactionAdapter;
import io.bcaas.base.BaseFragment;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.event.UpdateTransactionData;
import io.bcaas.event.UpdateWalletBalance;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.NumberTool;
import io.bcaas.tools.StringTool;
import io.bcaas.view.pop.BalancePopWindow;
import io.bcaas.vo.TransactionChainVO;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 「首页」
 */
public class MainFragment extends BaseFragment {
    @BindView(R.id.tv_currency)
    TextView tvCurrency;
    Unbinder unbinder;
    @BindView(R.id.btn_select_currency)
    Button btnSelectCurrency;
    private String TAG = MainFragment.class.getSimpleName();
    @BindView(R.id.tv_account_address_value)
    TextView tvMyAccountAddressValue;
    @BindView(R.id.tv_balance)
    TextView tvBalance;
    @BindView(R.id.rvPendingTransaction)
    RecyclerView rvPendingTransaction;
    @BindView(R.id.ll_transaction)
    LinearLayout llTransaction;
    @BindView(R.id.ib_copy)
    ImageButton ibCopy;
    @BindView(R.id.pb_balance)
    ProgressBar progressBar;

    private PendingTransactionAdapter pendingTransactionAdapter;//待交易数据
    private List<TransactionChainVO> transactionChainVOList;

    public static MainFragment newInstance() {
        MainFragment mainFragment = new MainFragment();
        return mainFragment;
    }

    @Override
    public void getArgs(Bundle bundle) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.frg_main;
    }

    @Override
    public void initViews(View view) {
        transactionChainVOList = new ArrayList<>();
        tvMyAccountAddressValue.setText(BcaasApplication.getWalletAddress());
        initTransactionsAdapter();
        setBalance(BcaasApplication.getWalletBalance());
        initData();
    }

    private void initData() {
        if (ListTool.noEmpty(getCurrency())) {
            tvCurrency.setText(getCurrency().get(0));
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
            tvBalance.setText(NumberTool.getBalance(balance));
        }
    }

    private void initTransactionsAdapter() {
        pendingTransactionAdapter = new PendingTransactionAdapter(this.context, transactionChainVOList);
        rvPendingTransaction.setHasFixedSize(true);
        rvPendingTransaction.setLayoutManager(new LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false));
        rvPendingTransaction.setAdapter(pendingTransactionAdapter);
    }


    @Override
    public void initListener() {
        ibCopy.setOnClickListener(v -> {
            //获取剪贴板管理器：
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText(Constants.KeyMaps.COPY_ADDRESS, tvMyAccountAddressValue.getText());
            // 将ClipData内容放到系统剪贴板里。
            if (cm == null) return;
            cm.setPrimaryClip(mClipData);
            showToast(getString(R.string.copy_acount_address_success));

        });
        tvBalance.setOnLongClickListener(v -> {
            showBalancePop();
            return false;
        });
        Disposable subscribe = RxView.clicks(btnSelectCurrency)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    showListPopWindow(onItemSelectListener, getCurrency());
                });
    }

    /*收到需要更新当前未签章区块的请求*/
    @Subscribe
    public void UpdateReceiveBlock(UpdateTransactionData updateReceiveBlock) {
        if (updateReceiveBlock == null) return;
        List<TransactionChainVO> transactionChainVOListTemp = updateReceiveBlock.getTransactionChainVOList();
        if (ListTool.isEmpty(transactionChainVOListTemp)) {
            TransactionChainVO transactionChainVO = updateReceiveBlock.getTransactionChainVO();
            if (transactionChainVO == null) {
                //清空当前的显示数据
                pendingTransactionAdapter.notifyDataSetChanged();
                llTransaction.setVisibility(View.INVISIBLE);
            } else {
                //需要删除当前已经签章成功的交易
                if (ListTool.noEmpty(transactionChainVOList)) {
                    transactionChainVOList.remove(transactionChainVO);
                    pendingTransactionAdapter.notifyDataSetChanged();
                }
            }

        } else {
            transactionChainVOList = transactionChainVOListTemp;
            //显示R区块布局
            llTransaction.setVisibility(View.VISIBLE);
            for (TransactionChainVO transactionChainVO : transactionChainVOList) {
                BcaasLog.d(TAG, transactionChainVO);
            }
            pendingTransactionAdapter.addAll(transactionChainVOList);

        }

    }

    /*更新钱包余额*/
    @Subscribe
    public void UpdateWalletBalance(UpdateWalletBalance updateWalletBalance) {
        if (updateWalletBalance == null) {
            return;
        }
        String walletBalance = updateWalletBalance.getWalletBalance();
        setBalance(walletBalance);
    }

    /**
     * 显示
     */
    private void showBalancePop() {
        BalancePopWindow window = new BalancePopWindow(context);
        View contentView = window.getContentView();
        //需要先测量，PopupWindow还未弹出时，宽高为0
        contentView.measure(makeDropDownMeasureSpec(window.getWidth()),
                makeDropDownMeasureSpec(window.getHeight()));
        int offsetX = Math.abs(window.getContentView().getMeasuredWidth() - tvBalance.getWidth()) / 2;
        int offsetY = -(window.getContentView().getMeasuredHeight() + tvBalance.getHeight());
        PopupWindowCompat.showAsDropDown(window, tvBalance, offsetX, offsetY, Gravity.START);

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

    private OnItemSelectListener onItemSelectListener = new OnItemSelectListener() {
        @Override
        public <T> void onItemSelect(T type) {
            tvCurrency.setText(type.toString());
        }
    };
}