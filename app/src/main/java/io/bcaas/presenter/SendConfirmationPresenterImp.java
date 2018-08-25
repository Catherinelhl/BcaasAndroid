package io.bcaas.presenter;

import io.bcaas.R;
import io.bcaas.base.BaseHttpPresenterImp;
import io.bcaas.base.BcaasApplication;
import io.bcaas.tools.StringTool;
import io.bcaas.ui.contracts.SendConfirmationContract;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/25
 */
public class SendConfirmationPresenterImp extends BaseHttpPresenterImp
        implements SendConfirmationContract.Presenter {
    private SendConfirmationContract.View view;

    public SendConfirmationPresenterImp(SendConfirmationContract.View view) {
        super(view);
        this.view = view;
    }

    /**
     * 点击「发送」
     *
     * @param passwordInput 用户输入的确认密码
     */
    @Override
    public void sendTransaction(String passwordInput) {
        //1：判断密码是否为空
        if (StringTool.isEmpty(passwordInput)) {
            view.onTip(getString(R.string.input_pwd));
        } else {
            //2:获取到用户的正确密码，判断与当前输入密码是否匹配
            String password = BcaasApplication.getPassword();
            if (StringTool.equals(passwordInput, password)) {
                //3:锁定当前页面
                view.lockView();
                //4:请求SFN的「login」、「verify」接口，返回成功方可进行AN的「获取余额」接口以及「发起交易」
                checkLogin();
                getLatestBlockAndBalance();
            } else {
                view.onTip(getString(R.string.password_error));
            }

        }

    }


}
