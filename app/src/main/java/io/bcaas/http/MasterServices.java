package io.bcaas.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.bcaas.base.BCAASApplication;
import io.bcaas.bean.ServerBean;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.gson.jsonTypeAdapter.RequestJsonTypeAdapter;
import io.bcaas.gson.jsonTypeAdapter.TransactionChainChangeVOTypeAdapter;
import io.bcaas.gson.jsonTypeAdapter.TransactionChainReceiveVOTypeAdapter;
import io.bcaas.gson.jsonTypeAdapter.TransactionChainSendVOTypeAdapter;
import io.bcaas.gson.jsonTypeAdapter.TransactionChainVOTypeAdapter;
import io.bcaas.listener.HttpRequestListener;
import io.bcaas.requester.BaseHttpRequester;
import io.bcaas.requester.SettingRequester;
import io.bcaas.tools.DateFormatTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.NetWorkTool;
import io.bcaas.tools.ServerTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.decimal.DecimalTool;
import io.bcaas.tools.ecc.KeyTool;
import io.bcaas.tools.ecc.Sha256Tool;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.vo.ClientIpInfoVO;
import io.bcaas.vo.DatabaseVO;
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
    private HttpRequestListener httpRequestListener;
    private static ResponseJson responseJson;


    public MasterServices(HttpRequestListener httpRequestListener) {
        super();
        this.httpRequestListener = httpRequestListener;
    }

    /**
     * 重置AN信息
     */
    public static void reset() {
        WalletVO walletVO = new WalletVO();
        walletVO.setWalletAddress(BCAASApplication.getWalletAddress());
        walletVO.setAccessToken(BCAASApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN));
        walletVO.setBlockService(BCAASApplication.getBlockService());
        RequestJson requestJson = new RequestJson(walletVO);
        BaseHttpRequester baseHttpRequester = new BaseHttpRequester();
        baseHttpRequester.resetAuthNode(GsonTool.beanToRequestBody(requestJson), new Callback<ResponseJson>() {
            @Override
            public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                ResponseJson walletVoResponseJson = response.body();
                if (walletVoResponseJson != null) {
                    if (walletVoResponseJson.isSuccess()) {
                        WalletVO walletVOResponse = walletVoResponseJson.getWalletVO();
                        if (walletVOResponse != null) {
                            BCAASApplication.setStringToSP(Constants.Preference.ACCESS_TOKEN, walletVO.getAccessToken());
                            clientIpInfoVO = walletVOResponse.getClientIpInfoVO();
                            if (clientIpInfoVO == null) {
                            } else {
                                BCAASApplication.setClientIpInfoVO(clientIpInfoVO);
                                BCAASApplication.setWalletExternalIp(walletVO.getWalletExternalIp());
                            }
                        }
                    } else {
                        int code = walletVoResponseJson.getCode();
                        if (code == MessageConstants.CODE_3003) {
                            //如果是3003，那么则没有可用的SAN，需要reset一个
                            LogTool.d(TAG, MessageConstants.ON_RESET_AUTH_NODE_INFO);

                        } else {
                            LogTool.d(TAG, walletVoResponseJson);
//                            httpView.httpExceptionStatus(walletVoResponseJson);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseJson> call, Throwable throwable) {
                if (NetWorkTool.connectTimeOut(throwable)) {
                    //如果當前是服務器訪問不到或者連接超時，那麼需要重新切換服務器
                    LogTool.d(TAG, MessageConstants.CONNECT_TIME_OUT);
                    //1：得到新的可用的服务器
                    ServerBean serverBean = ServerTool.checkAvailableServerToSwitch();
                    if (serverBean != null) {
////                        RetrofitFactory.cleanSFN();
////                        reset();
                    } else {
                        ServerTool.needResetServerStatus = true;
                    }
                } else {
                    LogTool.d(TAG, throwable.getMessage());
                }
            }
        });
    }

    /**
     * 请求authNode 获取余额 ResponseJson
     * 请求authNode 取得未簽章R區塊的Send區塊 & 取最新的R區塊 & wallet餘額
     *
     * @param virtualCoin   请求币种
     * @param walletAddress 钱包地址
     * @return
     */
    public static ResponseJson getWalletBalance(String apiUrl, String virtualCoin, String walletAddress) {
        Gson gson = GsonTool.getGson();
        //取得錢包
        WalletVO walletVO = new WalletVO(walletAddress, virtualCoin, BCAASApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN));
        RequestJson requestJson = new RequestJson(walletVO);
        try {
            //2018/8/22 请求余额响应数据
            String responseJson = RequestServerConnection.postContentToServer(gson.toJson(requestJson), apiUrl);
            ResponseJson walletResponseJson = gson.fromJson(responseJson, ResponseJson.class);

            return walletResponseJson;
        } catch (Exception e) {
            LogTool.d(TAG, e.getMessage());
            LogTool.d(TAG, "发送交易失败，请求余额出错！");
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
                    LogTool.d(TAG, "发送seedNode值有误");
                    return null;
            }
            String response = RequestServerConnection.postContentToServer(gson.toJson(requestJson), apiUrl);
            responseJson = gson.fromJson(response, ResponseJson.class);
            return responseJson;
        } catch (Exception e) {
            LogTool.d(TAG, e.getMessage());
            LogTool.d(TAG, "发送交易异常，请求seednode出错");
            return null;
        }
    }

    /**
     * receice
     *
     * @param previous     上一区块的hash
     * @param blockService 交易币种
     * @param amount       交易的金额
     * @return ResponseJson
     */
    public static ResponseJson receiveAuthNode(String previous, String blockService, String sourceTxHash, String amount, String signatureSend, String blockType, String representative, String receiveAmount) {
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
                    LogTool.d(TAG, "[Receive] responseJson = " + response.body());
                }

                @Override
                public void onFailure(Call<ResponseJson> call, Throwable t) {
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
     * send
     *
     * @param previous           上一区块的hash
     * @param blockService       交易币种
     * @param destinationWallet  目的钱包
     * @param balanceAfterAmount 交易剩余金额
     * @param amount             交易的金额
     * @return ResponseJson
     */
    public static ResponseJson sendAuthNode(String previous, String blockService, String destinationWallet, String balanceAfterAmount, String amount, String representative) {
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
                    LogTool.d(TAG, "[Send] ResponseJson Values:" + requestJson);
                    int code = responseJson.getCode();
                    if (code == MessageConstants.CODE_200) {

                    } else if (code == MessageConstants.CODE_2002) {
                        // {"success":false,"code":2002,"message":"Parameter foramt error.","size":0}

                    }

                }

                @Override
                public void onFailure(Call<ResponseJson> call, Throwable t) {

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
    public static ResponseJson change(String previous, String representative) {
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
            SettingRequester settingRequester = new SettingRequester();
            settingRequester.change(body, new Callback<ResponseJson>() {
                @Override
                public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                    if (response == null) {
                        return;
                    }
                    responseJson = response.body();
                    LogTool.d(TAG, "[Change] responseJson = " + response.body());
                }

                @Override
                public void onFailure(Call<ResponseJson> call, Throwable t) {
                }
            });

        } catch (Exception e) {
            LogTool.d(TAG, e.getMessage());
            e.printStackTrace();
            return null;
        }
        return responseJson;
    }

    /*获取最新的changeBlock，目前在「设置」点击「修改授权代表」进行访问；然后如果能进行代表的修改，那么在点击「确定」页面再次进行访问*/
    public static void getLatestChangeBlock() {
        RequestJson walletRequestJson = new RequestJson();
        WalletVO walletVO = new WalletVO();
        walletVO.setWalletAddress(BCAASApplication.getWalletAddress());
        walletVO.setBlockService(BCAASApplication.getBlockService());
        walletVO.setAccessToken(BCAASApplication.getStringFromSP(Constants.Preference.ACCESS_TOKEN));
        walletRequestJson.setWalletVO(walletVO);
        LogTool.d(TAG, walletRequestJson);
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
                            LogTool.d(TAG, MessageConstants.GETLATESTCHANGEBLOCK_SUCCESS);
                        } else {
                            LogTool.d(TAG, walletVoResponseJson.getMessage());
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseJson> call, Throwable t) {
                        LogTool.d(TAG, t.getMessage());

                    }
                }
        );
    }
}
