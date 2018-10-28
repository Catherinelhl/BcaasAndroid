package io.bcaas.view.pop;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import io.bcaas.R;
import io.bcaas.constants.Constants;
import io.bcaas.listener.ObservableTimerListener;
import io.bcaas.tools.ObservableTimerTool;
import io.bcaas.tools.StringTool;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/30
 * <p>
 * 显示通知
 */
public class NotificationPopWindow extends PopupWindow {
    private String TAG = NotificationPopWindow.class.getSimpleName();

    private View popWindow;

    public NotificationPopWindow(Context context, String blockService, String content) {
        super(context);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setOutsideTouchable(true);
        this.setTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popWindow = inflater.inflate(R.layout.layout_notification_toast, null);
        setContentView(popWindow);
        TextView toastTitle = popWindow.findViewById(R.id.toast_title);
        TextView toastContent = popWindow.findViewById(R.id.toast_content);
        toastTitle.setText(blockService);
        toastContent.setText(content);
        ObservableTimerTool.countDownTimerBySetTime(Constants.ValueMaps.COUNT_DOWN_NOTIFICATION, new ObservableTimerListener() {
            @Override
            public void timeUp(String from) {
                if (StringTool.equals(from, Constants.TimerType.COUNT_DOWN_NOTIFICATION))
                    if (context != null) {
                        dismiss();
                    }
            }
        });
    }


}
