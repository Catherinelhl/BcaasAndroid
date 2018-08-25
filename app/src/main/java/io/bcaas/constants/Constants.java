package io.bcaas.constants;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 存放当前的key/log信息
 */
public class Constants {

    public static final String PRODUCE_KEY_TYPE = "ECC";
    public static String BLOCK_TYPE_RECEIVE = "Receive";
    public static String BLOCK_TYPE_OPEN = "Open";
    public static String BLOCK_TYPE_SEND = "Send";
    public static String STATUS_DEFAULT = "default";
    public static String STATUS_SEND = "Send";

    public static class ValueMaps {
        public static final int brandSleepTime = 2000;//应用启动页睡眠时间
        public static final int sleepTime500 = 500;
        public static final int sleepTime1500 = 1500;
        public static final int sleepTime1000 = 1000;
        public static final int sleepTime2000 = 2000;
        public static final int sleepTime3000 = 3000;
        public static final int sleepTime4000 = 4000;
        public static final int sleepTime5000 = 5000;
        public static final int sleepTime30000 = 30000;

        // 區塊類型
        public static final String BLOCK_TYPE_OPEN = "Open";
        public static final String BLOCK_TYPE_SEND = "Send";
        public static final String BLOCK_TYPE_RECEIVE = "Receive";
        public static final String BLOCK_TYPE_CHANGE = "Change";

        public static final int TIME_OUT_TIME = 30000;

        public static final String FROM_BRAND = "brand";
        public static final String FROM_LOGIN = "login";
        // TODO: 2018/8/24 记得修改时间
        public static final long REQUEST_RECEIVE_TIME = 30 * 1000;//间隔五分钟去请求新的数据块，暂时写得5s
    }

    public enum SettingType {//定义一下设置的类型
        CHECK_WALLET_INFO,
        MODIFY_PASSWORD,
        MODIFY_AUTH,
        ADDRESS_MANAGE
    }

    public static class KeyMaps {
        public static String TAG = "io.bcaas";
        public static final String CURRENCY = "currency";//币种
        public static final String ALL_CURRENCY = "allCurrency";//所有币种
        public static final String DESTINATION_WALLET = "destinationWallet";//接收方的账户地址
        public static final String RECEIVE_CURRENCY = "receiveCurrency";//接收方到币种
        public static final String TRANSACTION_AMOUNT = "transactionAmount";//交易数额

        public static final String WALLET_ADDRESS = "walletAddress";
        public static final String PRIVATE_KEY = "privateKey";
        public static final String BLOCK_SERVICE = "blockService";
        public static String From = "from";//来自
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

    }

    public class BlockService {
        // TODO: 2018/8/20 待定
        public static final String BCC = "BCC";
    }

    public class Preference {
        public static final String PUBLIC_KEY = "publicKey";//公钥
        public static final String PRIVATE_KEY = "privateKey";//私钥
        public static final String CLIENT_IP_INFO = "clientIpInfo";//与之交互的AN的信息
        public static final String WALLET_INFO = "walletInfo";//钱包信息
        public static final String PASSWORD = "password";//密码
        public static final String ACCESS_TOKEN = "accessToken";//token 信息
        public static final String BLOCK_SERVICE = "blockService";//区块服务信息
        public static final String WALLET_BALANCE = "walletBalance";//钱包余额

    }
}
