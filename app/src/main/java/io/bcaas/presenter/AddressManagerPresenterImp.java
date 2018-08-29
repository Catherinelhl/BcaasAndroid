package io.bcaas.presenter;


import java.util.List;

import io.bcaas.base.BasePresenterImp;
import io.bcaas.db.vo.Address;
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
        List<Address> addressBeans = getWalletsAddressesFromDB();
        if (addressBeans == null) {
            view.noData();
        } else {
            if (addressBeans.size() == 0) {
                view.noData();
            } else {
                view.getAddresses(addressBeans);

            }
        }
    }

    @Override
    public void deleteSingleAddress(Address addressBean) {
        deleteAddressDataFromDB(addressBean);
        List<Address> addressList = getWalletsAddressesFromDB();
        if (addressList == null) {
            view.noData();
        } else {
            if (addressList.size() == 0) {
                view.noData();
            } else {
                view.getAddresses(addressList);

            }
        }
    }
}
