package io.bcaas.constants;

/**
 * Setting database IP, Port
 *
 * @author Costa Peng
 * @version 1.0.0
 * @since 2018/07/25
 * <p>
 * api ip manager
 * <p>
 * 定義對應Config常數(API)
 */

public class SystemConstants {

    /****************HTTP [SFN] API [START] ****************/
    /*Internet*/
    //SIT
//    public static String SFN_URL_INTERNATIONAL_SIT = "http://sitsn.bcaas.io:20000";
//    public static String SFN_URL_INTERNATIONAL_SIT_HK = "http://sitsnhk.bcaas.io:20000";
    public static String SFN_URL_INTERNATIONAL_SIT_SGPAWS = "http://sitsfnsgpaws.bcaas.io:20000";
    public static String SFN_URL_INTERNATIONAL_SIT_JPGOOGLE = "http://sitsfnjpgoogle.bcaas.io:20000";

    //UAT
    public static String SFN_URL_INTERNATIONAL_UAT = "http://uatsn.bcaas.io:20000";
    public static String SFN_URL_INTERNATIONAL_UAT_SN_ALI = "http://uatsnali.bcaas.io:20000";
    public static String SFN_URL_INTERNATIONAL_UAT_SN_GOOGLE = "http://uatsngoogle.bcaas.io:20000";

    //PRD
    public static String SFN_URL_INTERNATIONAL_PRD_AWSJP = "http://sfnsgpaws1.bcaas.io:20000";
    public static String SFN_URL_INTERNATIONAL_PRD_ALIJP = "http://sfnsgpaws2.bcaas.io:20000";
    public static String SFN_URL_INTERNATIONAL_PRD_GOOGLEJP = "http://sfnjpgoogle1.bcaas.io:20000";
    public static String SFN_URL_INTERNATIONAL_PRD_GOOGLESGP = "http://sfnjpgoogle2:20000";

    /****************HTTP [SFN] API [END] ****************/


    /***************BcassApplication api,默認端口80 [START] ********************/

    /*Internet*/
    //SIT
    public static String APPLICATION_URL_INTERNATIONAL_SIT = "https://sitapplication.bcaas.io";
    //UAT
    public static String APPLICATION_URL_INTERNATIONAL_UAT = "https://uatapp.bcaas.io/";
    //PRO
    public static String APPLICATION_URL_INTERNATIONAL_PRO = "https://application.bcaas.io";


    /***************BcassApplication api,默認端口80 [END] ********************/


    /********************Update Server ,默认端口80 [START] ***********************/

    /*Internet*/
    //SIT
    public static String UPDATE_URL_INTERNATIONAL_SIT = "https://situpdate.bcaas.io";
    //UAT
    public static String UPDATE_URL_INTERNATIONAL_UAT = "https://uatup.bcaas.io";
    //PRO
    public static String UPDATE_URL_INTERNATIONAL_PRO = "https://update.bcaas.io";

    /********************Update Server ,默认端口80 [END] ***********************/
}