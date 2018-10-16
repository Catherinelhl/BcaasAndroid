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

    public static boolean NeedReset(Exception e) {
        //            if (e instanceof SocketException) {
//                // 如果当前已经是连接到的状态，那么就不需要重新连接了
//                if (e.toString().equals(MessageConstants.ALREADY_CONNECTED)) {
//                } else {
//                    if (e.getMessage() != null) {
//                        //如果当前连接不上，代表需要重新设置AN,内网5s，外网10s
//                        resetSAN();
//                    }
//                }
//            }
        if (e.getMessage() != null) {
            //如果当前连接不上，代表需要重新设置AN,内网5s，外网10s
            return true;
        }
        return false;
    }
}
