package io.bcaas.ui.contracts;


import java.util.List;

import io.bcaas.db.vo.AddressVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 * 連接界面和數據操作互動：「地址管理」
 */
public interface AddressManagerContract {
    interface View {
        void getAddresses(List<AddressVO> addressVOS);

        void noAddress();
    }

    interface Presenter {
        void queryAllAddresses();

        void deleteSingleAddress(AddressVO addressVOBean);
    }
}
