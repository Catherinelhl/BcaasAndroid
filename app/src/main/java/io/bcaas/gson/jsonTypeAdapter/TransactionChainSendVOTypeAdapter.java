package io.bcaas.gson.jsonTypeAdapter;

import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import io.bcaas.constants.Constants;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.tools.GsonTool;
import io.bcaas.vo.DatabaseVO;
import io.bcaas.vo.TransactionChainOpenVO;
import io.bcaas.vo.TransactionChainReceiveVO;
import io.bcaas.vo.TransactionChainSendVO;
import io.bcaas.vo.TransactionChainVO;
import io.bcaas.vo.WalletVO;

/**
 * send 区块 TC
 * 对发送数据进行正序
 */
public class TransactionChainSendVOTypeAdapter extends TypeAdapter<TransactionChainSendVO> {

    @Override
    public void write(JsonWriter jsonWriter, TransactionChainSendVO transactionChainSendVO) throws IOException {
        jsonWriter.beginObject();
        if (transactionChainSendVO == null) return;
        //按自定义顺序输出字段信息
        jsonWriter.name(Constants.MONGODB_KEY_PREVIOUS).value(transactionChainSendVO.getPrevious());
        jsonWriter.name(Constants.MONGODB_KEY_BLOCKSERVICE).value(transactionChainSendVO.getBlockService());
        jsonWriter.name(Constants.MONGODB_KEY_BLOCKTYPE).value(transactionChainSendVO.getBlockType());
        jsonWriter.name(Constants.MONGODB_KEY_BLOCKTXTYPE).value(transactionChainSendVO.getBlockTxType());
        jsonWriter.name(Constants.MONGODB_KEY_DESTINATION_WALLET).value(transactionChainSendVO.getDestination_wallet());
        jsonWriter.name(Constants.MONGODB_KEY_BALANCE).value(transactionChainSendVO.getBalance());
        jsonWriter.name(Constants.MONGODB_KEY_AMOUNT).value(transactionChainSendVO.getAmount());
        jsonWriter.name(Constants.MONGODB_KEY_REPRESENTATIVE).value(transactionChainSendVO.getRepresentative());
        jsonWriter.name(Constants.MONGODB_KEY_WALLET).value(transactionChainSendVO.getWallet());
        jsonWriter.name(Constants.MONGODB_KEY_WORK).value(transactionChainSendVO.getWork());
        jsonWriter.name(Constants.MONGODB_KEY_DATE).value(transactionChainSendVO.getDate());
        jsonWriter.endObject();
    }


    @Override
    public TransactionChainSendVO read(JsonReader jsonReader) throws IOException {
        return GsonTool.getGsonBuilder().fromJson(jsonReader, new TypeToken<ResponseJson>() {
        }.getType());
    }
}
