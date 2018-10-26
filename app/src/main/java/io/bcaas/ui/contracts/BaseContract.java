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
        void showLoading();

        void hideLoading();

        void httpExceptionStatus(ResponseJson responseJson);

        void failure(String message, String from);

        void success(String message);

        //连接失败，请检查网路
        void connectFailure();

        void noNetWork();
    }

    interface Presenter {
    }

    interface HttpPresenter {
        /**
         * 檢查驗證
         *
         * @param from 来自于哪个验证
         */
        void checkVerify(String from);

        /*重置AN 信息*/
        void onResetAuthNodeInfo(String from);

        void getLatestBlockAndBalance();
    }

    //网络请求
    interface HttpView extends VerifyContracts.View {
        void httpGetLastestBlockAndBalanceSuccess();//http请求最新余额成功

        void httpGetLastestBlockAndBalanceFailure();//http请求最新余额失败

        void resetAuthNodeFailure(String message, String from);//重设AN失败

        void resetAuthNodeSuccess(String from);//重设AN成功

        void noData();//没有数据

        void responseDataError();

        void noNetWork();
    }


}
