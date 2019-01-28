package io.bcaas.tools;

import java.util.ArrayList;
import java.util.List;

import io.bcaas.vo.SeedFullNodeVO;
import io.bcaas.bean.ServerBean;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.constants.SystemConstants;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/27
 * 工具類：服务器数据管理
 */
public class ServerTool {

    private static String TAG = ServerTool.class.getSimpleName();
    /*存储当前可用的SFN*/
    public static List<ServerBean> SFNServerBeanList = new ArrayList<>();
    /*是否需要复活所有服务器*/
    public static boolean needResetServerStatus;
    /*当前默认的服务器*/
    private static ServerBean defaultServerBean;
    /*存储当前连接服务器的类型 国际SIT*/
//    private static String serverType = Constants.ServerType.INTERNATIONAL_SIT;

    /*存储当前连接服务器的类型 国际UAT*/
//    private static  String serverType = Constants.ServerType.INTERNATIONAL_UAT;
    /*存储当前连接服务器的类型 国际PRD*/
    private static String serverType = Constants.ServerType.INTERNATIONAL_PRD;

    public static String getServerType() {
        return serverType;
    }

    public static void setServerType(String serverType) {
        ServerTool.serverType = serverType;
    }

    /**
     * 添加国际版SIT服务器（开发）
     */
    public static List<ServerBean> addInternationalSTIServers() {
        List<ServerBean> SFNServerBeanDefaultList = new ArrayList<>();

        SFNServerBeanDefaultList.add(getServerBean(SFNServerBeanDefaultList.size(),
                SystemConstants.SFN_URL_INTERNATIONAL_SIT_SGPAWS,
                SystemConstants.APPLICATION_URL_INTERNATIONAL_SIT,
                SystemConstants.UPDATE_URL_INTERNATIONAL_SIT));

        SFNServerBeanDefaultList.add(getServerBean(SFNServerBeanDefaultList.size(),
                SystemConstants.SFN_URL_INTERNATIONAL_SIT_JPGOOGLE,
                SystemConstants.APPLICATION_URL_INTERNATIONAL_SIT,
                SystemConstants.UPDATE_URL_INTERNATIONAL_SIT));
        return SFNServerBeanDefaultList;
    }


    /**
     * 添加国际版UAT服务器（开发）
     */
    public static List<ServerBean> addInternationalUATServers() {
        List<ServerBean> SFNServerBeanDefaultList = new ArrayList<>();

        SFNServerBeanDefaultList.add(getServerBean(SFNServerBeanDefaultList.size(),
                SystemConstants.SFN_URL_INTERNATIONAL_UAT,
                SystemConstants.APPLICATION_URL_INTERNATIONAL_UAT,
                SystemConstants.UPDATE_URL_INTERNATIONAL_UAT));

        SFNServerBeanDefaultList.add(getServerBean(SFNServerBeanDefaultList.size(),
                SystemConstants.SFN_URL_INTERNATIONAL_UAT_SN_ALI,
                SystemConstants.APPLICATION_URL_INTERNATIONAL_UAT,
                SystemConstants.UPDATE_URL_INTERNATIONAL_UAT));

        SFNServerBeanDefaultList.add(getServerBean(SFNServerBeanDefaultList.size(),
                SystemConstants.SFN_URL_INTERNATIONAL_UAT_SN_GOOGLE,
                SystemConstants.APPLICATION_URL_INTERNATIONAL_UAT,
                SystemConstants.UPDATE_URL_INTERNATIONAL_UAT));

        return SFNServerBeanDefaultList;
    }

    /**
     * 添加国际版PRO服务器(正式)
     */
    public static List<ServerBean> addInternationalPRDServers() {
        List<ServerBean> SFNServerBeanDefaultList = new ArrayList<>();

        //国际PRD AWSJP
        SFNServerBeanDefaultList.add(getServerBean(SFNServerBeanDefaultList.size(),
                SystemConstants.SFN_URL_INTERNATIONAL_PRD_AWSJP,
                SystemConstants.APPLICATION_URL_INTERNATIONAL_PRO,
                SystemConstants.UPDATE_URL_INTERNATIONAL_PRO));

        //国际PRD ALIJP
        SFNServerBeanDefaultList.add(getServerBean(SFNServerBeanDefaultList.size(),
                SystemConstants.SFN_URL_INTERNATIONAL_PRD_ALIJP,
                SystemConstants.APPLICATION_URL_INTERNATIONAL_PRO,
                SystemConstants.UPDATE_URL_INTERNATIONAL_PRO));

        //国际PRD GOOGLEJP
        SFNServerBeanDefaultList.add(getServerBean(SFNServerBeanDefaultList.size(),
                SystemConstants.SFN_URL_INTERNATIONAL_PRD_GOOGLEJP,
                SystemConstants.APPLICATION_URL_INTERNATIONAL_PRO,
                SystemConstants.UPDATE_URL_INTERNATIONAL_PRO));

        //国际PRD GOOGLESGP
        SFNServerBeanDefaultList.add(getServerBean(SFNServerBeanDefaultList.size(),
                SystemConstants.SFN_URL_INTERNATIONAL_PRD_GOOGLESGP,
                SystemConstants.APPLICATION_URL_INTERNATIONAL_PRO,
                SystemConstants.UPDATE_URL_INTERNATIONAL_PRO));

        return SFNServerBeanDefaultList;

    }

    private static ServerBean getServerBean(int id, String sfn, String api, String update) {
        ServerBean serverBean = new ServerBean();
        serverBean.setSfnServer(sfn);
        serverBean.setApiServer(api);
        serverBean.setUpdateServer(update);
        serverBean.setId(id);
        return serverBean;
    }

    /**
     * 初始化默认的服务器
     */
    public static void initServerData() {
        // 1：为了数据添加不重复，先清理到所有的数据
        cleanServerInfo();
        //2：判断当前的服务器类型，根据标注的服务器类型添加相对应的服务器数据
        switch (serverType) {
            //3：添加所有的服务器至全局通用的服务器遍历数组里面进行stand by
            case Constants.ServerType.INTERNATIONAL_SIT:
                SFNServerBeanList.addAll(addInternationalSTIServers());
                break;
            case Constants.ServerType.INTERNATIONAL_UAT:
                SFNServerBeanList.addAll(addInternationalUATServers());
                break;
            case Constants.ServerType.INTERNATIONAL_PRD:
                SFNServerBeanList.addAll(addInternationalPRDServers());
                break;
            default:
                break;
        }

        LogTool.d(TAG, serverType + ":" + SFNServerBeanList);
    }

    //清除所有的服务器信息
    public static void cleanServerInfo() {
        setDefaultServerBean(null);
        SFNServerBeanList.clear();
    }

    /**
     * 添加服务器返回的节点信息
     *
     * @param seedFullNodeBeanListFromServer
     */
    public static void addServerInfo(List<SeedFullNodeVO> seedFullNodeBeanListFromServer) {
        //1：添加默认的服务器数据
        initServerData();
        //2：：添加服务器返回的数据
        if (ListTool.noEmpty(SFNServerBeanList)) {
            //3：遍历服务器返回的数据
            for (int position = 0; position < seedFullNodeBeanListFromServer.size(); position++) {
                //4:与本地默认的作比较
                for (ServerBean serverBeanLocal : SFNServerBeanList) {
                    //5:得到服务端传回的单条数据
                    String SFN_URL = Constants.SPLICE_CONVERTER(seedFullNodeBeanListFromServer.get(position).getIp(), seedFullNodeBeanListFromServer.get(position).getPort());
                    if (StringTool.equals(SFN_URL, serverBeanLocal.getSfnServer())) {
                        //如果遇到相同的url,则直接break当前循环，开始下一条判断
                        break;
                    }
                    //6:否则添加至当前所有的可请求的服务器存档
                    ServerBean serverBeanNew = new ServerBean(SFNServerBeanList.size(), SFN_URL, false);
                    //7：通过接口返回的数据没有API和Update接口的domain，所以直接添加当前默认的接口
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
        //1：取到当前默认的服务器
        ServerBean serverBeanDefault = getDefaultServerBean();
        //2：判断数据非空
        if (serverBeanDefault == null) {
            return null;
        }
        LogTool.d(TAG, MessageConstants.DEFAULT_SFN_SERVER + serverBeanDefault);
        //3：得到当前服务器的id：表示當前服務器的顺序
        int currentServerPosition = serverBeanDefault.getId();
        //4：新建变量用于得到当前需要切换的新服务器
        ServerBean serverBeanNext = null;
        //5：判断当前服务器的id，如果当前id>=0且小于当前数据的数据，代表属于数组里面的数据
        if (currentServerPosition < SFNServerBeanList.size() && currentServerPosition >= 0) {
            ServerBean currentServerBean = SFNServerBeanList.get(currentServerPosition);
            if (currentServerBean != null) {
                currentServerBean.setUnAvailable(true);
            }
            //6：如果当前id小于等于SFNServerBeanList数量-1，代表还有可取的数据
            if (currentServerPosition < SFNServerBeanList.size() - 1) {
                //7:得到新的请求地址信息
                ServerBean serverBeanNew = SFNServerBeanList.get(currentServerPosition + 1);
                if (serverBeanNew != null) {
                    //8：判断新取到的服务器是否可用
                    if (!serverBeanNew.isUnAvailable()) {
                        LogTool.d(TAG, MessageConstants.NEW_SFN_SERVER + serverBeanNew);
                        serverBeanNext = serverBeanNew;
                    }

                }
            } else {
                //否则，检测当前是否需要重置所有服务器的状态
                if (ServerTool.needResetServerStatus) {
                    //否则遍历其中可用的url
                    for (ServerBean serverBean : SFNServerBeanList) {
                        serverBean.setUnAvailable(false);
                    }
                    ServerTool.needResetServerStatus = false;
                }
                LogTool.d(TAG, MessageConstants.RESET_SERVER_DATA + SFNServerBeanList);
                //重新选取可用的服务器数据
                for (ServerBean serverBean : SFNServerBeanList) {
                    if (!serverBean.isUnAvailable()) {
                        LogTool.d(TAG, MessageConstants.NEW_SFN_SERVER + serverBean);
                        serverBeanNext = serverBean;
                        break;
                    }
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
        if (defaultServerBean == null) {
            //设置默认的服务器
            setDefaultServerBean(SFNServerBeanList.get(0));
        }
        return defaultServerBean;
    }

    public static void setDefaultServerBean(ServerBean defaultServerBean) {
        ServerTool.defaultServerBean = defaultServerBean;
    }
}
