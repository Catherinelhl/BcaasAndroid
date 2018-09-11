package io.bcaas.constants;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 放置讯息常数项
 */
public class MessageConstants {


    public static final boolean STATUS_SUCCESS = true;
    public static final boolean STATUS_FAILURE = false;

    public static final int CODE_200 = 200; // Success
    public static final int CODE_400 = 400; // Failure

    //请求接口的方式
    public static final String REQUEST_MOTHOD_POST = "POST";
    public static final String REQUEST_MOTHOD_GET = "GET";
    //Http连接
    public static final String REQUEST_HTTP = "http://";
    public static final String REQUEST_COLON = ":";

    // Common
    public static final String SUCCESS_REGEX = "Regex Success.";

    public static final int CODE_2001 = 2001;
    public static final String ERROR_LOST_PARAMETERS = "Lost parameters.";
    public static final int CODE_2002 = 2002;
    public static final String ERROR_PARAMETER_FORMAT = "Parameter foramt error.";
    public static final int CODE_2003 = 2003;
    public static final String ERROR_JSON_DECODE = "JSON decode error.";
    public static final int CODE_2004 = 2004;
    public static final String ERROR_NEXT_PAGE_EMPTY = "Next page is empty.";
    public static final int CODE_2005 = 2005;
    public static final String ERROR_SIGNATURE_VERIFY_ERROR = "Signature verify error.";
    public static final int CODE_2006 = 2006;
    public static final String ERROR_PUBLICKEY_NOT_MATCH = "PublicKey not match.";
    public static final int CODE_2007 = 2007;
    public static final String ERROR_TC_BLOCK_HASH_VERIFY = "TC Block hash verify error.";
    public static final int CODE_2008 = 2008;
    public static final String ERROR_BLOCK_PREVIOUS_DATA_VERIFY = "Block previous data verify error.";
    public static final int CODE_2009 = 2009;
    public static final String ERROR_BALANCE_VERIFY = "Balance verify error.";
    public static final int CODE_2010 = 2010;
    public static final String ERROR_AMOUNT_VERIFY = "Amount verify error.";
    public static final int CODE_2011 = 2011;
    public static final String CONFIG_LOADING_ERROR = "Config loading error.";
    public static final int CODE_2014 = 2014;//為變更AuthNode資訊
    public static final int CODE_2025 = 2025;//PublicUnit no data.
    public static final int CODE_3006 = 3006;//Redis data not found
    public static final int CODE_3003 = 3003;//Redis BlockService authnode mapping list not found
    public static final int CODE_3008 = 3008;//Redis data not found
    public static final int CODE_2026 = 2026;//没有创世块，不能修改授权代表
    public static final int CODE_2030 = 2030;//当前授权人与上一次授权人一致
    public static final int CODE_2033 = 2033;//授权人地址错误
    public static final int CODE_2012 = 2012;//授权人地址错误 http


    public static final String API_SERVER_NOT_RESPONSE = "Api server not response.";

    // Transaction Chain
    public static final String SUCCESS_TRANSACTION_CHAIN_ADD = "Add transaction Success.";
    public static final String FAILURE_TRANSACTION_CHAIN_ADD = "Add transaction Failure.";

    // Genesis Block
    public static final String SUCCESS_GENESIS_BLOCK = "Genesis Block Add Success.";
    public static final String FAILURE__GENESIS_BLOCK = "Genesis Block Add Failure.";
    public static final String EXIST__GENESIS_BLOCK = "Genesis Block Is Exist.";

    // public static final String SUCCESS_TRANSACTION_CHAIN_GETLATESTONE = "Get
    // latest one transaction Success.";
    public static final String FAILURE_TRANSACTION_CHAIN_GETLATESTONE = "Get latest one transaction Failure.";

    public static final String SUCCESS_TRANSACTION_CHAIN_GETPAGE = "Get page transaction Success.";
    public static final String FAILURE_TRANSACTION_CHAIN_GETPAGE = "Get page transaction Failure.";

    // Web RPC
    public static final String SUCCESS_RPC_STARTED = "RPC server started Success.";
    public static final String FAILURE_RPC_STARTED = "RPC server started Failure.";
    // Transaction Chain Send
    public static final String SUCCESS_TRANSACTION_CHAIN_SEND = "Transaction chain send Success.";
    public static final String FAILURE_TRANSACTION_CHAIN_SEND = "Transaction chain send Failure.";
    // Transaction Chain Receive
    public static final String SUCCESS_TRANSACTION_CHAIN_RECEIVE = "Transaction chain receive Success.";
    public static final String FAILURE_TRANSACTION_CHAIN_RECEIVE = "Transaction chain receive Failure.";
    // Transaction Chain Open
    public static final String SUCCESS_TRANSACTION_CHAIN_OPEN = "Transaction chain open Success.";
    public static final String FAILURE_TRANSACTION_CHAIN_OPEN = "Transaction chain open Failure.";
    // Transaction Chain Change
    public static final String SUCCESS_TRANSACTION_CHAIN_CHANGE = "Transaction chain change Success.";
    public static final String FAILURE_TRANSACTION_CHAIN_CHANGE = "Transaction chain change Failure.";

    // Get LatestBlock And Balance
    public static final String SUCCESS_GET_LATESTBLOCK_AND_BALANCE = "Get LatestBlock And Balance Success.";
    public static final String FAILURE_GET_LATESTBLOCK_AND_BALANCE = "Get LatestBlock And Balance Failure.";

    // Get SendBlock
    public static final String SUCCESS_GET_WALLET_RECEIVE_BLOCK = "Get  Wallet Waiting To Receive Block Success.";
    public static final String FAILURE_GET_WALLET_RECEIVE_BLOCK = "Get Wallet Waiting To Receive Block Failure.";

    //request param jude print out
    public static final String PREVIOUS_IS_NULL = "previous is null.";//previous
    public static final String VIRTUALCOIN_IS_NULL = "virtualCoin is null.";
    public static final String AMOUNT_IS_NULL = "amount is null.";
    public static final String DESTINATIONWALLET_IS_NULL = "destinationWallet is null.";
    public static final String RESPONSE_IS_NULL = "response is null.";
    public static final String METHOD_NAME_IS_NULL = "method name is null.";
    public static final String WALLETDATABASE_IS_NULL = "wallet database is null.";
    public static final String METHOD_NAME_ERROR = "methodName error.";
    public static final String NO_BLOCK_SERVICE = "no block service";
    public static final String VERIFY_SUCCESS = "Verify success";
    public static final String BALANCE = "balance ：";
    public static final String SEND_TRANSACTION_SATE = "Send Transaction:";
    public static final String HTTP_SEND_SUCCESS = "send http success,wait tcp...";
    public static final String NULL_WALLET = "http getLatestBlockAndBalance_SC wallet is null";
    public static final String SEND_HTTP_FAILED = "send http failed";
    public static final String LOGOUT_SUCCESSFULLY = "logout successfully";
    public static final int ADDRESS_LIMIT = 100;
    public static final String IP_SPLITE = "\\.";
    public static final String CHECK_UPDATE_FAILED = "check update failed";
    public static final String CHECK_UPDATE_SUCCESS = "check update successfully";
    public static final String GOOGLE_PLAY_URI = "https://play.google.com/store/apps/details?id=";
    public static final String GOOGLE_PLAY_MARKET = "market://details?id=";
    public static final String CREATEDB = "createDB";
    public static final String INSERT_KEY_STORE = "step 3:insertKeyStore";
    public static final String UPDATE_KEY_STORE = "step 3:updateKeyStore";
    public static final String HAD_WRITE_PERMISSION = "我已经获取读写权限了";
    public static final String WRITE_PERMISSION_REFUSED = "我被拒绝获取读写权限了";
    public static String WALLET_DATA_FAILURE = "wallet data httpExceptionStatus";


    public class socket {
        public static final String TAG = "+++++++++++";
        public static final String KILL = "socket kill...";
        public static final String EXCEPTION = "socket close Exception...";
        public static final String CLOSE = "socket closed..";
        public static final String SEND_DATA = "发送socket数据：";
        public static final String CONNET_EXCEPTION = "receive connect exception";
        public static final String TCP_RESPONSE = "step 1: tcp 返回数据: ";
        public static final String TCP_TRANSACTION_SUCCESS = "tcp transaction success .";
        public static final String TCP_TRANSACTION_FAILURE = "tcp transaction httpExceptionStatus .";
        public static final String RESET_AN = " 初始化socket失败，请求「sfn」resetAN:";
        public static final String GETLATESTCHANGEBLOCK_SUCCESS = "獲取最新更換委託人區塊成功";
    }

    public static final String GETLATESTBLOCKANDBALANCE_SC = "getLatestBlockAndBalance_SC";
    public static final String GETSENDTRANSACTIONDATA_SC = "getSendTransactionData_SC";
    public static final String GETRECEIVETRANSACTIONDATA_SC = "getReceiveTransactionData_SC";
    public static final String GETWALLETWAITINGTORECEIVEBLOCK_SC = "getWalletWaitingToReceiveBlock_SC";
    public static final String GETLATESTCHANGEBLOCK_SC = "getLatestChangeBlock_SC";//獲取最新委託人區塊
    public static final String GETCHANGETRANSACTIONDATA_SC = "getChangeTransactionData_SC";//更改委託人區塊


    public static final String KEYSTORE_IS_NULL = "keystore is null";

    public class REQUEST_PROPERTY {
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String CONTENT_TYPE_VALUE = "application/json;charset=utf-8";
        public static final String REQUEST_WITH = "X-Requested-With";
        public static final String REQUEST_WITH_VALUE = "XMLHttpRequest";
    }

    //字节码格式
    public static final String CHARSET_FORMAT = "UTF-8";
    public static String HTTP_CONTENT_ENCODING = "Content-Encoding";

}
