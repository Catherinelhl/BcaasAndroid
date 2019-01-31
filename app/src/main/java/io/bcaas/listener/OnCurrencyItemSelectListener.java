package io.bcaas.listener;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 * 回調監聽：币种條目选择器監聽回調響應
 */
public interface OnCurrencyItemSelectListener {
    /**
     * 返回當前選擇欄目的數據類
     * 「幣種切換」
     * //     *
     * //     * @param From     用以区分是从何处点击的币种选择
     * //     * @param isChange 是否改变了item的选择
     * //     *                 , String From, boolean isChange)
     */
    <T> void onItemSelect(T type, String from);

}
