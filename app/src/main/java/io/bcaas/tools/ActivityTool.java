package io.bcaas.tools;

import android.app.Activity;

import java.util.HashMap;
import java.util.Map;

import io.bcaas.base.BcaasApplication;
import io.bcaas.http.tcp.ReceiveThread;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/31
 * 用來管理當前啟動的Activity
 */
public class ActivityTool {
    private String TAG = ActivityTool.class.getSimpleName();

    private static ActivityTool activityTool;
    private Map<String, Activity> activityMap = new HashMap<>();//用來存儲加入的activity

    public static ActivityTool getInstance() {
        if (activityTool == null) {
            activityTool = new ActivityTool();

        }
        return activityTool;
    }

    /**
     * 保存指定key值的activity（activity启动时调用）
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        String key = String.valueOf(System.currentTimeMillis());
        if (activityMap.get(key) == null) {
            activityMap.put(key, activity);
        }
    }

    /**
     * 移除指定key值的activity （activity关闭时调用）
     */
    public void removeActivity(Activity activity) {
        String key = String.valueOf(System.currentTimeMillis());
        if (activity != null) {
            if (activity.isDestroyed() || activity.isFinishing()) {
                activityMap.remove(key);
                return;
            }
            activity.finish();
            activityMap.remove(key);
        }
    }

    /**
     * 移除所有的Activity
     */
    public void removeAllActivity() {
        for (Map.Entry<String, Activity> entry : activityMap.entrySet()) {
            Activity activity = entry.getValue();
            finishAty(activity);
        }
    }

    private void finishAty(Activity aty) {
        if (aty != null && !aty.isFinishing()) {
            aty.finish();
        }
    }

    public void exit() {
        ReceiveThread.stopSocket = true;
        BcaasApplication.setKeepHttpRequest(false);
        ReceiveThread.kill();
//        MobclickAgent.onKillProcess(context());
        killProcess();
        System.exit(0);
    }

    public void killProcess() {
        removeAllActivity();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
