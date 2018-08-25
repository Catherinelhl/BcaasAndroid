package io.bcaas.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.common.base.Verify;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.adapter.PendingTransactionAdapter;
import io.bcaas.base.BaseFragment;
import io.bcaas.base.BcaasApplication;
import io.bcaas.event.UpdateReceiveBlock;
import io.bcaas.event.UpdateWalletBalance;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.ListTool;
import io.bcaas.vo.TransactionChainVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 「首页」
 */
public class MainFragment extends BaseFragment {
    private String TAG = "MainFragment";
    @BindView(R.id.tvMyAccountAddressValue)
    TextView tvMyAccountAddressValue;
    @BindView(R.id.sp_select)
    Spinner spSelect;
    @BindView(R.id.tvBalance)
    TextView tvBalance;
    @BindView(R.id.rvPendingTransaction)
    RecyclerView rvPendingTransaction;
    @BindView(R.id.ll_transaction)
    LinearLayout llTransaction;

    private ArrayAdapter adapter;
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
        spSelect = view.findViewById(R.id.sp_select);
        tvMyAccountAddressValue.setText(BcaasApplication.getWalletAddress());
        initSpinnerAdapter();
        initTransactionsAdapter();
        tvBalance.setText(BcaasApplication.getWalletAddress());

    }


    private void initSpinnerAdapter() {
        //将可选内容与ArrayAdapter连接起来
        adapter = new ArrayAdapter<>(this.context, R.layout.spinner_item, getCurrency());
        //设置下拉列表的风格
        adapter.setDropDownViewResource(R.layout.dropdown_style);
        //将adapter 添加到spinner中
        spSelect.setAdapter(adapter);
    }

    private void initTransactionsAdapter() {
        pendingTransactionAdapter = new PendingTransactionAdapter(this.context, transactionChainVOList);
        rvPendingTransaction.setHasFixedSize(true);
        rvPendingTransaction.setLayoutManager(new LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false));
        rvPendingTransaction.setAdapter(pendingTransactionAdapter);
    }


    @Override
    public void initListener() {
        spSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 选择BlockService之后，应该对当前对blockService进行Verify，然后对数据返回的结果进行余额的拿取
                tvBalance.setText(BcaasApplication.getWalletBalance());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /*收到需要更新当前未签章区块的请求*/
    @Subscribe
    public void UpdateReceiveBlock(UpdateReceiveBlock updateReceiveBlock) {
        if (updateReceiveBlock == null) return;
        transactionChainVOList = updateReceiveBlock.getTransactionChainVOList();
        if (ListTool.isEmpty(transactionChainVOList)) {
            //清空当前的显示数据
            adapter.clear();
            llTransaction.setVisibility(View.INVISIBLE);
        } else {
            //显示R区块布局
            llTransaction.setVisibility(View.VISIBLE);
            for (TransactionChainVO transactionChainVO : transactionChainVOList) {
                BcaasLog.d(TAG, transactionChainVO);
            }
        }

    }

    /*更新钱包余额*/
    @Subscribe
    public void UpdateWalletBalance(UpdateWalletBalance updateWalletBalance) {
        if (updateWalletBalance == null) return;
        String walletBalance = updateWalletBalance.getWalletBalance();
        tvBalance.setText(walletBalance);
    }

}