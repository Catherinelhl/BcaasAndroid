package io.bcaas.event;

import java.util.List;

import io.bcaas.vo.TransactionChainVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/23
 * <p>
 * 更新首页待签章区块
 */
public class UpdateTransactionData {

    private List<TransactionChainVO> transactionChainVOList;

    public UpdateTransactionData(List<TransactionChainVO> transactionChainVOList) {
        this.transactionChainVOList = transactionChainVOList;
    }

    public List<TransactionChainVO> getTransactionChainVOList() {
        return transactionChainVOList;
    }
}
