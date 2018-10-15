package io.bcaas.ui.contracts;

import java.util.List;

import io.bcaas.vo.PublicUnitVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/21
 */
public interface BlockServiceContracts {

    interface View extends BaseContract.View {
        void getBlockServicesListSuccess(List<PublicUnitVO> publicUnitVOList);//獲取清單文件成功

        void getBlockServicesListFailure();//獲取清單文件失败

        void noBlockServicesList();// 沒有可顯示的幣種
    }

    interface Presenter {
        //獲取幣種清單
        void getBlockServiceList();
    }
}
