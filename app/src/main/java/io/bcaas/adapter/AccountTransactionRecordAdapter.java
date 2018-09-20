package io.bcaas.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import io.bcaas.R;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.NumberTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.TextTool;
import io.bcaas.tools.gson.JsonTool;
import io.bcaas.view.BcaasBalanceTextView;
import io.bcaas.vo.TransactionChainOpenVO;
import io.bcaas.vo.TransactionChainReceiveVO;
import io.bcaas.vo.TransactionChainSendVO;
import io.bcaas.vo.TransactionChainVO;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 显示当前账户的交易记录
 */
public class AccountTransactionRecordAdapter extends
        RecyclerView.Adapter<AccountTransactionRecordAdapter.viewHolder> {
    private String TAG = AccountTransactionRecordAdapter.class.getSimpleName();
    private Context context;
    private List<Object> objects;

    public AccountTransactionRecordAdapter(Context context, List<Object> paginationVOList) {
        this.context = context;
        this.objects = paginationVOList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, viewGroup, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder viewHolder, int i) {
        if (objects == null) {
            return;
        }
        Object object = objects.get(i);
        if (object == null) {
            return;
        }
        String walletAddress = null;
        String blockService = null;
        String amount = null;
        Gson gson = new Gson();
        String objectStr = gson.toJson(object);
        Type type = null;
        boolean isSend = false;
        if (JsonTool.isSendBlock(objectStr)) {
            type = new TypeToken<TransactionChainVO<TransactionChainSendVO>>() {
            }.getType();
            TransactionChainVO transactionChainVO = gson.fromJson(objectStr, type);
            Object tcObject = transactionChainVO.getTc();
            if (tcObject == null) {
                return;
            }
            TransactionChainSendVO transactionChainSendVO = gson.fromJson(gson.toJson(tcObject), TransactionChainSendVO.class);
            if (transactionChainSendVO == null) {
                return;
            }
            isSend = true;
            walletAddress = transactionChainSendVO.getDestination_wallet();
            blockService = transactionChainSendVO.getBlockService();
            amount = transactionChainSendVO.getAmount();

        } else if (JsonTool.isReceiveBlock(objectStr)) {
            type = new TypeToken<TransactionChainVO<TransactionChainReceiveVO>>() {
            }.getType();
            TransactionChainVO transactionChainVO = gson.fromJson(objectStr, type);
            Object tcObject = transactionChainVO.getTc();
            if (tcObject == null) {
                return;
            }
            TransactionChainReceiveVO transactionChainReceiveVO = gson.fromJson(gson.toJson(tcObject), TransactionChainReceiveVO.class);
            if (transactionChainReceiveVO == null) {
                return;
            }
            isSend = false;
            walletAddress = transactionChainVO.getWalletSend();
            blockService = transactionChainReceiveVO.getBlockService();
            amount = transactionChainReceiveVO.getAmount();
        } else if (JsonTool.isOpenBlock(objectStr)) {
            type = new TypeToken<TransactionChainVO<TransactionChainOpenVO>>() {
            }.getType();
            TransactionChainVO transactionChainVO = gson.fromJson(objectStr, type);
            Object tcObject = transactionChainVO.getTc();
            if (tcObject == null) {
                return;
            }
            TransactionChainOpenVO transactionChainOpenVO = gson.fromJson(gson.toJson(tcObject), TransactionChainOpenVO.class);
            if (transactionChainOpenVO == null) {
                return;
            }
            isSend = false;
            walletAddress = transactionChainVO.getWalletSend();
            blockService = transactionChainOpenVO.getBlockService();
            amount = transactionChainOpenVO.getAmount();
        }
        //获取当前text view占用的布局
        int layoutWidth = context.getResources().getDimensionPixelOffset(R.dimen.d44);
        int blockServiceWidth = context.getResources().getDimensionPixelOffset(R.dimen.d50);
        double width = (BcaasApplication.getScreenWidth() - layoutWidth - blockServiceWidth) / 2;
        viewHolder.tvAmount.setTextColor(context.getResources().getColor(isSend ? R.color.red70_da261f : R.color.green70_18ac22));
        viewHolder.tvAccountAddress.setText(TextTool.intelligentOmissionText(viewHolder.tvAmount, (int) width, walletAddress, 25));
        viewHolder.tvBlockService.setText(blockService);
        amount = NumberTool.formatNumber(StringTool.isEmpty(amount) ? Constants.ValueMaps.DEFAULT_BALANCE : amount);
        viewHolder.tvAmount.setText(isSend ? Constants.ValueMaps.SUBTRACT + amount : Constants.ValueMaps.ADD + amount);
    }

    @Override
    public int getItemCount() {
        return ListTool.isEmpty(objects) ? 0 : objects.size();
    }

    public void addAll(List<Object> objects) {
        this.objects = objects;
        this.notifyDataSetChanged();
    }


    class viewHolder extends RecyclerView.ViewHolder {
        private TextView tvAccountAddress;
        private BcaasBalanceTextView tvAmount;
        private TextView tvBlockService;

        public viewHolder(View view) {
            super(view);
            tvAccountAddress = view.findViewById(R.id.tv_account_address);
            tvAmount = view.findViewById(R.id.bbt_amount);
            tvBlockService = view.findViewById(R.id.tv_block_service);

        }
    }

}
