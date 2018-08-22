package io.bcaas.constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 存放当前的key/log信息
 */
public class Constants {

    // Log name for appender
    public static final String LOG_INFO = "log.info";
    public static final String LOG_DEBUG = "log.debug";

    public static final Logger LOGGER_INFO = LoggerFactory.getLogger(LOG_INFO);
    public static final Logger LOGGER_DEBUG = LoggerFactory.getLogger(LOG_DEBUG);

    public static class ValueMaps {
        public static final int brandSleepTime = 2000;//应用启动页睡眠时间
        public static final int sleepTime500 = 500;
        public static final int sleepTime1000 = 1000;
        public static final int sleepTime2000 = 2000;
        public static final int sleepTime3000 = 3000;
        public static final int sleepTime4000 = 4000;
        public static final int sleepTime5000 = 5000;

        // 區塊類型
        public static final String BLOCK_TYPE_OPEN = "Open";
        public static final String BLOCK_TYPE_SEND = "Send";
        public static final String BLOCK_TYPE_RECEIVE = "Receive";
        public static final String BLOCK_TYPE_CHANGE = "Change";

        public static final int TIMEOUTTIME = 30000;

        public static final String FROM_BRAND = "brand";
        public static final String FROM_LOGIN = "login";
    }

    public enum SettingType {//定义一下设置的类型
        CHECKWALLETINFO,
        MODIFYPOSSWORD,
        MODIFYAUTH,
        ADRESSMANNAGE
    }

    public static class KeyMaps {
        public static String TAG = "com.obt.bcaaswallet";
        public static final String CURRENCY = "currency";//币种
        public static final String ALLCURRENCY = "allCurrency";//所有币种
        public static final String RECEIVEADDRESS = "receiveAddress";//接收方的账户地址
        public static final String RECEIVECURRENCY = "receiveCurrency";//接收方到币种
        public static final String TRANSACTIONAMOUNT = "transactionAmount";//交易数额

        public static final String AccountAddress = "accountAddress";
        public static final String PrivateKey = "privateKey";
        public static final String BlockService = "blockService";
        public static String From = "from";//来自
    }

    public static class Domains {

        //        測試用domain(wifi:OBTUNI_5G\使用前请关闭vpn)
//        http://sitsn.bcaas.io:20000
        public static String LOCAL_DOMAIN = "http://localhost:8080/BcaasUpdate/";
        public static String DEVELOP_DOMAIN = "https://situp.bcaas.io/BcaasUpdate/";
        public static String PRODUCT_DOMAIN = "http://localhost:8080/BcaasUpdate/";
        public static String TEST_DOMAIN = "https://situp.bcaas.io/";
        public static String TEST_DOMAINANDPORT = "http://sitsn.bcaas.io:20000";

        public static String AUTHNODE_AUTHORIZE_KEY = "OrAanUgeTBlHocNkBOcaDasE";
        public static String PC_AUTHORIZE_KEY = "OranPgeBlockBCcaas";
        public static String MAC_AUTHORIZE_KEY = "OraMngeBAlockBCcaas";
        public static String ANDROID_AUTHORIZE_KEY = "OrAanNgeDBlRocOkBOcaIasD";
        public static String IOS_AUTHORIZE_KEY = "OrangeiBlockOBcaasS";

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
}
