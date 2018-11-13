package io.bcaas.event;

/**
 * @author catherine.brainwilliam
 * @since 2018/09/06
 * <p>
 * 發出此訂閱：切换當前Wallet的区块服务通知訂閱此事件的地方更新界面並且開始「Verify」，驗證Token以及拿取新的SAN信息
 */
public class SwitchBlockServiceAndVerifyEvent {

    private int tabPosition;//当前首页选中的tab

    private boolean isVerify;//是否验证区块服务
    private boolean isRefreshTransactionRecord;//是否更新交易记录

    public SwitchBlockServiceAndVerifyEvent(boolean isVerify, boolean isRefreshTransactionRecord) {
        super();
        this.isVerify = isVerify;
        this.isRefreshTransactionRecord = isRefreshTransactionRecord;
    }

    public SwitchBlockServiceAndVerifyEvent(boolean isVerify, boolean isRefreshTransactionRecord, int tabPosition) {
        super();
        this.isVerify = isVerify;
        this.isRefreshTransactionRecord = isRefreshTransactionRecord;
        this.tabPosition = tabPosition;
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

    public int getTabPosition() {
        return tabPosition;
    }

    public void setTabPosition(int tabPosition) {
        this.tabPosition = tabPosition;
    }
}
