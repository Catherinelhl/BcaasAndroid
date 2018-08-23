package io.bcaas.http.JsonTypeAdapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import io.bcaas.gson.WalletRequestJson;
import io.bcaas.tools.BcaasLog;
import io.bcaas.vo.GenesisVO;
import io.bcaas.vo.TransactionChainOpenVO;
import io.bcaas.vo.TransactionChainReceiveVO;
import io.bcaas.vo.TransactionChainSendVO;
import io.bcaas.vo.TransactionChainVO;

/**
 * <h3>Example</h3>
 * Suppose we'd like to encode a stream of messages such as the following: <pre> {@code
 * [
 *   {
 *     "id": 912345678901,
 *     "text": "How do I stream JSON in Java?",
 *     "geo": null,
 *     "user": {
 *       "name": "json_newb",
 *       "followers_count": 41
 *      }
 *   },
 *   {
 *     "id": 912345678902,
 *     "text": "@json_newb just use JsonWriter!",
 *     "geo": [50.454722, -104.606667],
 *     "user": {
 *       "name": "jesse",
 *       "followers_count": 2
 *     }
 *   }
 * ]}</pre>
 * This code encodes the above structure: <pre>   {@code
 *   public void writeJsonStream(OutputStream out, List<Message> messages) throws IOException {
 *     JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
 *     writer.setIndent("    ");
 *     writeMessagesArray(writer, messages);
 *     writer.close();
 *   }
 * <p>
 *   public void writeMessagesArray(JsonWriter writer, List<Message> messages) throws IOException {
 *     writer.beginArray();
 *     for (Message message : messages) {
 *       writeMessage(writer, message);
 *     }
 *     writer.endArray();
 *   }
 * <p>
 *   public void writeMessage(JsonWriter writer, Message message) throws IOException {
 *     writer.beginObject();
 *     writer.name("id").value(message.getId());
 *     writer.name("text").value(message.getText());
 *     if (message.getGeo() != null) {
 *       writer.name("geo");
 *       writeDoublesArray(writer, message.getGeo());
 *     } else {
 *       writer.name("geo").nullValue();
 *     }
 *     writer.name("user");
 *     writeUser(writer, message.getUser());
 *     writer.endObject();
 *   }
 * <p>
 *   public void writeUser(JsonWriter writer, User user) throws IOException {
 *     writer.beginObject();
 *     writer.name("name").value(user.getName());
 *     writer.name("followers_count").value(user.getFollowersCount());
 *     writer.endObject();
 *   }
 * <p>
 *   public void writeDoublesArray(JsonWriter writer, List<Double> doubles) throws IOException {
 *     writer.beginArray();
 *     for (Double value : doubles) {
 *       writer.value(value);
 *     }
 *     writer.endArray();
 *   }}</pre>
 * <p>
 */

public class ReceiveRequestJsonTypeAdapter extends TypeAdapter<WalletRequestJson> {

    private String TAG = "ReceiveRequestJsonTypeAdapter";

    @Override
    public void write(JsonWriter out, WalletRequestJson value) throws IOException {
        out.beginObject();
        //按自定义顺序输出字段信息
        out.name("accessToken").value(value.getAccessToken());
        out.name("blockService").value(value.getBlockService());
        out.name("walletAddress").value(value.getWalletAddress());
        out.name("transactionChainVO");
//        writeTransactionChainVO(out, value.getTransactionChainVO());
        out.endObject();
    }

    @Override
    public WalletRequestJson read(JsonReader in) throws IOException {
        return null;
    }
//
//    public void writeTransactionChainVO(JsonWriter writer, TransactionChainVO transactionChainVO) throws IOException {
//        writer.beginObject();
//        writer.name("_id").value(transactionChainVO.get_id());
//        writer.name("tc");
//        writeTC(writer, transactionChainVO.getTc());
//        writer.name("signature").value(transactionChainVO.getSignature());
//        writer.name("signatureSend").value(transactionChainVO.getSignatureSend());
//        writer.name("publicKey").value(transactionChainVO.getPublicKey());
//        writer.name("height").value(transactionChainVO.getHeight());
//        writer.name("produceKeyType").value(transactionChainVO.getProduceKeyType());
//        writer.name("systemTime").value(transactionChainVO.getSystemTime());
//        writer.endObject();
//
//    }
//
//    public <T> void writeTC(JsonWriter writer, T tc) throws IOException {
//        writer.beginObject();
//        if (tc instanceof TransactionChainReceiveVO) {
//            TransactionChainReceiveVO transactionChainReceiveVO = (TransactionChainReceiveVO) tc;
//            writer.name("previous").value(transactionChainReceiveVO.getPrevious());
//            writer.name("blockService").value(transactionChainReceiveVO.getBlockService());
//            writer.name("blockType").value(transactionChainReceiveVO.getBlockType());
//            writer.name("blockTxType").value(transactionChainReceiveVO.getBlockTxType());
//            writer.name("sourceTxhash").value(transactionChainReceiveVO.getSourceTxhash());
//            writer.name("amount").value(transactionChainReceiveVO.getAmount());
//            writer.name("representative").value(transactionChainReceiveVO.getRepresentative());
//            writer.name("wallet").value(transactionChainReceiveVO.getWallet());
//            writer.name("work").value(transactionChainReceiveVO.getWork());
//            writer.name("date").value(transactionChainReceiveVO.getDate());
//        } else if (tc instanceof TransactionChainSendVO) {
//            TransactionChainSendVO transactionChainSendVO = (TransactionChainSendVO) tc;
//            writer.name("previous").value(transactionChainSendVO.getPrevious());
//            writer.name("blockService").value(transactionChainSendVO.getBlockService());
//            writer.name("blockType").value(transactionChainSendVO.getBlockType());
//            writer.name("blockTxType").value(transactionChainSendVO.getBlockTxType());
//            writer.name("destination_wallet").value(transactionChainSendVO.getDestination_wallet());
//            writer.name("balance").value(transactionChainSendVO.getBalance());
//            writer.name("amount").value(transactionChainSendVO.getAmount());
//            writer.name("representative").value(transactionChainSendVO.getRepresentative());
//            writer.name("wallet").value(transactionChainSendVO.getWallet());
//            writer.name("work").value(transactionChainSendVO.getWork());
//            writer.name("date").value(transactionChainSendVO.getDate());
//        } else if (tc instanceof TransactionChainOpenVO) {
//            TransactionChainOpenVO transactionChainOpenVO = (TransactionChainOpenVO) tc;
//            writer.name("previous").value(transactionChainOpenVO.getPrevious());
//            writer.name("blockService").value(transactionChainOpenVO.getBlockService());
//            writer.name("blockType").value(transactionChainOpenVO.getBlockType());
//            writer.name("blockTxType").value(transactionChainOpenVO.getBlockTxType());
//            writer.name("sourceTxhash").value(transactionChainOpenVO.getSourceTxhash());
//            writer.name("amount").value(transactionChainOpenVO.getAmount());
//            writer.name("representative").value(transactionChainOpenVO.getRepresentative());
//            writer.name("wallet").value(transactionChainOpenVO.getWallet());
//            writer.name("work").value(transactionChainOpenVO.getWork());
//            writer.name("date").value(transactionChainOpenVO.getDate());
//        } else {
//            BcaasLog.d(TAG, "writeTC tc is" + tc);
//        }
//
//        writer.endObject();
//    }
}
