package io.bcaas.listener;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/31
 * 回調監聽：用於監聽密碼輸入框的輸入完成
 */
public interface PasswordWatcherListener {
    void onComplete(String password);
}
