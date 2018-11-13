package io.bcaas.gson.jsonTypeAdapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import io.bcaas.constants.Constants;
import io.bcaas.vo.TransactionChainChangeVO;
import io.bcaas.vo.TransactionChainSendVO;

/**
 * change 区块 TC
 * 对发送数据进行正序
 * 自定義Request/Response數據傳輸交互字段，作Gson輔助類：TransactionChainChangeVO
 */
public class TransactionChainChangeVOTypeAdapter extends TypeAdapter<TransactionChainChangeVO> {

    @Override
    public void write(JsonWriter jsonWriter, TransactionChainChangeVO transactionChainChangeVO) throws IOException {
        jsonWriter.beginObject();
        if (transactionChainChangeVO == null) {
            return;
        }
        //按自定义顺序输出字段信息
        jsonWriter.name(Constants.MONGODB_KEY_PREVIOUS).value(transactionChainChangeVO.getPrevious());
        jsonWriter.name(Constants.MONGODB_KEY_BLOCKSERVICE).value(transactionChainChangeVO.getBlockService());
        jsonWriter.name(Constants.MONGODB_KEY_BLOCKTYPE).value(transactionChainChangeVO.getBlockType());
        jsonWriter.name(Constants.MONGODB_KEY_REPRESENTATIVE).value(transactionChainChangeVO.getRepresentative());
        jsonWriter.name(Constants.MONGODB_KEY_WALLET).value(transactionChainChangeVO.getWallet());
        jsonWriter.name(Constants.MONGODB_KEY_WORK).value(transactionChainChangeVO.getWork());
        jsonWriter.name(Constants.MONGODB_KEY_DATE).value(transactionChainChangeVO.getDate());
        jsonWriter.endObject();
    }


    @Override
    public TransactionChainChangeVO read(JsonReader jsonReader) throws IOException {
        return null;
    }
}
