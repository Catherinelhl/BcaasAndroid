package io.bcaas.http.JsonTypeAdapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import io.bcaas.gson.RequestJson;
import io.bcaas.tools.BcaasLog;
import io.bcaas.vo.TransactionChainOpenVO;
import io.bcaas.vo.TransactionChainReceiveVO;
import io.bcaas.vo.TransactionChainSendVO;
import io.bcaas.vo.TransactionChainVO;

/**
 * 接收R区块的TC
 */

public class TransactionChainReceiveVOTypeAdapter extends TypeAdapter<TransactionChainReceiveVO> {

    private String TAG = "TransactionChainReceiveVOTypeAdapter";

    @Override
    public void write(JsonWriter writer, TransactionChainReceiveVO transactionChainReceiveVO) throws IOException {
        writer.beginObject();
        //按自定义顺序输出字段信息
        writer.name("previous").value(transactionChainReceiveVO.getPrevious());
        writer.name("blockService").value(transactionChainReceiveVO.getBlockService());
        writer.name("blockType").value(transactionChainReceiveVO.getBlockType());
        writer.name("blockTxType").value(transactionChainReceiveVO.getBlockTxType());
        writer.name("sourceTxhash").value(transactionChainReceiveVO.getSourceTxhash());
        writer.name("amount").value(transactionChainReceiveVO.getAmount());
        writer.name("representative").value(transactionChainReceiveVO.getRepresentative());
        writer.name("wallet").value(transactionChainReceiveVO.getWallet());
        writer.name("work").value(transactionChainReceiveVO.getWork());
        writer.name("date").value(transactionChainReceiveVO.getDate());
        writer.endObject();
    }

    @Override
    public TransactionChainReceiveVO read(JsonReader in) throws IOException {
        return null;
    }

    public <T> void writeTC(JsonWriter writer, T tc) throws IOException {
        writer.beginObject();
        if (tc instanceof TransactionChainReceiveVO) {
            TransactionChainReceiveVO transactionChainReceiveVO = (TransactionChainReceiveVO) tc;
            writer.name("previous").value(transactionChainReceiveVO.getPrevious());
            writer.name("blockService").value(transactionChainReceiveVO.getBlockService());
            writer.name("blockType").value(transactionChainReceiveVO.getBlockType());
            writer.name("blockTxType").value(transactionChainReceiveVO.getBlockTxType());
            writer.name("sourceTxhash").value(transactionChainReceiveVO.getSourceTxhash());
            writer.name("amount").value(transactionChainReceiveVO.getAmount());
            writer.name("representative").value(transactionChainReceiveVO.getRepresentative());
            writer.name("wallet").value(transactionChainReceiveVO.getWallet());
            writer.name("work").value(transactionChainReceiveVO.getWork());
            writer.name("date").value(transactionChainReceiveVO.getDate());
        } else if (tc instanceof TransactionChainSendVO) {
            TransactionChainSendVO transactionChainSendVO = (TransactionChainSendVO) tc;
            writer.name("previous").value(transactionChainSendVO.getPrevious());
            writer.name("blockService").value(transactionChainSendVO.getBlockService());
            writer.name("blockType").value(transactionChainSendVO.getBlockType());
            writer.name("blockTxType").value(transactionChainSendVO.getBlockTxType());
            writer.name("destination_wallet").value(transactionChainSendVO.getDestination_wallet());
            writer.name("balance").value(transactionChainSendVO.getBalance());
            writer.name("amount").value(transactionChainSendVO.getAmount());
            writer.name("representative").value(transactionChainSendVO.getRepresentative());
            writer.name("wallet").value(transactionChainSendVO.getWallet());
            writer.name("work").value(transactionChainSendVO.getWork());
            writer.name("date").value(transactionChainSendVO.getDate());
        } else if (tc instanceof TransactionChainOpenVO) {
            TransactionChainOpenVO transactionChainOpenVO = (TransactionChainOpenVO) tc;
            writer.name("previous").value(transactionChainOpenVO.getPrevious());
            writer.name("blockService").value(transactionChainOpenVO.getBlockService());
            writer.name("blockType").value(transactionChainOpenVO.getBlockType());
            writer.name("blockTxType").value(transactionChainOpenVO.getBlockTxType());
            writer.name("sourceTxhash").value(transactionChainOpenVO.getSourceTxhash());
            writer.name("amount").value(transactionChainOpenVO.getAmount());
            writer.name("representative").value(transactionChainOpenVO.getRepresentative());
            writer.name("wallet").value(transactionChainOpenVO.getWallet());
            writer.name("work").value(transactionChainOpenVO.getWork());
            writer.name("date").value(transactionChainOpenVO.getDate());
        } else {
            BcaasLog.d(TAG, "writeTC tc is" + tc);
        }

        writer.endObject();
    }
}
