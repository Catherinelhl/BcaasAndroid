package io.bcaas.base;

import android.content.Context;

import io.bcaas.constants.MessageConstants;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.gson.GsonTool;
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
        context = BCAASApplication.context();
    }

    /*存储当前新请求到的AN信息*/
    protected void updateClientIpInfoVO(WalletVO walletVO) {
        if (walletVO == null) {
            return;
        }
        String content = "{\"macAddressExternalIp\":\"ab61c77b6dcc94ec2f7c24bc6367dd5a0991f48c40ed4d33a810c332d37695bc\",\"externalIp\":\"140.206.56.118\",\"internalIp\":\"192.168.31.5\",\"clientType\":\"AuthNode\",\"externalPort\":45261,\"internalPort\":63068,\"virtualCoin\":[{\"BCC\":\"BCC\",\"COS\":\"COS\"}],\"rpcPort\":54964,\"internalRpcPort\":43802,\"walletAddress\":\"1HdRhxdydbhkZtBgrZpJQsm9eKDbksFDi1\"}";
//        ClientIpInfoVO clientIpInfoVO = walletVO.getClientIpInfoVO();
        ClientIpInfoVO clientIpInfoVO = GsonTool.getGson().fromJson(content, ClientIpInfoVO.class);
        BCAASApplication.setWalletExternalIp(walletVO.getWalletExternalIp());
        LogTool.d(TAG, MessageConstants.UPDATE_CLIENT_IP_INFO);
        LogTool.d(TAG, MessageConstants.NEW_CLIENT_IP_INFO + clientIpInfoVO);
        if (clientIpInfoVO != null) {
            LogTool.d(TAG, clientIpInfoVO);
            BCAASApplication.setClientIpInfoVO(clientIpInfoVO);
        }
    }
}
