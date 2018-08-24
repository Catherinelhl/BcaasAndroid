package io.bcaas.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Setting database IP, Port
 *
 * @author Costa Peng
 * @version 1.0.0
 * @since 2018/07/25
 */

public class SystemConstants {

    // BcaasApplication
    /**
     * Api IP
     */
    public static String SEEDFULLNODE_URL_DEFAULT_1 = "http://sitsn.bcaas.io:20000/";

    public static String SEEDFULLNODE_URL_DEFAULT_2 = "http://192.168.31.175:20000/";

    public static String SEEDFULLNODE_URL_DEFAULT_3 = "http://sitsn.bcaas.io:20000/";

    public static ArrayList<String> seedFullNodeList = new ArrayList<>();

    static {
        seedFullNodeList.add(SEEDFULLNODE_URL_DEFAULT_1);
        seedFullNodeList.add(SEEDFULLNODE_URL_DEFAULT_2);
        seedFullNodeList.add(SEEDFULLNODE_URL_DEFAULT_3);
    }

}