package io.bcaas.utils;

import com.squareup.otto.Bus;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/17
 * <p>
 * 时间监听者提供
 */
public class OttoU {
    private volatile static Bus bus = null;

    private OttoU() {
    }

    public static Bus getInstance() {
        if (bus == null) {
            synchronized (OttoU.class) {
                bus = new Bus();
            }
        }
        return bus;
    }
}
