package io.bcaas.ui.contracts;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/20
 */
public interface VerifyContracts {

    interface View extends BaseContract.View {
        void verifySuccess();//验证通过

        void verifyFailure();//验证失败

        void passwordError();
    }
}
