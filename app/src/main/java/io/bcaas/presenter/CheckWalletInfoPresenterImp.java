package io.bcaas.presenter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.bcaas.base.BCAASApplication;
import io.bcaas.bean.WalletBean;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.ui.contracts.CheckWalletInfoContract;

/**
 * @projectName: BcaasAndroid
 * @packageName: io.bcaas.presenter
 * @author: catherine
 * @time: 2018/9/11
 * Presenter：「錢包信息」界面相關的數據獲取&處理
 */
public class CheckWalletInfoPresenterImp implements CheckWalletInfoContract.Presenter {

    private static String TAG = CheckWalletInfoPresenterImp.class.getSimpleName();
    private CheckWalletInfoContract.View view;

    public CheckWalletInfoPresenterImp(CheckWalletInfoContract.View view) {
        super();
        this.view = view;
    }

    /*将当前钱包信息从数据库取出，然后存储*/
    @Override
    public void getWalletFileFromDB(File file) {
        //1:取出当前数据
        WalletBean walletBean = BCAASApplication.getWalletBean();
        if (walletBean == null) {
            view.getWalletFileFailed();
        } else {
            String keyStore = GsonTool.getGson().toJson(walletBean);
            //2：将数据写入本地文件
            if (writeKeyStoreToFile(keyStore, file)) {
                view.getWalletFileSuccess();
            } else {
                view.getWalletFileFailed();

            }
        }
    }

    /*存储钱包信息*/
    private boolean writeKeyStoreToFile(String keystore, File file) {
        boolean status = false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(keystore.getBytes());
            status = true;
        } catch (IOException e) {
            LogTool.d(TAG, e.getMessage());
            e.printStackTrace();
            status = false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    LogTool.d(TAG, e.getMessage());
                    e.printStackTrace();
                }
            }
            return status;
        }
    }
}