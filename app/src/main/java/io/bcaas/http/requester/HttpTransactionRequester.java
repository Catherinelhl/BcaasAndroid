package io.bcaas.http.requester;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.bcaas.base.BCAASApplication;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.gson.jsonTypeAdapter.*;
import io.bcaas.listener.HttpASYNTCPResponseListener;
import io.bcaas.listener.HttpTransactionListener;
import io.bcaas.requester.BaseHttpRequester;
import io.bcaas.tools.DateFormatTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.decimal.DecimalTool;
import io.bcaas.tools.ecc.KeyTool;
import io.bcaas.tools.ecc.Sha256Tool;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.tools.gson.JsonTool;
import io.bcaas.vo.*;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author: Catherine
 * @date: 2018/10/19
 * @description 執行HTTP請求類：交易相關的請求，such as：「Receive」、「Send」、「Change」
 */
public class HttpTransactionRequester {
    private static String TAG = HttpTransactionRequester.class.getSimpleName();
    private static ResponseJson responseJson;

    /**
     * receice
     *
     * @param previous     上一区块的hash
     * @param blockService 交易币种
     * @param amount       交易的金额
     * @return ResponseJson
     */
    public static void receiveAuthNode(String previous, String blockService, String sourceTxHash,
                                       String amount, String signatureSend, String blockType,
                                       String representative, String receiveAmount,
                                       HttpASYNTCPResponseListener httpASYNTCPResponseListener,
                                       HttpTransactionListener httpTransactionListener) {
        responseJson = null;
        LogTool.d(TAG, "[Receive] receiveAuthNode:" + BCAASApplication.getWalletAddress());
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeAdapter(ResponseJson.class, new RequestJsonTypeAdapter())
                .registerTypeAdapter(TransactionChainVO.class, new TransactionChainVOTypeAdapter())
                .registerTypeAdapter(TransactionChainReceiveVO.class, new TransactionChainReceiveVOTypeAdapter())
                .create();
        if (StringTool.isEmpty(blockService)) {
            blockService = Constants.BlockService.BCC;
        }
        try {
            //建立Receive區塊
            TransactionChainVO<TransactionChainReceiveVO> transactionChainVO = new TransactionChainVO<TransactionChainReceiveVO>();
            TransactionChainReceiveVO transactionChainReceiveVO = new TransactionChainReceiveVO();
            transactionChainReceiveVO.setPrevious(previous);
            transactionChainReceiveVO.setBlockService(blockService);
            transactionChainReceiveVO.setBlockType(blockType);
            transactionChainReceiveVO.setBlockTxType(Constants.ValueMaps.BLOCK_TX_TYPE);
            transactionChainReceiveVO.setSourceTxhash(sourceTxHash);
            transactionChainReceiveVO.setAmount(DecimalTool.transferStoreDatabase(amount));
            transactionChainReceiveVO.setReceiveAmount(receiveAmount);
            transactionChainReceiveVO.setRepresentative(representative);
            transactionChainReceiveVO.setWallet(BCAASApplication.getWalletAddress());
            transactionChainReceiveVO.setWork(Constants.ValueMaps.DEFAULT_REPRESENTATIVE);
            transactionChainReceiveVO.setDate(DateFormatTool.getUTCTimeStamp());
            // tc內容
            String sendJson = gson.toJson(transactionChainReceiveVO);
            //私鑰加密
            String signature = KeyTool.sign(BCAASApplication.getStringFromSP(Constants.Preference.PRIVATE_KEY), sendJson);

            LogTool.d(TAG, "[Receive] TC Signature Original Values：" + sendJson);
            LogTool.d(TAG, "[Receive] TC Signature Values:" + signature);

            //設定tc內容
            transactionChainVO.setTc(transactionChainReceiveVO);
            transactionChainVO.setTxHash(Sha256Tool.doubleSha256ToString(sendJson));

            //設定私鑰加密值
            transactionChainVO.setSignature(signature);
            //S区块的signature
            transactionChainVO.setSignatureSend(signatureSend);
            //公鑰值
            transactionChainVO.setPublicKey(BCAASApplication.getStringFromSP(Constants.Preference.PUBLIC_KEY));
            //產生公私鑰種類
            transactionChainVO.setProduceKeyType(Constants.ValueMaps.PRODUCE_KEY_TYPE);
            WalletVO walletVO = new WalletVO(BCAASApplication.getWalletAddress(),
                    blockService, BCAASApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN));
            DatabaseVO databaseVO = new DatabaseVO(transactionChainVO);
            //透過webRPC發送
            RequestJson requestJson = new RequestJson(walletVO);
            requestJson.setDatabaseVO(databaseVO);
            RequestBody body = GsonTool.beanToRequestBody(requestJson);
            BaseHttpRequester baseHttpRequester = new BaseHttpRequester();
            baseHttpRequester.receive(body, new Callback<ResponseJson>() {
                @Override
                public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                    if (response == null) {
                        return;
                    }
                    responseJson = response.body();
                    LogTool.d(TAG, "[Receive] Success responseJson = " + response.body());
                    if (responseJson != null) {
                        if (responseJson.isSuccess()) {
                            httpTransactionListener.receiveBlockHttpSuccess();
                        } else {
                            int code = responseJson.getCode();
                            if (JsonTool.isTransactionAlreadyExists(code)) {
                                httpTransactionListener.transactionAlreadyExists();
                            } else if (JsonTool.isTokenInvalid(code)) {
                                if (httpASYNTCPResponseListener != null) {
                                    httpASYNTCPResponseListener.logout();
                                }
                            } else {
                                httpTransactionListener.receiveBlockHttpFailure();

                            }
                        }
                    } else {
                        httpTransactionListener.receiveBlockHttpFailure();
                    }
                }

                @Override
                public void onFailure(Call<ResponseJson> call, Throwable t) {
                    LogTool.e(TAG, "[Receive] Failure responseJson = " + t.getMessage());
                    httpTransactionListener.receiveBlockHttpFailure();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            LogTool.e(TAG, e.getMessage());
            httpTransactionListener.receiveBlockHttpFailure();
        }
    }

    /**
     * send
     *
     * @param previous           上一区块的hash
     * @param blockService       交易币种
     * @param destinationWallet  目的钱包
     * @param balanceAfterAmount 交易剩余金额
     * @param amount             交易的金额
     * @return ResponseJson
     */
    public static ResponseJson sendAuthNode(String previous, String blockService,
                                            String destinationWallet, String balanceAfterAmount,
                                            String amount, String representative, HttpASYNTCPResponseListener httpASYNTCPResponseListener) {
        responseJson = null;
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeAdapter(ResponseJson.class, new RequestJsonTypeAdapter())
                .registerTypeAdapter(TransactionChainVO.class, new TransactionChainVOTypeAdapter())
                .registerTypeAdapter(TransactionChainSendVO.class, new TransactionChainSendVOTypeAdapter())
                .create();
        if (StringTool.isEmpty(blockService)) {
            blockService = Constants.BlockService.BCC;
        }
        try {
            //建立Send區塊
            TransactionChainVO<TransactionChainSendVO> transactionChainVO = new TransactionChainVO<TransactionChainSendVO>();
            TransactionChainSendVO transactionChainSendVO = new TransactionChainSendVO();
            transactionChainSendVO.setPrevious(previous);
            transactionChainSendVO.setBlockService(blockService);
            transactionChainSendVO.setBlockType(Constants.ValueMaps.BLOCK_TYPE_SEND);
            transactionChainSendVO.setBlockTxType(Constants.ValueMaps.BLOCK_TX_TYPE);
            transactionChainSendVO.setDestination_wallet(destinationWallet);
            transactionChainSendVO.setBalance(balanceAfterAmount);
            transactionChainSendVO.setAmount(DecimalTool.transferStoreDatabase(amount));
            transactionChainSendVO.setRepresentative(representative);
            transactionChainSendVO.setWallet(BCAASApplication.getWalletAddress());
            transactionChainSendVO.setWork(Constants.ValueMaps.DEFAULT_REPRESENTATIVE);
            transactionChainSendVO.setDate(DateFormatTool.getUTCTimeStamp());
            // tc內容
            String sendJson = gson.toJson(transactionChainSendVO);
            LogTool.d(TAG, "[Send] TC Original Values:" + sendJson);
            //私鑰加密
            String signature = KeyTool.sign(BCAASApplication.getStringFromSP(Constants.Preference.PRIVATE_KEY), sendJson);
            LogTool.d(TAG, "[Send] TC Signature Values:" + signature);

            //設定tc內容
            transactionChainVO.setTc(transactionChainSendVO);
            transactionChainVO.setTxHash(Sha256Tool.doubleSha256ToString(sendJson));

            //設定私鑰加密值
            transactionChainVO.setSignature(signature);
            //公鑰值
            transactionChainVO.setPublicKey(BCAASApplication.getStringFromSP(Constants.Preference.PUBLIC_KEY));
            //產生公私鑰種類
            transactionChainVO.setProduceKeyType(Constants.ValueMaps.PRODUCE_KEY_TYPE);

            WalletVO walletVO = new WalletVO(BCAASApplication.getWalletAddress(),
                    blockService, BCAASApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN));
            DatabaseVO databaseVO = new DatabaseVO(transactionChainVO);
            //透過webRPC發送
            RequestJson requestJson = new RequestJson(walletVO);
            requestJson.setDatabaseVO(databaseVO);
            LogTool.d(TAG, "[Send] RequestJson Values: " + gson.toJson(requestJson));
            RequestBody body = GsonTool.beanToRequestBody(requestJson);
            BaseHttpRequester baseHttpRequester = new BaseHttpRequester();
            baseHttpRequester.send(body, new Callback<ResponseJson>() {
                @Override
                public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                    responseJson = response.body();
                    LogTool.d(TAG, "[Send] ResponseJson Success Values:" + requestJson);
                    int code = responseJson.getCode();
                    if (code == MessageConstants.CODE_200) {

                    } else if (JsonTool.isPublicKeyNotMatch(code)) {
                        // {"success":false,"code":2006,"message":"PublicKey not match.","size":0}
                        if (httpASYNTCPResponseListener != null) {
                            httpASYNTCPResponseListener.sendFailure();
                        }
                    } else if (code == MessageConstants.CODE_2002) {
                        // {"success":false,"code":2002,"message":"Parameter foramt error.","size":0}

                    } else if (JsonTool.isTokenInvalid(code)) {
                        if (httpASYNTCPResponseListener != null) {
                            httpASYNTCPResponseListener.logout();
                        }
                    } else {
                        httpASYNTCPResponseListener.sendFailure();
                    }

                }

                @Override
                public void onFailure(Call<ResponseJson> call, Throwable t) {
                    LogTool.e(TAG, "[Send] ResponseJson Failure Values:" + t.getMessage());

                }
            });

        } catch (Exception e) {
            LogTool.d(TAG, e.getMessage());
            e.printStackTrace();
            return null;
        }
        return responseJson;

    }

    /**
     * 修改授權地址
     *
     * @param previous
     * @param representative
     * @return
     */
    public static ResponseJson change(String previous, String representative, HttpASYNTCPResponseListener httpASYNTCPResponseListener) {
        responseJson = null;
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeAdapter(ResponseJson.class, new RequestJsonTypeAdapter())
                .registerTypeAdapter(TransactionChainVO.class, new TransactionChainVOTypeAdapter())
                .registerTypeAdapter(TransactionChainChangeVO.class, new TransactionChainChangeVOTypeAdapter())
                .create();
        try {
            //建立Change區塊
            TransactionChainVO<TransactionChainChangeVO> transactionChainVO = new TransactionChainVO<>();
            TransactionChainChangeVO transactionChainChangeVO = new TransactionChainChangeVO();
            transactionChainChangeVO.setPrevious(previous);
            transactionChainChangeVO.setBlockService(BCAASApplication.getBlockService());
            transactionChainChangeVO.setBlockType(Constants.ValueMaps.BLOCK_TYPE_CHANGE);
            transactionChainChangeVO.setRepresentative(representative);
            transactionChainChangeVO.setWallet(BCAASApplication.getWalletAddress());
            transactionChainChangeVO.setWork(Constants.ValueMaps.DEFAULT_REPRESENTATIVE);
            transactionChainChangeVO.setDate(DateFormatTool.getUTCTimeStamp());
            // tc內容
            String sendJson = gson.toJson(transactionChainChangeVO);
            LogTool.d(TAG, "[Change] TC Original Values:" + sendJson);
            //私鑰加密
            String signature = KeyTool.sign(BCAASApplication.getStringFromSP(Constants.Preference.PRIVATE_KEY), sendJson);
            LogTool.d(TAG, "[Change] TC Signature Values:" + signature);
            transactionChainVO.setTxHash(Sha256Tool.doubleSha256ToString(sendJson));
            //設定tc內容
            transactionChainVO.setTc(transactionChainChangeVO);
            //設定私鑰加密值
            transactionChainVO.setSignature(signature);
            //公鑰值
            transactionChainVO.setPublicKey(BCAASApplication.getStringFromSP(Constants.Preference.PUBLIC_KEY));
            //產生公私鑰種類
            transactionChainVO.setProduceKeyType(Constants.ValueMaps.PRODUCE_KEY_TYPE);

            WalletVO walletVO = new WalletVO(BCAASApplication.getWalletAddress(),
                    BCAASApplication.getBlockService(),
                    BCAASApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN));
            DatabaseVO databaseVO = new DatabaseVO(transactionChainVO);
            //透過webRPC發送
            RequestJson requestJson = new RequestJson(walletVO);
            requestJson.setDatabaseVO(databaseVO);
            LogTool.d(TAG, "[Change] requestJson = " + gson.toJson(requestJson));
            RequestBody body = GsonTool.beanToRequestBody(requestJson);
            BaseHttpRequester baseHttpRequester = new BaseHttpRequester();
            baseHttpRequester.change(body, new Callback<ResponseJson>() {
                @Override
                public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                    if (response == null) {
                        httpASYNTCPResponseListener.getLatestChangeBlockFailure(MessageConstants.Empty);
                        return;
                    }

                    responseJson = response.body();
                    LogTool.d(TAG, "[Change] Success responseJson = " + responseJson);
                    if (responseJson != null) {
                        //如果当前请求失败
                        if (!responseJson.isSuccess()) {
                            int code = responseJson.getCode();
                            if (JsonTool.isTokenInvalid(code)) {
                                if (httpASYNTCPResponseListener != null) {
                                    httpASYNTCPResponseListener.logout();
                                }
                            } else {
                                httpASYNTCPResponseListener.getLatestChangeBlockFailure(MessageConstants.Empty);

                            }
                        }
                    }

                }

                @Override
                public void onFailure(Call<ResponseJson> call, Throwable t) {
                    LogTool.e(TAG, "[Change] Failure responseJson = " + t.getMessage());
                    httpASYNTCPResponseListener.getLatestChangeBlockFailure(MessageConstants.Empty);
                }
            });

        } catch (Exception e) {
            LogTool.e(TAG, e.getMessage());
            e.printStackTrace();
            httpASYNTCPResponseListener.getLatestChangeBlockFailure(MessageConstants.Empty);
            return null;
        }
        return responseJson;
    }

    /*获取最新的changeBlock，目前在「设置」点击「修改授权代表」进行访问；然后如果能进行代表的修改，那么在点击「确定」页面再次进行访问*/
    public static void getLatestChangeBlock(HttpASYNTCPResponseListener httpASYNTCPResponseListener) {
        RequestJson walletRequestJson = new RequestJson();
        WalletVO walletVO = new WalletVO();
        walletVO.setWalletAddress(BCAASApplication.getWalletAddress());
        walletVO.setBlockService(BCAASApplication.getBlockService());
        walletVO.setAccessToken(BCAASApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN));
        walletRequestJson.setWalletVO(walletVO);
        LogTool.d(TAG, walletRequestJson);
        RequestBody body = GsonTool.beanToRequestBody(walletRequestJson);
        BaseHttpRequester baseHttpRequester = new BaseHttpRequester();
        baseHttpRequester.getLastChangeBlock(body, new Callback<ResponseJson>() {
                    @Override
                    public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                        ResponseJson walletVoResponseJson = response.body();
                        if (walletVoResponseJson == null) {
                            httpASYNTCPResponseListener.getLatestChangeBlockFailure(MessageConstants.Empty);
                            return;
                        }
                        if (walletVoResponseJson.isSuccess()) {
                            LogTool.d(TAG, MessageConstants.GETLATESTCHANGEBLOCK_SUCCESS);
                            httpASYNTCPResponseListener.getLatestChangeBlockSuccess();
                        } else {
                            int code = responseJson.getCode();
                            if (JsonTool.isTokenInvalid(code)) {
                                if (httpASYNTCPResponseListener != null) {
                                    httpASYNTCPResponseListener.logout();
                                }
                            } else {
                                LogTool.d(TAG, walletVoResponseJson.getMessage());
                                httpASYNTCPResponseListener.getLatestChangeBlockFailure(walletVoResponseJson.getMessage());
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseJson> call, Throwable throwable) {
                        LogTool.e(TAG, throwable.getMessage());
                        httpASYNTCPResponseListener.getLatestChangeBlockFailure(throwable.getMessage());
                    }
                }
        );
    }

}
