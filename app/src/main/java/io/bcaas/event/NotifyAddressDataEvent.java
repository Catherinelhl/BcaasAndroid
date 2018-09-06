package io.bcaas.event;

import java.io.Serializable;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/17
 */
public class NotifyAddressDataEvent implements Serializable{
    private boolean isNotify;
    public NotifyAddressDataEvent(boolean isNotify){
        this.isNotify=isNotify;
    }

    public boolean isNotify() {
        return isNotify;
    }
}
