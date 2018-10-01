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

        void failure(String message);

        void success(String message);

        //连接失败，请检查网路
        void connectFailure();

        void noNetWork();
    }

    interface Presenter {
    }

    interface HttpPresenter {
        void checkVerify();

        void onResetAuthNodeInfo();

        void stopTCP();

        void getLatestBlockAndBalance();

        void startToGetWalletWaitingToReceiveBlockLoop();

        void removeGetWalletWaitingToReceiveBlockRunnable();


    }

    //网络请求
    interface HttpView extends VerifyContracts.View {
        void httpGetWalletWaitingToReceiveBlockSuccess();//http请求R成功

        void httpGetWalletWaitingToReceiveBlockFailure();//http请求R失败

        void httpGetLastestBlockAndBalanceSuccess();//http请求最新余额成功

        void httpGetLastestBlockAndBalanceFailure();//http请求最新余额失败

        //获取余额成功
        void getBalanceSuccess();

        //获取余额失败
        void getBalanceFailure();

        void resetAuthNodeFailure(String message);//重设AN失败

        void resetAuthNodeSuccess();//重设AN成功

        void noData();//没有数据

        void responseDataError();

        void noNetWork();
    }


}
