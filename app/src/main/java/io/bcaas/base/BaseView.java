package io.bcaas.base;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/17
 */
public interface BaseView {
    void showLoadingDialog(String loading);

    void hideLoadingDialog();

    void failure(String message);

    void success(String message);

}
