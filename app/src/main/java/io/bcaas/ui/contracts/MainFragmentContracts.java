package io.bcaas.ui.contracts;

import java.util.List;

import io.bcaas.vo.PublicUnitVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/21
 * 連接界面和數據操作互動：「首頁Fragment」
 */
public interface MainFragmentContracts {
    interface View extends BaseContract.View {
        //获取交易记录失败
        void getAccountDoneTCFailure(String message);

        //获取交易记录成功
        void getAccountDoneTCSuccess(List<Object> objectList);

        //没有交易记录
        void noAccountDoneTC();

        //當前點擊更多
        void getNextObjectId(String nextObjectId);
    }

    interface Presenter extends BaseContract.Presenter {
        //获取账户已完成交易
        void getAccountTransactions(String nextObjectId,boolean isDone);

    }
}

