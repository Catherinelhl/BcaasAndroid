package io.bcaas.event;

/**
 * @author catherine.brainwilliam
 * @since 2018/10/23
 * <p>
 * 通知显示成功收款通知
 */
public class ShowNotificationEvent {
    //收到的币种信息
    private String blockService;
    //收到的币种金额
    private String amount;

    public ShowNotificationEvent(String blockService, String amount) {
        super();
        this.blockService = blockService;
        this.amount = amount;
    }

    public String getBlockService() {
        return blockService;
    }

    public void setBlockService(String blockService) {
        this.blockService = blockService;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
