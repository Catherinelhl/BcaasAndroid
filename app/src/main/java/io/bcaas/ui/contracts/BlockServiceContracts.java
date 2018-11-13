package io.bcaas.ui.contracts;

import java.util.List;

import io.bcaas.vo.PublicUnitVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/21
 * 連接界面和數據操作互動：「獲取幣種」
 */
public interface BlockServiceContracts {

    interface View extends BaseContract.View {
        //獲取清單文件成功
        void getBlockServicesListSuccess(String from, List<PublicUnitVO> publicUnitVOList);

        //獲取清單文件失败
        void getBlockServicesListFailure(String from);

        // 沒有可顯示的幣種
        void noBlockServicesList(String from);
    }

    interface Presenter {
        //獲取幣種清單
        void getBlockServiceList(String from);
    }
}
