package io.bcaas.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.bcaas.base.BcaasApplication;
import io.bcaas.bean.SeedFullNodeBean;
import io.bcaas.constants.Constants;
import io.bcaas.ecc.KeyTool;
import io.bcaas.gson.WalletResponseJson;
import io.bcaas.gson.WalletRequestJson;
import io.bcaas.tools.BcaasLog;
import io.bcaas.vo.*;

import java.util.List;

/**
 * @author: tianyonghong
 * @date: 2018/8/17
 * @description 组装访问AuthNode接口的数据类
 */
public class MasterServices {
    private static String TAG = "MasterServices";

    // 存放用户登录返回的seedFullNode信息
    public static List<SeedFullNodeBean> seedFullNodeBeanList;

    // 存放用户登录验证地址以后返回的ClientIpInfoVO
    public static ClientIpInfoVO clientIpInfoVO;


    /**
     * 初始化时登录以后验证钱包地址
     *
     * @return boolean
     */
//    public static ClientIpInfoVO verify() {
//        try {
//            SeedNodeResponseJson seedNodeResponseJson = getSeedNode(SystemConstants.SEEDFULLNODE_URL_DEFAULT_1 + APIURLConstants.API_WALLET_VERIFY, "BCC", 4, MessageConstants.ACCOUNT_HASHMAP.get("accessToken"), MessageConstants.ACCOUNT_HASHMAP.get("address"));
//
//            if (seedNodeResponseJson != null && seedNodeResponseJson.getCode() == 2014) {
//                System.out.println("钱包地址验证成功");
//
//                MessageConstants.ACCOUNT_HASHMAP.put("accessToken", seedNodeResponseJson.getWalletVO().getAccessToken());
//                clientIpInfoVO = seedNodeResponseJson.getWalletVO().getClientIpInfoVO();
//
//                return clientIpInfoVO;
//            } else {
//                System.out.println("钱包地址验证失败");
//                return null;
//            }
//        } catch (Exception e) {
//            System.out.println("登录异常，检查seedNode。login连接。" + e.getMessage());
//            return null;
//        }
//    }

    /**
     * 请求authNode 获取余额 WalletResponseJson
     * 请求authNode 取得未簽章R區塊的Send區塊 & 取最新的R區塊 & wallet餘額
     *
     * @param virtualCoin   请求币种
     * @param walletAddress 钱包地址
     * @return
     */
    public static WalletResponseJson getWalletBalance(String apiurl, String virtualCoin, String walletAddress, String accessToken) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        //取得錢包
        WalletRequestJson walletRequestJson = new WalletRequestJson(accessToken, virtualCoin, walletAddress);
        try {
            //2018/8/22 请求余额响应数据
            String responseJson = RequestServerConnection.postContentToServer(gson.toJson(walletRequestJson), apiurl);
            WalletResponseJson walletResponseJson = gson.fromJson(responseJson, WalletResponseJson.class);

            return walletResponseJson;
        } catch (Exception e) {
            BcaasLog.d(TAG, "发送交易失败，请求余额出错！");
            return null;
        }
    }

    /**
     * 从seedNode SeedNodeResponseJson 对象,获取IP and PORT and accessToken
     *
     * @param apiUrl      请求路径
     * @param virtualCoin 币种
     * @param option      操作，1:登入login  2:登出logout  3:重置reset  4:验证钱包地址
     * @return SeedNodeResponseJson
    //     */
//    public static SeedNodeResponseJson getSeedNode(String apiUrl, String virtualCoin, int option, String accessToken, String address) {
//        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
//        try {
//            SeedNodeRequestJson seedNodeRequestJson = new SeedNodeRequestJson();
//            WalletVO walletVO = new WalletVO();
//            SeedNodeResponseJson seedNodeResponseJson = null;
//            switch (option) {
//                case 1:
//                    seedNodeRequestJson.setWalletAddress(address);
//                    break;
//                case 2:
//                    seedNodeRequestJson.setWalletAddress(address);
//                    break;
//                case 3:
//                    walletVO.setWalletAddress(address);
//                    walletVO.setAccessToken(accessToken);
//                    walletVO.setBlockService(virtualCoin);
//                    seedNodeRequestJson.setWalletVO(walletVO);
//                    break;
//                case 4:
//                    walletVO.setWalletAddress(address);
//                    walletVO.setAccessToken(accessToken);
//                    walletVO.setBlockService(virtualCoin);
//                    seedNodeRequestJson.setWalletVO(walletVO);
//                    break;
//                default:
//                    System.out.println("发送seedNode值有误");
//                    return null;
//            }
//            String response = RequestServerConnection.postContentToServer(gson.toJson(seedNodeRequestJson), apiUrl);
//            seedNodeResponseJson = gson.fromJson(response, SeedNodeResponseJson.class);
////            System.out.println("response seedNodeRequestJson:" + gson.toJson(seedNodeRequestJson));
////            System.out.println("response seednode:" + response);
//            return seedNodeResponseJson;
//        } catch (Exception e) {
//            SystemTool.showDialog("发送交易异常，请求seednode出错");
//            return null;
//        }
//    }

    /**
     * receice
     *
     * @param previous    上一区块的hash
     * @param virtualCoin 交易币种
     * @param amount      交易的金额
     * @param apiurl      交易路径
     * @return WalletResponseJson
     */
    public static WalletResponseJson receiveAuthNode(String apiurl, String previous, String virtualCoin, String sourceTxHash, String amount, String accessToken, String signatureSend, String blockType) {

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        try {
            //建立Send區塊
            TransactionChainVO<TransactionChainReceiveVO> transactionChainVO = new TransactionChainVO<TransactionChainReceiveVO>();
            TransactionChainReceiveVO transactionChainReceiveVO = new TransactionChainReceiveVO();
            transactionChainReceiveVO.setPrevious(previous);
            transactionChainReceiveVO.setBlockService(virtualCoin);
            transactionChainReceiveVO.setBlockType(blockType);
            transactionChainReceiveVO.setBlockTxType("Matrix");
            transactionChainReceiveVO.setSourceTxhash(sourceTxHash);
            transactionChainReceiveVO.setAmount(amount);
            transactionChainReceiveVO.setRepresentative(BcaasApplication.getWalletAddress());
            transactionChainReceiveVO.setWallet(BcaasApplication.getWalletAddress());
            transactionChainReceiveVO.setWork("0");
            transactionChainReceiveVO.setDate(String.valueOf(System.currentTimeMillis()));
            // tc內容
            String sendJson = gson.toJson(transactionChainReceiveVO);
            //私鑰加密
            String signature = KeyTool.sign(BcaasApplication.getPrivateKey(), sendJson);

            BcaasLog.d(TAG, "[ApiTest_WebRPC_Receive][sendJson] = " + sendJson);

            //設定tc內容
            transactionChainVO.setTc(transactionChainReceiveVO);
            //設定私鑰加密值
            transactionChainVO.setSignature(signature);
            //S区块的signature
            transactionChainVO.setSignatureSend(signatureSend);
            //公鑰值
            transactionChainVO.setPublicKey(BcaasApplication.getPublicKey());
            //產生公私鑰種類
            transactionChainVO.setProduceKeyType(Constants.PRODUCE_KEY_TYPE);

            //透過webRPC發送
            WalletRequestJson walletSendRequestJson = new WalletRequestJson(accessToken, virtualCoin, BcaasApplication.getWalletAddress(), transactionChainVO);
            String walletSendRequestJsonStr = gson.toJson(walletSendRequestJson);
            BcaasLog.d(TAG, "walletReceiveRequestJsonStr = " + walletSendRequestJsonStr);
            //  2018/8/22 发送签章的R区块
            String sendResponseJson = RequestServerConnection.postContentToServer(walletSendRequestJsonStr, apiurl);

            BcaasLog.d(TAG, "[Receive] responseJson = " + sendResponseJson);

            WalletResponseJson walletResponseJson = gson.fromJson(sendResponseJson, WalletResponseJson.class);
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
     * send
     *
     * @param previous           上一区块的hash
     * @param virtualCoin        交易币种
     * @param destinationWallet  目的钱包
     * @param balanceAfterAmount 交易剩余金额
     * @param amount             交易的金额
     * @param apiurl             交易路径
     * @return WalletResponseJson
     */
    public static WalletResponseJson sendAuthNode(String apiurl, String previous, String virtualCoin, String destinationWallet, int balanceAfterAmount, String amount, String accessToken) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        try {
            //建立Send區塊
            TransactionChainVO<TransactionChainSendVO> transactionChainVO = new TransactionChainVO<TransactionChainSendVO>();
            TransactionChainSendVO transactionChainSendVO = new TransactionChainSendVO();
            transactionChainSendVO.setPrevious(previous);
            transactionChainSendVO.setBlockService(virtualCoin);
            transactionChainSendVO.setBlockType(Constants.BLOCK_TYPE_SEND);
            transactionChainSendVO.setBlockTxType("Matrix");
            transactionChainSendVO.setDestination_wallet(destinationWallet);
            transactionChainSendVO.setBalance(String.valueOf(balanceAfterAmount));
            transactionChainSendVO.setAmount(amount);
            transactionChainSendVO.setRepresentative(BcaasApplication.getAccessToken());
            transactionChainSendVO.setWallet(BcaasApplication.getWalletAddress());
            transactionChainSendVO.setWork("0");
            transactionChainSendVO.setDate(String.valueOf(System.currentTimeMillis()));
            // tc內容
            String sendJson = gson.toJson(transactionChainSendVO);
            //私鑰加密
            String signature = KeyTool.sign(BcaasApplication.getPrivateKey(), sendJson);

            BcaasLog.d(TAG, "[ApiTest_WebRPC_Send][sendJson] = " + sendJson);

            //設定tc內容
            transactionChainVO.setTc(transactionChainSendVO);
            //設定私鑰加密值
            transactionChainVO.setSignature(signature);
            //公鑰值
            transactionChainVO.setPublicKey(BcaasApplication.getPublicKey());
            //產生公私鑰種類
            transactionChainVO.setProduceKeyType(Constants.PRODUCE_KEY_TYPE);

            //透過webRPC發送
            WalletRequestJson walletSendRequestJson = new WalletRequestJson(accessToken, virtualCoin, BcaasApplication.getWalletAddress(), transactionChainVO);
            String walletSendRequestJsonStr = gson.toJson(walletSendRequestJson);
            BcaasLog.d(TAG, "walletSendRequestJsonStr = " + walletSendRequestJsonStr);
            String sendResponseJson = RequestServerConnection.postContentToServer(walletSendRequestJsonStr, apiurl);

            BcaasLog.d(TAG, "[Send] responseJson = " + sendResponseJson);
            BcaasLog.d(TAG, "[Send] " + BcaasApplication.getWalletAddress() + "發送後剩餘 = " + balanceAfterAmount);

            WalletResponseJson walletResponseJson = gson.fromJson(sendResponseJson, WalletResponseJson.class);
            if (walletResponseJson.getCode() != 200) {
                return null;
            }
            return walletResponseJson;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
