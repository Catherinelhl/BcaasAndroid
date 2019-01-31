package io.bcaas.constants;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 定義Log, MongoDB Key常數
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
    public static final String MONGODB_KEY_RECEIVEAMOUNT = "receiveAmount";
    public static final String MONGODB_KEY_WALLET = "wallet";
    public static final String MONGODB_KEY_WORK = "work";
    public static final String MONGODB_KEY_DATE = "date";
    public static final String MONGODB_KEY_SIGNATURE = "signature";
    public static final String MONGODB_KEY_PUBLICKEY = "publicKey";
    public static final String MONGODB_KEY_PRODUCEKEYTYPE = "produceKeyType";
    public static final String MONGODB_KEY_TXHASH = "txHash";
    public static final String MONGODB_KEY_HEIGHT = "height";
    public static final String MONGODB_KEY_SYSTEMTIME = "systemTime";
    public static final String MONGODB_KEY_SIGNATURESEND = "signatureSend";
    public static final String MONGODB_KEY_CURRENCYUNIT = "currencyUnit";
    public static final String MONGODB_KEY_CIRCULATION = "circulation";
    public static final String MONGODB_KEY_COINBASE = "coinBase";
    public static final String MONGODB_KEY_GENEISISBLOCKACCOUNT = "genesisBlockAccount";
    public static final String MONGODB_KEY_COINBASEACCOUNT = "coinBaseAccount";
    public static final String MONGODB_KEY_INTERESTRATE = "interestRate";
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
    public static final String GSON_KEY_SUCCESS = "success";
    public static final String GSON_KEY_CODE = "code";
    public static final String GSON_KEY_MESSAGE = "message";
    public static final String GSON_KEY_METHODNAME = "methodName";
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



    //RequestCode
    public static final int REQUEST_CODE_CAMERA_OK = 0x001;
    public static final int REQUEST_CODE_EXTERNAL_STORAGE = 0x002;
    public static final int REQUEST_CODE_INSTALL = 0x003;
    public static final int REQUEST_CODE_SEND_CONFIRM_ACTIVITY = 0x004;// 跳转确认密码发送界面
    public static final int REQUEST_CODE_INSERT_ADDRESS_ACTIVITY = 0x005;// 跳转添加地址
    public static final int REQUEST_CODE_SEND_FILL_IN_ACTIVITY = 0x006;// 跳转发送填入信息界面
    public static final int REQUEST_CODE_IMPORT = 0x007;//跳轉至導入的code
    public static final int REQUEST_CODE_CREATE = 0x008; //跳轉至創建的code


    // 區塊類型
    public static final String BLOCK_TYPE_OPEN = "Open";
    public static final String BLOCK_TYPE_SEND = "Send";
    public static final String BLOCK_TYPE_RECEIVE = "Receive";
    public static final String BLOCK_TYPE_CHANGE = "Change";
    public static final String BLOCK_TYPE = "\"blockType\":\"";
    public static final String BLOCK_TYPE_QUOTATION = "\"";

    public static final String CHANGE_LINE = "\r\n";
    public static final String CHANGE_OPEN = "changeOpen";//change的open区块
    public static final String CHANGE = "change";//change区块
    public static final String ENCODE_INGORE_CASE = "identity";//http設置encode忽略
    public static final String LOCAL_DEFAULT_IP = "0.0.0.0";
    public static final String HTTP_PREFIX = "http://";
    public static final String HTTP_COLON = ":";
    public static final String PROGRESS_MAX = "/100";
    public static final String NOTIFICATION_CHANNEL_ID = "bcaas";
    public static final String NOTIFICATION_CHANNEL_NAME = "bcaasWallet";
    public static final String ACCOUNT_TRANSACTION = "accountTransaction";
    public static final String TRANSACTION_STR = "transactionStr";

    public class From {
        public static final String INIT_VIEW = "initView";//初始化界面调用
        public static final String SELECT_CURRENCY = "selectCurrency";//选择币种调用
        public static final String CHECK_BALANCE = "checkBalance";//查看余额调用
        public static final String SEND_FRAGMENT = "sendFragment";//发送界面
        public static final String CHECK_WALLET_INFO = "checkWalletInfo";//查看钱包信息
        public static final String SEND = "send";//send界面调用
    }

    public static final String SPLICE_CONVERTER(String ip, int port) {
        return HTTP_PREFIX + ip + HTTP_COLON + port;
    }

    public static final String GSON_KEY_MAC_ADDRESS_EXTERNAL_IP = "macAddressExternalIp";
    public static final String GSON_KEY_EXTERNAL_IP = "externalIp";
    public static final String GSON_KEY_INTERNAL_IP = "internalIp";
    public static final String GSON_KEY_CLIENT_TYPE = "clientType";
    public static final String GSON_KEY_EXTERNAL_PORT = "externalPort";
    public static final String GSON_KEY_INTERNAL_PORT = "internalPort";
    public static final String GSON_KEY_VIRTUAL_COIN = "virtualCoin";
    public static final String GSON_KEY_RPC_PORT = "rpcPort";

    public static final String GSON_KEY_OBJECT_LIST = "objectList";
    public static final String GSON_KEY_NEXT_OBJECT_ID = "nextObjectId";
    public static final String WIF_PRIVATE_KEY = "WIFPrivateKey";
    public static final int PASSWORD_MIN_LENGTH = 8;// 输入密码的最小长度
    public static final int PASSWORD_MAX_LENGTH = 16;// 输入密码的最大长度

    public  class ServerType {
        public static final String INTERNATIONAL_SIT = "internationalSIT";
        public static final String INTERNATIONAL_UAT = "internationalUAT";
        public static final String INTERNATIONAL_PRD = "internationalPRD";

    }

    public  class ServerTypeName {
        public static final String INTERNATIONAL_SIT = "国际 SIT";
        public static final String INTERNATIONAL_UAT = "国际 UAT";
        public static final String INTERNATIONAL_PRD = "国际 PRD";

    }

    public  class Time {
        public static final int sleep500 = 500;
        public static final int sleep400 = 400;
        public static final int sleep200 = 200;
        public static final int sleep100 = 100;
        public static final int sleep1000 = 1000;
        public static final int sleep10000 = 10000;
        public static final int sleep800 = 800;
        public static final int sleep2000 = 2000;
        public static final int INTERNET_TIME_OUT = 5 * 60 * 1000;//内网连接时间，ms，超时5s之后
        public static final int EXTERNAL_TIME_OUT = 10 * 60 * 1000;//外网连接超时时间，超过10s之后
        public static final int STAY_AUTH_ACTIVITY = 3;//如果当前不用编辑页面，停留在页面的时间3s
        public static final int STAY_BRAND_ACTIVITY = 2;//如果当前不用编辑页面，停留在页面的时间2s

        public static final int LONG_TIME_OUT = 30;//设置超时时间
        public static final long COUNT_DOWN_TIME = 10;//倒数计时
        public static final long COUNT_DOWN_RECEIVE_BLOCK = 1; //接收「receive」TCP响应结果倒计时
        public static final long COUNT_DOWN_NOTIFICATION = 2;// 通知倒计时
        public static final long COUNT_DOWN_REPRESENTATIVES = 1;//停留当前在「change」授权页面的时间
        public static final long COUNT_DOWN_GUIDE_TV = 200;//TV版Guide頁面需要的倒計時
        public static final long GET_RECEIVE_BLOCK = 10;//获取未签章区块间隔=
        public static final long GET_BALANCE = 10;//获取餘額
        public static final long HEART_BEAT = 30;//TCP  C-S 发送心跳信息间隔
        public static final long PRINT_LOG = 1; //打印当前设备的内存
        public static final int TOAST_LONG = 3;
        public static final int TOAST_SHORT = 0;
    }

    public static class ValueMaps {
        public static final String PRODUCE_KEY_TYPE = "ECC";
        public static final String BLOCK_TX_TYPE = "Matrix";
        public static final String DEFAULT_REPRESENTATIVE = "0000000000000000000000000000000000000000000000000000000000000000";//64個零
        public static final String DEFAULT_PRIVATE_KEY = "****************************************************";
        public static final String THREE_STAR = "***";
        public static final int ALIAS_LENGTH = 20;
        public static final String AUTH_KEY = "OrAanNgeDBlRocOkBOcaIasD";
        public static final String PACKAGE_URL = "/data/data/io.bcaas/";
        public static final String DOWNLOAD_APK_NAME = "download.apk";
        public static final String FILE_STUFF = ".txt";
        public static final String FILE_PROVIDER = ".fileprovider";
        public static final String EMAIL_TYPE = "*/*";
        public static final String DEFAULT_PAGINATION = "0";
        public static final String SUBTRACT = "-";
        public static final String ADD = "+";
        public static final String MORNING = "上午";
        public static final String AFTERROON = "下午";
        public static final String AM = "AM";
        public static final String PM = "PM";
        public static final String BCAAS_FILE_DIR = "bcaas";
        public static final String ACTIVITY_STATUS_TODO = "TODO"; //界面状态还未开始
        public static final String ACTIVITY_STATUS_TRADING = "TRADING";//界面状态交易正在进行
        public static String STATUS_DEFAULT = "default";
        public static String STATUS_SEND = "Send";

        public static final String FROM_BRAND = "brand";
        public static final String FROM_LANGUAGE_SWITCH = "languageSwitch";
        public static final String FROM_LOGIN = "login";
    }


    /* 國際化語言*/
    public class Language {
        public static final String CN = "CN";//中文簡體
        public static final String TW = "TW";// 台灣繁體
        public static final String EN = "EN";//英文
        public static final String HK = "HK";// 香港繁體
    }

    public enum SettingType {//定义一下设置的类型
        CHECK_WALLET_INFO,
        MODIFY_AUTH,
        ADDRESS_MANAGE,
        LANGUAGE_SWITCHING//语言切换
    }

    public static class KeyMaps {
        public static final String BCAAS_DIR_NAME = "bcaas";
        public static final String IS_FROM = "isFrom";
        public static final String CURRENCY_SWITCH = "CURRENCY_SWITCH";//幣種切換;
        public static final String LANGUAGE_SWITCH = "LANGUAGE_SWITCH";//語言切換;
        public static final String TAG = "io.bcaas";
        public static final String DESTINATION_WALLET = "destinationWallet";//接收方的账户地址
        public static final String SCAN_ADDRESS = "scanAddress";//扫描得到地址信息
        public static final String ADDRESS_NAME = "addressName";//接收方的账户地址
        public static final String TRANSACTION_AMOUNT = "transactionAmount";//交易数额

        public static final String WALLET_ADDRESS = "walletAddress";
        public static final String PRIVATE_KEY = "privateKey";
        public static final String From = "From";//来自
        public static final String ACTIVITY_STATUS = "activityStatus";//activity的状态
        public static final String COPY_ADDRESS = "address";
        public static final String blank = " ";
    }

    public class BlockService {
        public static final String BCC = "BCC"; //默认的币种
        public static final String CLOSE = "0";//isStartUp 关闭
        public static final String OPEN = "1";//isStartUp 开放
    }

    public class Preference {

        public static final String SP_BCAAS_TUTORIAL_PAGE = "BCAASTutorialPage";
        public static final String PUBLIC_KEY = "publicKey";//公钥
        public static final String PRIVATE_KEY = "privateKey";//私钥
        public static final String PASSWORD = "password";//密码
        public static final String ACCESS_TOKEN = "accessToken";//token 信息
        public static final String LANGUAGE_TYPE = "languageType";//當前的語言環境

        //引导页面的显示tag值
        public static final String GUIDE_CREATE = "createWallet";//创建钱包
        public static final String GUIDE_IMPORT = "importWallet";//导入钱包
        public static final String GUIDE_UNLOCK = "unlockWallet";//解锁钱包
        public static final String GUIDE_MAIN_COPY = "mainCopy";//首页复制
        public static final String GUIDE_MAIN_BALANCE = "mainBalance";//首页余额
        public static final String GUIDE_MAIN_CURRENCY = "mainCurrency";//首页币种
        public static final String GUIDE_SEND_ADDRESS = "sendAddress";//发送页面添加地址
        public static final String GUIDE_SCAN_ADDRESS = "scanAddress";//发送页面扫描地址

        public static final String GUIDE_TV_LOGIN_SWITCH_LANGUAGE = "loginSwitchLanguage";//TV版login页面提示切换语言
        public static final String GUIDE_TV_LOGIN_CREATE_WALLET = "loginCreateWalletTV";//TV版login页面提示创建钱包
        public static final String GUIDE_TV_LOGIN_IMPORT_WALLET = "loginImportWalletTV";//TV版login页面提示导入钱包
        public static final String GUIDE_TV_LOGIN_UNLOCK_WALLET = "loginUnlockWalletTV";//TV版login页面提示解锁钱包
        public static final String GUIDE_TV_HOME_CURRENCY = "homeCurrencyTV";//TV版Home页面提示币种切换

    }

    public static final String RESULT = "result";//扫描二维码返回的结果
    public static final int RESULT_CODE = 1;//发送二维码扫描结果的code
    public static final int SWITCH_TAB = 3;//切换TAB
    public static final int SWITCH_BLOCK_SERVICE = 4;//切换更新区块
    public static final String SYMBOL_DOT = "\\.";
    public static final String FROM = "From:";

    /*验证*/
    public class Verify {

        //verify 接口请求成功
        public static final String VERIFY_SUCCESS = FROM + "verifySuccess";
        //切换币种
        public static final String SWITCH_BLOCK_SERVICE = FROM + "switchBlockService";
        //发送交易
        public static final String SEND_TRANSACTION = FROM + "sendTransaction";
        //背景执行获取余额
        public static final String GET_BALANCE_LOOP = FROM + "getBalanceLoop";
    }

    /*重置*/
    public class Reset {
        //2035 TCP NOT CONNECT
        public static final String TCP_NOT_CONNECT = FROM + "tcpNotConnect";
        //点击币种
        public static final String RESET_SAN = FROM + "resetSAN";
        //网络变化
        public static final String NET_CHANGE = FROM + "netChange";
    }

    /*定时、倒计时管理*/
    public class TimerType {
        public static final String COUNT_DOWN_TCP_CONNECT = "countDownTCPConnect";
        public static final String COUNT_DOWN_TCP_HEARTBEAT = "countDownTCPHeartBeat";
        public static final String COUNT_DOWN_NOTIFICATION = "countDownNotification";
        public static final String COUNT_DOWN_REFRESH_VIEW = "countDownRefreshView";
        public static final String COUNT_DOWN_RECEIVE_BLOCK_RESPONSE = "countDownReceiveBlockResponse";
    }

    public enum EventSubscriber {
        ALL,
        HOME,
        RECEIVE,
        SEND,
        SETTING,
        CHECK_WALLET_INFO
    }
}
