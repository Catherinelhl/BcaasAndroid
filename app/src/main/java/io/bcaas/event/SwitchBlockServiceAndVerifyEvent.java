package io.bcaas.event;

/**
 * @author catherine.brainwilliam
 * @since 2018/09/06
 * <p>
 * 切换当前的区块服务并且更新，重新验证
 */
public class SwitchBlockServiceAndVerifyEvent {

    private boolean isVerify;//是否验证区块服务
    private boolean isRefreshTransactionRecord;//是否更新交易记录

    public SwitchBlockServiceAndVerifyEvent(boolean isVerify, boolean isRefreshTransactionRecord) {
        super();
        this.isVerify = isVerify;
        this.isRefreshTransactionRecord = isRefreshTransactionRecord;
    }

    public boolean isRefreshTransactionRecord() {
        return isRefreshTransactionRecord;
    }

    public void setRefreshTransactionRecord(boolean refreshTransactionRecord) {
        isRefreshTransactionRecord = refreshTransactionRecord;
    }

    public boolean isVerify() {
        return isVerify;
    }

    public void setVerify(boolean verify) {
        isVerify = verify;
    }
}
