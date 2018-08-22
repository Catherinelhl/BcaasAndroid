package io.bcaas.ui.contracts;


import io.bcaas.base.BaseView;
import io.bcaas.database.Address;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/17
 * 新增地址
 */
public interface InsertAddressContract {

    interface View extends BaseView {
        void saveDataSuccess();
        void saveDataFailure();
    }

    interface Presenter{
        void saveData(Address address);
    }
}
