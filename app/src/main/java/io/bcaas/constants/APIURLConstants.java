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

    public static final String API_WALLET_LOGOUT = "wallet/logoutDialog";

    public static final String API_WALLET_VERIFY = "wallet/verify";


    /******* AnthNode HTTP ******/
    public static final String API_WALLET_GETLATESTBLOCKANDBALANCE = "/wallet/getLatestBlockAndBalance";

    public static final String API_WALLET_GETWALLETWAITINGTORECEIVEBLOCK = "/wallet/getWalletWaitingToReceiveBlock";

    /******* AnthNode TCP ******/
    public static final String TCP_GETLATESTBLOCKANDBALANCE_SC="getLatestBlockAndBalance_SC";
    public static final String TCP_GETSENDTRANSACTIONDATA_SC="getSendTransactionData_SC";
    public static final String TCP_GETRECEIVETRANSACTIONDATA_SC="getReceiveTransactionData_SC";
    public static final String TCP_GETWALLETWAITINGTORECEIVEBLOCK_SC="getWalletWaitingToReceiveBlock_SC";
}
