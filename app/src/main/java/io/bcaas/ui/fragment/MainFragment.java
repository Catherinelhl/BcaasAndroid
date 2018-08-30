package io.bcaas.ui.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.adapter.PendingTransactionAdapter;
import io.bcaas.base.BaseFragment;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.event.UpdateTransactionData;
import io.bcaas.event.UpdateWalletBalance;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.NumberTool;
import io.bcaas.tools.StringTool;
import io.bcaas.vo.TransactionChainVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 「首页」
 */
public class MainFragment extends BaseFragment {
    private String TAG = MainFragment.class.getSimpleName();
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
    @BindView(R.id.ib_copy)
    ImageButton ibCopy;
    @BindView(R.id.pb_balance)
    ProgressBar progressBar;

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
        tvMyAccountAddressValue.setText(BcaasApplication.getWalletAddress());
        initSpinnerAdapter();
        initTransactionsAdapter();
        setBalance(BcaasApplication.getWalletBalance());
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
        spSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 选择BlockService之后，应该对当前对blockService进行Verify，然后对数据返回的结果进行余额的拿取
//                tvBalance.setText(BcaasApplication.getWalletBalance());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
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

}