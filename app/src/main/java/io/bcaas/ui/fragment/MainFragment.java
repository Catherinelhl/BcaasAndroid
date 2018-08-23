package io.bcaas.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.adapter.PendingTransactionAdapter;
import io.bcaas.base.BaseFragment;
import io.bcaas.base.BcaasApplication;
import io.bcaas.bean.TransactionsBean;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * '首页
 */
public class MainFragment extends BaseFragment {
    @BindView(R.id.tvMyAccountAddressValue)
    TextView tvMyAccountAddressValue;
    @BindView(R.id.sp_select)
    Spinner spSelect;
    @BindView(R.id.tvBalance)
    TextView tvBalance;
    @BindView(R.id.rvPendingTransaction)
    RecyclerView rvPendingTransaction;

    private String balance;//当前币种下面的余额

    private ArrayAdapter adapter;
    private PendingTransactionAdapter pendingTransactionAdapter;//待交易数据
    private List<TransactionsBean> transactionsBeanList;

    public static MainFragment newInstance() {
        MainFragment mainFragment = new MainFragment();
        return mainFragment;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.frg_main;
    }

    @Override
    public void initViews(View view) {
        initTransactionList();
        spSelect = view.findViewById(R.id.sp_select);
        tvMyAccountAddressValue.setText(BcaasApplication.getWalletAddress());
        initSpinnerAdapter();
        initTransactionsAdapter();
        getCurrentCurrency();
    }

    private void getCurrentCurrency() {
        if (getCurrency() == null) return;
        if (getCurrency().size() > 0) {
            tvBalance.setText(getAllTransactionData().get(0).getBalance());

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

    private void initTransactionList() {
        transactionsBeanList = new ArrayList<>();
        // TODO: 2018/8/17 暂时是假数据
        for (int i = 0; i < 50; i++) {
            TransactionsBean transactionsBean = new TransactionsBean(i + "asdkjfbakjhsdvfjahvfaghvdfh==",
                    getAllTransactionData().get(i % getAllTransactionData().size()).getBalance(),
                    getCurrency().get(i % getCurrency().size()));
            transactionsBeanList.add(transactionsBean);
        }
    }

    private void initTransactionsAdapter() {
        pendingTransactionAdapter = new PendingTransactionAdapter(this.context, transactionsBeanList);
        rvPendingTransaction.setHasFixedSize(true);
        rvPendingTransaction.setLayoutManager(new LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false));
        rvPendingTransaction.setAdapter(pendingTransactionAdapter);
    }


    @Override
    public void initListener() {
        //添加事件Spinner事件监听
        spSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvBalance.setText(getAllTransactionData().get(position).getBalance());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });
    }

}