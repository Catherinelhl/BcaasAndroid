package io.bcaas.http.JsonTypeAdapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.GsonTool;
import io.bcaas.vo.TransactionChainOpenVO;
import io.bcaas.vo.TransactionChainReceiveVO;
import io.bcaas.vo.TransactionChainSendVO;
import io.bcaas.vo.TransactionChainVO;

/**
 * 接收R区块的TC
 */

public class TransactionChainVOTypeAdapter extends TypeAdapter<TransactionChainVO> {

    private String TAG = "TransactionChainVOTypeAdapter";

    private String transactionType;

    public TransactionChainVOTypeAdapter(String transactionType) {
        this.transactionType = transactionType;
    }

    @Override
    public void write(JsonWriter writer, TransactionChainVO transactionChainVO) throws IOException {
        writer.beginObject();
        writer.name("_id").value(transactionChainVO.get_id());
        writer.name("tc");
        writeTC(writer, transactionChainVO.getTc());
        writer.name("signature").value(transactionChainVO.getSignature());
        writer.name("signatureSend").value(transactionChainVO.getSignatureSend());
        writer.name("publicKey").value(transactionChainVO.getPublicKey());
        writer.name("height").value(transactionChainVO.getHeight());
        writer.name("produceKeyType").value(transactionChainVO.getProduceKeyType());
        writer.name("systemTime").value(transactionChainVO.getSystemTime());
        writer.endObject();
    }

    @Override
    public TransactionChainVO read(JsonReader in) throws IOException {
        return null;
    }

    public void writeTC(JsonWriter writer, Object tc) throws IOException {
        writer.beginObject();
        String json = GsonTool.getGson().toJson(tc);
        // TODO: 2018/8/24 改为静态
        switch (transactionType) {
            case "TransactionChainReceiveVO":
                TransactionChainReceiveVO transactionChainReceiveVO = GsonTool.getGson().fromJson(json, TransactionChainReceiveVO.class);
                //按自定义顺序输出字段信息
                writer.name("previous").value(transactionChainReceiveVO.getPrevious());
                writer.name("blockService").value(transactionChainReceiveVO.getBlockService());
                writer.name("blockType").value(transactionChainReceiveVO.getBlockType());
                writer.name("blockTxType").value(transactionChainReceiveVO.getBlockTxType());
                writer.name("sourceTxhash").value(transactionChainReceiveVO.getSourceTxhash());
                writer.name("destination_wallet").value(transactionChainReceiveVO.getDestination_wallet());
                writer.name("amount").value(transactionChainReceiveVO.getAmount());
                writer.name("representative").value(transactionChainReceiveVO.getRepresentative());
                writer.name("wallet").value(transactionChainReceiveVO.getWallet());
                writer.name("work").value(transactionChainReceiveVO.getWork());
                writer.name("date").value(transactionChainReceiveVO.getDate());
                break;
            case "TransactionChainSendVO":
                TransactionChainSendVO transactionChainSendVO = GsonTool.getGson().fromJson(json, TransactionChainSendVO.class);
                writer.name("previous").value(transactionChainSendVO.getPrevious());
                writer.name("blockService").value(transactionChainSendVO.getBlockService());
                writer.name("blockType").value(transactionChainSendVO.getBlockType());
                writer.name("blockTxType").value(transactionChainSendVO.getBlockTxType());
                writer.name("sourceTxhash").value(transactionChainSendVO.getSourceTxhash());
                writer.name("destination_wallet").value(transactionChainSendVO.getDestination_wallet());
                writer.name("balance").value(transactionChainSendVO.getBalance());
                writer.name("amount").value(transactionChainSendVO.getAmount());
                writer.name("representative").value(transactionChainSendVO.getRepresentative());
                writer.name("wallet").value(transactionChainSendVO.getWallet());
                writer.name("work").value(transactionChainSendVO.getWork());
                writer.name("date").value(transactionChainSendVO.getDate());
                break;
            case "TransactionChainOpenVO":
                TransactionChainOpenVO transactionChainOpenVO = GsonTool.getGson().fromJson(json, TransactionChainOpenVO.class);
                writer.name("previous").value(transactionChainOpenVO.getPrevious());
                writer.name("blockService").value(transactionChainOpenVO.getBlockService());
                writer.name("blockType").value(transactionChainOpenVO.getBlockType());
                writer.name("blockTxType").value(transactionChainOpenVO.getBlockTxType());
                writer.name("destination_wallet").value(transactionChainOpenVO.getDestination_wallet());
                writer.name("sourceTxhash").value(transactionChainOpenVO.getSourceTxhash());
                writer.name("amount").value(transactionChainOpenVO.getAmount());
                writer.name("representative").value(transactionChainOpenVO.getRepresentative());
                writer.name("wallet").value(transactionChainOpenVO.getWallet());
                writer.name("work").value(transactionChainOpenVO.getWork());
                writer.name("date").value(transactionChainOpenVO.getDate());
                break;
            default:
                BcaasLog.d(TAG, "writeTC tc is" + tc);
                break;
        }
        writer.endObject();
    }
}
