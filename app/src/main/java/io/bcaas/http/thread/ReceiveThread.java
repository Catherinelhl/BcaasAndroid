package io.bcaas.http.thread;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import io.bcaas.listener.TCPReceiveBlockListener;
import io.bcaas.utils.L;

/**
 * TCP请求服务端，请求R区块的数据
 */
public class ReceiveThread extends Thread {
    private static String TAG = "ReceiveThread:socket_tcp";
    //服务器地址
    public String ip;
    //服务器端口号
    public int port;

    public String writeStr = null;

    //是否存活
    public static boolean alive = true;
    public static Socket socket = null;
    private TCPReceiveBlockListener tcpReceiveBlockListener;

    public ReceiveThread(String ip, int port, String writeString, TCPReceiveBlockListener tcpReceiveBlockListener) {
        this.ip = ip;
        this.port = port;
        this.writeStr = writeString;
        this.tcpReceiveBlockListener = tcpReceiveBlockListener;
        L.d(TAG, "socket 请求地址：" + this.ip + ":" + this.port);
    }


    /**
     * 关闭线程
     */
    public static void kill() {

        alive = false;
        L.d(TAG, "socket close...");
        try {
            socket.close();
        } catch (Exception e) {
            L.d(TAG, "socket close Exception..." + e.getMessage());
        }

    }

    @Override
    public final void run() {
        try {
            L.d(TAG, "初始化连接socket..." + ip + ":" + port);
            //  socket = new Socket(ip, port);
            //初始化连接socket
            socket = new Socket();
            //new InetSocketAddress（）这个后面可以设置超时时间，默认的超时时间可能会久一点
            int timeout = 20000;
            socket.connect(new InetSocketAddress(ip, port));

            socket.setKeepAlive(true);
            alive = true;

            writeTOSocket(socket, writeStr);
            //开启接收线程
            new HandlerThread(socket);
            //为了能让http 请求提醒在socket之后，所以这里暂时让其睡眠1500；
            Thread.sleep(1500);

            if (socket.isConnected()) {
                L.d(TAG, "发送Http+++++++++++");
                tcpReceiveBlockListener.httpToRequestReceiverBlock();
            }
        } catch (Exception e) {
            tcpReceiveBlockListener.tcpConnectFailure(e.getMessage());
            L.d(TAG, " 初始化socket失败。。");
            e.printStackTrace();
        }

    }

    /**
     * 用于向服务端写入数据
     *
     * @param socket   socket对象
     * @param writeStr 写入字符串
     */
    public static void writeTOSocket(Socket socket, String writeStr) {
        PrintWriter printWriter = null;
        try {
            if (socket.isConnected()) {
                //向服务器端发送数据
                printWriter = new PrintWriter(socket.getOutputStream());
                printWriter.write(writeStr + " \n");
                printWriter.flush();

                L.d(TAG, "已发送socket数据 json:" + writeStr);
            } else {
                L.d(TAG, "socket closed..");
            }
        } catch (Exception e) {
            L.d(TAG, "receive client exception");
            e.printStackTrace();
        }
    }

    /**
     * 用于接受服务端响应数据
     */
    public class HandlerThread implements Runnable {
        private Socket socket;

        public HandlerThread(Socket client) {
            socket = client;
            new Thread(this).start();
        }

        public final void run() {

            L.d(TAG, socket);
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            String doubleHashTc = null;
            String apiUrl = null;
            String amount = null;
            String blockService = "BCC";
            while (alive) {
                L.d(TAG, "+++++++++++");
                try {
                    //读取服务器端数据
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    try {
                        while (socket.isConnected() && alive) {
                            try {
                                socket.sendUrgentData(0xFF); // 發送心跳包
                            } catch (Exception e) {
                                L.d(TAG, "socket连接异常。。");
                                socket.close();
                                break;
                            }
                            String readLine = bufferedReader.readLine();
                            if (readLine != null && readLine.trim().length() != 0) {
                                L.d(TAG, " 服务器端receive值是: " + readLine);
                                tcpReceiveBlockListener.receiveBlockData(readLine);

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (bufferedReader != null) {
                            bufferedReader.close();
                        }
                        try {
                            socket.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        L.d(TAG, " 关闭socket 连线。。");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

}
