package io.bcaas.tools;

import java.util.ArrayList;
import java.util.List;

import io.bcaas.BuildConfig;
import io.bcaas.base.BCAASApplication;
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
    /*存储当前可用的SFN*/
    public static List<ServerBean> SFNServerBeanList = new ArrayList<>();
    /*默认的所有服务器*/
    private static List<ServerBean> SFNServerBeanDefaultList = new ArrayList<>();
    /*是否需要复活所有服务器*/
    public static boolean needResetServerStatus;
    //    /*是否打开国际版服务连接*/
//    private static boolean openInternationalServer = true;
    /*存储当前连接服务器的类型 国际SIT*/
//    private static Constants.ServerType ServerType = Constants.ServerType.INTERNATIONAL_SIT;
    /*存储当前连接服务器的类型 国际UAT*/
//    private static Constants.ServerType ServerType = Constants.ServerType.INTERNATIONAL_UAT;
    /*存储当前连接服务器的类型 国际PRO*/
//    private static Constants.ServerType ServerType = Constants.ServerType.INTERNATIONAL_PRO;
    /*存储当前连接服务器的类型 国内*/
    private static Constants.ServerType ServerType = Constants.ServerType.CHINA;
    /*存储当前连接服务器的类型 国内HK*/
//    private static Constants.ServerType ServerType = Constants.ServerType.CHINA_HK;
    /*存储当前连接服务器的类型 国内SH*/
//    private static Constants.ServerType ServerType = Constants.ServerType.CHINA_SH;
    /*当前默认的服务器*/
    private static ServerBean defaultServerBean;

    /**
     * 添加国际版SIT服务器（开发）
     */
    public static void addInternationalSTIServers() {
        //国际SIT
        getServerBean(SystemConstants.SFN_URL_INTERNATIONAL_SIT,
                SystemConstants.APPLICATION_URL_INTERNATIONAL_SIT,
                SystemConstants.UPDATE_URL_INTERNATIONAL_SIT);
    }


    /**
     * 添加国际版UAT服务器（开发）
     */
    public static void addInternationalUATServers() {
        //国际UAT
        getServerBean(SystemConstants.SFN_URL_INTERNATIONAL_UAT,
                SystemConstants.APPLICATION_URL_INTERNATIONAL_UAT,
                SystemConstants.UPDATE_URL_INTERNATIONAL_UAT);
    }

    /**
     * 添加国际版PRO服务器(正式)
     */
    public static void addInternationalPROServers() {
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

    /**
     * 添加国内服务器
     */
    public static void addChinaServers() {
//        //国内SFN上海
//        getServerBean(SystemConstants.SFN_URL_CHINA_SH,
//                SystemConstants.APPLICATION_URL_CHINA,
//                SystemConstants.UPDATE_URL_CHINA);

        //        //国内SFN上海2
//        getServerBean(SystemConstants.SFN_URL_CHINA_SH2,
//                SystemConstants.APPLICATION_URL_CHINA,
//                SystemConstants.UPDATE_URL_CHINA);
        //国内SFN香港
        getServerBean(SystemConstants.SFN_URL_CHINA_HK,
                SystemConstants.APPLICATION_URL_CHINA,
                SystemConstants.UPDATE_URL_CHINA);

        //国内SFN香港2
        getServerBean(SystemConstants.SFN_URL_CHINA_HK2,
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
        serverBean.setId(SFNServerBeanDefaultList.size());
        serverBean.setChoose(false);
        SFNServerBeanDefaultList.add(serverBean);
    }

    /**
     * 初始化默认的服务器
     */
    public static void initServerData() {
        SFNServerBeanDefaultList.clear();
        if (BuildConfig.DEBUG) {
            //（因为TV版运行较慢，所以此为方便之举）
            boolean isPhone = DeviceTool.checkIsPhone(BCAASApplication.context());
            if (!isPhone) {
                addInternationalSTIServers();
                addInternationalUATServers();
                addInternationalPROServers();
            }
        }
        //1：判断当前的服务器类型，根据标注的服务器类型添加相对应的服务器数据
        switch (ServerType) {
            case INTERNATIONAL_SIT:
                addInternationalSTIServers();
                break;
            case INTERNATIONAL_UAT:
                addInternationalUATServers();
                break;
            case INTERNATIONAL_PRO:
                addInternationalPROServers();
                break;
            case CHINA:
                addChinaServers();
                break;
            case CHINA_HK:
                break;
            case CHINA_SH:
                break;
            default:
                break;
        }
        LogTool.d(TAG, SFNServerBeanDefaultList);
        //2：添加所有的服务器至全局通用的服务器遍历数组里面进行stand by
        SFNServerBeanList.addAll(SFNServerBeanDefaultList);
        //3:设置默认的服务器
        setDefaultServerBean(SFNServerBeanList.get(0));
    }

    //清除所有的服务器信息
    public static void cleanServerInfo() {
        SFNServerBeanList.clear();
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
        SFNServerBeanList.addAll(SFNServerBeanDefaultList);
        //3：：添加服务器返回的数据
        if (ListTool.noEmpty(SFNServerBeanList)) {
            //4：遍历服务器返回的数据
            for (int position = 0; position < seedFullNodeBeanListFromServer.size(); position++) {
                //5:与本地默认的作比较
                for (ServerBean serverBeanLocal : SFNServerBeanList) {
                    //6:得到服务端传回的单条数据
                    String SFN_URL = Constants.SPLICE_CONVERTER(seedFullNodeBeanListFromServer.get(position).getIp(), seedFullNodeBeanListFromServer.get(position).getPort());
                    if (StringTool.equals(SFN_URL, serverBeanLocal.getSfnServer())) {
                        //如果遇到相同的url,则直接break当前循环，开始下一条判断
                        break;
                    }
                    //7:否则添加至当前所有的可请求的服务器存档
                    ServerBean serverBeanNew = new ServerBean(SFNServerBeanList.size() + position, SFN_URL, false);
                    //8：通过接口返回的数据没有API和Update接口的domain，所以直接添加当前默认的接口
                    ServerBean serverBean = getDefaultServerBean();
                    if (serverBean != null) {
                        serverBeanNew.setApiServer(serverBean.getApiServer());
                        serverBeanNew.setUpdateServer(serverBean.getUpdateServer());
                    }
                    SFNServerBeanList.add(serverBeanNew);
                    break;
                }
            }
            LogTool.d(TAG, MessageConstants.ALL_SERVER_INFO + SFNServerBeanList);

        }
    }

    /**
     * 更换服务器,查看是否有可更换的服务器信息
     */
    public static ServerBean checkAvailableServerToSwitch() {
        //取到当前默认的服务器
        ServerBean serverBeanDefault = getDefaultServerBean();
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
        if (currentServerPosition < SFNServerBeanList.size() - 1 && currentServerPosition >= 0) {
            ServerBean serverBean = SFNServerBeanList.get(currentServerPosition);
            if (serverBean != null) {
                serverBean.setUnavailable(true);
            }
            //4:得到新的请求地址信息
            ServerBean serverBeanNew = SFNServerBeanList.get(currentServerPosition + 1);
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
                for (ServerBean serverBean : SFNServerBeanList) {
                    serverBean.setUnavailable(false);
                }
                ServerTool.needResetServerStatus = false;
            }
            //遍历其中可用的url
            for (ServerBean serverBean : SFNServerBeanList) {
                if (!serverBean.isUnavailable()) {
                    LogTool.d(TAG, MessageConstants.NEW_SFN_SERVER + serverBean);
                    serverBeanNext = serverBean;
                    break;
                }
            }
        }
        if (serverBeanNext != null) {
            setDefaultServerBean(serverBeanNext);
            return serverBeanNext;
        }
        return null;
    }

    public static ServerBean getDefaultServerBean() {
        return defaultServerBean;
    }

    public static void setDefaultServerBean(ServerBean defaultServerBean) {
        ServerTool.defaultServerBean = defaultServerBean;
    }
}
