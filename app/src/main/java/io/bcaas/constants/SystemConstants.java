package io.bcaas.constants;

import io.bcaas.bean.ServerBean;
import io.bcaas.tools.ServerTool;

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

    /****************HTTP [SFN] API [START] ****************/
    /*Internet*/
    public static String SEEDFULLNODE_URL_INTERNATIONAL_SIT = "http://sitsn.bcaas.io:20000/";
    public static String SEEDFULLNODE_URL_INTERNATIONAL = "http://192.168.31.175:20000/";
    public static String SEEDFULLNODE_URL_INTERNATIONAL_UAT = "http://uatsn.bcaas.io:20000/";

    /*CHINA */
    public static String SEEDFULLNODE_URL_CHINA_hk = "http://sfnhk.bcaasc.com:20000/";
    public static String SEEDFULLNODE_URL_CHINA_sh = "http://sfnsh.bcaasc.com:20000/";

    /****************HTTP [SFN] API [END] ****************/


    /***************BcassApplication api,默認端口80 [START] ********************/

    /*Internet*/
    public static String APPLICATION_INTERNATIONAL_SIT = "https://sitapp.bcaas.io/";
    public static String APPLICATION_INTERNATIONAL_UAT = "https://uatapp.bcaas.io/";

    /*CHINA*/
    public static String APPLICATION_CHINA_URL = "http://application.bcaasc.com/";

    /***************BcassApplication api,默認端口80 [END] ********************/


    /********************Update Server ,默认端口80 [START] ***********************/

    /*Internet*/
    public static String UPDATE_SERVER_INTERNATIONAL_URL = "https://situp.bcaas.io/";

    /*CHINA*/
    public static String UPDATE_SERVER_CHINA_URL = "http://update.bcaasc.com/";

    /********************Update Server ,默认端口80 [END] ***********************/

    public static String SEEDFULLNODE_URL_DEFAULT = SEEDFULLNODE_URL_INTERNATIONAL_SIT;
    public static String APPLICATION_URL = APPLICATION_INTERNATIONAL_SIT;
    public static String UPDATE_SERVER_URL = UPDATE_SERVER_INTERNATIONAL_URL;


}