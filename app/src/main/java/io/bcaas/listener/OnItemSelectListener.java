package io.bcaas.listener;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 * 选择器监听
 */
public interface OnItemSelectListener {
    /**
     * 返回當前選擇欄目的數據類
     *
     * @param from 用於區分「語言切換」、「幣種切換」
     */
    <T> void onItemSelect(T type, String from);

    // 是否改变了item的选择
    void changeItem(boolean isChange);

}
