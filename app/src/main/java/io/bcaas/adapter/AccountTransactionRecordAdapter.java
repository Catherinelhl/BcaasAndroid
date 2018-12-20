package io.bcaas.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import io.bcaas.R;
import io.bcaas.base.BCAASApplication;
import io.bcaas.bean.TransactionDetailBean;
import io.bcaas.constants.Constants;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.tools.DateFormatTool;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.decimal.DecimalTool;
import io.bcaas.tools.gson.JsonTool;
import io.bcaas.view.textview.BcaasBalanceTextView;
import io.bcaas.vo.TransactionChainOpenVO;
import io.bcaas.vo.TransactionChainReceiveVO;
import io.bcaas.vo.TransactionChainSendVO;
import io.bcaas.vo.TransactionChainVO;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * Phone:當前Wallet的所有「交易紀錄」數據填充顯示適配器
 */
public class AccountTransactionRecordAdapter extends
        RecyclerView.Adapter<AccountTransactionRecordAdapter.viewHolder> {
    private String TAG = AccountTransactionRecordAdapter.class.getSimpleName();
    private Context context;
    private List<Object> objects;
    private OnItemSelectListener onItemSelectListener;

    public AccountTransactionRecordAdapter(Context context, List<Object> paginationVOList) {
        this.context = context;
        this.objects = paginationVOList;
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
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
        String blockType = null;
        String amount = null;
        String time = null;
        String blockService = null;
        Gson gson = new Gson();
        String objectStr = gson.toJson(object);
        //获取当前交易列表的详情
        TransactionDetailBean transactionDetailBean = new TransactionDetailBean();
        //獲取txHash值
        try {
            JSONObject jsonObject = new JSONObject(objectStr);
            if (jsonObject.has("txHash")) {
                transactionDetailBean.setTxHash(jsonObject.getString("txHash"));
            }
            if (jsonObject.has("height")) {
                transactionDetailBean.setHeight(String.valueOf(jsonObject.getLong("height")));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            LogTool.e(TAG, e.getMessage());
        }
        Type type = null;
        boolean isSend = false;
        if (JsonTool.isSendBlock(objectStr)) {
            type = new TypeToken<TransactionChainVO<TransactionChainSendVO>>() {
            }.getType();
            try {
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
                transactionDetailBean.setReceiveAccount(walletAddress);
                transactionDetailBean.setSendAccount(BCAASApplication.getWalletAddress());
                blockType = Constants.BLOCK_TYPE_SEND;
                amount = transactionChainSendVO.getAmount();
                time = transactionChainSendVO.getDate();
                blockService = transactionChainSendVO.getBlockService();

            } catch (Exception e) {
                LogTool.e(TAG, e.toString());
            }


        } else if (JsonTool.isReceiveBlock(objectStr)) {
            type = new TypeToken<TransactionChainVO<TransactionChainReceiveVO>>() {
            }.getType();
            try {
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
                transactionDetailBean.setReceiveAccount(BCAASApplication.getWalletAddress());
                transactionDetailBean.setSendAccount(walletAddress);
                blockType = Constants.BLOCK_TYPE_RECEIVE;
                amount = transactionChainReceiveVO.getAmount();
                time = transactionChainReceiveVO.getDate();
                blockService = transactionChainReceiveVO.getBlockService();
            } catch (Exception e) {
                LogTool.e(TAG, e.getMessage());
            }
        } else if (JsonTool.isOpenBlock(objectStr)) {
            type = new TypeToken<TransactionChainVO<TransactionChainOpenVO>>() {
            }.getType();
            try {
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
                transactionDetailBean.setReceiveAccount(BCAASApplication.getWalletAddress());
                transactionDetailBean.setSendAccount(walletAddress);
                blockType = Constants.BLOCK_TYPE_OPEN;
                amount = transactionChainOpenVO.getAmount();
                time = transactionChainOpenVO.getDate();
                blockService = transactionChainOpenVO.getBlockService();

            } catch (Exception e) {
                LogTool.e(TAG, e.getMessage());
            }
        }
        //获取当前text view占用的布局
        int layoutWidth = context.getResources().getDimensionPixelOffset(R.dimen.d44);
        int blockServiceWidth = context.getResources().getDimensionPixelOffset(R.dimen.d65);
        double width = (BCAASApplication.getScreenWidth() - layoutWidth - blockServiceWidth) / 2;
        viewHolder.tvAmount.setTextColor(context.getResources().getColor(isSend ? R.color.red70_da261f : R.color.green70_18ac22));
//        viewHolder.tvAccountAddress.setText(TextTool.intelligentOmissionText(viewHolder.tvAmount, (int) width, walletAddress));
        time = DateFormatTool.getUTCDateTransferCurrentTimeZoneHMS(time);
        transactionDetailBean.setTransactionTime(time);
        transactionDetailBean.setBlockService(blockService);
        transactionDetailBean.setSend(isSend);
        transactionDetailBean.setHashType(blockType);
        viewHolder.tvAccountAddress.setText(time);
        viewHolder.tvBlockService.setText(blockType);
        amount = DecimalTool.transferDisplay(amount);
        amount = isSend ? Constants.ValueMaps.SUBTRACT + amount : Constants.ValueMaps.ADD + amount;
        transactionDetailBean.setBalance(amount);
        viewHolder.tvAmount.setText(amount);
        viewHolder.tvAmount.setShowPop(false);
        viewHolder.tvAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemSelectListener != null) {
                    onItemSelectListener.onItemSelect(transactionDetailBean, Constants.ACCOUNT_TRANSACTION);
                }
            }
        });
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemSelectListener != null) {
                    onItemSelectListener.onItemSelect(transactionDetailBean, Constants.ACCOUNT_TRANSACTION);
                }
            }
        });

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
        private LinearLayout llItemTransaction;

        public viewHolder(View view) {
            super(view);
            tvAccountAddress = view.findViewById(R.id.tv_account_address);
            tvAmount = view.findViewById(R.id.bbt_amount);
            tvBlockService = view.findViewById(R.id.tv_block_service);
            llItemTransaction = view.findViewById(R.id.ll_item_transaction);

        }
    }

}
