package io.bcaas.gson.jsonTypeAdapter;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import io.bcaas.constants.Constants;
import io.bcaas.gson.RequestJson;
import io.bcaas.vo.DatabaseVO;
import io.bcaas.vo.GenesisVO;
import io.bcaas.vo.TransactionChainChangeVO;
import io.bcaas.vo.TransactionChainOpenVO;
import io.bcaas.vo.TransactionChainReceiveVO;
import io.bcaas.vo.TransactionChainSendVO;
import io.bcaas.vo.TransactionChainVO;
import io.bcaas.vo.WalletVO;

/**
 * 
 * Gson 解決順序問題, 可自定義輸出順序
 * <br><br>
 * Type [RequestJson]
 * 
 * @since 2018/08/25
 * 
 * @author Costa Peng
 * 
 * @version 1.0.0
 * 
 */

public class RequestJsonTypeAdapter extends TypeAdapter<RequestJson> {
	
	// RequestJson 順序
    @Override
    public void write(JsonWriter jsonWriter, RequestJson requestJson) throws IOException {
    	jsonWriter.beginObject();
    	jsonWriter.name(Constants.GSON_KEY_WALLETVO);
    	writeWalletVO(jsonWriter, requestJson.getWalletVO());
        jsonWriter.name(Constants.GSON_KEY_DATABASEVO);
        writeDatabaseVO(jsonWriter, requestJson.getDatabaseVO());
        jsonWriter.endObject();
    }
    // WalletVO 順序
    private void writeWalletVO(JsonWriter jsonWriter, WalletVO walletVO) throws IOException {
    	jsonWriter.beginObject();
    	jsonWriter.name(Constants.GSON_KEY_ACCESSTOKEN).value(walletVO.getAccessToken());
    	jsonWriter.name(Constants.MONGODB_KEY_BLOCKSERVICE).value(walletVO.getBlockService());
    	jsonWriter.name(Constants.GSON_KEY_WALLETADDRESS).value(walletVO.getWalletAddress());
    	jsonWriter.endObject();
    }
    // DatabaseVO 順序
    private void writeDatabaseVO(JsonWriter jsonWriter, DatabaseVO databaseVO) throws IOException {
    	jsonWriter.beginObject();
    	jsonWriter.name(Constants.GSON_KEY_TRANSACTIONCHAINVO);
        writeTransactionChainVO(jsonWriter, databaseVO.getTransactionChainVO());
    	jsonWriter.name(Constants.GSON_KEY_GENESISVO);
    	writeGenesisVO(jsonWriter, databaseVO.getGenesisVO());
        jsonWriter.endObject();
    }
    // GenesisVO 順序
    private void writeGenesisVO(JsonWriter jsonWriter, GenesisVO genesisVO) throws IOException {
		jsonWriter.beginObject();
		jsonWriter.name(Constants.MONGODB_KEY_ID).value(genesisVO.get_id());
		jsonWriter.name(Constants.MONGODB_KEY_PREVIOUS).value(genesisVO.getPrevious());
		jsonWriter.name(Constants.MONGODB_KEY_PUBLICUNIT).value(genesisVO.getPublicUnit());
		jsonWriter.name(Constants.MONGODB_KEY_BLOCKSERVICE).value(genesisVO.getBlockService());
		jsonWriter.name(Constants.MONGODB_KEY_CURRENCYUNIT).value(genesisVO.getCurrencyUnit());
		jsonWriter.name(Constants.MONGODB_KEY_CIRCULATION).value(genesisVO.getCirculation());
		jsonWriter.name(Constants.MONGODB_KEY_COINBASE).value(genesisVO.getCoinBase());
		jsonWriter.name(Constants.MONGODB_KEY_GENEISISBLOCKACCOUNT).value(genesisVO.getGenesisBlockAccount());
		jsonWriter.name(Constants.MONGODB_KEY_COINBASEACCOUNT).value(genesisVO.getCoinBaseAccount());
		jsonWriter.name(Constants.MONGODB_KEY_WORK).value(genesisVO.getWork());
		jsonWriter.name(Constants.MONGODB_KEY_CREATETIME).value(genesisVO.getCreateTime());
		jsonWriter.name(Constants.MONGODB_KEY_SYSTEMTIME).value(genesisVO.getSystemTime());
		jsonWriter.endObject();
    }
    // TransactionChainVO 順序
    private void writeTransactionChainVO(JsonWriter jsonWriter, TransactionChainVO<?> transactionChainVO) throws IOException {
        jsonWriter.beginObject();
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
    // Tc區塊 順序
    public <T> void writeTC(JsonWriter jsonWriter, T tc) throws IOException {
        jsonWriter.beginObject();
        if (tc instanceof TransactionChainReceiveVO) { // Receive Block
            TransactionChainReceiveVO transactionChainReceiveVO = (TransactionChainReceiveVO) tc;
            jsonWriter.name(Constants.MONGODB_KEY_PREVIOUS).value(transactionChainReceiveVO.getPrevious());
            jsonWriter.name(Constants.MONGODB_KEY_BLOCKSERVICE).value(transactionChainReceiveVO.getBlockService());
            jsonWriter.name(Constants.MONGODB_KEY_BLOCKTYPE).value(transactionChainReceiveVO.getBlockType());
            jsonWriter.name(Constants.MONGODB_KEY_BLOCKTXTYPE).value(transactionChainReceiveVO.getBlockTxType());
            jsonWriter.name(Constants.MONGODB_KEY_SOURCETXHASH).value(transactionChainReceiveVO.getSourceTxhash());
            jsonWriter.name(Constants.MONGODB_KEY_AMOUNT).value(transactionChainReceiveVO.getAmount());
            jsonWriter.name(Constants.MONGODB_KEY_REPRESENTATIVE).value(transactionChainReceiveVO.getRepresentative());
            jsonWriter.name(Constants.MONGODB_KEY_WALLET).value(transactionChainReceiveVO.getWallet());
            jsonWriter.name(Constants.MONGODB_KEY_WORK).value(transactionChainReceiveVO.getWork());
            jsonWriter.name(Constants.MONGODB_KEY_DATE).value(transactionChainReceiveVO.getDate());
        } else if (tc instanceof TransactionChainSendVO) { // Send Block
            TransactionChainSendVO transactionChainSendVO = (TransactionChainSendVO) tc;
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
        } else if (tc instanceof TransactionChainOpenVO) { // Open Block
            TransactionChainOpenVO transactionChainOpenVO = (TransactionChainOpenVO) tc;
            jsonWriter.name(Constants.MONGODB_KEY_PREVIOUS).value(transactionChainOpenVO.getPrevious());
            jsonWriter.name(Constants.MONGODB_KEY_BLOCKSERVICE).value(transactionChainOpenVO.getBlockService());
            jsonWriter.name(Constants.MONGODB_KEY_BLOCKTYPE).value(transactionChainOpenVO.getBlockType());
            jsonWriter.name(Constants.MONGODB_KEY_BLOCKTXTYPE).value(transactionChainOpenVO.getBlockTxType());
            jsonWriter.name(Constants.MONGODB_KEY_SOURCETXHASH).value(transactionChainOpenVO.getSourceTxhash());
            jsonWriter.name(Constants.MONGODB_KEY_AMOUNT).value(transactionChainOpenVO.getAmount());
            jsonWriter.name(Constants.MONGODB_KEY_REPRESENTATIVE).value(transactionChainOpenVO.getRepresentative());
            jsonWriter.name(Constants.MONGODB_KEY_WALLET).value(transactionChainOpenVO.getWallet());
            jsonWriter.name(Constants.MONGODB_KEY_WORK).value(transactionChainOpenVO.getWork());
            jsonWriter.name(Constants.MONGODB_KEY_DATE).value(transactionChainOpenVO.getDate());
        } else if (tc instanceof TransactionChainChangeVO) { // Change Block
        	TransactionChainChangeVO transactionChainChangeVO = (TransactionChainChangeVO) tc;
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

    @Override
    public RequestJson read(JsonReader jsonReader) throws IOException {
        return null;
    }
}
