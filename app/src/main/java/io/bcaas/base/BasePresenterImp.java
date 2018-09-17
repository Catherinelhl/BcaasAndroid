package io.bcaas.base;

import android.content.Context;
import android.content.res.AssetManager;

import io.bcaas.tools.LogTool;
import io.bcaas.vo.ClientIpInfoVO;
import io.bcaas.vo.WalletVO;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/17
 * presenter的基类，用于统一presenter都会用到的逻辑
 */
public abstract class BasePresenterImp {
    private String TAG = BasePresenterImp.class.getSimpleName();
    protected Context context;


    public BasePresenterImp() {
        context = BcaasApplication.context();
    }

    /*存储当前新请求到的AN信息*/
    protected void updateClientIpInfoVO(WalletVO walletVO) {
        if (walletVO == null) {
            return;
        }
        ClientIpInfoVO clientIpInfoVO = walletVO.getClientIpInfoVO();
        BcaasApplication.setWalletExternalIp(walletVO.getWalletExternalIp());
        if (clientIpInfoVO != null) {
            LogTool.d(TAG, clientIpInfoVO);
            BcaasApplication.setClientIpInfoVO(clientIpInfoVO);
        }
    }
}
