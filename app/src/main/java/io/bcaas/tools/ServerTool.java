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


    //添加默认的服务器
    public static void initServerData() {
        //国际SIT
        ServerBean serverBeanSIT = new ServerBean();
        serverBeanSIT.setSfnServer(SystemConstants.SEEDFULLNODE_URL_INTERNATIONAL_SIT);
        serverBeanSIT.setApiServer(SystemConstants.APPLICATION_INTERNATIONAL_SIT);
        serverBeanSIT.setUpdateServer(SystemConstants.UPDATE_SERVER_INTERNATIONAL_URL);
        serverBeanSIT.setId(0);
        serverBeanSIT.setChoose(false);
        seedFullNodeServerBeanDefault.add(serverBeanSIT);

        //国际UAT
        ServerBean serverBeanUAT = new ServerBean();
        serverBeanUAT.setSfnServer(SystemConstants.SEEDFULLNODE_URL_INTERNATIONAL_UAT);
        serverBeanUAT.setApiServer(SystemConstants.APPLICATION_INTERNATIONAL_UAT);
        serverBeanUAT.setUpdateServer(SystemConstants.UPDATE_SERVER_INTERNATIONAL_URL);
        serverBeanUAT.setId(1);
        serverBeanUAT.setChoose(false);
        seedFullNodeServerBeanDefault.add(serverBeanUAT);

        //国际NORMAL
        ServerBean serverBeanNORMAL = new ServerBean();
        serverBeanNORMAL.setSfnServer(SystemConstants.SEEDFULLNODE_URL_INTERNATIONAL);
        serverBeanNORMAL.setApiServer(SystemConstants.APPLICATION_INTERNATIONAL_SIT);
        serverBeanNORMAL.setUpdateServer(SystemConstants.UPDATE_SERVER_INTERNATIONAL_URL);
        serverBeanNORMAL.setId(2);
        serverBeanNORMAL.setChoose(false);
        seedFullNodeServerBeanDefault.add(serverBeanNORMAL);

        //国内香港
        ServerBean serverBeanHK = new ServerBean();
        serverBeanHK.setSfnServer(SystemConstants.SEEDFULLNODE_URL_CHINA_hk);
        serverBeanHK.setApiServer(SystemConstants.APPLICATION_CHINA_URL);
        serverBeanHK.setUpdateServer(SystemConstants.UPDATE_SERVER_CHINA_URL);
        serverBeanHK.setId(3);
        serverBeanHK.setChoose(false);
        seedFullNodeServerBeanDefault.add(serverBeanHK);

        //国内上海
        ServerBean serverBeanSH = new ServerBean();
        serverBeanSH.setSfnServer(SystemConstants.SEEDFULLNODE_URL_CHINA_sh);
        serverBeanSH.setApiServer(SystemConstants.APPLICATION_CHINA_URL);
        serverBeanSH.setUpdateServer(SystemConstants.UPDATE_SERVER_CHINA_URL);
        serverBeanSH.setId(4);
        serverBeanSH.setChoose(false);
        seedFullNodeServerBeanDefault.add(serverBeanSH);

        LogTool.d(TAG, seedFullNodeServerBeanDefault);
        seedFullNodeServerBeanList.addAll(seedFullNodeServerBeanDefault);
    }

    /**
     * 添加服务器返回的节点信息
     *
     * @param seedFullNodeBeanListFromServer
     */
    public static void addServerInfo(List<SeedFullNodeBean> seedFullNodeBeanListFromServer) {
        // 为了数据添加不重复，先清理到所有的数据
        seedFullNodeServerBeanList.clear();
        //添加默认的服务器数据
        seedFullNodeServerBeanList.addAll(seedFullNodeServerBeanDefault);
        //1：添加服务器返回的数据
        if (ListTool.noEmpty(seedFullNodeServerBeanList)) {
            //2：遍历服务器返回的数据
            for (int position = 0; position < seedFullNodeBeanListFromServer.size(); position++) {
                //3:与本地默认的作比较
                for (ServerBean serverBeanLocal : seedFullNodeServerBeanList) {
                    //4:得到服务端传回的单条数据
                    String SFN_URL = Constants.SPLICE_CONVERTER(seedFullNodeBeanListFromServer.get(position).getIp(), seedFullNodeBeanListFromServer.get(position).getPort());
                    if (StringTool.equals(SFN_URL, serverBeanLocal.getSfnServer())) {
                        //5:如果遇到相同的url,则直接break当前循环，开始下一条判断
                        break;
                    }
                    //5:否则添加至当前所有的可请求的服务器存档
                    ServerBean serverBeanNew = new ServerBean(seedFullNodeServerBeanList.size() + position, SFN_URL, false);
                    serverBeanNew.setApiServer(SystemConstants.APPLICATION_URL);
                    serverBeanNew.setUpdateServer(SystemConstants.UPDATE_SERVER_URL);
                    seedFullNodeServerBeanList.add(serverBeanNew);
                    LogTool.d(TAG, MessageConstants.ALL_SERVER_INFO + seedFullNodeServerBeanList);
                    break;
                }
            }
        }
    }

    /**
     * 更换服务器,是否有可更换的服务器信息
     */
    public static boolean switchServer() {
        //取到当前默认的服务器
        ServerBean serverBeanDefault = BcaasApplication.getServerBean();
        if (serverBeanDefault == null) {
            return false;
        }
        String defaultServer = serverBeanDefault.getSfnServer();
        LogTool.d(TAG, MessageConstants.DEFAULT_SFN_SERVER + defaultServer);
        //id：表示當前服務器的順序，如果為-1，代表沒有取到服務器
        int id = -1;
        //去得到当前需要切换的新服务器
        ServerBean serverBeanNext = null;
        //1：遍历标注当前已经连接的服务器地址，然后请求下一条
        for (ServerBean serverBean : seedFullNodeServerBeanList) {
            if (StringTool.equals(serverBean.getSfnServer(), defaultServer)) {
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
                    serverBeanNext = serverBean;
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
                        serverBeanNext = serverBeanNew;
                    }

                }
            } else {
                for (ServerBean serverBean : seedFullNodeServerBeanList) {
                    if (!serverBean.isUnavailable()) {
                        LogTool.d(TAG, MessageConstants.NEW_SFN_SERVER + serverBean);
                        serverBeanNext = serverBean;
                        break;
                    }
                }
            }
        }
        if (serverBeanNext != null) {
            BcaasApplication.setServerBean(serverBeanNext);
            return true;
        } else {
            return false;

        }
    }

    //返回当前默认的服务器信息
    public static ServerBean getDefaultServerBean() {
        if (ListTool.noEmpty(seedFullNodeServerBeanList)) {
            return seedFullNodeServerBeanList.get(0);
        }
        return new ServerBean();
    }
}
