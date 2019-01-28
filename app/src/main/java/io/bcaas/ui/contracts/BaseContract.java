package io.bcaas.ui.contracts;

import io.bcaas.gson.ResponseJson;
import io.bcaas.vo.WalletVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/25
 * 連接界面和數據操作互動：所有界面操作互動著的基類，一些普遍的方法定義在此
 */
public interface BaseContract {
    //基本页面
    interface View {
        void showLoading();

        void hideLoading();

        void httpExceptionStatus(ResponseJson responseJson);

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
    interface HttpView extends BaseContract.View {
        void httpGetLastestBlockAndBalanceSuccess();//http请求最新余额成功

        void httpGetLastestBlockAndBalanceFailure();//http请求最新余额失败

        void resetAuthNodeFailure(String message, String from);//重设AN失败

        void resetAuthNodeSuccess(String from);//重设AN成功

        void verifySuccessAndResetAuthNode(String from);//verify成功，并且重设SAN


        void noNetWork();

        //验证通过
        void verifySuccess(String from);

        //验证失败
        void verifyFailure(String from);
    }


}
