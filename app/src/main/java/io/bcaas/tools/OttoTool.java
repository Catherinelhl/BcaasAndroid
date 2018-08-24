package io.bcaas.tools;

import com.squareup.otto.Bus;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/17
 * <p>
 * 时间监听者提供
 */
public class OttoTool {
    private volatile static Bus bus = null;

    private OttoTool() {
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
