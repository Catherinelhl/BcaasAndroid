package io.bcaas.listener;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 * 选择器监听
 */
public interface OnItemSelectListener {
    <T> void onItemSelect(T type);

    // 是否改变了item的选择
    void changeItem(boolean isChange);

}
