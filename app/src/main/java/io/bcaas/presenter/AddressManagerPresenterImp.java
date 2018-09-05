package io.bcaas.presenter;


import java.util.List;

import io.bcaas.base.BasePresenterImp;
import io.bcaas.db.vo.AddressVO;
import io.bcaas.ui.contracts.AddressManagerContract;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 */
public class AddressManagerPresenterImp extends BasePresenterImp
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
            view.noData();
        } else {
            if (addressVOBeans.size() == 0) {
                view.noData();
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
            view.noData();
        } else {
            if (addressVOList.size() == 0) {
                view.noData();
            } else {
                view.getAddresses(addressVOList);

            }
        }
    }
}
