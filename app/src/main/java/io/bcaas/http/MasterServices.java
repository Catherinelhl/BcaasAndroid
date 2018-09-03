package io.bcaas.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import io.bcaas.base.BcaasApplication;
import io.bcaas.bean.SeedFullNodeBean;
import io.bcaas.constants.APIURLConstants;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.constants.SystemConstants;
import io.bcaas.ecc.KeyTool;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.gson.jsonTypeAdapter.RequestJsonTypeAdapter;
import io.bcaas.gson.jsonTypeAdapter.TransactionChainChangeVOTypeAdapter;
import io.bcaas.gson.jsonTypeAdapter.TransactionChainReceiveVOTypeAdapter;
import io.bcaas.gson.jsonTypeAdapter.TransactionChainSendVOTypeAdapter;
import io.bcaas.gson.jsonTypeAdapter.TransactionChainVOTypeAdapter;
import io.bcaas.listener.RequestResultListener;
import io.bcaas.requester.SettingRequester;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.GsonTool;
import io.bcaas.vo.ClientIpInfoVO;
import io.bcaas.vo.DatabaseVO;
import io.bcaas.vo.PaginationVO;
import io.bcaas.vo.TransactionChainChangeVO;
import io.bcaas.vo.TransactionChainReceiveVO;
import io.bcaas.vo.TransactionChainSendVO;
import io.bcaas.vo.TransactionChainVO;
import io.bcaas.vo.WalletVO;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author: tianyonghong
 * @date: 2018/8/17
 * @description 组装访问AuthNode接口的数据类
 */
public class MasterServices {
    private static String TAG = MasterServices.class.getSimpleName();

    // 存放用户登录验证地址以后返回的ClientIpInfoVO
    public static ClientIpInfoVO clientIpInfoVO;

    /**
     * 重置AN信息
     */
    public static ClientIpInfoVO reset() {
        try {
            ResponseJson responseJson = getSeedNode(SystemConstants.SEEDFULLNODE_URL_DEFAULT_1 + APIURLConstants.API_WALLET_RESETAUTHNODEINFO,
                    BcaasApplication.getBlockService(),
                    4,
                    BcaasApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN),
                    BcaasApplication.getWalletAddress());

            if (responseJson != null && responseJson.isSuccess()) {
                BcaasLog.d(TAG, "AuthNode reset success:" + responseJson);
                WalletVO walletVO = responseJson.getWalletVO();
                BcaasLog.d(TAG, "AuthNode reset success:" + responseJson);

                if (walletVO != null) {
                    BcaasApplication.setStringToSP(Constants.Preference.ACCESS_TOKEN, walletVO.getAccessToken());
                    clientIpInfoVO = responseJson.getWalletVO().getClientIpInfoVO();
                    if (clientIpInfoVO == null) {
//                        requestResultListener.resetAuthNodeFailure("AuthNode reset clientIpInfoVO is null");
                    } else {
                        return clientIpInfoVO;
//                        requestResultListener.resetAuthNodeSuccess(clientIpInfoVO);

                    }
                } else {
//                    requestResultListener.resetAuthNodeFailure("AuthNode  reset walletVO is null");
                }
            } else {
//                requestResultListener.resetAuthNodeFailure("AuthNode reset failure");
            }
        } catch (Exception e) {
            BcaasLog.d(TAG, e.getMessage());
//            requestResultListener.resetAuthNodeFailure("login seedFullNode exception ,reset connect:" + e.getMessage());
        }
        return null;
    }

    /**
     * 初始化时登录以后验证钱包地址
     *
     * @return boolean
     */
    public static ClientIpInfoVO verify() {
        try {
            ResponseJson responseJson = getSeedNode(SystemConstants.SEEDFULLNODE_URL_DEFAULT_1 + APIURLConstants.API_WALLET_VERIFY, "BCC", 4, BcaasApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN), BcaasApplication.getWalletAddress());

            if (responseJson != null && responseJson.isSuccess()) {
                BcaasLog.d(TAG, "钱包地址验证成功");
                BcaasApplication.setStringToSP(Constants.Preference.ACCESS_TOKEN, responseJson.getWalletVO().getAccessToken());
                clientIpInfoVO = responseJson.getWalletVO().getClientIpInfoVO();

                return clientIpInfoVO;
            } else if (responseJson.getCode() == 3006) {//Redis data not found.
                BcaasLog.d(TAG, "登录失效,请重新登录");
                return clientIpInfoVO;
            } else {
                return null;
            }
        } catch (Exception e) {
            BcaasLog.d(TAG, "登录异常，检查seedNode。login连接。" + e.getMessage());
            return null;
        }
    }

    /**
     * 请求authNode 取得未簽章R區塊的Send區塊 & 取最新的R區塊 & wallet餘額
     *
     * @param virtualCoin   请求币种
     * @param walletAddress 钱包地址
     * @return
     */
    public static ResponseJson getWalletWaiting(String apiurl, String virtualCoin, String walletAddress, String accessToken, String nextObjectId) {
        Gson gson = GsonTool.getGson();

        //取得錢包
        WalletVO walletVO = new WalletVO();
        walletVO.setBlockService(virtualCoin);
        walletVO.setWalletAddress(walletAddress);
        walletVO.setAccessToken(accessToken);

        PaginationVO paginationVO = new PaginationVO();
        paginationVO.setNextObjectId(nextObjectId);

        RequestJson requestJson = new RequestJson();
        requestJson.setWalletVO(walletVO);
        requestJson.setPaginationVO(paginationVO);

        try {
            apiurl = "http://" + clientIpInfoVO.getExternalIp() + ":" + clientIpInfoVO.getRpcPort() + apiurl;

            String responseStr = RequestServerConnection.postContentToServer(gson.toJson(requestJson), apiurl);
            ResponseJson responseJson = gson.fromJson(responseStr, ResponseJson.class);

            return responseJson;
        } catch (Exception e) {
            System.out.println("请求authNode 取得未簽章R區塊出错！");
            return null;
        }
    }

    /**
     * 初始化时登入
     *
     * @return boolean
     */
    public static List<SeedFullNodeBean> login() {
        try {
            ResponseJson responseJson = getSeedNode(SystemConstants.SEEDFULLNODE_URL_DEFAULT_1 + APIURLConstants.API_WALLET_LOGIN, "BCC", 1, null, BcaasApplication.getWalletAddress());

            if (responseJson != null && responseJson.getCode() == 200) {
                BcaasLog.d(TAG, "登录成功");
                BcaasApplication.setStringToSP(Constants.Preference.ACCESS_TOKEN, responseJson.getWalletVO().getAccessToken());
                // 存放用户登录返回的seedFullNode信息
                List<SeedFullNodeBean> seedFullNodeBeanList = responseJson.getWalletVO().getSeedFullNodeList();

                return seedFullNodeBeanList;
            } else {
                BcaasLog.d(TAG, "登录失败");
                return null;
            }
        } catch (Exception e) {
            BcaasLog.d(TAG, "登录异常，检查seedNode。login连接。" + e.getMessage());
            return null;
        }
    }

    /**
     * 请求authNode 获取余额 ResponseJson
     * 请求authNode 取得未簽章R區塊的Send區塊 & 取最新的R區塊 & wallet餘額
     *
     * @param virtualCoin   请求币种
     * @param walletAddress 钱包地址
     * @return
     */
    public static ResponseJson getWalletBalance(String apiurl, String virtualCoin, String walletAddress, String accessToken) {
        Gson gson = GsonTool.getGson();
        //取得錢包
        WalletVO walletVO = new WalletVO(walletAddress, virtualCoin, accessToken);
        RequestJson requestJson = new RequestJson(walletVO);
        try {
            //2018/8/22 请求余额响应数据
            String responseJson = RequestServerConnection.postContentToServer(gson.toJson(requestJson), apiurl);
            ResponseJson walletResponseJson = gson.fromJson(responseJson, ResponseJson.class);

            return walletResponseJson;
        } catch (Exception e) {
            BcaasLog.d(TAG, "发送交易失败，请求余额出错！");
            return null;
        }
    }

    /**
     * 从seedNode ServerResponseJson 对象,获取IP and PORT and accessToken
     *
     * @param apiUrl      请求路径
     * @param virtualCoin 币种
     * @param option      操作，1:登入login  2:登出logout  3:重置reset  4:验证钱包地址
     * @return ServerResponseJson
     */
    public static ResponseJson getSeedNode(String apiUrl, String virtualCoin, int option, String accessToken, String address) {
        Gson gson = GsonTool.getGson();
        try {
            RequestJson requestJson = new RequestJson();
            WalletVO walletVO = new WalletVO();
            ResponseJson responseJson = null;
            switch (option) {
                case 1:
                    walletVO.setWalletAddress(address);
                    requestJson.setWalletVO(walletVO);
                    break;
                case 2:
                    walletVO.setWalletAddress(address);
                    requestJson.setWalletVO(walletVO);
                    break;
                case 3:
                    walletVO.setWalletAddress(address);
                    walletVO.setAccessToken(accessToken);
                    walletVO.setBlockService(virtualCoin);
                    requestJson.setWalletVO(walletVO);
                    break;
                case 4:
                    walletVO.setWalletAddress(address);
                    walletVO.setAccessToken(accessToken);
                    walletVO.setBlockService(virtualCoin);
                    requestJson.setWalletVO(walletVO);
                    break;
                default:
                    BcaasLog.d(TAG, "发送seedNode值有误");
                    return null;
            }
            String response = RequestServerConnection.postContentToServer(gson.toJson(requestJson), apiUrl);
            responseJson = gson.fromJson(response, ResponseJson.class);
            return responseJson;
        } catch (Exception e) {
            // TODO: 2018/8/24
            BcaasLog.d(TAG, "发送交易异常，请求seednode出错");
            return null;
        }
    }

    /**
     * receice
     *
     * @param previous    上一区块的hash
     * @param virtualCoin 交易币种
     * @param amount      交易的金额
     * @return ResponseJson
     */
    public static ResponseJson receiveAuthNode(String previous, String virtualCoin, String sourceTxHash, String amount, String accessToken, String signatureSend, String blockType) {
        BcaasLog.d(TAG, "receiveAuthNode:" + BcaasApplication.getWalletAddress());
        String address = BcaasApplication.getWalletAddress();
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeAdapter(ResponseJson.class, new RequestJsonTypeAdapter())
                .registerTypeAdapter(TransactionChainVO.class, new TransactionChainVOTypeAdapter())
                .registerTypeAdapter(TransactionChainReceiveVO.class, new TransactionChainReceiveVOTypeAdapter())
                .create();
        try {
            //建立Receive區塊
            TransactionChainVO<TransactionChainReceiveVO> transactionChainVO = new TransactionChainVO<TransactionChainReceiveVO>();
            TransactionChainReceiveVO transactionChainReceiveVO = new TransactionChainReceiveVO();
            transactionChainReceiveVO.setPrevious(previous);
            transactionChainReceiveVO.setBlockService(virtualCoin);
            transactionChainReceiveVO.setBlockType(blockType);
            transactionChainReceiveVO.setBlockTxType(Constants.ValueMaps.BLOCK_TX_TYPE);
            transactionChainReceiveVO.setSourceTxhash(sourceTxHash);
            transactionChainReceiveVO.setAmount(amount);
            transactionChainReceiveVO.setRepresentative(address);
            transactionChainReceiveVO.setWallet(address);
            transactionChainReceiveVO.setWork("0");
            transactionChainReceiveVO.setDate(String.valueOf(System.currentTimeMillis()));
            // tc內容
            String sendJson = gson.toJson(transactionChainReceiveVO);
            //私鑰加密
            String signature = KeyTool.sign(BcaasApplication.getStringFromSP(Constants.Preference.PRIVATE_KEY), sendJson);

            BcaasLog.d(TAG, "[ApiTest_WebRPC_Receive][sendJson] = " + sendJson);

            //設定tc內容
            transactionChainVO.setTc(transactionChainReceiveVO);
            //設定私鑰加密值
            transactionChainVO.setSignature(signature);
            //S区块的signature
            transactionChainVO.setSignatureSend(signatureSend);
            //公鑰值
            transactionChainVO.setPublicKey(BcaasApplication.getStringFromSP(Constants.Preference.PUBLIC_KEY));
            //產生公私鑰種類
            transactionChainVO.setProduceKeyType(Constants.ValueMaps.PRODUCE_KEY_TYPE);
            WalletVO walletVO = new WalletVO(BcaasApplication.getWalletAddress(),
                    virtualCoin, accessToken);
            DatabaseVO databaseVO = new DatabaseVO(transactionChainVO);
            //透過webRPC發送
            RequestJson requestJson = new RequestJson(walletVO);
            requestJson.setDatabaseVO(databaseVO);

            //  2018/8/22 发送签章的R区块
            String sendResponseJson = RequestServerConnection.postContentToServer(gson.toJson(requestJson), BcaasApplication.getANHttpAddress() + Constants.RequestUrl.receive);

            BcaasLog.d(TAG, "[Receive] responseJson = " + sendResponseJson);
            ResponseJson walletResponseJson = gson.fromJson(sendResponseJson, ResponseJson.class);
            BcaasLog.d(TAG, walletResponseJson);
            if (walletResponseJson != null) {
                if (walletResponseJson.getCode() != 200) {
                    return null;
                }
            }
            return walletResponseJson;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * send
     *
     * @param previous           上一区块的hash
     * @param virtualCoin        交易币种
     * @param destinationWallet  目的钱包
     * @param balanceAfterAmount 交易剩余金额
     * @param amount             交易的金额
     * @return ResponseJson
     */
    public static ResponseJson sendAuthNode(String previous, String virtualCoin, String destinationWallet, long balanceAfterAmount, String amount, String accessToken) {
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeAdapter(ResponseJson.class, new RequestJsonTypeAdapter())
                .registerTypeAdapter(TransactionChainVO.class, new TransactionChainVOTypeAdapter())
                .registerTypeAdapter(TransactionChainSendVO.class, new TransactionChainSendVOTypeAdapter())
                .create();
        try {
            //建立Send區塊
            TransactionChainVO<TransactionChainSendVO> transactionChainVO = new TransactionChainVO<TransactionChainSendVO>();
            TransactionChainSendVO transactionChainSendVO = new TransactionChainSendVO();
            transactionChainSendVO.setPrevious(previous);
            transactionChainSendVO.setBlockService(virtualCoin);
            transactionChainSendVO.setBlockType(Constants.ValueMaps.BLOCK_TYPE_SEND);
            transactionChainSendVO.setBlockTxType(Constants.ValueMaps.BLOCK_TX_TYPE);
            transactionChainSendVO.setDestination_wallet(destinationWallet);
            transactionChainSendVO.setBalance(String.valueOf(balanceAfterAmount));
            transactionChainSendVO.setAmount(amount);
            transactionChainSendVO.setRepresentative(BcaasApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN));
            transactionChainSendVO.setWallet(BcaasApplication.getWalletAddress());
            transactionChainSendVO.setWork("0");
            transactionChainSendVO.setDate(String.valueOf(System.currentTimeMillis()));
            // tc內容
            String sendJson = gson.toJson(transactionChainSendVO);
            BcaasLog.d(TAG, "Send TC Original Values:" + sendJson);
            //私鑰加密
            String signature = KeyTool.sign(BcaasApplication.getStringFromSP(Constants.Preference.PRIVATE_KEY), sendJson);
            BcaasLog.d(TAG, "Send TC Signature Values:" + signature);


            BcaasLog.d(TAG, "[ApiTest_WebRPC_Send][sendJson] = " + sendJson);

            //設定tc內容
            transactionChainVO.setTc(transactionChainSendVO);
            //設定私鑰加密值
            transactionChainVO.setSignature(signature);
            //公鑰值
            transactionChainVO.setPublicKey(BcaasApplication.getStringFromSP(Constants.Preference.PUBLIC_KEY));
            //產生公私鑰種類
            transactionChainVO.setProduceKeyType(Constants.ValueMaps.PRODUCE_KEY_TYPE);

            WalletVO walletVO = new WalletVO(BcaasApplication.getWalletAddress(),
                    virtualCoin, accessToken);
            DatabaseVO databaseVO = new DatabaseVO(transactionChainVO);
            //透過webRPC發送
            RequestJson requestJson = new RequestJson(walletVO);
            requestJson.setDatabaseVO(databaseVO);
            String walletSendRequestJsonStr = gson.toJson(requestJson);
            BcaasLog.d(TAG, "walletSendRequestJsonStr = " + walletSendRequestJsonStr);
            String sendResponseJson = RequestServerConnection.postContentToServer(walletSendRequestJsonStr, BcaasApplication.getANHttpAddress() + Constants.RequestUrl.send);

            BcaasLog.d(TAG, "[Send] responseJson = " + sendResponseJson);
            BcaasLog.d(TAG, "[Send] " + BcaasApplication.getWalletAddress() + "發送後剩餘 = " + balanceAfterAmount);

            ResponseJson walletResponseJson = GsonTool.getGson().fromJson(sendResponseJson, ResponseJson.class);
            if (walletResponseJson.getCode() != 200) {
                return null;
            }
            return walletResponseJson;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 修改授權地址
     *
     * @param previous
     * @param representative
     * @param blockService
     * @return
     */
    public static ResponseJson change(String previous, String representative, String blockService) {
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
            transactionChainChangeVO.setBlockService(blockService);
            transactionChainChangeVO.setBlockType(Constants.ValueMaps.BLOCK_TYPE_CHANGE);
            transactionChainChangeVO.setRepresentative(representative);
            transactionChainChangeVO.setWallet(BcaasApplication.getWalletAddress());
            transactionChainChangeVO.setWork("0");
            transactionChainChangeVO.setDate(String.valueOf(System.currentTimeMillis()));
            // tc內容
            String sendJson = gson.toJson(transactionChainChangeVO);
            BcaasLog.d(TAG, "Change TC Original Values:" + sendJson);
            //私鑰加密
            String signature = KeyTool.sign(BcaasApplication.getStringFromSP(Constants.Preference.PRIVATE_KEY), sendJson);
            BcaasLog.d(TAG, "Change TC Signature Values:" + signature);


            BcaasLog.d(TAG, "[ApiTest_WebRPC_Send][sendJson] = " + sendJson);

            //設定tc內容
            transactionChainVO.setTc(transactionChainChangeVO);
            //設定私鑰加密值
            transactionChainVO.setSignature(signature);
            //公鑰值
            transactionChainVO.setPublicKey(BcaasApplication.getStringFromSP(Constants.Preference.PUBLIC_KEY));
            //產生公私鑰種類
            transactionChainVO.setProduceKeyType(Constants.ValueMaps.PRODUCE_KEY_TYPE);

            WalletVO walletVO = new WalletVO(BcaasApplication.getWalletAddress(),
                    blockService, BcaasApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN));
            DatabaseVO databaseVO = new DatabaseVO(transactionChainVO);
            //透過webRPC發送
            RequestJson requestJson = new RequestJson(walletVO);
            requestJson.setDatabaseVO(databaseVO);
            String walletSendRequestJsonStr = gson.toJson(requestJson);
            BcaasLog.d(TAG, "walletSendRequestJsonStr = " + walletSendRequestJsonStr);
            String sendResponseJson = RequestServerConnection.postContentToServer(walletSendRequestJsonStr, BcaasApplication.getANHttpAddress() + Constants.RequestUrl.change);

            BcaasLog.d(TAG, "[Change] responseJson = " + sendResponseJson);
            ResponseJson walletResponseJson = GsonTool.getGson().fromJson(sendResponseJson, ResponseJson.class);
            if (walletResponseJson.getCode() != 200) {
                return null;
            }
            return walletResponseJson;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*获取最新的changeBlock*/
    public static void getLatestChangeBlock() {
        RequestJson walletRequestJson = new RequestJson();
        WalletVO walletVO = new WalletVO();
        walletVO.setWalletAddress(BcaasApplication.getWalletAddress());
        walletVO.setBlockService(BcaasApplication.getBlockService());
        walletVO.setAccessToken(BcaasApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN));
        walletRequestJson.setWalletVO(walletVO);
        RequestBody body = GsonTool.beanToRequestBody(walletRequestJson);
        SettingRequester settingRequester = new SettingRequester();
        settingRequester.getLastChangeBlock(body, new Callback<ResponseJson>() {
                    @Override
                    public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                        ResponseJson walletVoResponseJson = response.body();
                        if (walletVoResponseJson == null) {
                            return;
                        }
                        if (walletVoResponseJson.isSuccess()) {
                            BcaasLog.d(TAG, MessageConstants.socket.GETLATESTCHANGEBLOCK_SUCCESS);
                        } else {
                            BcaasLog.d(TAG, walletVoResponseJson.getMessage());
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseJson> call, Throwable t) {
                        BcaasLog.d(TAG, t.getMessage());

                    }
                }
        );
    }
}
