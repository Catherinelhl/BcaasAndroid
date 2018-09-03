package io.bcaas.listener;

import java.util.List;

import io.bcaas.vo.PublicUnitVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/3
 * <p>
 * 刷新Fragment的监听
 */
public interface RefreshFragmentListener {
    void refreshBlockService(List<PublicUnitVO> publicUnitVOS);
}
