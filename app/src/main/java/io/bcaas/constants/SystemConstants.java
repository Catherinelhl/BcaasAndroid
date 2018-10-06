package io.bcaas.constants;

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
    //SIT
    public static String SFN_URL_INTERNATIONAL_SIT = "http://sitsn.bcaas.io:20000/";
    public static String SFN_URL_INTERNATIONAL_SIT_HK = "http://sitsnhk.bcaas.io:20000";
    //UAT
    public static String SFN_URL_INTERNATIONAL_UAT = "http://uatsn.bcaas.io:20000/";
    public static String SFN_URL_INTERNATIONAL_UAT_SN_ALI = "http://uatsnali.bcaas.io:20000";
    public static String SFN_URL_INTERNATIONAL_UAT_SN_GOOGLE = "http://uatsngoogle.bcaas.io:20000";
    //PRO
    public static String SFN_URL_INTERNATIONAL_PRO_AWSJP = "http://sfnawsjp.bcaas.io:20000";
    public static String SFN_URL_INTERNATIONAL_PRO_ALIJP = "http://sfnalijp.bcaas.io:20000";
    public static String SFN_URL_INTERNATIONAL_PRO_GOOGLEJP = "http://sfngooglejp.bcaas.io:20000";
    public static String SFN_URL_INTERNATIONAL_PRO_GOOGLESGP = "http://sfngooglesgp.bcaas.io:20000";
    public static String SFN_URL_INTERNATIONAL_PRO_GOOGLESDN = "http://sfngooglesdn.bcaas.io:20000";


    /*CHINA */
    public static String SFN_URL_CHINA_HK = "http://sfnhk.bcaasc.com:20000/";
    public static String SFN_URL_CHINA_HK2 = "http://sfnhk2.bcaasc.com:20000/";
    public static String SFN_URL_CHINA_SH = "http://sfnsh.bcaasc.com:20000/";
    public static String SFN_URL_CHINA_SH2 = "http://sfnsh2.bcaasc.com:20000/";
    public static String SAN_URL_CHINA_HK = "http://sanhk.bcaasc.com:20000/";
    public static String SAN_URL_CHINA_SH = "http://sansh.bcaasc.com:20000/";

    /****************HTTP [SFN] API [END] ****************/


    /***************BcassApplication api,默認端口80 [START] ********************/

    /*Internet*/
    //SIT
    public static String APPLICATION_URL_INTERNATIONAL_SIT = "https://sitapp.bcaas.io/";
    //UAT
    public static String APPLICATION_URL_INTERNATIONAL_UAT = "https://uatapp.bcaas.io/";
    //PRO
    public static String APPLICATION_URL_INTERNATIONAL_PRO = "https://application.bcaas.io";

    /*CHINA*/
    public static String APPLICATION_URL_CHINA = "http://application.bcaasc.com/";

    /***************BcassApplication api,默認端口80 [END] ********************/


    /********************Update Server ,默认端口80 [START] ***********************/

    /*Internet*/
    //SIT
    public static String UPDATE_URL_INTERNATIONAL_SIT = "https://situp.bcaas.io/";
    //UAT
    public static String UPDATE_URL_INTERNATIONAL_UAT = "https://uatup.bcaas.io";
    //PRO
    public static String UPDATE_URL_INTERNATIONAL_PRO = "https://update.bcaas.io";

    /*CHINA*/
    public static String UPDATE_URL_CHINA = "http://update.bcaasc.com/";

    /********************Update Server ,默认端口80 [END] ***********************/

}