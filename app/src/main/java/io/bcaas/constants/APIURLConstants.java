package io.bcaas.constants;

/**
 * @author Costa Peng
 * @version 1.0.0
 * @since 2018/07/01
 */

public class APIURLConstants {

    /********* seedNode methodName *********/
    public static final String API_WALLET_RESETAUTHNODEINFO = "wallet/resetAuthNodeInfo";

    public static final String API_WALLET_LOGIN = "wallet/login";

    public static final String API_TRANSACTIONCHAIN_RECEIVE = "/transactionChain/receive";

    public static final String API_TRANSACTIONCHAIN_SEND = "/transactionChain/send";

    public static final String API_WALLET_LOGOUT = "wallet/logout";

    public static final String API_WALLET_VERIFY = "wallet/verify";

    /******* API HTTP ******/
    /*sitapp.bcaas.io*/
    /*获取账户已完成交易*/
    public static final String API_ACCOUNT_DONE_TC = "/transactionChain/getAccountDoneTc";
    /*取得幣種清單*/
    public static final String API_GET_BLOCK_SERVICE_LIST = "/publicUnit/getList";


    /******* AnthNode HTTP ******/
    public static final String API_WALLET_GETLATESTBLOCKANDBALANCE = "/wallet/getLatestBlockAndBalance";

    public static final String API_WALLET_GETWALLETWAITINGTORECEIVEBLOCK = "/wallet/getWalletWaitingToReceiveBlock";

    /******* AnthNode TCP ******/
    public static final String TCP_GETLATESTBLOCKANDBALANCE_SC = "getLatestBlockAndBalance_SC";
    public static final String TCP_GETSENDTRANSACTIONDATA_SC = "getSendTransactionData_SC";
    public static final String TCP_GETRECEIVETRANSACTIONDATA_SC = "getReceiveTransactionData_SC";
    public static final String TCP_GETWALLETWAITINGTORECEIVEBLOCK_SC = "getWalletWaitingToReceiveBlock_SC";
}
