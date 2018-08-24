package io.bcaas.http.JsonTypeAdapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import io.bcaas.gson.RequestJson;
import io.bcaas.vo.DatabaseVO;
import io.bcaas.vo.TransactionChainOpenVO;
import io.bcaas.vo.TransactionChainReceiveVO;
import io.bcaas.vo.TransactionChainSendVO;
import io.bcaas.vo.TransactionChainVO;
import io.bcaas.vo.WalletVO;

/**
 *
 * send 区块 TC
 * 对发送数据进行正序
 */
public class TransactionChainSendVOTypeAdapter extends TypeAdapter<TransactionChainSendVO> {

    @Override
    public void write(JsonWriter writer, TransactionChainSendVO transactionChainSendVO) throws IOException {
        writer.beginObject();
        //按自定义顺序输出字段信息
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
        writer.endObject();
    }


    @Override
    public TransactionChainSendVO read(JsonReader in) throws IOException {
        return null;
    }
}
