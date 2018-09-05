package io.bcaas.base;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.util.List;

import io.bcaas.db.vo.Address;
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
    protected List<Address> getWalletsAddressesFromDB() {
        if (BcaasApplication.bcaasDBHelper != null) {
            List<Address> addresses = BcaasApplication.bcaasDBHelper.queryAddress();
            return addresses;
        }
        return null;
    }

    //向数据库里面插入新添加的地址信息
    protected void insertAddressDataTODB(Address address) {
        if (address == null) {
            return;
        }
        if (BcaasApplication.bcaasDBHelper != null) {
            BcaasApplication.bcaasDBHelper.insertAddress(address);
        }
    }

    //从数据库里面删除相对应的地址信息
    protected void deleteAddressDataFromDB(Address address) {
        if (address == null) {
            return;
        }
        if (BcaasApplication.bcaasDBHelper != null) {
            BcaasApplication.bcaasDBHelper.deleteAddress(address.getAddress());
        }
    }
}
