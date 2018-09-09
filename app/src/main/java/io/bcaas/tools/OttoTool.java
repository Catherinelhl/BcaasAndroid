package io.bcaas.tools;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/17
 * <p>
 * 时间监听者提供
 */
public class OttoTool extends Bus {

    /**
     * 通过单例模式返回唯一的bus对象,而且重写父类的post方法,通过handler实现任意线程可以调用
     */
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private volatile static Bus bus = null;
    private OttoTool() {
        super();
    }

    @Override
    public void post(Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mHandler.post(() -> OttoTool.super.post(event));
        }
    }

    public static Bus getInstance() {
        if (bus == null) {
            synchronized (OttoTool.class) {
                bus = new Bus();
            }
        }
        return bus;
    }
}
