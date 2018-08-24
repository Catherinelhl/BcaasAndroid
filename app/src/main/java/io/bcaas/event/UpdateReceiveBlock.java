package io.bcaas.event;

import java.util.List;

import io.bcaas.vo.TransactionChainVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/23
 *
 * 更新未签章的R区块
 */
public class UpdateReceiveBlock {

    private List<TransactionChainVO> transactionChainVOList ;
    public UpdateReceiveBlock(List<TransactionChainVO> transactionChainVOList ){
        this.transactionChainVOList=transactionChainVOList;
    }

    public List<TransactionChainVO> getTransactionChainVOList() {
        return transactionChainVOList;
    }
}
