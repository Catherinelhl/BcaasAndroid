package io.bcaas.event;

import java.io.Serializable;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/17
 * 切换Tab
 */
public class SwitchTabEvent implements Serializable {

    private int position;

    public SwitchTabEvent(int position){
        this.position=position;
    }

    public int getPosition() {
        return position;
    }
}
