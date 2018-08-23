package io.bcaas.presenter;


import java.util.List;

import io.bcaas.R;
import io.bcaas.base.BaseAuthNodePresenterImp;
import io.bcaas.base.BasePresenterImp;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.database.ANClientIpInfo;
import io.bcaas.database.WalletInfo;
import io.bcaas.gson.WalletRequestJson;
import io.bcaas.gson.WalletResponseJson;
import io.bcaas.gson.WalletVoRequestJson;
import io.bcaas.gson.WalletVoResponseJson;
import io.bcaas.http.thread.ReceiveThread;
import io.bcaas.interactor.AuthNodeInteractor;
import io.bcaas.listener.TCPReceiveBlockListener;
import io.bcaas.ui.contracts.MainContracts;
import io.bcaas.tools.GsonTool;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.WalletTool;
import io.bcaas.vo.ClientIpInfoVO;
import io.bcaas.vo.PaginationVO;
import io.bcaas.vo.TransactionChainVO;
import io.bcaas.vo.WalletVO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/21
 * <p>
 * 后台需要和服务器建立长连接，进行R区块的请求
 */
public class MainPresenterImp extends BaseAuthNodePresenterImp
        implements MainContracts.Presenter {

    private String TAG = "MainPresenterImp";
    private MainContracts.View view;
    private AuthNodeInteractor authNodeInteractor;

    public MainPresenterImp(MainContracts.View view) {
        super(view);
        this.view = view;
        authNodeInteractor = new AuthNodeInteractor();
    }

    @Override
    public void onGetWalletWaitingToReceiveBlock() {
        getWalletWaitingToReceiveBlock();
    }

    @Override
    public void onResetAuthNodeInfo() {
        resetAuthNodeInfo();
    }


    @Override
    public void checkANClientIPInfo(String from) {
        BcaasLog.d(TAG, from);
        //根据当前的进入方式去检查此钱包的AN访问地址
        if (StringTool.isEmpty(from)) {
            return;
        }
        if (StringTool.equals(from, Constants.ValueMaps.FROM_BRAND)) {
            //如果当前用户是直接进入的，那么需要从数据库里面拿到之前存储的AN请求IP
            List<ANClientIpInfo> clientIpInfos = clientIpInfoDao.queryBuilder().list();
            if (ListTool.isEmpty(clientIpInfos)) {
                //没有数据，需要重新reset
                view.noAnClientInfo();
            } else {
                ClientIpInfoVO clientIpInfoVO = new ClientIpInfoVO();
                for (ANClientIpInfo anClientIpInfo : clientIpInfos) {
                    BcaasLog.d(TAG, anClientIpInfo);
                }
                ANClientIpInfo anClientIpInfo = clientIpInfos.get(0);
                if (anClientIpInfo == null) return;
                clientIpInfoVO.setInternalIp(anClientIpInfo.getInternalIp());
                anClientIpInfo.setExternalIp(clientIpInfoVO.getExternalIp());
                anClientIpInfo.setExternalPort(clientIpInfoVO.getExternalPort());
                clientIpInfoVO.setRpcPort(anClientIpInfo.getRpcPort());
                anClientIpInfo.setInternalPort(clientIpInfoVO.getInternalPort());
                BcaasApplication.setClientIpInfoVO(clientIpInfoVO);
                startTCPConnectToGetReceiveBlock();
            }
        } else {//如果是重新「登录」进入，那么就重新获取子节点信息
            resetAuthNodeInfo();
        }

    }

    /*开启连线
     * 1：通过TCP传给服务器的数据不需要加密
     * 2:开始socket连线之后，然后Http请求该接口，通知服务器可以下发数据了。
     * */
    @Override
    public void startTCPConnectToGetReceiveBlock() {
        WalletInfo walletInfo = getWalletInfo();
        if (walletInfo == null) {
            return;
        }
        WalletRequestJson walletRequestJson = new WalletRequestJson(walletInfo.getAccessToken(),
                walletInfo.getBlockService(), walletInfo.getBitcoinAddressStr());
        String json = GsonTool.encodeToString(walletRequestJson);

//        InitDataThread initDataThread = new InitDataThread();
//        initDataThread.start();
        ReceiveThread sendActionThread = new ReceiveThread(json + "\n", tcpReceiveBlockListener);
        sendActionThread.start();

    }

    //监听Tcp数据返回
    TCPReceiveBlockListener tcpReceiveBlockListener = new TCPReceiveBlockListener() {
        @Override
        public void httpToRequestReceiverBlock() {
            getWalletWaitingToReceiveBlock();
        }

        @Override
        public void receiveBlockData(List<PaginationVO> paginationVOS) {
            //得到尚未产生的Receiver区块
            if (ListTool.isEmpty(paginationVOS)) {
                return;
            } else {
                //遍历每一条数据，然后对每一条数据进行签章，然后方给服务器
                view.showPaginationVoList(paginationVOS);
            }
        }

        @Override
        public void resetANSocket() {
            startTCPConnectToGetReceiveBlock();
        }
    };
}
