package io.bcaas.ui.contracts;

import java.io.File;

/**
 * @projectName: BcaasAndroid
 * @packageName: io.bcaas.ui.contracts
 * @author: catherine
 * @time: 2018/9/11
 * <p>
 * 連接界面和數據操作互動：「检查钱包信息」
 */
public interface CheckWalletInfoContract {

    interface View {
        void getWalletFileSuccess();

        void getWalletFileFailed();

    }

    interface Presenter {
        void getWalletFileFromDB(File file);
    }
}
