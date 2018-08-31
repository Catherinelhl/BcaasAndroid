package io.bcaas.listener;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/31
 * 用於監聽密碼輸入框的監聽
 */
public interface PasswordWatcherListener {
    void onComplete(String password);
}
