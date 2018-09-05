package io.bcaas.ui.contracts;


import java.util.List;

import io.bcaas.db.vo.AddressVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 *
 */
public interface AddressManagerContract {
    interface View{
        void getAddresses(List<AddressVO> addressVOS);
        void noData();
    }
    interface Presenter{
        void queryAllAddresses();
        void deleteSingleAddress(AddressVO addressVOBean);
    }
}
