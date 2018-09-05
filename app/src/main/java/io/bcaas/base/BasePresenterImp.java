package io.bcaas.base;

import android.content.Context;
import android.content.res.AssetManager;

import java.util.List;

import io.bcaas.db.vo.AddressVO;
import io.bcaas.tools.BcaasLog;
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
        if (clientIpInfoVO != null) {
            BcaasLog.d(TAG, clientIpInfoVO);
            BcaasApplication.setClientIpInfoVO(clientIpInfoVO);
        }
    }

    protected AssetManager getAssets() {
        return context.getAssets();
    }


    /*得到存储的所有的钱包信息*/
    protected List<AddressVO> getWalletsAddressesFromDB() {
        if (BcaasApplication.bcaasDBHelper != null) {
            List<AddressVO> addressVOS = BcaasApplication.bcaasDBHelper.queryAddress();
            return addressVOS;
        }
        return null;
    }

    //向数据库里面插入新添加的地址信息
    protected void insertAddressDataTODB(AddressVO addressVO) {
        if (addressVO == null) {
            return;
        }
        if (BcaasApplication.bcaasDBHelper != null) {
            BcaasApplication.bcaasDBHelper.insertAddress(addressVO);
        }
    }

    //从数据库里面删除相对应的地址信息
    protected void deleteAddressDataFromDB(AddressVO addressVO) {
        if (addressVO == null) {
            return;
        }
        if (BcaasApplication.bcaasDBHelper != null) {
            BcaasApplication.bcaasDBHelper.deleteAddress(addressVO.getAddress());
        }
    }
}
