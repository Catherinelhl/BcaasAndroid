package io.bcaas.constants;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 存放当前的key/log信息
 */
public class Constants {

    // MongoDB
    public static final String MONGODB_KEY_ID = "_id";
    public static final String MONGODB_KEY_PREVIOUS = "previous";
    public static final String MONGODB_KEY_BLOCKSERVICE = "blockService";
    public static final String MONGODB_KEY_BLOCKTYPE = "blockType";
    public static final String MONGODB_KEY_BLOCKTXTYPE = "blockTxType";
    public static final String MONGODB_KEY_DESTINATION_WALLET = "destination_wallet";
    public static final String MONGODB_KEY_SOURCETXHASH = "sourceTxhash";
    public static final String MONGODB_KEY_BALANCE = "balance";
    public static final String MONGODB_KEY_AMOUNT = "amount";
    public static final String MONGODB_KEY_REPRESENTATIVE = "representative";
    public static final String MONGODB_KEY_WALLET = "wallet";
    public static final String MONGODB_KEY_WORK = "work";
    public static final String MONGODB_KEY_DATE = "date";
    public static final String MONGODB_KEY_SIGNATURE = "signature";
    public static final String MONGODB_KEY_PUBLICKEY = "publicKey";
    public static final String MONGODB_KEY_PRODUCEKEYTYPE = "produceKeyType";
    public static final String MONGODB_KEY_HEIGHT = "height";
    public static final String MONGODB_KEY_SYSTEMTIME = "systemTime";
    public static final String MONGODB_KEY_SIGNATURESEND = "signatureSend";
    public static final String MONGODB_KEY_CURRENCYUNIT = "currencyUnit";
    public static final String MONGODB_KEY_CIRCULATION = "circulation";
    public static final String MONGODB_KEY_COINBASE = "coinBase";
    public static final String MONGODB_KEY_GENEISISBLOCKACCOUNT = "genesisBlockAccount";
    public static final String MONGODB_KEY_COINBASEACCOUNT = "coinBaseAccount";
    public static final String MONGODB_KEY_CREATETIME = "createTime";
    public static final String MONGODB_KEY_PUBLICUNIT = "publicUnit";

    // Gson Key
    public static final String GSON_KEY_VERSIONVO = "versionVO";
    public static final String GSON_KEY_VERSIONVOLIST = "versionVOList";
    public static final String GSON_KEY_CLIENTIPINFOVO = "clientIpInfoVO";
    public static final String GSON_KEY_CLIENTIPINFOVOLIST = "clientIpInfoVOList";
    public static final String GSON_KEY_DATABASEVOLIST = "databaseVOList";
    public static final String GSON_KEY_PAGINATIONVO = "paginationVO";
    public static final String GSON_KEY_PAGINATIONVOLIST = "paginationVOList";
    public static final String GSON_KEY_WALLETVO = "walletVO";
    public static final String GSON_KEY_DATABASEVO = "databaseVO";
    public static final String GSON_KEY_GENESISVO = "genesisVO";
    public static final String GSON_KEY_TRANSACTIONCHAINVO = "transactionChainVO";
    public static final String GSON_KEY_ACCESSTOKEN = "accessToken";
    public static final String GSON_KEY_WALLETADDRESS = "walletAddress";
    public static final String GSON_KEY_TC = "tc";
    public static final String GSON_KEY_TRANSACTIONCHAINRECEIVEVO = "TransactionChainReceiveVO";
    public static final String GSON_KEY_TRANSACTIONCHAINSENDVO = "TransactionChainSendVO";
    public static final String GSON_KEY_TRANSACTIONCHAINOPENVO = "TransactionChainOpenVO";
    public static final String GSON_KEY_TRANSACTIONCHAINCHANGEVO = "TransactionChainChangeVO";
    public static final String TRANSACTIONCHAINRECEIVEVO = "TransactionChainReceiveVO";
    public static final String TRANSACTIONCHAINSENDVO = "TransactionChainSendVO";
    public static final String TRANSACTIONCHAINOPENVO = "TransactionChainOpenVO";
    public static final String TRANSACTIONCHAINCHANGEVO = "TransactionChainChangeVO";
    public static final String GSON_KEY_SUCCESS = "success";
    public static final String GSON_KEY_CODE = "code";
    public static final String GSON_KEY_MESSAGE = "message";
    public static final String GSON_KEY_METHODNAME = "methodName";

    // 區塊類型
    public static final String BLOCK_TYPE_OPEN = "Open";
    public static final String BLOCK_TYPE_SEND = "Send";
    public static final String BLOCK_TYPE_RECEIVE = "Receive";
    public static final String BLOCK_TYPE_CHANGE = "Change";
    public static final String BLOCK_TYPE = "\"blockType\":\"";
    public static final String BLOCK_TYPE_QUOTATION = "\"";
    public static final String GSON_KEY_OBJECTLIST = "objectList";
    public static final String GSON_KEY_NEXTOBJECTID = "nextObjectId";

    public static final String GSON_KEY_ID = "_id";
    public static final String GSON_KEY_AUTH_KEY = "authKey";
    public static final String GSON_KEY_VERSION = "version";
    public static final String GSON_KEY_UPDATE_URL = "updateUrl";
    public static final String GSON_KEY_FORCE_UPGRADE = "forceUpgrade";
    public static final String GSON_KEY_TYPE = "type";
    public static final String GSON_KEY_MOTIFY_TIME = "motifyTime";
    public static final String GSON_KEY_SYSTEM_TIME = "systemTime";
    public static final String CHANGE_LINE = " \n";
    public static final String CHANGE_OPEN = "changeOpen";//change的open区块
    public static final String CHANGE = "change";//change区块
    public static final String ENCODE_INGORE_CASE = "identity";//http設置encode忽略

    public static final String SPLICE_CONVERTER(String ip, int port) {
        return "http://" + ip + ":" + port;
    }

    public static String GSON_KEY_MAC_ADDRESS_EXTERNAL_IP = "macAddressExternalIp";
    public static String GSON_KEY_EXTERNAL_IP = "externalIp";
    public static String GSON_KEY_INTERNAL_IP = "internalIp";
    public static String GSON_KEY_CLIENT_TYPE = "clientType";
    public static String GSON_KEY_EXTERNAL_PORT = "externalPort";
    public static String GSON_KEY_INTERNAL_PORT = "internalPort";
    public static String GSON_KEY_VIRTUAL_COIN = "virtualCoin";
    public static String GSON_KEY_RPC_PORT = "rpcPort";

    public static String GSON_KEY_OBJECT_LIST = "objectList";
    public static String GSON_KEY_NEXT_OBJECT_ID = "nextObjectId";
    public static String WIF_PRIVATE_KEY = "WIFPrivateKey";
    public static int PWD_LENGTH = 8;// 输入密码的长度
    public static String SP_NAME = "bcaas";

    // 当前数据库的信息
    public static class DB {
        public static String DB_NAME = "Bcaas";// 当前数据库的名字
        public static String BCAAS_SECRET_KEY = "BcaasSecretKey";//存储当前钱包信息的表
        public static String BCAAS_ADDRESS = "BcaasAddress";//存储地址管理的表
        public static String UID = "uid";
        public static String KEYSTORE = "keyStore";
        public static String CREATETIME = "createTime";
        public static String ADDRESS_NAME = "addressName";
        public static String ADDRESS = "address";


    }

    public static class ValueMaps {
        public static final int brandSleepTime = 2000;//应用启动页睡眠时间
        public static final int sleepTime500 = 500;
        public static final int sleepTime1500 = 1500;
        public static final int sleepTime1000 = 1000;
        public static final int sleepTime800 = 800;
        public static final int sleepTime2000 = 2000;
        public static final int sleepTime3000 = 3000;
        public static final int sleepTime4000 = 4000;
        public static final int sleepTime5000 = 5000;
        public static final int sleepTime30000 = 30000;

        public static final String PRODUCE_KEY_TYPE = "ECC";

        // 區塊類型
        public static final String BLOCK_TYPE_OPEN = "Open";
        public static final String BLOCK_TYPE_SEND = "Send";
        public static final String BLOCK_TYPE_RECEIVE = "Receive";
        public static final String BLOCK_TYPE_CHANGE = "Change";
        public static final String BLOCK_TX_TYPE = "Matrix";
        public static final String DEFAULT_REPRESENTATIVE = "0000000000000000000000000000000000000000000000000000000000000000";//64個零


        public static String STATUS_DEFAULT = "default";
        public static String STATUS_SEND = "Send";

        public static final int TIME_OUT_TIME = 30000;

        public static final String FROM_BRAND = "brand";
        public static final String FROM_LOGIN = "login";
        // TODO: 2018/8/24 记得修改时间
        public static final long REQUEST_RECEIVE_TIME = 30 * 1000;//间隔五分钟去请求新的数据块，暂时写得5s
        public static final String CN = "CN";
        public static final String TW = "TW";
        public static final String EN = "EN";
        public static final String HK = "HK";
    }

    public enum SettingType {//定义一下设置的类型
        CHECK_WALLET_INFO,
        MODIFY_PASSWORD,
        MODIFY_AUTH,
        ADDRESS_MANAGE,
        LANGUAGE_SWITCHING//语言切换
    }

    public static class KeyMaps {
        public static final int CAMERA_OK = 0x001;
        public static String TAG = "io.bcaas";
        public static final String CURRENCY = "currency";//币种
        public static final String ALL_CURRENCY = "allCurrency";//所有币种
        public static final String DESTINATION_WALLET = "destinationWallet";//接收方的账户地址
        public static final String ADDRESS_NAME = "addressName";//接收方的账户地址
        public static final String RECEIVE_CURRENCY = "receiveCurrency";//接收方到币种
        public static final String TRANSACTION_AMOUNT = "transactionAmount";//交易数额

        public static final String WALLET_ADDRESS = "walletAddress";
        public static final String PRIVATE_KEY = "privateKey";
        public static final String BLOCK_SERVICE = "blockService";
        public static String From = "from";//来自
        public static String COPY_ADDRESS = "address";
        public static String CN = "中文（简体）";
        public static String TW = "中文（繁體）";
        public static String EN = "英文";

    }

    // Wallet API
    public static class RequestUrl {

        public static final String login = "/wallet/login";//登入SFN
        public static final String logout = "/wallet/logout";//登出SFN
        public static final String resetAuthNodeInfo = "/wallet/resetAuthNodeInfo";//当钱包与AuthNode无法通过时调用，取得新的AnthNode Ip资讯
        public static final String verify = "/wallet/verify";//验证AccessToken是否可以使用
        public static final String send = "/transactionChain/send";//TC Send
        public static final String receive = "/transactionChain/receive";//TC Receive
        public static final String getWalletWaitingToReceiveBlock = "/wallet/getWalletWaitingToReceiveBlock";//取得未簽章R區塊的Send區塊 &取最新的R區塊 &wallet餘額
        public static final String getLatestBlockAndBalance = "/wallet/getLatestBlockAndBalance";//获取最新的区块和Wallet余额 AN
        public static final String getLatestChangeBlock = "/wallet/getLatestChangeBlock";//获取最新的更換委託人區塊 AN
        public static final String change = "/transactionChain/change";//TC change AN
        public static final String getBlockServiceList = "/publicUnit/getList";//取得幣種清單 sitapp.bcaas.io

    }

    public class BlockService {
        public static final String BCC = "BCC";
        public static final String TCC = "TCC";
        public static final String CLOSE = "0";//isStartUp 关闭
        public static final String OPEN = "1";//isStartUp 开放
    }

    public class Preference {
        public static final String PUBLIC_KEY = "publicKey";//公钥
        public static final String PRIVATE_KEY = "privateKey";//私钥
        public static final String CLIENT_IP_INFO = "clientIpInfo";//与之交互的AN的信息
        public static final String WALLET_INFO = "walletInfo";//钱包信息
        public static final String PASSWORD = "password";//密码
        public static final String ACCESS_TOKEN = "accessToken";//token 信息
        public static final String BLOCK_SERVICE = "blockService";//区块服务信息
        public static final String BLOCK_SERVICE_LIST = "blockServiceList";//所有的可顯示的幣種
        public static final String WALLET_BALANCE = "walletBalance";//钱包余额
        public static final String LANGUAGE_TYPE = "languageType";//當前的語言環境

    }

    public static final String RESULT = "result";//扫描二维码返回的结果
    public static final int RESULT_CODE = 1;//发送二维码扫描结果的code
    public static final int UPDATE_WALLET_BALANCE = 2;//更新余额
    public static final int SWITCH_TAB = 3;//切换TAB

    //正则
    public static class REGEX {
        public static String IS_CHARACTER = "^[0-9a-zA-Z]*";
        public static String REPLACE_BLANK = "\t|\r|\n|\\s*";
        public static String IS_EIGHT_BITS = "^[0-9a-zA-Z]{8}$";
        ;
    }
}
