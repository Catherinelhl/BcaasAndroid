package io.bcaas.bean;

import java.io.Serializable;

/**
 * @author catherine.brainwilliam
 * @since 2018/10/17
 * <p>
 * <p>
 * 向服务器传送心跳信息
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
