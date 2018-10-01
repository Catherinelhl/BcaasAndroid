package io.bcaas.constants;

/**
 * @author Costa Peng
 * @version 1.0.0
 * @since 2018/07/01
 */

public class APIURLConstants {

    /********* seedNode methodName *********/
    //当钱包与AuthNode无法通过时调用，取得新的AnthNode Ip资讯
    public static final String API_SFN_WALLET_RESETAUTHNODEINFO = "/wallet/resetAuthNodeInfo";
    //登入SFN
    public static final String API_SFN_WALLET_LOGIN = "/wallet/login";
    //登出SFN
    public static final String API_SFN_WALLET_LOGOUT = "/wallet/logout";
    //验证AccessToken是否可以使用
    public static final String API_SFN_WALLET_VERIFY = "/wallet/verify";


    /******* API HTTP [sitapp.bcaas.io] ******/
    /*获取账户已完成交易*/
    public static final String API_ACCOUNT_DONE_TC = "/transactionChain/getAccountDoneTc";
    /*取得幣種清單*/
    public static final String API_GET_BLOCK_SERVICE_LIST = "/publicUnit/getList";
    /* 检查Android版本信息*/
    public static final String API_GETANDROIDVERSIONINFO = "/getAndroidVersionInfo";


    /******* AnthNode HTTP ******/
    //获取最新的区块和Wallet余额 AN
    public static final String API_SAN_WALLET_GETLATESTBLOCKANDBALANCE = "/wallet/getLatestBlockAndBalance";
    //取得未簽章R區塊的Send區塊 &取最新的R區塊 &wallet餘額
    public static final String API_SAN_WALLET_GETWALLETWAITINGTORECEIVEBLOCK = "/wallet/getWalletWaitingToReceiveBlock";
    /*单独获取余额*/
    public static final String API_SAN_WALLET_GETBALANCE= "/wallet/getBalance";
    //获取最新的更換委託人區塊 AN
    public static final String API_SAN_WALLET_GETLATESTCHANGEBLOCK = "/wallet/getLatestChangeBlock";
    //TC Receive
    public static final String API_SAN_WALLET_TRANSACTIONCHAIN_RECEIVE = "/transactionChain/receive";
    //TC Send
    public static final String API_SAN_WALLET_TRANSACTIONCHAIN_SEND = "/transactionChain/send";
    //TC change AN
    public static final String API_SAN_WALLET_CHANGE = "/transactionChain/change";


    /******* AnthNode TCP ******/
    public static final String TCP_GETLATESTBLOCKANDBALANCE_SC = "getLatestBlockAndBalance_SC";

    public static final String TCP_GETSENDTRANSACTIONDATA_SC = "getSendTransactionData_SC";

    public static final String TCP_GETRECEIVETRANSACTIONDATA_SC = "getReceiveTransactionData_SC";

    public static final String TCP_GETWALLETWAITINGTORECEIVEBLOCK_SC = "getWalletWaitingToReceiveBlock_SC";
}
