package io.bcaas.ui.contracts;


import io.bcaas.db.vo.AddressVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/17
 * 連接界面和數據操作互動：「新增地址」
 */
public interface InsertAddressContract {

    interface View extends BaseContract.View {
        void saveDataSuccess();
        void saveDataFailure();
        void addressRepeat();
    }

    interface Presenter{
        void saveData(AddressVO addressVO);
    }
}
