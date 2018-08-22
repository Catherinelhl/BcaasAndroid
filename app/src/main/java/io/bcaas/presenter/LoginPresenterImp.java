package io.bcaas.presenter;

import com.google.gson.Gson;

import java.util.List;

import io.bcaas.R;
import io.bcaas.base.BasePresenterImp;
import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.database.ANClientIpInfo;
import io.bcaas.database.WalletInfo;
import io.bcaas.encryption.AES;
import io.bcaas.gson.WalletVoResponseJson;
import io.bcaas.interactor.LoginInteractor;
import io.bcaas.ui.contracts.LoginContracts;
import io.bcaas.tools.GsonTool;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.StringTool;
import io.bcaas.vo.ClientIpInfoVO;
import io.bcaas.vo.WalletVO;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/20
 * <p>
 * 1：查询当前本地数据库，如果没有钱包数据，代表没有可解锁的钱包，提示用户创建钱包/导入钱包
 * 2：如果当前有钱包数据，然后拿到是否有「accessToken」字段，如果没有，那么就进行网络请求，进行「登入」的操作，拿到返回的数据
 * 4：得到钱包登入「accessToken」，存储到当前用户下，然后以此来判断是否需要重新「登入」
 * 5：如果有「accessToken」那么就直接进入首页
 * todo  貌似判断Token是否存在应该在Brand页面去检测
 */
public class LoginPresenterImp extends BasePresenterImp
        implements LoginContracts.Presenter {

    private String TAG = "LoginPresenterImp";
    private LoginContracts.View view;
    private LoginInteractor loginInteractor;

    public LoginPresenterImp(LoginContracts.View view) {
        super();
        this.view = view;
        loginInteractor = new LoginInteractor();

    }


    @Override
    public void queryWalletInfo(String password) {
        List<WalletInfo> walletInfos = getAllWallets();
        if (ListTool.isEmpty(walletInfos)) {
            view.noWalletInfo();
        } else {
            WalletInfo wallet = walletInfos.get(0);//得到当前的钱包
            // TODO: 2018/8/21 当前App可以有多个钱包在线么？还是保持唯一
            for (WalletInfo walletInfo : walletInfos) {
                //todo  如果当前可以多个账户存在，要这样去遍历得到账户？不对，这样输入错误的密码不就不知道了。
                if (StringTool.equals(walletInfo.getPassword(), password)) {
                    wallet = walletInfo;
                    BcaasLog.d(TAG,"登入的钱包是==》" + wallet);

                }
            }
            String walletAddress = wallet.getBitcoinAddressStr();
            String blockService = wallet.getBlockService();
            if (StringTool.isEmpty(blockService) || StringTool.isEmpty(walletAddress)) {
                //TODO 对当前的参数进行判空「自定义弹框」
                //检查到当前数据库没有钱包地址数据，那么需要提示用户先创建或者导入钱包
                view.loginFailure(context.getString(R.string.localdata_exception));
            } else {
                WalletVO walletVO = new WalletVO();
//                walletVO.setBlockService(blockService); //08-21 「登入」去掉此参数
                walletVO.setWalletAddress(walletAddress);
                login(walletVO);
            }

        }

    }

    //得到所有得钱包信息
    private List<WalletInfo> getAllWallets() {
        if (walletInfoDao == null) {
            throw new NullPointerException("walletInfoDao is null");
        }
        return walletInfoDao.queryBuilder().list();
    }

    @Override
    public void login(WalletVO walletVO) {
        final String json = GsonTool.encodeToString(walletVO);
        BcaasLog.d(TAG, json);
        try {
            RequestBody body = GsonTool.beanToRequestBody(walletVO);
            loginInteractor.login(body, new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Gson gson = new Gson();
                    WalletVoResponseJson walletVOResponse = gson.fromJson(response.body(), WalletVoResponseJson.class);
                    BcaasLog.d(TAG, walletVOResponse);
                    if (walletVOResponse.getSuccess()) {
                        parseData(walletVOResponse.getWalletVO());
                    } else {
                        view.loginFailure(response.message());
                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    view.loginFailure(t.getMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void parseData(WalletVO walletVO) {
        //得到当前回传的信息，存储当前的accessToken
        if (walletVO == null) {
            throw new NullPointerException(" loginPresenterImp parseData walletVO is null");
        }
        /*2018-08-21 修改「登入」接口数据，不予返回AN的连接信息，即：clientIpInfoVO。*/
//        getANAddress(walletVO);
        String accessToken = walletVO.getAccessToken();
        walletVO.setBlockService(Constants.BlockService.BCC);
        BcaasLog.d(TAG, accessToken);
        if (StringTool.isEmpty(accessToken)) {
            view.loginFailure(getString(R.string.login_failure));
        } else {
            saveWalletInfo(walletVO);
            // TODO: 2018/8/20 存储当前的token，具体存储方式待跟进
            updateWalletData(accessToken);
        }
    }

    private void getANAddress(WalletVO walletVO) {
        if (walletVO == null) return;
        ClientIpInfoVO clientIpInfoVO = walletVO.getClientIpInfoVO();
        BcaasLog.d("getANAddress", clientIpInfoVO);
        // TODO: 2018/8/21 暂时先存储需要的两个参数，到时候需要再添加
        ANClientIpInfo anClientIpInfo = new ANClientIpInfo();
        anClientIpInfo.setInternalIp(clientIpInfoVO.getInternalIp());
        anClientIpInfo.setExternalIp(clientIpInfoVO.getExternalIp());
        anClientIpInfo.setExternalPort(clientIpInfoVO.getExternalPort());
        anClientIpInfo.setRpcPort(clientIpInfoVO.getRpcPort());
        anClientIpInfo.setInternalPort(clientIpInfoVO.getInternalPort());
        clientIpInfoDao.insert(anClientIpInfo);
        BcaasApplication.setClientIpInfoVO(clientIpInfoVO);
    }

    //更新钱包信息
    private void updateWalletData(String accessToken) {
        List<WalletInfo> walletInfos = getAllWallets();
        if (ListTool.isEmpty(walletInfos)) {
            view.noWalletInfo();
        } else {
            WalletInfo wallet = walletInfos.get(0);
            wallet.setAccessToken(accessToken);
            walletInfoDao.update(wallet);
            view.loginSuccess();

        }
    }
}
