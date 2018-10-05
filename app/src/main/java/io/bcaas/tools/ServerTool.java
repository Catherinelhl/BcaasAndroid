package io.bcaas.tools;

import java.util.ArrayList;
import java.util.List;

import io.bcaas.base.BcaasApplication;
import io.bcaas.bean.SeedFullNodeBean;
import io.bcaas.bean.ServerBean;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.constants.SystemConstants;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/27
 * 服务器数据管理
 */
public class ServerTool {

    private static String TAG = ServerTool.class.getSimpleName();
    //存储当前可用的SFN
    public static List<ServerBean> seedFullNodeServerBeanList = new ArrayList<>();
    //默认的服务器
    private static List<ServerBean> seedFullNodeServerBeanDefault = new ArrayList<>();
    /*是否需要复活所有服务器*/
    public static boolean needResetServerStatus;
    /*是否打开国际版服务连接*/
    private static boolean openInternationalServer = true;
    /*当前默认的服务器*/
    private static ServerBean defaultServer;

    //添加国际服务器
    public static void addInternationalServers() {
        //国际SIT
        getServerBean(SystemConstants.SFN_URL_INTERNATIONAL_SIT,
                SystemConstants.APPLICATION_URL_INTERNATIONAL_SIT,
                SystemConstants.UPDATE_URL_INTERNATIONAL_SIT);

        //国际UAT
        getServerBean(SystemConstants.SFN_URL_INTERNATIONAL_UAT,
                SystemConstants.APPLICATION_URL_INTERNATIONAL_UAT,
                SystemConstants.UPDATE_URL_INTERNATIONAL_UAT);

        //国际PRO AWSJP
        getServerBean(SystemConstants.SFN_URL_INTERNATIONAL_PRO_AWSJP,
                SystemConstants.APPLICATION_URL_INTERNATIONAL_PRO,
                SystemConstants.UPDATE_URL_INTERNATIONAL_PRO);
        //国际PRO ALIJP
        getServerBean(SystemConstants.SFN_URL_INTERNATIONAL_PRO_ALIJP,
                SystemConstants.APPLICATION_URL_INTERNATIONAL_PRO,
                SystemConstants.UPDATE_URL_INTERNATIONAL_PRO);
        //国际PRO GOOGLEJP
        getServerBean(SystemConstants.SFN_URL_INTERNATIONAL_PRO_GOOGLEJP,
                SystemConstants.APPLICATION_URL_INTERNATIONAL_PRO,
                SystemConstants.UPDATE_URL_INTERNATIONAL_PRO);
        //国际PRO GOOGLESGP
        getServerBean(SystemConstants.SFN_URL_INTERNATIONAL_PRO_GOOGLESGP,
                SystemConstants.APPLICATION_URL_INTERNATIONAL_PRO,
                SystemConstants.UPDATE_URL_INTERNATIONAL_PRO);
        //国际PRO GOOGLESDN
        getServerBean(SystemConstants.SFN_URL_INTERNATIONAL_PRO_GOOGLESDN,
                SystemConstants.APPLICATION_URL_INTERNATIONAL_PRO,
                SystemConstants.UPDATE_URL_INTERNATIONAL_PRO);
    }

    //添加国内服务器
    public static void addChinaServers() {
//        //国内SFN上海
//        getServerBean(SystemConstants.SFN_URL_CHINA_SH,
//                SystemConstants.APPLICATION_URL_CHINA,
//                SystemConstants.UPDATE_URL_CHINA);

        //国内SFN香港
        getServerBean(SystemConstants.SFN_URL_CHINA_HK,
                SystemConstants.APPLICATION_URL_CHINA,
                SystemConstants.UPDATE_URL_CHINA);

//        //国内SAN上海
//        getServerBean(SystemConstants.SAN_URL_CHINA_SH,
//                SystemConstants.APPLICATION_URL_CHINA,
//                SystemConstants.UPDATE_URL_CHINA);
//
//        //国内SAN香港
//        getServerBean(SystemConstants.SAN_URL_CHINA_SH,
//                SystemConstants.APPLICATION_URL_CHINA,
//                SystemConstants.UPDATE_URL_CHINA);

    }

    private static void getServerBean(String sfn, String api, String update) {
        ServerBean serverBean = new ServerBean();
        serverBean.setSfnServer(sfn);
        serverBean.setApiServer(api);
        serverBean.setUpdateServer(update);
        serverBean.setId(seedFullNodeServerBeanDefault.size());
        serverBean.setChoose(false);
        seedFullNodeServerBeanDefault.add(serverBean);
    }

    //添加默认的服务器
    public static void initServerData() {
        seedFullNodeServerBeanDefault.clear();
        boolean isPhone = DeviceTool.checkIsPhone(BcaasApplication.context());
        addChinaServers();
        if (openInternationalServer || !isPhone) {
            addInternationalServers();
        }
        LogTool.d(TAG, seedFullNodeServerBeanDefault);
        seedFullNodeServerBeanList.addAll(seedFullNodeServerBeanDefault);
        setDefaultServer(seedFullNodeServerBeanList.get(0));
    }

    //清除所有的服务器信息
    public static void cleanServerInfo() {
        seedFullNodeServerBeanList.clear();
    }

    /**
     * 添加服务器返回的节点信息
     *
     * @param seedFullNodeBeanListFromServer
     */
    public static void addServerInfo(List<SeedFullNodeBean> seedFullNodeBeanListFromServer) {
        // 1：为了数据添加不重复，先清理到所有的数据
        cleanServerInfo();
        //2：添加默认的服务器数据
        seedFullNodeServerBeanList.addAll(seedFullNodeServerBeanDefault);
        //3：：添加服务器返回的数据
        if (ListTool.noEmpty(seedFullNodeServerBeanList)) {
            //4：遍历服务器返回的数据
            for (int position = 0; position < seedFullNodeBeanListFromServer.size(); position++) {
                //5:与本地默认的作比较
                for (ServerBean serverBeanLocal : seedFullNodeServerBeanList) {
                    //6:得到服务端传回的单条数据
                    String SFN_URL = Constants.SPLICE_CONVERTER(seedFullNodeBeanListFromServer.get(position).getIp(), seedFullNodeBeanListFromServer.get(position).getPort());
                    if (StringTool.equals(SFN_URL, serverBeanLocal.getSfnServer())) {
                        //如果遇到相同的url,则直接break当前循环，开始下一条判断
                        break;
                    }
                    //7:否则添加至当前所有的可请求的服务器存档
                    ServerBean serverBeanNew = new ServerBean(seedFullNodeServerBeanList.size() + position, SFN_URL, false);
                    //8：通过接口返回的数据没有API和Update接口的domain，所以直接添加当前默认的接口
                    ServerBean serverBean = getDefaultServer();
                    if (serverBean != null) {
                        serverBeanNew.setApiServer(serverBean.getApiServer());
                        serverBeanNew.setUpdateServer(serverBean.getUpdateServer());
                    }
                    seedFullNodeServerBeanList.add(serverBeanNew);
                    break;
                }
            }
            LogTool.d(TAG, MessageConstants.ALL_SERVER_INFO + seedFullNodeServerBeanList);

        }
    }

    /**
     * 更换服务器,查看是否有可更换的服务器信息
     */
    public static ServerBean checkAvailableServerToSwitch() {
        //取到当前默认的服务器
        ServerBean serverBeanDefault = getDefaultServer();
        //如果数据为空，则没有可用的服务器
        if (serverBeanDefault == null) {
            return null;
        }
        LogTool.d(TAG, MessageConstants.DEFAULT_SFN_SERVER + serverBeanDefault);
        //id：表示當前服務器的順序，如果為-1，那么就重新开始请求
        int currentServerPosition = serverBeanDefault.getId();
        //去得到当前需要切换的新服务器
        ServerBean serverBeanNext = null;
        //如果当前id>=0且小于当前数据的数据-1，代表当前还有可取的数据
        if (currentServerPosition < seedFullNodeServerBeanList.size() - 1 && currentServerPosition >= 0) {
            ServerBean serverBean = seedFullNodeServerBeanList.get(currentServerPosition);
            if (serverBean != null) {
                serverBean.setUnavailable(true);
            }
            //4:得到新的请求地址信息
            ServerBean serverBeanNew = seedFullNodeServerBeanList.get(currentServerPosition + 1);
            if (serverBeanNew != null) {
                //得到是否可用
                if (!serverBeanNew.isUnavailable()) {
                    LogTool.d(TAG, MessageConstants.NEW_SFN_SERVER + serverBeanNew);
                    serverBeanNext = serverBeanNew;
                }

            }
        } else {
            //检测当前是否需要重置所有服务器的状态
            if (ServerTool.needResetServerStatus) {
                //否则遍历其中可用的url
                for (ServerBean serverBean : seedFullNodeServerBeanList) {
                    serverBean.setUnavailable(false);
                }
                ServerTool.needResetServerStatus = false;
            }
            //遍历其中可用的url
            for (ServerBean serverBean : seedFullNodeServerBeanList) {
                if (!serverBean.isUnavailable()) {
                    LogTool.d(TAG, MessageConstants.NEW_SFN_SERVER + serverBean);
                    serverBeanNext = serverBean;
                    break;
                }
            }
        }
        if (serverBeanNext != null) {
            setDefaultServer(serverBeanNext);
            return serverBeanNext;
        }
        return null;
    }

    public static ServerBean getDefaultServer() {
        return defaultServer;
    }

    public static void setDefaultServer(ServerBean defaultServer) {
        ServerTool.defaultServer = defaultServer;
    }
}
