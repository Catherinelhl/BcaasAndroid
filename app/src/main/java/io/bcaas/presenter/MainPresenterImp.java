package io.bcaas.presenter;

import com.google.gson.Gson;

import java.util.List;

import io.bcaas.R;
import io.bcaas.base.BasePresenterImp;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.database.ANClientIpInfo;
import io.bcaas.database.WalletInfo;
import io.bcaas.gson.RequestJson;
import io.bcaas.gson.ResponseJson;
import io.bcaas.http.thread.ReceiveThread;
import io.bcaas.interactor.MainInteractor;
import io.bcaas.listener.TCPReceiveBlockListener;
import io.bcaas.ui.contracts.MainContracts;
import io.bcaas.utils.GsonU;
import io.bcaas.utils.L;
import io.bcaas.utils.ListU;
import io.bcaas.utils.StringU;
import io.bcaas.utils.WalletU;
import io.bcaas.vo.ClientIpInfoVO;
import io.bcaas.vo.DatabaseVO;
import io.bcaas.vo.GenesisVO;
import io.bcaas.vo.PaginationVO;
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
public class MainPresenterImp extends BasePresenterImp
        implements MainContracts.Presenter {
    private MainContracts.View view;
    private MainInteractor mainInteractor;

    public MainPresenterImp(MainContracts.View view) {
        super();
        this.view = view;
        mainInteractor = new MainInteractor();
    }

    @Override
    public void getWalletWaitingToReceiveBlock() {
        RequestJson requestJson = new RequestJson();
        WalletInfo walletInfo = getWalletInfo();
        if (walletInfo == null) {
            view.failure(context.getString(R.string.walletdata_failure));
            return;
        }
        WalletVO walletVO = new WalletVO(walletInfo.getBitcoinAddressStr(), walletInfo.getBlockService(), walletInfo.getAccessToken());
        requestJson.setWalletVO(walletVO);
        mainInteractor.getWalletWaitingToReceiveBlock(GsonU.beanToRequestBody(requestJson),
                new Callback<ResponseJson>() {
                    @Override
                    public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                        L.d("getWalletWaitingToReceiveBlock==>onResponse" + response.body());
                        ResponseJson walletResponseJson = response.body();
                        if (walletResponseJson.isSuccess()) {
                            view.responseSuccess();
                        } else {
                            view.failure(walletResponseJson.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseJson> call, Throwable t) {
                        L.d("getWalletWaitingToReceiveBlock==>onFailure" + t.getMessage());
                        // TODO: 2018/8/21 如果当前AN的接口请求不通过的时候，应该重新去SFN拉取新AN的数据
                        view.failure(t.getMessage());
                        resetAuthNodeInfo();

                    }
                });
    }

    @Override
    public void resetAuthNodeInfo() {
        WalletVO walletVO = WalletU.infoToVo(getWalletInfo());
        RequestJson walletVoRequestJson = new RequestJson(walletVO);
        L.d("resetAuthNodeInfo", walletVoRequestJson);
        mainInteractor.resetAuthNode(GsonU.beanToRequestBody(walletVoRequestJson), new Callback<ResponseJson>() {
            @Override
            public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                L.d("resetAuthNodeInfo", response.body());
                ResponseJson walletVoResponseJson = response.body();
                if (walletVoResponseJson.isSuccess()) {
                    getANAddress(walletVoResponseJson.getWalletVO());
                }
            }

            @Override
            public void onFailure(Call<ResponseJson> call, Throwable t) {
                view.resetAuthNodeFailure(t.getMessage());
            }
        });
    }

    private void getANAddress(WalletVO walletVO) {
        if (walletVO == null) {
            view.failure(context.getString(R.string.null_wallet));
            return;
        }
        ClientIpInfoVO clientIpInfoVO = walletVO.getClientIpInfoVO();
        if (clientIpInfoVO == null) {
            view.failure(context.getString(R.string.null_wallet));
            return;
        }
        L.d("getANAddress", clientIpInfoVO);
        //1:遍历得到数据库ANClientIpInfo里面的数据
        //2：根据钱包地址得到与之匹配的AN ip信息
        //3：组装Ip+port，以备An访问
        //4：重新登入以及reset之后需要重新存储
        BcaasApplication.setClientIpInfoVO(clientIpInfoVO);
        // TODO: 2018/8/21 是否自己的实体类可以替代数据库的实体类 ？
        // TODO: 2018/8/21 暂时先存储需要的两个参数，到时候需要再添加
        ANClientIpInfo anClientIpInfo = new ANClientIpInfo();
        anClientIpInfo.setInternalIp(clientIpInfoVO.getInternalIp());
        anClientIpInfo.setExternalIp(clientIpInfoVO.getExternalIp());
        anClientIpInfo.setExternalPort(clientIpInfoVO.getExternalPort());
        anClientIpInfo.setRpcPort(clientIpInfoVO.getRpcPort());
        anClientIpInfo.setInternalPort(clientIpInfoVO.getInternalPort());
        clientIpInfoDao.insert(anClientIpInfo);
        view.resetAuthNodeSuccess();

    }

    @Override
    public void checkANClientIPInfo(String from) {
        L.d("checkANClientIPInfo", from);
        //根据当前的进入方式去检查此钱包的AN访问地址
        if (StringU.isEmpty(from)) {
            return;
        }
        if (StringU.equals(from, Constants.ValueMaps.FROM_BRAND)) {
            //如果当前用户是直接进入的，那么需要从数据库里面拿到之前存储的AN请求IP
            List<ANClientIpInfo> clientIpInfos = clientIpInfoDao.queryBuilder().list();
            if (ListU.isEmpty(clientIpInfos)) {
                //没有数据，需要重新reset
                view.noAnClientInfo();
            } else {
                ClientIpInfoVO clientIpInfoVO = new ClientIpInfoVO();
                for (ANClientIpInfo anClientIpInfo : clientIpInfos) {
                    L.d(anClientIpInfo);
                }
                // TODO: 2018/8/21 暂时取第一条数据
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

        WalletVO walletVO = new WalletVO(walletInfo.getBitcoinAddressStr(), walletInfo.getBlockService(), walletInfo.getAccessToken());
        RequestJson requestJson = new RequestJson(walletVO);
        String json = GsonU.encodeToString(requestJson);
        String ip = BcaasApplication.getExternalIp();
        int port = BcaasApplication.getExternalPort();
        ReceiveThread sendActionThread = new ReceiveThread(ip, port, json + "\n", tcpReceiveBlockListener);
        sendActionThread.start();

    }

    //监听Tcp数据返回
    TCPReceiveBlockListener tcpReceiveBlockListener = new TCPReceiveBlockListener() {
        @Override
        public void httpToRequestReceiverBlock() {
            getWalletWaitingToReceiveBlock();
        }

        @Override
        public void receiveBlockData(String data) {
            L.d("tcpReceiveBlockListener", data);
            Gson gson = new Gson();
            if (StringU.notEmpty(data)) {
                ResponseJson responseJson = gson.fromJson(data, ResponseJson.class);
                L.d(responseJson);
                DatabaseVO databaseVO = responseJson.getDatabaseVO();
                if (databaseVO == null) return;
                GenesisVO genesisVO = databaseVO.getGenesisVO();
                //如果「genesisVO」此区块有数据，那就是「open」区块，否则是「receive」区块
                if (genesisVO == null) {
                } else {

                }
                //得到尚未产生的Receiver区块
                List<PaginationVO> paginationVOList = responseJson.getPaginationVOList();
                if (ListU.isEmpty(paginationVOList)) {
                    return;
                } else {
                    //遍历每一条数据，然后对每一条数据进行签章，然后方给服务器
                    view.showPaginationVoList(paginationVOList);
                }

            }

        }

        @Override
        public void tcpConnectFailure(String message) {
            // TODO: 2018/8/21 TCP 连接异常，发起重新连接？
            L.e(message);
        }
    };
}
