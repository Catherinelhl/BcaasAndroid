package io.bcaas.ui.contracts;

import java.util.List;

import io.bcaas.service.TCPService;
import io.bcaas.vo.PublicUnitVO;
import io.bcaas.vo.TransactionChainVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/21
 */
public interface MainContracts {
    interface View extends BaseContract.HttpView {
        void getBlockServicesListSuccess(List<PublicUnitVO> publicUnitVOList);//獲取清單文件成功

        void noBlockServicesList();// 沒有可顯示的幣種

        void updateVersion(boolean forceUpgrade);//更新版本，是否强制更新呢
    }

    interface Presenter extends BaseContract.HttpPresenter {

        void unSubscribe();

        void getBlockServiceList();//獲取幣種清單

        void checkUpdate();
    }
}

