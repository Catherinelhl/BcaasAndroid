package io.bcaas.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import io.bcaas.R;
import io.bcaas.bean.TransactionsBean;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.GsonTool;
import io.bcaas.tools.NumberTool;
import io.bcaas.tools.StringTool;
import io.bcaas.vo.PaginationVO;
import io.bcaas.vo.TransactionChainReceiveVO;
import io.bcaas.vo.TransactionChainSendVO;
import io.bcaas.vo.TransactionChainVO;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 显示待处理的交易数据
 */
public class PendingTransactionAdapter extends
        RecyclerView.Adapter<PendingTransactionAdapter.viewHolder> {
    private String TAG = PendingTransactionAdapter.class.getSimpleName();
    private Context context;
    private List<TransactionChainVO> transactionChainVOS;

    public PendingTransactionAdapter(Context context, List<TransactionChainVO> paginationVOList) {
        this.context = context;
        this.transactionChainVOS = paginationVOList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, viewGroup, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder viewHolder, int i) {
        if (transactionChainVOS == null) return;
        TransactionChainVO transactionChainVO = transactionChainVOS.get(i);
        if (transactionChainVO == null) return;
        Gson gson = new Gson();
        Object tcObject = transactionChainVO.getTc();
        if (tcObject == null) return;
        TransactionChainSendVO transactionChainSendVO = gson.fromJson(gson.toJson(tcObject), TransactionChainSendVO.class);
        if (transactionChainSendVO == null) return;
        viewHolder.tvAccountAddress.setText(transactionChainSendVO.getWallet());
        viewHolder.tvCurrency.setText(transactionChainSendVO.getBlockService());
        String amount = transactionChainSendVO.getAmount();
        viewHolder.tvBalance.setText(NumberTool.getBalance(StringTool.isEmpty(amount) ? "0" : amount));
    }

    @Override
    public int getItemCount() {
        return transactionChainVOS.size();
    }

    public void addAll(List<TransactionChainVO> transactionChainVOList) {
        this.transactionChainVOS = transactionChainVOList;
        this.notifyDataSetChanged();
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
