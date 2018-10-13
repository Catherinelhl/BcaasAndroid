package io.bcaas.tools;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * @author catherine.brainwilliam
 * @since 2018/10/4
 * <p>
 * 網絡管理
 */
public class NetWorkTool {

    /*HTTP 連接超時*/
    public static boolean connectTimeOut(Throwable throwable) {
        return throwable instanceof UnknownHostException
                || throwable instanceof SocketTimeoutException
                || throwable instanceof ConnectException;
    }

    /*TCP 連接超時*/
    public static boolean tcpConnectTimeOut(Exception e) {
        return e instanceof ConnectException
                || e instanceof SocketTimeoutException
                || e instanceof UnknownHostException
                || e instanceof SocketException;
    }
}
