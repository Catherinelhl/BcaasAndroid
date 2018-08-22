package io.bcaas.ui.contracts;


import java.util.List;

import io.bcaas.database.Address;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 *
 */
public interface AddressManagerContract {
    interface View{
        void getAddresses(List<Address> addresses);
        void noData();
    }
    interface Presenter{
        void queryAllAddresses();
        void deleteSingleAddress(Address addressBean);
    }
}
