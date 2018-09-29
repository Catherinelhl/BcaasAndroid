package io.bcaas.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.bcaas.constants.MessageConstants;
import io.bcaas.tools.LogTool;

public class LanguageReceiver extends BroadcastReceiver {
    private String TAG = LanguageReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_LOCALE_CHANGED)) {
            LogTool.d(TAG, MessageConstants.LANGUAGE_SWITCH);
            //这里就可以全局获取判断更换语言
        }
    }
}
