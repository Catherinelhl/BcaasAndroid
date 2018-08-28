package io.bcaas.base;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

import io.bcaas.database.ANClientIpInfoDao;
import io.bcaas.database.Address;
import io.bcaas.database.AddressDao;
import io.bcaas.database.DaoSession;
import io.bcaas.database.WalletInfo;
import io.bcaas.database.WalletInfoDao;
import io.bcaas.tools.BcaasLog;
import io.bcaas.vo.ClientIpInfoVO;
import io.bcaas.vo.WalletVO;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/17
 */
public abstract class BasePresenterImp {
    private String TAG = "BasePresenterImp";
    protected Context context;
    protected WalletInfoDao walletInfoDao;//钱包信息数据库
    protected AddressDao addressDao;//地址管理数据库

    public BasePresenterImp() {
        context = BcaasApplication.context();
        initDaoData();

    }

    private void initDaoData() {
        DaoSession session = BcaasApplication.getDaoSession();
        walletInfoDao = session.getWalletInfoDao();
        addressDao = session.getAddressDao();
    }

    protected WalletInfo getWalletInfo() {
        return BcaasApplication.getWalletInfo();
    }

    /*存储当前在线钱包信息的转换口*/
    protected void saveWalletInfo(WalletVO walletVO) {
        if (walletVO == null) {
            return;
        }
        ClientIpInfoVO clientIpInfoVO = walletVO.getClientIpInfoVO();
        if (clientIpInfoVO != null) {
            BcaasLog.d(TAG, clientIpInfoVO);
            BcaasApplication.setClientIpInfoVO(clientIpInfoVO);
        }
        WalletInfo walletInfo = BcaasApplication.getWalletInfo();
        BcaasApplication.setAccessToken(walletVO.getAccessToken());
        walletInfo.setBitcoinAddressStr(walletVO.getWalletAddress());
        BcaasApplication.setWalletInfo(walletInfo);
    }

    protected String getString(int resId) {
        return context.getString(resId);
    }

    protected Resources getString() {
        return context.getResources();
    }

    protected AssetManager getAssets() {
        return context.getAssets();
    }


    //得到本地存储的钱包信息
    protected List<WalletInfo> getWalletDataFromDB() {
        return walletInfoDao == null ? new ArrayList<WalletInfo>() : walletInfoDao.queryBuilder().list();
    }

    /*得到存储的所有的钱包信息*/
    protected List<Address> getWalletsAddressesFromDB() {
        return addressDao == null ? new ArrayList<Address>() : addressDao.queryBuilder().list();
    }

    //向数据库里面插入新添加的地址信息
    protected void insertAddressDataTODB(Address address) {
        if (addressDao == null) return;
        addressDao.insert(address);
    }

    //从数据库里面删除相对应的地址信息
    protected void deleteAddressDataFromDB(Address address) {
        if (addressDao == null) return;
        addressDao.delete(address);

    }
}
