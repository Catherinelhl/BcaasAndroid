package io.bcaas.tools;

import android.annotation.TargetApi;
import android.app.*;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import io.bcaas.R;
import io.bcaas.base.BCAASApplication;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * @author catherine.brainwilliam
 * @since 2018/10/18
 * <p>
 * 「签章成功」之后，弹出通知
 */
public class NotificationTool {

    /**
     * 设置通知
     *
     * @param activity
     * @param title
     * @param content
     */
    public static void setNotification(Activity activity, String title, String content) {
        //第一步:设置判断当前SDK的版本是否是大于8.0，如果是，就设置渠道信息
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = Constants.NOTIFICATION_CHANNEL_ID;
            String channelName = Constants.NOTIFICATION_CHANNEL_NAME;
            int importance = NotificationManager.IMPORTANCE_HIGH;//设置该通知优先级
            createNotificationChannel(channelId, channelName, importance);
        }
        if (activity != null) {
            //第二步：获取状态通知栏管理
            NotificationManager manager = (NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = manager.getNotificationChannel(Constants.NOTIFICATION_CHANNEL_ID);
                if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                    Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, activity.getPackageName());
                    intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId());
                    activity.startActivity(intent);
                }
            }

            //第三步：实例化通知栏构造器NotificationCompat.Builder，对Builder进行配置
            NotificationCompat.Builder builder = new NotificationCompat.Builder(BCAASApplication.context(), Constants.NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(content)//设置通知栏显示内容
                    .setContentIntent(getDefaultIntent(activity, Notification.FLAG_AUTO_CANCEL))//设置通知栏点击意图
                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                    .setTicker(MessageConstants.BCAASWALLET)//通知首次出现在通知栏，带上升动画效果的
                    .setSmallIcon(R.drawable.ic_launcher)//设置通知小ICON
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)//在任何情况下都显示，不受锁屏影响。
                    .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                    .setLargeIcon(BitmapFactory.decodeResource(BCAASApplication.context().getResources(), R.drawable.ic_launcher))
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_VIBRATE);//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合;
//            if (Build.VERSION.SDK_INT >=
//                    //悬挂式Notification，5.0后显示
//                    Build.VERSION_CODES.LOLLIPOP) {
//                builder.setFullScreenIntent(getDefaultIntent(activity, Notification.FLAG_AUTO_CANCEL), true);
//                builder.setCategory(NotificationCompat.CATEGORY_MESSAGE);
//                builder.setVisibility(Notification.VISIBILITY_PUBLIC);
//            }
            Notification notification = builder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            //第四步：发出通知
            manager.notify(1, notification);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) BCAASApplication.context().getSystemService(
                NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    private static PendingIntent getDefaultIntent(Activity activity, int flags) {
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 1, new Intent(), flags);
        return pendingIntent;
    }
}
