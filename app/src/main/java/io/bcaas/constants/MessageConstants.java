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
    public static final int CODE_0 = 0;
    public static final int CODE_200 = 200; // Success
    public static final int CODE_400 = 400; // Failure
    static final int CODE_500 = 500; // Server error

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
    public static final int CODE_2035 = 2035;
    public static final String TCP_NOT_CONNECT = "TCP not connect";
    public static final int CODE_2036 = 2036;
    public static final String DESTINATION_WALLET_ADDRESS = "Destination_wallet address same.";
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

    // Transaction Chain
    public static final String SUCCESS_TRANSACTION_CHAIN_ADD = "Add transaction Success.";
    public static final String FAILURE_TRANSACTION_CHAIN_ADD = "Add transaction Failure.";

    // Genesis Block
    public static final String SUCCESS_GENESIS_BLOCK = "Genesis Block Add Success.";
    public static final String FAILURE__GENESIS_BLOCK = "Genesis Block Add Failure.";
    public static final String EXIST__GENESIS_BLOCK = "Genesis Block Is Exist.";

    public static final String FAILURE_TRANSACTION_CHAIN_GETLATESTONE = "Get latest one transaction Failure.";

    public static final String SUCCESS_TRANSACTION_CHAIN_GETNEWONE = "Get new one transaction Success.";
    public static final String FAILURE_TRANSACTION_CHAIN_GETNEWONE = "Get new one transaction Failure.";

    public static final String SUCCESS_TRANSACTION_CHAIN_GETPAGE = "Get page transaction Success.";
    public static final String FAILURE_TRANSACTION_CHAIN_GETPAGE = "Get page transaction Failure.";

    // NAT Information
    public static final String SUCCESS_NAT_INFO_ADD = "Add NAT information Success.";
    public static final String FAILURE_NAT_INFO_ADD = "Add NAT information Failure.";

    public static final String SUCCESS_NAT_INFO_GETSPECIFY = "Get specify NAT information Success.";
    public static final String FAILURE_NAT_INFO_GETSPECIFY = "Get specify NAT information Failure.";

    public static final String SUCCESS_NAT_INFO_GETPAGE = "Get page NAT information Success.";
    public static final String FAILURE_NAT_INFO_GETPAGE = "Get page NAT information Failure.";

    public static final String SUCCESS_NAT_INFO_GETRANDOM = "Get random NAT information Success.";
    public static final String FAILURE_NAT_INFO_GETRANDOM = "Get random NAT information Failure.";

    public static final String SUCCESS_LATEST_BLOCK_AND_BALANCE = "Get latest block and balance Success.";
    public static final String FAILURE_LATEST_BLOCK_AND_BALANCE = "Get latest block and balance Failure.";
    public static final String SUCCESS_LATEST_CHANGE_BLOCK = "Get latest change block Success.";
    public static final String FAILURE_LATEST_CHANGE_BLOCK = "Get latest change block Failure.";

    // Redis
    public static final String SUCCESS_REDIS_STATUS = "Redis status Success.";
    public static final String FAILURE_REDIS_STATUS = "Redis status Failure.";
    public static final String SUCCESS_REDIS_INIT_JEDIS_POOL = "Init jedisPool Success.";
    public static final String FAILURE_REDIS_INIT_JEDIS_POOL = "Init jedisPool Failure.";

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
    public static final String SUCCESS_GET_SENDBLOCK = "Get SendBlock Success.";
    public static final String FAILURE_GET_SENDBLOCK = "Get SendBlock Failure.";
    // Prefix defined
    public static final String LOG_PREFIX_TO_NEW_CONNECTION = "-- New socket connection : ";
    public static final String LOG_PREFIX_TO_SERVER_RECEIVE = "-- Server received : ";
    public static final String LOG_PREFIX_TO_SERVER_SEND_RESPONSE = "-- Server response send : ";
    public static final String LOG_PREFIX_TO_SERVER_SEND_REQUEST = "-- Server request send : ";
    public static final String LOG_PREFIX_TO_THREAD_START = "-- ClientWorkerRunnable start : ";
    public static final String LOG_PREFIX_TO_THREAD_STOP = "-- ClientWorkerRunnable stop : ";

    // Seednode socket service
    public static final String SUCCESS_GET_ALL_AUTHNODE_AND_FULLNODE_IP_INFO = "Service getAllAuthNodeAndFullNodeIpInfo Success.";
    public static final String FAILURE_GET_ALL_AUTHNODE_AND_FULLNODE_IP_INFO = "Service getAllAuthNodeAndFullNodeIpInfo Failure.";

    public static final String FAILURE_INIT_CLIENT_IP_INFO = "Service initClientIpInfo Failure. ";
    public static final String FAILURE_INIT_CLIENT_IP_INFO_DTAT = "Service initClientIpInfo data is exist.";
    public static final String UPDATE_CLIENT_IP_INFO_DTAT = "Service update clientIpInfo data.";

    public static final String SUCCESS_VERIFY_ACCESS_TOKEN = "Service verify Access Token Success.";
    public static final String FAILURE_VERIFY_ACCESS_TOKEN = "Service verify Access Token Failure.";
    public static final String SUCCESS_GET_ALL_SEEDAUTHNODE_IP_INFO = "Service getAllSeedAuthNodeIpInfo Success.";
    public static final String FAILURE_GET_ALL_SEEDAUTHNODE_IP_INFO = "Service getAllSeedAuthNodeIpInfo Failure.";
    public static final String SUCCESS_SYNCHRONIZED_SEEDAUTHNODE_IP_INFO = "Service synchronized seedAuthNode Success.";
    public static final String FAILURE_SYNCHRONIZED_SEEDAUTHNODE_IP_INFO = "Service synchronized seedAuthNode Failure.";
    public static final String SYNCHRONIZED_AUTHNODE_IP_INFO_START = "Synchronized authNode ip info start.";
    public static final String SYNCHRONIZED_AUTHNODE_IP_INFO = "Synchronized authNode ip info.";
    public static final String SYNCHRONIZED_AUTHNODE_IP_INFO_END = "Synchronized authNode ip info end.";
    // RPC
    public static final String SUCCESS_GET_ONE_AUTHNODE_INFO = "Get one authNode info Success.";
    public static final String FAILURE_GET_ONE_AUTHNODE_INFO = "Get one authNode info Failure.";
    public static final String SUCCESS_RESET_AUTHNODE_INFO = "Reset authNode mapping info Success.";
    public static final String FAILURE_RESET_AUTHNODE_INFO = "Reset authNode mapping info Failure.";
    public static final String SUCCESS_LOGIN = "Login Success.";
    public static final String FAILURE_LOGIN = "Login Failure.";
    public static final String SUCCESS_LOGOUT = "Logout Success.";
    public static final String FAILURE_LOGOUT = "Logout Failure.";
    public static final String SUCCESS_VERIFY = "Verify Success.";
    public static final String FAILURE_VERIFY = "Verify Failure.";

    public static final String FAILURE_GET_RANDON_ONE_FULLNODE_INFO = "Get randon one fullNode info Failure.";
    public static final String SUCCESS_GET_RANDON_ONE_FULLNODE_INFO = "Get randon one fullNode info Success.";

    // Block API
    public static final String FAILURE_GET_NODE_AND_BLOCKCOUNT = "Get Node And BlockCount Failure.";
    public static final String SUCCESS_GET_NODE_AND_BLOCKCOUNT = "Get Node And BlockCount Success.";
    public static final String FAILURE_GET_BALANCE = "Get Balance Failure.";
    public static final String SUCCESS_GET_BALANCE = "Get Balance Success.";

    // Get SendBlock
    public static final String SUCCESS_GET_WALLET_RECEIVE_BLOCK = "Get  Wallet Waiting To Receive Block Success.";
    public static final String FAILURE_GET_WALLET_RECEIVE_BLOCK = "Get Wallet Waiting To Receive Block Failure.";

    // Get Balance
    public static final String SUCCESS_GET_WALLET_GETBALANCE = "Get  Wallet Balance Success.";
    public static final String FAILURE_GET_WALLET_GETBALANCE = "Get Wallet Balance Failure.";

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
    public static final String PING = "----Ping-------:";
    public static final String START_R_HTTP = "getWalletWaitingToReceiveBlock";
    public static final String GET_BALANCE = "getBalance:";
    public static final String WALLET_INFO = "WalletBean by parse keystore :";
    public static final Object WALLET_CREATE_EXCEPTION = "Use PrivateKey WIFStr Create Exception ";
    public static final String GET_TCP_DATA_EXCEPTION = "获取TCP数据返回code!=200的异常:";
    public static final String STOP_TCP = "stop tcp";
    public static final String START_TCP = "start tcp";
    public static final String ON_RESET_AUTH_NODE_INFO = "onResetAuthNodeInfo:";
    public static final String VERIFY = "verify:";
    public static final String UNSUBSCRIBE = "unsubscribe";
    public static final String RESPONSE_TIME_OUT = "response time out...";
    public static final String TO_LOGIN = "to login";
    public static final String BIND_TCP_SERVICE = "bind tcp service";
    public static final String SERVICE_DISCONNECTED = "onServiceDisconnected";
    public static final String ALL_SERVER_INFO = "all server info:";
    public static final String CONNECT_TIME_OUT = " 连接超时，切換服務器.....";
    public static final String CONNECT_EXCEPTION = "connect exception,need switch server...";
    public static final String NEW_SFN_SERVER = "Got a new SFN server url:";
    public static final String WALLET_DATA_FAILURE = "wallet data httpExceptionStatus";
    public static final String GETLATESTCHANGEBLOCK_SUCCESS = " 獲取最新更換委託人區塊成功";
    public static final String DEFAULT_SFN_SERVER = "current default SFN server:";
    public static final String NO_TRANSACTION_RECORD = "noAccountDoneTC";
    public static final String GET_ACCOUNT_DONE_TC_SUCCESS = "Get Account Transaction Info Success.";
    public static final String NEXT_PAGE_IS_EMPTY = "NextPageIsEmpty";
    public static final String UPDATE_CLIENT_IP_INFO = "Authnode clientIpInfo change.";
    public static final String NEW_CLIENT_IP_INFO = "new client info is:";
    public static final String LOADING_MORE = "loading more";
    public static final String ISREAL_NET = "is real net:";
    public static final String REMOVE_RESETSAN_RUNNABLE = "removeResetSANRunnable";
    public static final String REMOVE_VERIFY_RUNNABLE = "removeVerifyRunnable";
    public static final String REMOVE_GET_WALLET_R_BLOCK = "remove GetWalletWaitingToReceiveBlockRunnable";
    public static final String REMOVE_GET_BALANCE = "remove removeGetBalanceRunnable";
    public static final String SCREEN_WIDTH = "screen width:";
    public static final String SCREEN_HEIGHT = "screen height:";
    public static final String DESTROY = "destroy:";
    public static final String NO_ENOUGH_BALANCE = "-1";
    public static final String AMOUNT_EXCEPTION_CODE = "-1";
    public static final String AMOUNT_EXCEPTION = "amount exception";
    public static final String RESET_SAN_SUCCESS = "reset san success";
    public static final String RESET_SAN_FAILURE = "reset san failure";
    public static final String ONBACKPRESSED = "onBackPressed";
    public static final String DEVICE_INFO = "Deices info:";
    public static final String TV_DEVICE = "TV DEVICE";
    public static final String NON_TV_DEVICE = "NON TV DEVICE";
    public static final String LANGUAGE_SWITCH = "language switch";
    public static final String CPU_INFO = "CPU info:";
    public static final String HTTPEXCEPTIONSTATUS = "httpExceptionStatus";
    public static final String TCP_STATUS = "【TCP】TCP status：";
    public static final String GET_PREVIOUS_MODIFY_REPRESENTATIVE = "getPreviousModifyRepresentative";
    public static final String ONRESUME = "onResume:";
    public static final String NOT_NEED_UPDATE = "not need update version info";
    public static final String NEED_UPDATE = "need update version info";
    public static final String RESET_SERVER_DATA = "重置后的数据：";
    public static final String DEFAULT_PASSWORD = "aaaaaaa1";
    public static String CHECKSIMSTATUSISTV = "checkSIMStatusIsTv";
    public static String CHECKSCREENISTV = "checkScreenIsTv";
    public static String CHECKLAYOUTISTV = "checkLayoutIsTv";
    public static String MANUFACTURER = "manufacturer:";
    public static String BRAND = "brand:";
    public static String BOARD = "board:";
    public static String DEVICE = "device:";
    public static String MODEL = "model:";
    public static String DISPLAY = "display:";
    public static String PRODUCT = "product:";
    public static String fingerprint = "fingerPrint:";


    public class socket {
        public static final String TAG = "[TCP] +++++++++++";
        public static final String KILL = "[TCP] socket kill...";
        public static final String EXCEPTION = "[TCP] socket close Exception...";
        public static final String CLOSE = "[TCP] socket closed..";
        public static final String SEND_DATA = "[TCP] 发送socket数据：";
        public static final String CONNECT_EXCEPTION = "[TCP] receive connect exception:";
        public static final String TCP_RESPONSE = "[TCP] step 1: tcp 返回数据: ";
        public static final String TCP_TRANSACTION_SUCCESS = "[TCP] transaction success .";
        public static final String TCP_TRANSACTION_FAILURE = "[TCP] transaction httpExceptionStatus .";
        public static final String RESET_AN = "[TCP]  初始化socket失败，请求「sfn」resetAN:";
        public static final int HEART_BEAT = 0xFF;
        public static final int RESET_MAX_COUNT = 5;
        public static final int RESET_AN_INFO = 5;
        public static final String GETLATESTBLOCKANDBALANCE_SC = "getLatestBlockAndBalance_SC";
        public static final String GETSENDTRANSACTIONDATA_SC = "getSendTransactionData_SC";
        public static final String GETRECEIVETRANSACTIONDATA_SC = "getReceiveTransactionData_SC";
        public static final String GETWALLETWAITINGTORECEIVEBLOCK_SC = "getWalletWaitingToReceiveBlock_SC";
        public static final String GETLATESTCHANGEBLOCK_SC = "getLatestChangeBlock_SC";//獲取最新委託人區塊
        public static final String GETCHANGETRANSACTIONDATA_SC = "getChangeTransactionData_SC";//更改委託人區塊
        public static final String CLOSESOCKET_SC = "closeSocket_SC";//关闭socket
        public static final String GETBALANCE_SC = "getBalance_SC";//获取余额
        public static final String RESET_SAN = "[TCP] reset SAN";//需要重新reset数据
        public static final String STOP_SOCKET_TO_LOGIN = "[TCP] stop socket to re-login";
        public static final String CODE_EXCEPTION = "[TCP] 返回数据CODE不是200，异常信息：";
        public static final String CLIENT_INFO_NULL = "[TCP] Client info must not null";
        public static final String CONNECT_EXTERNAL_IP = "[TCP] connectExternalIP";
        public static final String CONNECT_INTERNAL_IP = "[TCP] connectInternalIP";
        public static final int RESET_LOOP = 4;
        public static final String SIGNATUREING = "[TCP] Signatureing";

        public static final String SIGNATURE_FAILED = "[TCP] Signature Failed:";
        public static final String CURRENT_RECEIVEQUEUE_SIZE = "[TCP] current need receive queue size:";

        // Get Balance
        public static final String SUCCESS_GET_WALLET_GETBALANCE = "[TCP] Get  Wallet Balance Success.";
        public static final String FAILURE_GET_WALLET_GETBALANCE = "[TCP] Get Wallet Balance Failure.";
        public static final String TCP_START = "[TCP] start";
        public static final String BALANCE_AFTER_SEND = "[TCP] Balance after 「Send」:";
        public static final String CALCULATE_AFTER_RECEIVE_BALANCE = "[TCP] calculateAfterReceiveBalance balance:";
        public static final String OVER_FIVE_TIME_TO_RESET = "[TCP] more than five time to reset";
        public static final String BUILD_SOCKET = "[TCP] BUILD SOCKET";
    }


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
