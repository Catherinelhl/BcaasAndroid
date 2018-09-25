package io.bcaas.constants;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.bcaas.base.BcaasApplication;
import io.bcaas.bean.SeedFullNodeBean;
import io.bcaas.bean.ServerBean;
import io.bcaas.http.retrofit.RetrofitFactory;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.LogTool;
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

    /*HTTP SFN api*/
    public static String SEEDFULLNODE_URL_DEFAULT_1 = "http://sitsn.bcaas.io:20000/";
    public static String SEEDFULLNODE_URL_DEFAULT_2 = "http://192.168.31.175:20000/";
    public static String SEEDFULLNODE_URL_DEFAULT_3 = "http://uatsn.bcaas.io:20000/";


    /*BcassApplication api,默認端口80*/
    public static String APPLICATION_URL = "https://sitapp.bcaas.io/";
    public static String APPLICATION_URL2 = "https://uatapp.bcaas.io/";
    /*Update Server ,默认端口80*/
    public static String UPDATE_SERVER_URL = "https://situp.bcaas.io/";

    public static ArrayList<String> seedFullNodeList = new ArrayList<>();
    //存储当前可用的SFN
    public static List<ServerBean> seedFullNodeServerBeanList = new ArrayList<>();

    static {
        seedFullNodeList.add(SEEDFULLNODE_URL_DEFAULT_1);
        seedFullNodeList.add(SEEDFULLNODE_URL_DEFAULT_2);
        seedFullNodeList.add(SEEDFULLNODE_URL_DEFAULT_3);
        addDefaultServer();
    }

    //添加本地默认的服务连接
    private static void addDefaultServer() {
        if (ListTool.noEmpty(seedFullNodeList)) {
            for (int i = 0; i < seedFullNodeList.size(); i++) {
                String seedUrl = seedFullNodeList.get(i);
                ServerBean serverBean = new ServerBean(i, seedUrl, false);
                seedFullNodeServerBeanList.add(serverBean);
            }
        }
        LogTool.d(TAG, seedFullNodeServerBeanList);
    }

    /**
     * 添加服务器返回的节点信息
     *
     * @param seedFullNodeBeanListFromServer
     */
    public static void addServerInfo(List<SeedFullNodeBean> seedFullNodeBeanListFromServer) {
        if (ListTool.isEmpty(seedFullNodeBeanListFromServer)) {
            return;
        }
        //1：添加服务器返回的数据
        if (ListTool.noEmpty(seedFullNodeServerBeanList)) {
            //2：遍历服务器返回的数据
            for (int i = 0; i < seedFullNodeBeanListFromServer.size(); i++) {
                //3:与本地默认的作比较
                for (ServerBean serverBeanLocal : seedFullNodeServerBeanList) {
                    //4:得到服务端传回的单条数据
                    String seedFullNodeUrl = Constants.SPLICE_CONVERTER(seedFullNodeBeanListFromServer.get(i).getIp(), seedFullNodeBeanListFromServer.get(i).getPort());
                    if (StringTool.equals(seedFullNodeUrl, serverBeanLocal.getServer())) {
                        //5:如果遇到相同的url,则直接break当前循环，开始下一条判断
                        break;
                    }
                    //5:否则添加至当前所有的可请求的服务器存档
                    ServerBean serverBeanNew = new ServerBean(seedFullNodeServerBeanList.size() + i, seedFullNodeUrl, false);
                    seedFullNodeServerBeanList.add(serverBeanNew);
                    break;
                }
            }
        }
        LogTool.d(TAG, MessageConstants.ALL_SERVER_INFO + seedFullNodeServerBeanList);
    }

    /**
     * 更换服务器,是否有可更换的服务器信息
     */
    public static boolean switchServer() {
        //取到当前默认的服务器
        String defaultServer = BcaasApplication.getSFNServer();
        LogTool.d(TAG, MessageConstants.DEFAULT_SFN_SERVER + defaultServer);
        int id = -1;
        String serverUrl = "";
        //1：遍历标注当前已经连接的服务器地址，然后请求下一条
        for (ServerBean serverBean : seedFullNodeServerBeanList) {
            if (StringTool.equals(serverBean.getServer(), defaultServer)) {
                //2:设置其不可用
                serverBean.setUnavailable(true);
                //3：取出当前id，比对得到下一个请求地址
                id = serverBean.getId();
            }
        }

        if (id == -1) {
            for (ServerBean serverBean : seedFullNodeServerBeanList) {
                if (!serverBean.isUnavailable()) {
                    LogTool.d(TAG, MessageConstants.NEW_SFN_SERVER + serverBean);
                    serverUrl = serverBean.getServer();
                    break;
                }
            }
        } else {
            //代表当前有相同的id
            if (id < seedFullNodeServerBeanList.size() - 1) {
                //4:得到新的请求地址信息
                ServerBean serverBeanNew = seedFullNodeServerBeanList.get(id + 1);
                if (serverBeanNew != null) {
                    //得到是否可用
                    if (!serverBeanNew.isUnavailable()) {
                        LogTool.d(TAG, MessageConstants.NEW_SFN_SERVER + serverBeanNew);
                        serverUrl = serverBeanNew.getServer();
                    }

                }
            } else {
                for (ServerBean serverBean : seedFullNodeServerBeanList) {
                    if (!serverBean.isUnavailable()) {
                        LogTool.d(TAG, MessageConstants.NEW_SFN_SERVER + serverBean);
                        serverUrl = serverBean.getServer();
                        break;
                    }
                }
            }
        }
        if (StringTool.notEmpty(serverUrl)) {
            BcaasApplication.setSFNServer(serverUrl);
            return true;
        } else {
            return false;

        }
    }
}