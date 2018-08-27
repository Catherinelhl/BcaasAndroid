package io.bcaas.gson.jsonTypeAdapter;

import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;

import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.gson.ResponseJson;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.GsonTool;
import io.bcaas.vo.TransactionChainChangeVO;
import io.bcaas.vo.TransactionChainOpenVO;
import io.bcaas.vo.TransactionChainReceiveVO;
import io.bcaas.vo.TransactionChainSendVO;
import io.bcaas.vo.TransactionChainVO;

/**
 * 接收R区块的TC
 */

public class TransactionChainVOTypeAdapter extends TypeAdapter<TransactionChainVO> {

    @Override
    public void write(JsonWriter jsonWriter, TransactionChainVO transactionChainVO) throws IOException {
        jsonWriter.beginObject();
        if (transactionChainVO == null) return;
        jsonWriter.name(Constants.MONGODB_KEY_ID).value(transactionChainVO.get_id());
        jsonWriter.name(Constants.GSON_KEY_TC);
        writeTC(jsonWriter, transactionChainVO.getTc());
        jsonWriter.name(Constants.MONGODB_KEY_SIGNATURE).value(transactionChainVO.getSignature());
        jsonWriter.name(Constants.MONGODB_KEY_SIGNATURESEND).value(transactionChainVO.getSignatureSend());
        jsonWriter.name(Constants.MONGODB_KEY_PUBLICKEY).value(transactionChainVO.getPublicKey());
        jsonWriter.name(Constants.MONGODB_KEY_HEIGHT).value(transactionChainVO.getHeight());
        jsonWriter.name(Constants.MONGODB_KEY_PRODUCEKEYTYPE).value(transactionChainVO.getProduceKeyType());
        jsonWriter.name(Constants.MONGODB_KEY_SYSTEMTIME).value(transactionChainVO.getSystemTime());
        jsonWriter.endObject();
    }

    @Override
    public TransactionChainVO read(JsonReader jsonReader) throws IOException {
        return null;
    }

    public void writeTC(JsonWriter jsonWriter, Object tc) throws IOException {
        jsonWriter.beginObject();
        if (tc == null) return;
        //判断当前属于什么区块
        String objectStr = GsonTool.getGsonBuilder().toJson(tc);
        if (objectStr.contains(Constants.BLOCK_TYPE + Constants.BLOCK_TYPE_OPEN + Constants.BLOCK_TYPE_QUOTATION)) {
            TransactionChainOpenVO transactionChainOpenVO = GsonTool.getGson().fromJson(objectStr, TransactionChainOpenVO.class);
            jsonWriter.name(Constants.MONGODB_KEY_PREVIOUS).value(transactionChainOpenVO.getPrevious());
            jsonWriter.name(Constants.MONGODB_KEY_BLOCKSERVICE).value(transactionChainOpenVO.getBlockService());
            jsonWriter.name(Constants.MONGODB_KEY_BLOCKTYPE).value(transactionChainOpenVO.getBlockType());
            jsonWriter.name(Constants.MONGODB_KEY_BLOCKTXTYPE).value(transactionChainOpenVO.getBlockTxType());
            jsonWriter.name(Constants.MONGODB_KEY_SOURCETXHASH).value(transactionChainOpenVO.getSourceTxhash());
            jsonWriter.name(Constants.MONGODB_KEY_DESTINATION_WALLET).value(transactionChainOpenVO.getDestination_wallet());
            jsonWriter.name(Constants.MONGODB_KEY_BALANCE).value(transactionChainOpenVO.getBalance());
            jsonWriter.name(Constants.MONGODB_KEY_AMOUNT).value(transactionChainOpenVO.getAmount());
            jsonWriter.name(Constants.MONGODB_KEY_REPRESENTATIVE).value(transactionChainOpenVO.getRepresentative());
            jsonWriter.name(Constants.MONGODB_KEY_WALLET).value(transactionChainOpenVO.getWallet());
            jsonWriter.name(Constants.MONGODB_KEY_WORK).value(transactionChainOpenVO.getWork());
            jsonWriter.name(Constants.MONGODB_KEY_DATE).value(transactionChainOpenVO.getDate());

        } else if (objectStr.contains(Constants.BLOCK_TYPE + Constants.BLOCK_TYPE_SEND + Constants.BLOCK_TYPE_QUOTATION)) {
            TransactionChainSendVO transactionChainSendVO = GsonTool.getGson().fromJson(objectStr, TransactionChainSendVO.class);
            jsonWriter.name(Constants.MONGODB_KEY_PREVIOUS).value(transactionChainSendVO.getPrevious());
            jsonWriter.name(Constants.MONGODB_KEY_BLOCKSERVICE).value(transactionChainSendVO.getBlockService());
            jsonWriter.name(Constants.MONGODB_KEY_BLOCKTYPE).value(transactionChainSendVO.getBlockType());
            jsonWriter.name(Constants.MONGODB_KEY_BLOCKTXTYPE).value(transactionChainSendVO.getBlockTxType());
            jsonWriter.name(Constants.MONGODB_KEY_SOURCETXHASH).value(transactionChainSendVO.getSourceTxhash());
            jsonWriter.name(Constants.MONGODB_KEY_DESTINATION_WALLET).value(transactionChainSendVO.getDestination_wallet());
            jsonWriter.name(Constants.MONGODB_KEY_BALANCE).value(transactionChainSendVO.getBalance());
            jsonWriter.name(Constants.MONGODB_KEY_AMOUNT).value(transactionChainSendVO.getAmount());
            jsonWriter.name(Constants.MONGODB_KEY_REPRESENTATIVE).value(transactionChainSendVO.getRepresentative());
            jsonWriter.name(Constants.MONGODB_KEY_WALLET).value(transactionChainSendVO.getWallet());
            jsonWriter.name(Constants.MONGODB_KEY_WORK).value(transactionChainSendVO.getWork());
            jsonWriter.name(Constants.MONGODB_KEY_DATE).value(transactionChainSendVO.getDate());

        } else if (objectStr.contains(Constants.BLOCK_TYPE + Constants.BLOCK_TYPE_RECEIVE + Constants.BLOCK_TYPE_QUOTATION)) {
            TransactionChainReceiveVO transactionChainReceiveVO = GsonTool.getGson().fromJson(objectStr, TransactionChainReceiveVO.class);
            //按自定义顺序输出字段信息
            jsonWriter.name(Constants.MONGODB_KEY_PREVIOUS).value(transactionChainReceiveVO.getPrevious());
            jsonWriter.name(Constants.MONGODB_KEY_BLOCKSERVICE).value(transactionChainReceiveVO.getBlockService());
            jsonWriter.name(Constants.MONGODB_KEY_BLOCKTYPE).value(transactionChainReceiveVO.getBlockType());
            jsonWriter.name(Constants.MONGODB_KEY_BLOCKTXTYPE).value(transactionChainReceiveVO.getBlockTxType());
            jsonWriter.name(Constants.MONGODB_KEY_SOURCETXHASH).value(transactionChainReceiveVO.getSourceTxhash());
            jsonWriter.name(Constants.MONGODB_KEY_DESTINATION_WALLET).value(transactionChainReceiveVO.getDestination_wallet());
            jsonWriter.name(Constants.MONGODB_KEY_BALANCE).value(transactionChainReceiveVO.getBalance());
            jsonWriter.name(Constants.MONGODB_KEY_AMOUNT).value(transactionChainReceiveVO.getAmount());
            jsonWriter.name(Constants.MONGODB_KEY_REPRESENTATIVE).value(transactionChainReceiveVO.getRepresentative());
            jsonWriter.name(Constants.MONGODB_KEY_WALLET).value(transactionChainReceiveVO.getWallet());
            jsonWriter.name(Constants.MONGODB_KEY_WORK).value(transactionChainReceiveVO.getWork());
            jsonWriter.name(Constants.MONGODB_KEY_DATE).value(transactionChainReceiveVO.getDate());

        } else if (objectStr.contains(Constants.BLOCK_TYPE + Constants.BLOCK_TYPE_CHANGE + Constants.BLOCK_TYPE_QUOTATION)) {
            TransactionChainChangeVO transactionChainChangeVO = GsonTool.getGson().fromJson(objectStr, TransactionChainChangeVO.class);
            jsonWriter.name(Constants.MONGODB_KEY_PREVIOUS).value(transactionChainChangeVO.getPrevious());
            jsonWriter.name(Constants.MONGODB_KEY_BLOCKSERVICE).value(transactionChainChangeVO.getBlockService());
            jsonWriter.name(Constants.MONGODB_KEY_BLOCKTYPE).value(transactionChainChangeVO.getBlockType());
            jsonWriter.name(Constants.MONGODB_KEY_REPRESENTATIVE).value(transactionChainChangeVO.getRepresentative());
            jsonWriter.name(Constants.MONGODB_KEY_WALLET).value(transactionChainChangeVO.getWallet());
            jsonWriter.name(Constants.MONGODB_KEY_WORK).value(transactionChainChangeVO.getWork());
            jsonWriter.name(Constants.MONGODB_KEY_DATE).value(transactionChainChangeVO.getDate());
        }
        jsonWriter.endObject();
    }
}
