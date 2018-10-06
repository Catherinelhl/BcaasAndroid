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
import io.bcaas.listener.AdapterNotifyFinishListener;
import io.bcaas.tools.DateFormatTool;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.TextTool;
import io.bcaas.tools.decimal.DecimalTool;
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
 * TV显示当前账户的交易记录
 */
public class TVAccountTransactionRecordAdapter extends
        RecyclerView.Adapter<TVAccountTransactionRecordAdapter.viewHolder> {
    private String TAG = TVAccountTransactionRecordAdapter.class.getSimpleName();
    private Context context;
    private List<Object> objects;

    private AdapterNotifyFinishListener adapterNotifyFinishListener;

    public TVAccountTransactionRecordAdapter(Context context, List<Object> paginationVOList, boolean isLand) {
        this.context = context;
        this.objects = paginationVOList;
        setHasStableIds(true);
    }

    public void setAdapterNotifyFinishListener(AdapterNotifyFinishListener adapterNotifyFinishListener) {
        this.adapterNotifyFinishListener = adapterNotifyFinishListener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.tv_item_transaction, viewGroup, false);
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
        String time = null;
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
            time = transactionChainSendVO.getDate();
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
            time = transactionChainReceiveVO.getDate();
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
            time = transactionChainOpenVO.getDate();
            amount = transactionChainOpenVO.getAmount();
        }

        viewHolder.tvAmount.setTextColor(context.getResources().getColor(isSend ? R.color.red70_da261f : R.color.green70_18ac22));
        //获取当前邊距占用的布局
        int layoutWidth = context.getResources().getDimensionPixelOffset(R.dimen.d90);
        int transactionMarginLeft = context.getResources().getDimensionPixelOffset(R.dimen.d34);
        //得到當前交易紀錄區塊的寬度
        double transactionRecordWidth = (BcaasApplication.getScreenWidth() - layoutWidth - transactionMarginLeft) / 2;
        //除去當前佈局的邊距以及Date和Amount內容的邊距
        int transactionRecordMargin = context.getResources().getDimensionPixelOffset(R.dimen.d26);
        viewHolder.tvAccountAddress.setText(TextTool.intelligentOmissionText(viewHolder.tvAmount,
                (int) ((transactionRecordWidth - transactionRecordMargin) / 2), walletAddress));
        try {
            time = DateFormatTool.getCurrentTimeOfAMPM(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] currentTime = time.split(Constants.KeyMaps.blank);
        if (currentTime != null && currentTime.length > 2) {
            viewHolder.tvTime.setText(String.format(context.getString(R.string.three_place_holders), currentTime[0], currentTime[1], currentTime[2]));
        } else {
            viewHolder.tvTime.setText(time);
        }
        amount = DecimalTool.transferDisplay(amount);
        viewHolder.tvAmount.setText(isSend ? Constants.ValueMaps.SUBTRACT + amount : Constants.ValueMaps.ADD + amount);
        if (i == getItemCount() - 1) {
            //最後一條，發送監聽
            adapterNotifyFinishListener.notifyFinish(true);
        }
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
        private TextView tvTime;

        public viewHolder(View view) {
            super(view);
            tvAccountAddress = view.findViewById(R.id.tv_account_address);
            tvAmount = view.findViewById(R.id.bbt_amount);
            tvTime = view.findViewById(R.id.tv_time);

        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
