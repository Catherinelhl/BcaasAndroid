package io.bcaas.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.bcaas.R;
import io.bcaas.bean.TransactionsBean;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 显示待处理的交易数据
 */
public class PendingTransactionAdapter extends RecyclerView.Adapter<PendingTransactionAdapter.viewHolder> {
    private Context context;
    private List<TransactionsBean> transactionsBeanList;

    public PendingTransactionAdapter(Context context, List<TransactionsBean> transactionsBeanList) {
        this.context = context;
        this.transactionsBeanList = transactionsBeanList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, viewGroup, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder viewHolder, int i) {
        if (transactionsBeanList == null) return;
        TransactionsBean transactionsBean = transactionsBeanList.get(i);
        if (transactionsBean == null) return;
        viewHolder.tvAccountAddress.setText(transactionsBean.getAccountAddress());
        viewHolder.tvCurrency.setText(transactionsBean.getCurrency());
        viewHolder.tvBalance.setText(transactionsBean.getBalance());

    }

    @Override
    public int getItemCount() {
        return transactionsBeanList.size();
    }


    class viewHolder extends RecyclerView.ViewHolder {
        private TextView tvAccountAddress;
        private TextView tvCurrency;
        private TextView tvBalance;

        public viewHolder(View view) {
            super(view);
            tvAccountAddress = view.findViewById(R.id.tvAccountAddress);
            tvCurrency = view.findViewById(R.id.tvCurrency);
            tvBalance = view.findViewById(R.id.tvBalance);

        }
    }

}
