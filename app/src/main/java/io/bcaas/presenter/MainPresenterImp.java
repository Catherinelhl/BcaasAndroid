package io.bcaas.presenter;

import com.google.gson.Gson;

import java.util.List;

import io.bcaas.R;
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
import io.bcaas.interactor.MainInteractor;
import io.bcaas.listener.TCPReceiveBlockListener;
import io.bcaas.ui.contracts.MainContracts;
import io.bcaas.tools.GsonTool;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.WalletTool;
import io.bcaas.vo.ClientIpInfoVO;
import io.bcaas.vo.GenesisVO;
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
        WalletRequestJson walletRequestJson = new WalletRequestJson();
        WalletInfo walletInfo = getWalletInfo();
        if (walletInfo == null) {
            view.failure(context.getString(R.string.walletdata_failure));
            return;
        }
        walletRequestJson.setWalletAddress(walletInfo.getBitcoinAddressStr());
        walletRequestJson.setBlockService(walletInfo.getBlockService());
        walletRequestJson.setAccessToken(walletInfo.getAccessToken());
        mainInteractor.getWalletWaitingToReceiveBlock(GsonTool.beanToRequestBody(walletRequestJson),
                new Callback<WalletResponseJson>() {
                    @Override
                    public void onResponse(Call<WalletResponseJson> call, Response<WalletResponseJson> response) {
                        BcaasLog.d("getWalletWaitingToReceiveBlock==>onResponse" + response.body());
                        WalletResponseJson walletResponseJson = response.body();
                        if (walletResponseJson.isSuccess()) {
                            view.responseSuccess();
                        } else {
                            view.failure(walletResponseJson.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<WalletResponseJson> call, Throwable t) {
                        BcaasLog.d("getWalletWaitingToReceiveBlock==>onFailure" + t.getMessage());
                        // TODO: 2018/8/21 如果当前AN的接口请求不通过的时候，应该重新去SFN拉取新AN的数据
                        view.failure(t.getMessage());
                        resetAuthNodeInfo();

                    }
                });
    }

    @Override
    public void resetAuthNodeInfo() {
        WalletVO walletVO = WalletTool.infoToVo(getWalletInfo());
        WalletVoRequestJson walletVoRequestJson = new WalletVoRequestJson(walletVO);
        BcaasLog.d("resetAuthNodeInfo", walletVoRequestJson);
        mainInteractor.resetAuthNode(GsonTool.beanToRequestBody(walletVoRequestJson), new Callback<WalletVoResponseJson>() {
            @Override
            public void onResponse(Call<WalletVoResponseJson> call, Response<WalletVoResponseJson> response) {
                BcaasLog.d("resetAuthNodeInfo", response.body());
                WalletVoResponseJson walletVoResponseJson = response.body();
                if (walletVoResponseJson.getSuccess()) {
                    getANAddress(walletVoResponseJson.getWalletVO());
                }
            }

            @Override
            public void onFailure(Call<WalletVoResponseJson> call, Throwable t) {
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
        BcaasLog.d("getANAddress", clientIpInfoVO);
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
        BcaasLog.d("checkANClientIPInfo", from);
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
                    BcaasLog.d(anClientIpInfo);
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
        WalletRequestJson walletRequestJson = new WalletRequestJson(walletInfo.getAccessToken(),
                walletInfo.getBlockService(), walletInfo.getBitcoinAddressStr());
        String json = GsonTool.encodeToString(walletRequestJson);
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
            Gson gson = new Gson();
            if (StringTool.notEmpty(data)) {
                WalletResponseJson walletResponseJson = gson.fromJson(data, WalletResponseJson.class);
                BcaasLog.d(walletResponseJson);
                GenesisVO genesisVO = walletResponseJson.getGenesisVO();
                //如果「genesisVO」此区块有数据，那就是「open」区块，否则是「receive」区块
                if (genesisVO == null) {
                } else {

                }
                //得到尚未产生的Receiver区块
                List<PaginationVO> paginationVOList = walletResponseJson.getPaginationVOList();
                if (ListTool.isEmpty(paginationVOList)) {
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
            view.onTip(message);
        }
    };

    @Override
    public void signatureReceiveBlock(PaginationVO paginationVO) {
        WalletInfo walletInfo = getWalletInfo();//取得当前的钱包信息
        if (walletInfo == null) {
            view.onTip(context.getString(R.string.wallet_exception));
            return;
        }
        WalletRequestJson walletRequestJson = new WalletRequestJson();
        walletRequestJson.setAccessToken(walletInfo.getAccessToken());
        walletRequestJson.setBlockService(walletInfo.getBlockService());
        walletRequestJson.setWalletAddress(walletInfo.getBitcoinAddressStr());
        TransactionChainVO transactionChainVO=new TransactionChainVO();
//        transactionChainVO.setSignature();//将TC层double sha 256 然后用私钥加密
//        transactionChainVO.setPublicKey();
//        walletRequestJson.setTransactionChainVO();
    }
}
