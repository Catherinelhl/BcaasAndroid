package io.bcaas.presenter;


import io.bcaas.base.BCAASApplication;
import io.bcaas.db.vo.AddressVO;
import io.bcaas.ui.contracts.AddressManagerContract;

import java.util.List;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 * Presenter：「地址管理」界面需要的數據獲取&處理
 */
public class AddressManagerPresenterImp
        implements AddressManagerContract.Presenter {

    private AddressManagerContract.View view;

    public AddressManagerPresenterImp(AddressManagerContract.View view) {
        super();
        this.view = view;
    }

    @Override
    public void queryAllAddresses() {
        List<AddressVO> addressVOBeans = getWalletsAddressesFromDB();
        if (addressVOBeans == null) {
            view.noAddress();
        } else {
            if (addressVOBeans.size() == 0) {
                view.noAddress();
            } else {
                view.getAddresses(addressVOBeans);

            }
        }
    }

    @Override
    public void deleteSingleAddress(AddressVO addressVOBean) {
        deleteAddressDataFromDB(addressVOBean);
        List<AddressVO> addressVOList = getWalletsAddressesFromDB();
        if (addressVOList == null) {
            view.noAddress();
        } else {
            if (addressVOList.size() == 0) {
                view.noAddress();
            } else {
                view.getAddresses(addressVOList);

            }
        }
    }

    //从数据库里面删除相对应的地址信息
    protected void deleteAddressDataFromDB(AddressVO addressVO) {
        if (addressVO == null) {
            return;
        }
        if (BCAASApplication.bcaasDBHelper != null) {
            BCAASApplication.bcaasDBHelper.deleteAddress(addressVO.getAddress());
        }
    }

    /*得到存储的所有的钱包信息*/
    protected List<AddressVO> getWalletsAddressesFromDB() {
        if (BCAASApplication.bcaasDBHelper != null) {
            List<AddressVO> addressVOS = BCAASApplication.bcaasDBHelper.queryAddress();
            return addressVOS;
        }
        return null;
    }

}
