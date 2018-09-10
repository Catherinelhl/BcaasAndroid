package io.bcaas.ui.contracts;

import io.bcaas.gson.ResponseJson;
import io.bcaas.vo.WalletVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/25
 */
public interface BaseContract {
    //基本页面
    interface View {
        void showLoadingDialog();

        void hideLoadingDialog();

        void httpExceptionStatus(ResponseJson responseJson);

        void failure(String message);

        void success(String message);

        void noNetWork();
    }

    interface Presenter {
    }

    interface HttpPresenter {
        void toLogin();

        void checkVerify();

        void onResetAuthNodeInfo();

        void getLatestBlockAndBalance();


    }

    //网络请求
    interface HttpView extends LoginContracts.View {
        void httpGetLatestBlockAndBalanceSuccess();//http请求R成功

        void httpGetLatestBlockAndBalanceFailure();//http请求R失败

        void resetAuthNodeFailure(String message);//重设AN失败

        void resetAuthNodeSuccess();//重设AN成功

        void noData();//没有数据

        void responseDataError();

        void noNetWork();
    }


}
