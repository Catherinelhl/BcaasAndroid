package io.bcaas.ui.contracts;


import java.util.List;

import io.bcaas.db.vo.Address;

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
