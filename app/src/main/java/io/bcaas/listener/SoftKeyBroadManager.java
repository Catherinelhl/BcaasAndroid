package io.bcaas.listener;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.LinkedList;
import java.util.List;

/**
 * BcaasAndroid
 * <p>
 * io.bcaas.listener
 * <p>
 * created by catherine in 九月/05/2018/下午5:58
 */
public class SoftKeyBroadManager implements ViewTreeObserver.OnGlobalLayoutListener {

    private String TAG = SoftKeyBroadManager.class.getSimpleName();

    public interface SoftKeyboardStateListener {
        void onSoftKeyboardOpened(int keyboardHeightInPx, int bottom);

        void onSoftKeyboardClosed();
    }

    private final List<SoftKeyboardStateListener> listeners = new LinkedList<SoftKeyboardStateListener>();
    private final View activityRootView;
    private final View scrollView;
    private int lastSoftKeyboardHeightInPx;
    private boolean isSoftKeyboardOpened;

    public SoftKeyBroadManager(View activityRootView, View scrollView) {
        this(activityRootView, scrollView, false);
    }

    public SoftKeyBroadManager(View activityRootView, View scrollView, boolean isSoftKeyboardOpened) {
        this.activityRootView = activityRootView;
        this.scrollView = scrollView;
        this.isSoftKeyboardOpened = isSoftKeyboardOpened;
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        final Rect r = new Rect();
        //r will be populated with the coordinates of your view that area still visible.
        activityRootView.getWindowVisibleDisplayFrame(r);
        int screenHeight = activityRootView.getRootView().getHeight();
        final int heightDiff = screenHeight - (r.bottom - r.top);
        Log.d("SoftKeyboardStateHelper", "heightDiff:" + heightDiff);
        boolean isKeyboardShowing = heightDiff > screenHeight / 3;
        if (!isSoftKeyboardOpened && isKeyboardShowing) { // if more than 100 pixels， its probably a keyboard...
            isSoftKeyboardOpened = true;
            float factHeight = scrollView.getY() + scrollView.getHeight();
            if (factHeight > heightDiff) {
                int[] location = new int[2];
                //获取scrollToView在窗体的坐标
                scrollView.getLocationInWindow(location);
                //计算root滚动高度，使scrollToView在可见区域
                int scrollHeight = (location[1] + scrollView.getHeight()) - r.bottom;
                activityRootView.scrollTo(0, scrollHeight);
            }

            notifyOnSoftKeyboardOpened(heightDiff, r.bottom);
        } else if (isSoftKeyboardOpened && !isKeyboardShowing) {
            isSoftKeyboardOpened = false;
            activityRootView.scrollTo(0, 0);
            notifyOnSoftKeyboardClosed();
        }
    }

    public void setIsSoftKeyboardOpened(boolean isSoftKeyboardOpened) {
        this.isSoftKeyboardOpened = isSoftKeyboardOpened;
    }

    public boolean isSoftKeyboardOpened() {
        return isSoftKeyboardOpened;
    }

    /**
     * Default value is zero (0)
     *
     * @return last saved keyboard height in px
     */
    public int getLastSoftKeyboardHeightInPx() {
        return lastSoftKeyboardHeightInPx;
    }

    public void addSoftKeyboardStateListener(SoftKeyboardStateListener listener) {
        listeners.add(listener);
    }

    public void removeSoftKeyboardStateListener(SoftKeyboardStateListener listener) {
        listeners.remove(listener);
    }

    private void notifyOnSoftKeyboardOpened(int keyboardHeightInPx, int bottom) {
        this.lastSoftKeyboardHeightInPx = keyboardHeightInPx;

        for (SoftKeyboardStateListener listener : listeners) {
            if (listener != null) {
                listener.onSoftKeyboardOpened(keyboardHeightInPx, bottom);
            }
        }
    }

    private void notifyOnSoftKeyboardClosed() {
        for (SoftKeyboardStateListener listener : listeners) {
            if (listener != null) {
                listener.onSoftKeyboardClosed();
            }
        }
    }
}