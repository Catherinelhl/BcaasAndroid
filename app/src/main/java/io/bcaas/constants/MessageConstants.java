package io.bcaas.constants;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 定義訊息常數
 */
public class MessageConstants {


    public static final int CODE_0 = 0;
    public static final int CODE_200 = 200; // Success
    public static final int CODE_400 = 400; // Failure
    public static final int CODE_404 = 404; // Failure
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
    public static final String ERROR_CONFIG_LOADING = "Config loading error.";
    public static final int CODE_2012 = 2012;//授权人地址错误 http
    public static final String ERROR_WALLET_ADDRESS_INVALID = "Wallet address invalid error.";
    public static final int CODE_2013 = 2013;
    public static final String ERROR_ADD_TRANSACTION_DATA = "Add Transaction Data error.";
    public static final int CODE_2014 = 2014;//為變更AuthNode資訊
    public static final String SUCCESS_AUTHNODE_IP_INFO_CHANGE = "Authnode clientIpInfo change.";
    public static final int CODE_2015 = 2015;
    public static final String ERROR_API_TTL_LIMIT = "Too many failed attempts. Please try again later.";
    public static final int CODE_2016 = 2016;
    public static final String ERROR_API_SIGNATURESEND = "SignatureSend verify error.";
    public static final int CODE_2026 = 2026;
    public static final String ERROR_API_ACCOUNT = "Account is empty.";
    public static final int CODE_2028 = 2028;
    public static final String TRANSACTION_ALREADY_EXISTS = "Transaction already exists.";
    public static final int CODE_2029 = 2029;//token 失效
    public static final String TOKEY_VERIFY_ERROR = "ToKey verify error.";
    public static final int CODE_2030 = 2030;//当前授权人与上一次授权人一致
    public static final String WALLET_ADDRESS_SAME = "Representative wallet address same.";
    public static final int CODE_2033 = 2033;//授权人地址错误
    public static final String WALLET_ADDRESS_ERROR = "Representative wallet address invalid error.";
    public static final int CODE_2034 = 2034;
    public static final String BLOCK_SERVICE_NOT_SUPPORT = "Block service not support.";
    public static final int CODE_2035 = 2035;
    public static final String TCP_NOT_CONNECT = "TCP not connect";
    public static final int CODE_2036 = 2036;
    public static final String DESTINATION_WALLET_ADDRESS = "Destination_wallet address same.";
    public static final int CODE_2097 = 2097;
    public static final String THE_BALANCE_DATA_IS_SYNCHRONIZING = "The balance data is synchronizing.";
    public static final String API_SERVER_NOT_RESPONSE = "Api server not response.";

    // Database 3xxx
    public static final int CODE_3001 = 3001;
    public static final String ERROR_REDIS_CONNECTION_POOL = "Redis connection pool error.";
    public static final int CODE_3003 = 3003;
    public static final String ERROR_REDIS_BLOCKSERVICE_AUTHNODE_MAPPING_LIST_NOT_FOUND = "Redis BlockService authnode mapping list not found.";
    public static final int CODE_3004 = 3004;
    public static final String ERROR_REDIS_BLOCKSERVICE_FULLNODE_MAPPING_LIST_NOT_FOUND = "Redis BlockService fullnode mapping list not found.";
    public static final int CODE_3005 = 3005;
    public static final String ERROR_REDIS_RUNTIME_EXCEPTION = "Redis runtime exception.";
    public static final int CODE_3006 = 3006;
    public static final String ERROR_REDIS_DATA_NOT_FOUND = "Redis data not found.";
    public static final int CODE_3007 = 3007;
    public static final String ERROR_REDIS_DATA_NOT_MAPPING = "Redis data not mapping.";
    public static final int CODE_3008 = 3008;
    public static final String ERROR_REDIS_ACCESS_TOKEN_AUTH_FAIL = "Redis access token auth fail.";

    //API 返回
    public static final int CODE_2025 = 2025;//PublicUnit no data.


    // Get LatestBlock And Balance
    public static final String SUCCESS_GET_LATEST_BLOCK_AND_BALANCE = "Get LatestBlock And Balance Success.";
    public static final String FAILURE_GET_LATEST_BLOCK_AND_BALANCE = "Get LatestBlock And Balance Failure.";

    // Get SendBlock
    public static final String SUCCESS_GET_WALLET_RECEIVE_BLOCK = "Get  Wallet Waiting To Receive Block Success.";
    public static final String FAILURE_GET_WALLET_RECEIVE_BLOCK = "Get Wallet Waiting To Receive Block Failure.";

    // Get Balance
    public static final String SUCCESS_GET_WALLET_GET_BALANCE = "Get  Wallet Balance Success.";
    public static final String FAILURE_GET_WALLET_GET_BALANCE = "Get Wallet Balance Failure.";

    //request param jude print out
    public static final String PREVIOUS_IS_NULL = "previous is null.";//previous
    public static final String AMOUNT_IS_NULL = "amount is null.";
    public static final String DESTINATION_WALLET_IS_NULL = "destinationWallet is null.";
    public static final String RESPONSE_IS_NULL = "response is null.";
    public static final String METHOD_NAME_IS_NULL = "method name is null.";
    public static final String METHOD_NAME_ERROR = "methodName error.";
    public static final String NO_BLOCK_SERVICE = "no block service";
    public static final String VERIFY_SUCCESS = "Verify success;";
    public static final String VERIFY_FAILURE = "Verify failure;";
    public static final String BALANCE = "balance ：";
    public static final String SEND_TRANSACTION_SATE = "Send Transaction:";
    public static final String HTTP_SEND_SUCCESS = "send http success,wait tcp...";
    public static final String NULL_WALLET = "http getLatestBlockAndBalance_SC wallet is null";
    public static final String SEND_HTTP_FAILED = "send http failed";
    public static final String LOGOUT_SUCCESSFULLY = "logout successfully";
    public static final int ADDRESS_LIMIT = 100;
    public static final String CHECK_UPDATE_FAILED = "check update failed:";
    public static final String CHECK_UPDATE_SUCCESS = "check update successfully";
    public static final String GOOGLE_PLAY_MARKET = "market://details?id=";
    public static final String CREATE_DB = "createDB";
    public static final String INSERT_KEY_STORE = "step 3:insertKeyStore";
    public static final String UPDATE_KEY_STORE = "step 3:updateKeyStore";
    public static final String HAD_WRITE_PERMISSION = "我已经获取读写权限了";
    public static final String WRITE_PERMISSION_REFUSED = "我被拒绝获取读写权限了";
    public static final String GET_WALLET_WAITING_TO_RECEIVE_BLOCK = "getWalletWaitingToReceiveBlock";
    public static final String GET_BALANCE = "getBalance:";
    public static final String GET_LATEST_BLOCK_AND_BALANCE = "getLatestBlockAndBalance:";
    public static final String WALLET_INFO = "WalletBean by parse keystore :";
    public static final Object WALLET_CREATE_EXCEPTION = "Use PrivateKey WIFStr Create Exception ";
    public static final String GET_TCP_DATA_EXCEPTION = "获取TCP数据返回code!=200的异常:";
    public static final String TO_LOGIN = "to login";
    public static final String BIND_TCP_SERVICE = "bind tcp service";
    public static final String START_TCP_SERVICE_BY_ALREADY_CONNECTED = "start tcp service by already connected";
    public static final String SERVICE_DISCONNECTED = "onServiceDisconnected";
    public static final String ALL_SERVER_INFO = "all server info:";
    public static final String CONNECT_TIME_OUT = " 连接超时，切換服務器.....";
    public static final String CONNECT_EXCEPTION = "connect exception,need switch server...";
    public static final String NEW_SFN_SERVER = "Got a new SFN server url:";
    public static final String WALLET_DATA_FAILURE = "wallet data httpExceptionStatus";
    public static final String NO_TRANSACTION_RECORD = "noAccountDoneTC";
    public static final String GET_ACCOUNT_DONE_TC_SUCCESS = "Get Account Transaction Info Success.";
    public static final String NEXT_PAGE_IS_EMPTY = "NextPageIsEmpty";
    public static final String UPDATE_CLIENT_IP_INFO = "Authnode clientIpInfo change:";
    public static final String LOADING_MORE = "loading more";
    public static final String SCREEN_WIDTH = "screen width:";
    public static final String SCREEN_HEIGHT = "screen height:";
    public static final String DESTROY = "destroy:";
    public static final String NO_ENOUGH_BALANCE = "-1";
    public static final String AMOUNT_EXCEPTION_CODE = "-1";
    public static final String AMOUNT_EXCEPTION = "amount exception";
    public static final String RESET_SAN_SUCCESS = "reset san success；";
    public static final String VERIFY_SUCCESS_AND_RESET_SAN = "verify success and reset san ；";
    public static final String RESET_SAN_FAILURE = "reset san failure；";
    public static final String ON_BACK_PRESSED = "onBackPressed";
    public static final String DEVICE_INFO = "Devices info:";
    public static final String TV_DEVICE = "TV DEVICE";
    public static final String NON_TV_DEVICE = "NON TV DEVICE";
    public static final String CPU_INFO = "CPU info:";
    public static final String HTTP_EXCEPTION_STATUS = "httpExceptionStatus";
    public static final String GET_PREVIOUS_MODIFY_REPRESENTATIVE = "getPreviousModifyRepresentative";
    public static final String NOT_NEED_UPDATE = "not need update version info";
    public static final String NEED_UPDATE = "need update version info";
    public static final String DEFAULT_PASSWORD = "aaaaaaa1";
    public static final String UNBIND_SERVICE = "UNBIND_SERVICE";
    public static final String SOCKET_HAD_CONNECTED_START_TO_RECEIVE = "[TCP] socket had connected start to receive tcp info+++++";
    public static final String FINISH_DOWNLOAD = "finish download...";
    public static final String DOWNLOAD_ID = "download id:";
    public static final String START_DOWNLOAD_ANDROID_APK = "Start Download Android APK:";
    public static final String INSTALL_ANDROID_APK = "Install Android APK";
    public static final String DOWNLOAD_FINISH_RECEIVER = "Download Finish Receiver";
    public static final String APK_PATH_IS_NULL = "APKPath is null";
    public static final String CHECK_SIM_STATUS_IS_TV = "checkSIMStatusIsTv";
    public static final String MANUFACTURER = "manufacturer:";
    public static final String BRAND = "brand:";
    public static final String BOARD = "board:";
    public static final String DEVICE = "device:";
    public static final String MODEL = "model:";
    public static final String DISPLAY = "display:";
    public static final String PRODUCT = "product:";
    public static final String fingerprint = "fingerPrint:";
    public static final String startAppSYNCDownload = "start download app inside app";
    public static final String getMyIpInfo = "getMyIpInfo:";
    public static final String getAccountDoneTCFailure = "getAccountDoneTCFailure:";


    /*socket 连接会用到的log以及字段*/
    public class socket {
        public static final String TAG = "[TCP] +++++++++++";
        public static final String KILL = "[TCP] socket closeSocket...";
        public static final String EXCEPTION = "[TCP] socket close Exception...";
        public static final String CLOSE_SOCKET = "[TCP] closeSocket:";
        public static final String SEND_DATA = "[TCP] 发送socket数据：";
        public static final String CONNECT_EXCEPTION = "[TCP] receive connect exception:";
        public static final String TCP_RESPONSE = "[TCP] step 1: tcp 返回数据: ";
        public static final String TCP_TRANSACTION_SUCCESS = "[TCP] transaction success .";
        public static final String TCP_TRANSACTION_FAILURE = "[TCP] transaction httpExceptionStatus .";
        public static final String RESET_AN = "[TCP]  初始化socket失败，重新请求「sfn」resetAN:";
        public static final int RESET_MAX_COUNT = 5;
        //获取最新的余额和区块
        public static final String GET_LATEST_BLOCK_AND_BALANCE_SC = "getLatestBlockAndBalance_SC";
        //交易发送结果
        public static final String GET_SEND_TRANSACTION_DATA_SC = "getSendTransactionData_SC";
        // 签章区块结果
        public static final String GET_RECEIVE_TRANSACTION_DATA_SC = "getReceiveTransactionData_SC";
        // 拿去未签章区块的数据
        public static final String GET_WALLET_WAITING_TO_RECEIVE_BLOCK_SC = "getWalletWaitingToReceiveBlock_SC";
        //獲取最新委託人區塊
        public static final String GET_LATEST_CHANGE_BLOCK_SC = "getLatestChangeBlock_SC";
        //更改委託人區塊
        public static final String GET_CHANGE_TRANSACTION_DATA_SC = "getChangeTransactionData_SC";
        //关闭socket
        public static final String CLOSE_SOCKET_SC = "closeSocket_SC";
        //成功连接到TCP
        public static final String CONNECTION_SUCCESS_SC = "connectionSuccess_SC";
        //TCP连接的心跳 S_C
        public static final String HEARTBEAT_SC = "heartbeat_SC";
        //获取余额
        public static final String GET_BALANCE_SC = "getBalance_SC";
        public static final String STOP_SOCKET_TO_LOGIN = "[TCP] stop socket to re-login";
        public static final String CODE_EXCEPTION = "[TCP] 返回数据CODE不是200，异常信息：";
        public static final String CLIENT_INFO_NULL = "[TCP] Client info must not null";
        public static final String SIGNATURE = "[TCP] Signature";

        public static final String SIGNATURE_FAILED = "[TCP] Signature Failed:";
        public static final String CURRENT_RECEIVE_QUEUE_SIZE = "[TCP] current need receive queue size:";

        // Get Balance
        public static final String FAILURE_GET_WALLET_GET_BALANCE = "[TCP] Get Wallet Balance Failure.";
        public static final String BALANCE_AFTER_SEND = "[TCP] Balance after 「Send」:";
        public static final String CALCULATE_AFTER_RECEIVE_BALANCE = "[TCP] calculateAfterReceiveBalance balance:";
        public static final String OVER_FIVE_TIME_TO_RESET = "[TCP] more than five time to reset";
        public static final String BUILD_SOCKET = "[TCP] BUILD SOCKET:";
        public static final String HEART_BEAT_CS = "heartbeat_CS";
        public static final String CLOSE_TCP_RECEIVE_THREAD = "[TCP] closeTCPReceiveThread";
        public static final String COUNT_DOWN_OVER = "[TCP] 倒数计时到，没有收到SAN连接成功信息";
        public static final String CONNECT_SUCCESS = "[TCP] 与SAN成功建立连接，关闭等待倒计时；";
        public static final String WALLET_EXTERNAL_IP = "[TCP]  当前Wallet的外网IP是:";
        public static final String CAN_RESET = "[TCP] 当前是否可以ResetSAN:";
        public static final String DATA_ACQUISITION_ERROR = "[TCP] data acquisition error ";

        public static final String CLEAR_QUEUE_AND_RECEIVE = "[TCP_RECEIVE] clearQueueAndReceive";
        public static final String TCP_NOT_CONNECT = "2035";
        public static final String CHARSET_NAME = "UTF-8";
    }


    public static final String KEYSTORE_IS_NULL = "keystore is null";
    public static final String CHARSET_FORMAT = "UTF-8";   //字节码格式
    public static final String HTTP_CONTENT_ENCODING = "Content-Encoding";
    public static final String INTENT_GOOGLE_PLAY = "intentToGooglePlay:";
    public static final String CHECK_WRITE_STORAGE_PERMISSION = "check permission";
    public static final String NOT_FOUND = "not found";
    public static final String BCAAS_WALLET = "BcaasWallet";
    public static final String GET_BALANCE_DATA_ERROR = "[GetBalance] Account data exception";
    public static final String GET_WALLET_WAITING_TO_RECEIVE_BLOCK_DATA_ERROR = "[getWalletWaitingToReceiveBlock] Account data exception";
    public static final String DATA_ERROR = "Account data exception";
    public static final String Empty = "";
    public static final String REQUEST_JSON = LogInfo.RESET_TAG + "requestJson:";


    /*日志信息*/
    public class LogInfo {
        public static final String LOGOUT_TAG = "[LogOut] \r";
        public static final String SERVICE_TAG = "[Service] \r";
        public static final String VERIFY_TAG = "[Verify] \r";
        public static final String RESET_TAG = "[Reset]";

    }

}
