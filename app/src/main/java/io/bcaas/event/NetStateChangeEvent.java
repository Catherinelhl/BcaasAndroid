package io.bcaas.event;

/**
 * @projectName: BottomBar
 * @packageName: cn.catherine.bottombar.event
 * @author: catherine
 * @time: 2018/9/9
 * <p>
 * 發出此訂閱：網絡狀態發生變化
 */
public class NetStateChangeEvent {

    private boolean connect;

    public NetStateChangeEvent(boolean connect) {
        this.connect = connect;
    }

    public boolean isConnect() {
        return connect;
    }

    public void setConnect(boolean connect) {
        this.connect = connect;
    }
}
