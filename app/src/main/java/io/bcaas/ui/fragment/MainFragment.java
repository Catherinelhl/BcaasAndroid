package io.bcaas.ui.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
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
import io.bcaas.event.UpdateTransactionData;
import io.bcaas.event.UpdateWalletBalance;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.listener.RefreshFragmentListener;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.GsonTool;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.NumberTool;
import io.bcaas.tools.StringTool;
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
    @BindView(R.id.ll_transaction)
    LinearLayout llTransaction;
    @BindView(R.id.ib_copy)
    ImageButton ibCopy;
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
        setBalance(BcaasApplication.getStringFromSP(Constants.Preference.WALLET_BALANCE));
        initData();
    }

    private void initData() {
        publicUnitVOList = BcaasApplication.getPublicUnitVO();
        setCurrency();
    }

    /*显示默认币种*/
    private void setCurrency() {
        //1:检测历史选中币种，如果没有，默认显示币种的第一条数据
        String blockService = BcaasApplication.getStringFromSP(Constants.Preference.BLOCK_SERVICE);
        if (ListTool.noEmpty(publicUnitVOList)) {
            if (StringTool.isEmpty(blockService)) {
                tvCurrency.setText(publicUnitVOList.get(0).getBlockService());
            } else {
                //2:是否应该去比对获取的到币种是否关闭，否则重新赋值
                String isStartUp = Constants.BlockService.CLOSE;
                for (PublicUnitVO publicUnitVO : publicUnitVOList) {
                    if (StringTool.equals(blockService, publicUnitVO.getBlockService())) {
                        isStartUp = publicUnitVO.isStartup();
                        break;
                    }
                }
                if (StringTool.equals(isStartUp, Constants.BlockService.OPEN)) {
                    tvCurrency.setText(blockService);
                } else {
                    tvCurrency.setText(publicUnitVOList.get(0).getBlockService());

                }
            }
        } else {
            tvCurrency.setText(blockService);
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
            showBalancePop(tvBalance);
            return false;
        });
        Disposable subscribe = RxView.clicks(tvCurrency)
                .throttleFirst(Constants.ValueMaps.sleepTime800, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (ListTool.isEmpty(publicUnitVOList)) {
                        showToast(getString(R.string.no_block_service));
                        return;
                    } else {
                        if (publicUnitVOList.size() == 1) {
                            //默认显示，就不需要再弹框选中了
                        } else {
                            showCurrencyListPopWindow(onItemSelectListener, publicUnitVOList);

                        }
                    }
                });
    }

    /*收到需要更新当前未签章区块的请求*/
    @Subscribe
    public void UpdateReceiveBlock(UpdateTransactionData updateReceiveBlock) {
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
//                BcaasLog.d(TAG, transactionChainVO);
//            }
//            pendingTransactionAdapter.addAll(transactionChainVOList);
//
//        }

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


    private OnItemSelectListener onItemSelectListener = new OnItemSelectListener() {
        @Override
        public <T> void onItemSelect(T type) {
            tvCurrency.setText(type.toString());
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
}