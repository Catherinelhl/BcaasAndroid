package io.bcaas.gson.jsonTypeAdapter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import io.bcaas.constants.Constants;
import io.bcaas.gson.RequestJson;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.tools.gson.JsonTool;
import io.bcaas.tools.ListTool;
import io.bcaas.vo.ClientIpInfoVO;
import io.bcaas.vo.DatabaseVO;
import io.bcaas.vo.GenesisVO;
import io.bcaas.vo.PaginationVO;
import io.bcaas.vo.TransactionChainChangeVO;
import io.bcaas.vo.TransactionChainOpenVO;
import io.bcaas.vo.TransactionChainReceiveVO;
import io.bcaas.vo.TransactionChainSendVO;
import io.bcaas.vo.TransactionChainVO;
import io.bcaas.vo.VersionVO;
import io.bcaas.vo.WalletVO;

/**
 * Gson 解決順序問題, 可自定義輸出順序
 * <br><br>
 * Type [RequestJson]
 *
 * @author Costa Peng
 * @version 1.0.0
 * @since 2018/08/25
 */

public class RequestJsonTypeAdapter extends TypeAdapter<RequestJson> {

    private String TAG = RequestJsonTypeAdapter.class.getSimpleName();

    // RequestJson 順序
    @Override
    public void write(JsonWriter jsonWriter, RequestJson requestJson) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name(Constants.GSON_KEY_VERSIONVO);
        writeVersionVO(jsonWriter, requestJson.getVersionVO());

        jsonWriter.name(Constants.GSON_KEY_VERSIONVOLIST);
        writeVersionListVO(jsonWriter, requestJson.getVersionVOList());

        jsonWriter.name(Constants.GSON_KEY_CLIENTIPINFOVO);
        writeGetClientInfoVO(jsonWriter, requestJson.getClientIpInfoVO());

        jsonWriter.name(Constants.GSON_KEY_CLIENTIPINFOVOLIST);
        writeGetClientInfoListVO(jsonWriter, requestJson.getClientIpInfoVOList());
        jsonWriter.name(Constants.GSON_KEY_WALLETVO);
        writeWalletVO(jsonWriter, requestJson.getWalletVO());
        jsonWriter.name(Constants.GSON_KEY_DATABASEVO);
        writeDatabaseVO(jsonWriter, requestJson.getDatabaseVO());

        jsonWriter.name(Constants.GSON_KEY_DATABASEVOLIST);
        writeDatabaseVOList(jsonWriter, requestJson.getDatabaseVOList());

        jsonWriter.name(Constants.GSON_KEY_PAGINATIONVO);
        writeGetPaginationVO(jsonWriter, requestJson.getPaginationVO());
        jsonWriter.name(Constants.GSON_KEY_PAGINATIONVOLIST);
        writeGetPaginationVOList(jsonWriter, requestJson.getPaginationVOList());

        jsonWriter.endObject();
    }

    private void writeVersionVO(JsonWriter jsonWriter, VersionVO versionVO) throws IOException {
        jsonWriter.beginObject();
        if (versionVO == null) {
            return;
        }
        jsonWriter.name(Constants.GSON_KEY_ID).value(versionVO.get_id());
        jsonWriter.name(Constants.GSON_KEY_AUTH_KEY).value(versionVO.getAuthKey());
        jsonWriter.name(Constants.GSON_KEY_VERSION).value(versionVO.getVersion());
        jsonWriter.name(Constants.GSON_KEY_FORCE_UPGRADE).value(versionVO.getForceUpgrade());
        jsonWriter.name(Constants.GSON_KEY_UPDATE_URL).value(versionVO.getUpdateUrl());
        jsonWriter.name(Constants.GSON_KEY_TYPE).value(versionVO.getType());
        jsonWriter.name(Constants.GSON_KEY_MOTIFY_TIME).value(versionVO.getMotifyTime());
        jsonWriter.name(Constants.GSON_KEY_SYSTEM_TIME).value(versionVO.getSystemTime());
        jsonWriter.endObject();
    }

    private void writeVersionListVO(JsonWriter jsonWriter, List<VersionVO> versionVOList) throws IOException {
        jsonWriter.beginArray();
        if (ListTool.isEmpty(versionVOList)) {
            return;
        }
        for (VersionVO versionVO : versionVOList) {
            writeVersionVO(jsonWriter, versionVO);
        }
        jsonWriter.endArray();
    }


    private void writeGetClientInfoVO(JsonWriter jsonWriter, ClientIpInfoVO clientIpInfoVO) throws IOException {
        jsonWriter.beginObject();
        if (clientIpInfoVO == null) {
            return;
        }
        jsonWriter.name(Constants.GSON_KEY_MAC_ADDRESS_EXTERNAL_IP).value(clientIpInfoVO.getMacAddressExternalIp());
        jsonWriter.name(Constants.GSON_KEY_EXTERNAL_IP).value(clientIpInfoVO.getExternalIp());
        jsonWriter.name(Constants.GSON_KEY_INTERNAL_IP).value(clientIpInfoVO.getInternalIp());
        jsonWriter.name(Constants.GSON_KEY_CLIENT_TYPE).value(clientIpInfoVO.getClientType());
        jsonWriter.name(Constants.GSON_KEY_EXTERNAL_PORT).value(clientIpInfoVO.getExternalPort());
        jsonWriter.name(Constants.GSON_KEY_INTERNAL_PORT).value(clientIpInfoVO.getInternalPort());
        jsonWriter.name(Constants.GSON_KEY_VIRTUAL_COIN).value(clientIpInfoVO.getVirtualCoin().toString());
//        writeVirtualCoin(jsonWriter, clientIpInfoVO.getVirtualCoin());
        jsonWriter.name(Constants.GSON_KEY_RPC_PORT).value(clientIpInfoVO.getRpcPort());
        jsonWriter.endObject();
    }

    private void writeVirtualCoin(JsonWriter jsonWriter, ArrayList<LinkedHashMap<String, String>> virtualCoin) throws IOException {
        jsonWriter.beginArray();
        jsonWriter.endArray();
    }

    private void writeGetClientInfoListVO(JsonWriter jsonWriter, List<ClientIpInfoVO> clientIpInfoVOList) throws IOException {
        jsonWriter.beginArray();
        if (ListTool.isEmpty(clientIpInfoVOList)) {
            return;
        }
        for (ClientIpInfoVO clientIpInfoVO : clientIpInfoVOList) {
            writeGetClientInfoVO(jsonWriter, clientIpInfoVO);
        }
        jsonWriter.endArray();
    }

    private void writeDatabaseVOList(JsonWriter jsonWriter, List<DatabaseVO> databaseVOList) throws IOException {
        jsonWriter.beginArray();
        if (ListTool.isEmpty(databaseVOList)) {
            return;
        }
        for (DatabaseVO databaseVO : databaseVOList) {
            writeDatabaseVO(jsonWriter, databaseVO);
        }
        jsonWriter.endArray();
    }

    private void writeGetPaginationVO(JsonWriter jsonWriter, PaginationVO paginationVO) throws IOException {
        jsonWriter.beginObject();
        if (paginationVO == null) {
            return;
        }
        jsonWriter.name(Constants.GSON_KEY_OBJECT_LIST).value(paginationVO.getObjectList().toString());
//        writeObjectList(jsonWriter,paginationVO.getObjectList());
        jsonWriter.name(Constants.GSON_KEY_NEXT_OBJECT_ID).value(paginationVO.getNextObjectId());
        jsonWriter.endObject();

    }

    private void writeObjectList(JsonWriter jsonWriter, List<Object> objectList) throws IOException {
        jsonWriter.beginArray();
        for (Object o : objectList) {
        }
        jsonWriter.endArray();
    }

    private void writeGetPaginationVOList(JsonWriter jsonWriter, List<PaginationVO> paginationVOList) throws IOException {
        jsonWriter.beginArray();
        Gson gson = new Gson();
        if (ListTool.isEmpty(paginationVOList)) {
            return;
        }
        for (PaginationVO paginationVO : paginationVOList) {
            jsonWriter.beginObject();
            List<Object> objectList = paginationVO.getObjectList();
            jsonWriter.name(Constants.GSON_KEY_OBJECTLIST);
            jsonWriter.beginArray();
            for (Object object : objectList) {
                String objectStr = gson.toJson(object);
                Type type = null;
                if (JsonTool.isOpenBlock(objectStr)) {
                    type = new TypeToken<TransactionChainVO<TransactionChainOpenVO>>() {
                    }.getType();
                } else if (JsonTool.isSendBlock(objectStr)) {
                    type = new TypeToken<TransactionChainVO<TransactionChainSendVO>>() {
                    }.getType();
                } else if (JsonTool.isReceiveBlock(objectStr)) {
                    type = new TypeToken<TransactionChainVO<TransactionChainReceiveVO>>() {
                    }.getType();
                } else if (JsonTool.isChangeBlock(objectStr)) {
                    type = new TypeToken<TransactionChainVO<TransactionChainChangeVO>>() {
                    }.getType();
                }

                TransactionChainVO transactionChainVO = gson.fromJson(objectStr, type);

                writeTransactionChainVO(jsonWriter, transactionChainVO);
            }
            jsonWriter.endArray();

            jsonWriter.name(Constants.GSON_KEY_NEXTOBJECTID).value(paginationVO.getNextObjectId());
            jsonWriter.endObject();
        }

        jsonWriter.endArray();

    }


    // WalletVO 順序
    private void writeWalletVO(JsonWriter jsonWriter, WalletVO walletVO) throws IOException {
        jsonWriter.beginObject();
        if (walletVO == null) {
            return;
        }
        jsonWriter.name(Constants.GSON_KEY_ACCESSTOKEN).value(walletVO.getAccessToken());
        jsonWriter.name(Constants.MONGODB_KEY_BLOCKSERVICE).value(walletVO.getBlockService());
        jsonWriter.name(Constants.GSON_KEY_WALLETADDRESS).value(walletVO.getWalletAddress());
        jsonWriter.endObject();
    }

    // DatabaseVO 順序
    private void writeDatabaseVO(JsonWriter jsonWriter, DatabaseVO databaseVO) throws
            IOException {
        jsonWriter.beginObject();
        if (databaseVO == null) {
            return;
        }
        jsonWriter.name(Constants.GSON_KEY_TRANSACTIONCHAINVO);
        writeTransactionChainVO(jsonWriter, databaseVO.getTransactionChainVO());
        jsonWriter.name(Constants.GSON_KEY_GENESISVO);
        writeGenesisVO(jsonWriter, databaseVO.getGenesisVO());
        jsonWriter.endObject();
    }

    // GenesisVO 順序
    private void writeGenesisVO(JsonWriter jsonWriter, GenesisVO genesisVO) throws IOException {
        jsonWriter.beginObject();
        if (genesisVO == null) {
            return;
        }
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
    private void writeTransactionChainVO(JsonWriter jsonWriter, TransactionChainVO<?>
            transactionChainVO) throws IOException {
        jsonWriter.beginObject();
        if (transactionChainVO == null) {
            return;
        }
        jsonWriter.name(Constants.MONGODB_KEY_ID).value(transactionChainVO.get_id());
        jsonWriter.name(Constants.GSON_KEY_TC).value(GsonTool.getGsonBuilder().toJson(transactionChainVO.getTc()));
//        writeTC(jsonWriter, transactionChainVO.getTc());
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
        if (tc == null) {
            return;
        }
        //判断当前属于什么区块
        String objectStr = GsonTool.getGsonBuilder().toJson(tc);
        if (JsonTool.isOpenBlock(objectStr)) {
            TransactionChainOpenVO transactionChainOpenVO = GsonTool.getGson().fromJson(objectStr, TransactionChainOpenVO.class);
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

        } else if (JsonTool.isSendBlock(objectStr)) {
            TransactionChainSendVO transactionChainSendVO = GsonTool.getGson().fromJson(objectStr, TransactionChainSendVO.class);
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

        } else if (JsonTool.isReceiveBlock(objectStr)) {
            TransactionChainReceiveVO transactionChainReceiveVO = GsonTool.getGson().fromJson(objectStr, TransactionChainReceiveVO.class);
            //按自定义顺序输出字段信息
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

        } else if (JsonTool.isChangeBlock(objectStr)) {
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

    @Override
    public RequestJson read(JsonReader jsonReader) throws IOException {
        return null;
    }
}
