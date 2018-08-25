package io.bcaas.http.typeadapter;

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
}
