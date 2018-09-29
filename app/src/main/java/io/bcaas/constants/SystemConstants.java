package io.bcaas.constants;

import java.util.ArrayList;
import java.util.List;

import io.bcaas.base.BcaasApplication;
import io.bcaas.bean.SeedFullNodeBean;
import io.bcaas.bean.ServerBean;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.ServerTool;
import io.bcaas.tools.StringTool;

/**
 * Setting database IP, Port
 *
 * @author Costa Peng
 * @version 1.0.0
 * @since 2018/07/25
 * <p>
 * api ip manager
 */

public class SystemConstants {

    private static String TAG = SystemConstants.class.getSimpleName();

    /*HTTP SFN CHINA api*/
    public static String SEEDFULLNODE_URL_CHINA_2 = "http://sfnhk.bcaasc.com:20000/";
    public static String SEEDFULLNODE_URL_CHINA_1 = "http://sfnsh.bcaasc.com:20000/";

    /*HTTP SFN Internet api*/
    public static String SEEDFULLNODE_URL_INTERNATIONAL_1 = "http://sitsn.bcaas.io:20000/";
    public static String SEEDFULLNODE_URL_INTERNATIONAL_2 = "http://192.168.31.175:20000/";
    public static String SEEDFULLNODE_URL_INTERNATIONAL_3 = "http://uatsn.bcaas.io:20000/";


    /*BcassApplication api,默認端口80*/
    public static String APPLICATION_CHINA_URL = "http://application.bcaasc.com/";
    public static String APPLICATION_INTERNATIONAL_URL = "https://sitapp.bcaas.io/";
    public static String APPLICATION_INTERNATIONAL_URL2 = "https://uatapp.bcaas.io/";


    /*Update Server ,默认端口80*/
    public static String UPDATE_SERVER_CHINA_URL = "http://update.bcaasc.com/";
    public static String UPDATE_SERVER_INTERNATIONAL_URL = "https://situp.bcaas.io/";

    public static String SEEDFULLNODE_URL_DEFAULT = SEEDFULLNODE_URL_INTERNATIONAL_1;
    public static String APPLICATION_URL = APPLICATION_INTERNATIONAL_URL;
    public static String UPDATE_SERVER_URL = UPDATE_SERVER_INTERNATIONAL_URL;

    static {
        ServerTool.addDefaultServer(SEEDFULLNODE_URL_INTERNATIONAL_1);
        ServerTool.addDefaultServer(SEEDFULLNODE_URL_INTERNATIONAL_2);
        ServerTool.addDefaultServer(SEEDFULLNODE_URL_INTERNATIONAL_3);

//        ServerTool.addDefaultServer(SEEDFULLNODE_URL_DEFAULT_1);
//        ServerTool.addDefaultServer(SEEDFULLNODE_URL_DEFAULT_2);
        ServerTool.addAllServer();
    }
}