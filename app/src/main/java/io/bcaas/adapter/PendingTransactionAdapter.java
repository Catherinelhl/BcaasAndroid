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
import io.bcaas.tools.BcaasLog;
import io.bcaas.vo.PaginationVO;
import io.bcaas.vo.TransactionChainReceiveVO;
import io.bcaas.vo.TransactionChainVO;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 显示待处理的交易数据
 */
public class PendingTransactionAdapter extends
        RecyclerView.Adapter<PendingTransactionAdapter.viewHolder> {
    private String TAG = "PendingTransactionAdapter";
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
        Object tcObject = transactionChainVO.getTc();
        if (tcObject instanceof TransactionChainReceiveVO) {
            TransactionChainReceiveVO transactionChainReceiveVO = ((TransactionChainReceiveVO) tcObject);
//            String amount= transactionChainReceiveVO.getAmount();
//             transactionChainReceiveVO.getBlockService();
//             transactionChainReceiveVO.
            // TODO: 2018/8/23 显示R区块
//            viewHolder.tvAccountAddress.setText(transactionChainReceiveVO.getAmount());
//            viewHolder.tvCurrency.setText(transactionChainVO.get.getCurrency());
//            viewHolder.tvBalance.setText(tr.getBalance());
        } else {
            // TODO: 2018/8/23 解析异常
            BcaasLog.d(TAG, context.getResources().getString(R.string.data_error));
        }

    }

    @Override
    public int getItemCount() {
        return transactionChainVOS.size();
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
