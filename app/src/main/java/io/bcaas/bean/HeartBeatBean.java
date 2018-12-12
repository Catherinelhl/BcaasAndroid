package io.bcaas.bean;

import java.io.Serializable;

/**
 * @author catherine.brainwilliam
 * @since 2018/10/17
 * Socket's TCP 連接成功客戶端需要向服务器传送的心跳信息數據類
 */
public class HeartBeatBean implements Serializable {
    private String methodName;

    public HeartBeatBean(String methodName) {
        super();
        this.methodName = methodName;

    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public String toString() {
        return "HeartBeatBean{" +
                "methodName='" + methodName + '\'' +
                '}';
    }
}
