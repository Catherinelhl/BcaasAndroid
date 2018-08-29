package io.bcaas.constants;

import java.util.ArrayList;
import java.util.List;

import io.bcaas.tools.BcaasLog;

/**
 * Setting database IP, Port
 *
 * @author Costa Peng
 * @version 1.0.0
 * @since 2018/07/25
 */

public class SystemConstants {

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

    /**
     * 添加服务器返回的节点信息
     *
     * @param seedFullNodeIp
     * @param seedFullNodePort
     */
    public static void add(String seedFullNodeIp, int seedFullNodePort) {
        String seedFullNodeUrl = "http://" + seedFullNodeIp + ":" + seedFullNodePort;
        BcaasLog.d(SystemConstants.class.getSimpleName(), "seedFullNodeUrl====" + seedFullNodeUrl);
        seedFullNodeList.add(seedFullNodeUrl);
    }

}