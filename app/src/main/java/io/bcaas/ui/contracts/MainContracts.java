package io.bcaas.ui.contracts;

import java.util.List;

import io.bcaas.base.BaseAuthNodeView;
import io.bcaas.base.BaseView;
import io.bcaas.database.WalletInfo;
import io.bcaas.vo.PaginationVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/21
 */
public interface MainContracts {
    interface View extends BaseAuthNodeView {
        void resetAuthNodeFailure(String message);//重设AN失败

        void resetAuthNodeSuccess();//重设AN成功

        void noAnClientInfo();

        void showPaginationVoList(List<PaginationVO> paginationVOList);//显示未产生的R区块
    }

    interface Presenter {
        void onResetAuthNodeInfo();
        void onGetWalletWaitingToReceiveBlock();
        void checkANClientIPInfo(String from);

        void startTCPConnectToGetReceiveBlock();//开始TCP连线，请求未处理的交易

    }
}

