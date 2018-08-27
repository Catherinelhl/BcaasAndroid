package io.bcaas.presenter;

import java.util.List;

import io.bcaas.R;
import io.bcaas.base.BaseHttpPresenterImp;
import io.bcaas.base.BcaasApplication;
import io.bcaas.database.WalletInfo;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.StringTool;
import io.bcaas.ui.contracts.BaseContract;
import io.bcaas.ui.contracts.LoginContracts;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/20
 * <p>
 * 登入
 * 1：查询当前本地数据库，如果没有钱包数据，代表没有可解锁的钱包，提示用户创建钱包/导入钱包
 * 2：如果当前有钱包数据，然后拿到是否有「accessToken」字段，如果没有，那么就进行网络请求，进行「登入」的操作，拿到返回的数据
 * 4：得到钱包登入「accessToken」，存储到当前用户下，然后以此来判断是否需要重新「登入」
 * 5：把拿到的钱包信息得到，然后「verify」
 */
public class LoginPresenterImp extends BaseHttpPresenterImp
        implements LoginContracts.Presenter {

    private String TAG = "LoginPresenterImp";
    private LoginContracts.View view;

    public LoginPresenterImp(BaseContract.HttpView view) {
        super(view);
        this.view = view;
    }

    /**
     * 查询当前钱包
     *
     * @param password
     */
    @Override
    public void queryWalletInfoFromDB(String password) {
        //1：查询当前数据库数据
        List<WalletInfo> walletInfo = getWalletDataFromDB();
        if (ListTool.isEmpty(walletInfo)) {
            view.noWalletInfo();
        } else {
            //2：比对当前密码是否正确
            WalletInfo wallet = walletInfo.get(0);//得到当前的钱包
            if (StringTool.equals(BcaasApplication.getPassword(), password)) {
                BcaasLog.d(TAG, "登入的钱包是:" + wallet);
                BcaasApplication.setWalletInfo(wallet);
            } else {
                view.loginFailure(getString(R.string.no_wallet_to_unlock));
            }
            //3：判断当前的钱包地址是否为空
            String walletAddress = wallet.getBitcoinAddressStr();
            if (StringTool.isEmpty(walletAddress)) {
                view.loginFailure(context.getString(R.string.localdata_exception));
            } else {
                //4：开始「登入」
                checkLogin();
            }

        }

    }
}
