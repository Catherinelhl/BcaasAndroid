package io.bcaas.ui.contracts;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/20
 */
public interface VerifyContracts {

    interface View extends BaseContract.View {
        //验证通过
        void verifySuccess(boolean isReset);

        //验证失败
        void verifyFailure();

        void passwordError();
    }
}
