package io.bcaas.ui.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.adapter.PendingTransactionAdapter;
import io.bcaas.base.BaseFragment;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.event.UpdateBlockServiceEvent;
import io.bcaas.event.UpdateTransactionEvent;
import io.bcaas.event.UpdateWalletBalanceEvent;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.listener.RefreshFragmentListener;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.NumberTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.ecc.WalletTool;
import io.bcaas.ui.activity.MainActivity;
import io.bcaas.vo.PublicUnitVO;
import io.bcaas.vo.TransactionChainVO;
import io.reactivex.disposables.Disposable;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 「首页」
 */
public class MainFragment extends BaseFragment implements RefreshFragmentListener {
    @BindView(R.id.tv_currency)
    TextView tvCurrency;
    private String TAG = MainFragment.class.getSimpleName();
    @BindView(R.id.tv_account_address_value)
    TextView tvMyAccountAddressValue;
    @BindView(R.id.tv_balance)
    TextView tvBalance;
    @BindView(R.id.rvPendingTransaction)
    RecyclerView rvPendingTransaction;
    @BindView(R.id.rl_transaction)
    RelativeLayout rlTransaction;
    @BindView(R.id.ll_select_currency)
    LinearLayout llSelectCurrency;
    @BindView(R.id.iv_no_record)
    ImageView ivNoRecord;
    @BindView(R.id.iv_copy)
    ImageView ivCopy;
    @BindView(R.id.tv_no_transaction_record)
    TextView tvNoTransactionRecord;
    @BindView(R.id.pb_balance)
    ProgressBar progressBar;

    private PendingTransactionAdapter pendingTransactionAdapter;//待交易数据
    private List<TransactionChainVO> transactionChainVOList;
    private List<PublicUnitVO> publicUnitVOList;

    public static MainFragment newInstance() {
        MainFragment mainFragment = new MainFragment();
        return mainFragment;
    }

    @Override
    public void getArgs(Bundle bundle) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_main;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        MainActivity mainActivity = ((MainActivity) context);
        mainActivity.setRefreshFragmentListener(this);
    }

    @Override
    public void initViews(View view) {
        transactionChainVOList = new ArrayList<>();
        tvMyAccountAddressValue.setText(BcaasApplication.getWalletAddress());
        initTransactionsAdapter();
        setBalance(BcaasApplication.getWalletBalance());
        initData();
        noTransactionRecord();
    }

    /*没有交易记录*/
    private void noTransactionRecord() {
        ivNoRecord.setVisibility(View.VISIBLE);
        rvPendingTransaction.setVisibility(View.GONE);
        tvNoTransactionRecord.setVisibility(View.VISIBLE);
    }

    private void initData() {
        publicUnitVOList = WalletTool.getPublicUnitVO();
        setCurrency();
    }

    /*显示默认币种*/
    private void setCurrency() {
        if (activity == null || tvCurrency == null) {
            return;
        }
        tvCurrency.setText(WalletTool.getDisplayBlockService(publicUnitVOList));
    }

    //对当前的余额进行赋值，如果当前没有读取到数据，那么就显示进度条，否则显示余额
    private void setBalance(String balance) {
        if (StringTool.isEmpty(balance)) {
            //隐藏显示余额的文本，展示进度条
            tvBalance.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            tvBalance.setVisibility(View.VISIBLE);
            tvBalance.setText(NumberTool.formatNumber(balance));
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
        ivCopy.setOnClickListener(v -> {
            //获取剪贴板管理器：
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText(Constants.KeyMaps.COPY_ADDRESS, tvMyAccountAddressValue.getText());
            // 将ClipData内容放到系统剪贴板里。
            if (cm != null) {
                cm.setPrimaryClip(mClipData);
                showToast(getString(R.string.successfully_copied));
            }
        });
        tvBalance.setOnClickListener(v -> {
            showBalancePop(tvBalance);
        });
        Disposable subscribe = RxView.clicks(llSelectCurrency)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    showCurrencyListPopWindow(onItemSelectListener, publicUnitVOList);

                });
    }

    /*收到需要更新当前未签章区块的请求*/
    @Subscribe
    public void UpdateReceiveBlock(UpdateTransactionEvent updateReceiveBlock) {
        if (updateReceiveBlock == null) return;
        //暫時去掉首頁的待交易區塊
//        List<TransactionChainVO> transactionChainVOListTemp = updateReceiveBlock.getTransactionChainVOList();
//        if (ListTool.isEmpty(transactionChainVOListTemp)) {
//            TransactionChainVO transactionChainVO = updateReceiveBlock.getTransactionChainVO();
//            if (transactionChainVO == null) {
//                //清空当前的显示数据
//                pendingTransactionAdapter.notifyDataSetChanged();
//                llTransaction.setVisibility(View.INVISIBLE);
//            } else {
//                //需要删除当前已经签章成功的交易
//                if (ListTool.noEmpty(transactionChainVOList)) {
//                    transactionChainVOList.remove(transactionChainVO);
//                    pendingTransactionAdapter.notifyDataSetChanged();
//                }
//            }
//
//        } else {
//            transactionChainVOList = transactionChainVOListTemp;
//            //显示R区块布局
//            llTransaction.setVisibility(View.VISIBLE);
//            for (TransactionChainVO transactionChainVO : transactionChainVOList) {
//                LogTool.d(TAG, transactionChainVO);
//            }
//            pendingTransactionAdapter.addAll(transactionChainVOList);
//
//        }

    }

    /*更新钱包余额*/
    @Subscribe
    public void UpdateWalletBalance(UpdateWalletBalanceEvent updateWalletBalanceEvent) {
        if (updateWalletBalanceEvent == null) {
            return;
        }
        String walletBalance = updateWalletBalanceEvent.getWalletBalance();
        setBalance(BcaasApplication.getWalletBalance());
    }


    /*币种重新选择返回*/
    private OnItemSelectListener onItemSelectListener = new OnItemSelectListener() {
        @Override
        public <T> void onItemSelect(T type) {
            if (type != null) {
                /*显示币种*/
                tvCurrency.setText(type.toString());
                /*存储币种*/
                BcaasApplication.setBlockService(type.toString());
                /*重新verify，获取新的区块数据*/
                if (activity != null) {
                    ((MainActivity) activity).verify();
                }
                /*重置余额*/
                BcaasApplication.setWalletBalance("");
                tvBalance.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }
        }
    };

    /*刷新当前清单*/
    @Override
    public void refreshBlockService(List<PublicUnitVO> publicUnitVOS) {
        if (ListTool.noEmpty(publicUnitVOS)) {
            this.publicUnitVOList = publicUnitVOS;
            setCurrency();
        }
    }

    @Subscribe
    public void updateBlockService(UpdateBlockServiceEvent updateBlockServiceEvent) {
        if (activity != null && tvCurrency != null) {
            tvCurrency.setText(BcaasApplication.getBlockService());

        }
    }
}