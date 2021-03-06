package io.bcaas.http.tcp;


import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import io.bcaas.base.BCAASApplication;
import io.bcaas.bean.HeartBeatBean;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.gson.ResponseJson;
import io.bcaas.gson.jsonTypeAdapter.GenesisVOTypeAdapter;
import io.bcaas.gson.jsonTypeAdapter.TransactionChainVOTypeAdapter;
import io.bcaas.http.requester.HttpIntervalRequester;
import io.bcaas.http.requester.HttpTransactionRequester;
import io.bcaas.http.requester.MasterRequester;
import io.bcaas.listener.GetMyIpInfoListener;
import io.bcaas.listener.HttpASYNTCPResponseListener;
import io.bcaas.listener.HttpTransactionListener;
import io.bcaas.listener.ObservableTimerListener;
import io.bcaas.listener.TCPRequestListener;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.ObservableTimerTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.decimal.DecimalTool;
import io.bcaas.tools.ecc.Sha256Tool;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.tools.gson.JsonTool;
import io.bcaas.vo.ClientIpInfoVO;
import io.bcaas.vo.DatabaseVO;
import io.bcaas.vo.GenesisVO;
import io.bcaas.vo.PaginationVO;
import io.bcaas.vo.TransactionChainChangeVO;
import io.bcaas.vo.TransactionChainOpenVO;
import io.bcaas.vo.TransactionChainReceiveVO;
import io.bcaas.vo.TransactionChainSendVO;
import io.bcaas.vo.TransactionChainVO;
import io.bcaas.vo.WalletVO;

import static java.lang.Thread.currentThread;

/**
 * @author catherine.brainwilliam
 * update 2018/08/31
 * TCP请求服务端，请求R区块的数据
 * <p>
 * TCP：開啟Socket以及和服務器建立TCP連接的數據讀取
 */
public class TCPThread {
    private static String TAG = TCPThread.class.getSimpleName();

    /*向服务器TCP发送的数据*/
    private static String writeStr;
    /*是否存活*/
    public static volatile boolean keepAlive = true;
    /*建立連結的socket*/
    public static volatile Socket buildSocket = null;
    /*得到当前需要去签章的交易区块 */
    private static TransactionChainVO currentSendVO;
    /*监听TCP的一些返回，通知界面作出改动 */
    private TCPRequestListener tcpRequestListener;
    /*存儲當前請求回來的需要簽章的交易區塊，做一個現城池，異步處理*/
    private static Queue<TransactionChainVO> getWalletWaitingToReceiveQueue = new LinkedList<>();
    /*声明一个参数用来存储更改授权代表的返回状态，默认是「change」*/
    private static String changeStatus = Constants.CHANGE;
    /*用来停止socket请求,这个比kill()大*/
    private static boolean stopSocket = false;
    /*当前重连的次数*/
    private int resetCount;
    /*当前TCP连接的SAN地址信息*/
    private static ClientIpInfoVO clientIpInfoVO;
    /*当前连接的网络是否是内网*/
    private boolean isInternal;
    /*得到當前建立長鏈接的Handler*/
    private static TCPReceiveThread tcpReceiveThread;
    /*得到當前建立的長鏈接的looper*/
    private static Looper TCPReceiveLooper;
    /*存儲當前「Send」之後，自己計算的balance*/
    private String balanceAfterSend = "";
    /*判断当前是否Socket是否已经连接*/
    private boolean socketIsConnect;
    /*判断当前是否是主动断开，以此来判断是否需要重连*/
    private static boolean activeDisconnect;

    /*当前是否可以重置SAN，默认为可以*/
    public static boolean canReset;

    /*提示当前是否提示过资料同步中*/
    private boolean hadToastBalanceSync;

    /*当前授权的账户代表*/
    private static String representativeFromInput;

    public static String getRepresentativeFromInput() {
        return representativeFromInput;
    }

    public static void setRepresentativeFromInput(String representativeFromInput) {
        TCPThread.representativeFromInput = representativeFromInput;
    }

    public boolean isActiveDisconnect() {
        return activeDisconnect;
    }

    public static void setActiveDisconnect(boolean activeDisconnect) {
        TCPThread.activeDisconnect = activeDisconnect;
    }

    public TCPThread(String writeString, TCPRequestListener tcpRequestListener) {
        LogTool.d(TAG, "[TCP] 开启TCPThread");
        this.writeStr = writeString;
        this.tcpRequestListener = tcpRequestListener;
        /*建立Socket连接*/
        buildSocket = new Socket();
        activeDisconnect = false;
        socketIsConnect = false;
        canReset = true;
        stopSocket = false;
        hadToastBalanceSync = false;
        requestWalletRealIP();
    }

    /**
     * 創建socket,并且连接
     */
    private void createSocket() {
        //判断当前的socket是否建立连接，如果当前是建立连接的状态，那么就不需要再进行连接
//        LogTool.d(TAG, socketIsConnect);
        if (socketIsConnect) {
            return;
        }
        socketIsConnect = true;
        setActiveDisconnect(false);
        SocketAddress socketAddress = new InetSocketAddress(BCAASApplication.getTcpIp(), BCAASApplication.getTcpPort());
        LogTool.d(TAG, "step 1:" + MessageConstants.socket.TAG + socketAddress);
        tcpRequestListener.refreshTCPConnectIP(BCAASApplication.getTcpIp() + Constants.HTTP_COLON + BCAASApplication.getTcpPort());
        try {
            //设置socket连接超时时间，如果是内网的话，那么5s之后重连，如果是外网10s之后重连
            buildSocket.connect(socketAddress,
                    isInternal ? Constants.Time.INTERNET_TIME_OUT
                            : Constants.Time.EXTERNAL_TIME_OUT);
            //让其在建立连接的时候保持存活
            buildSocket.setKeepAlive(true);
            keepAlive = true;
            //开始对当前连接的socket进行数据写入
            LogTool.d(TAG, MessageConstants.socket.BUILD_SOCKET + stopSocket);
            //当前stopSocket为false的时候才能允许连接
            if (!stopSocket) {
                isResetExceedTheLimit();
                resetCount++;
                if (buildSocket != null && buildSocket.isConnected()) {
                    //发送封包信息
                    writeTOSocket(false);
                    /*2:开启接收线程*/
                    tcpReceiveThread = new TCPReceiveThread(buildSocket);
                    tcpReceiveThread.start();
                }
            }
        } catch (Exception e) {
            LogTool.e(TAG, e.toString());
            if (e instanceof SocketException) {
                socketIsConnect = false;
                createSocket();
            } else {
                resetSAN();
            }
        }
    }

    /*判断重置是否超过限定,重置次数已经5次了，那么让他睡10s，然后继续*/
    private void isResetExceedTheLimit() {
        if (resetCount >= MessageConstants.socket.RESET_MAX_COUNT) {
            resetCount = 0;
            try {
                LogTool.d(TAG, MessageConstants.socket.OVER_FIVE_TIME_TO_RESET);
                Thread.sleep(Constants.Time.sleep10000);
            } catch (InterruptedException e) {
                LogTool.d(TAG, e.getMessage());
            }
        }
    }

    /*重新连接SAN*/
    private void resetSAN() {
//        tcpRequestListener.needUnbindService();
        LogTool.d(TAG, MessageConstants.socket.RESET_AN + stopSocket);
        //当前stopSocket为false的时候才继续重连
        MasterRequester.reset(httpASYNTCPResponseListener, canReset);
        canReset = false;
    }

    /**
     * 获取当前Wallet 的real ip
     */
    private void requestWalletRealIP() {
        MasterRequester.getMyIpInfo(getMyIpInfoListener);

    }

    private GetMyIpInfoListener getMyIpInfoListener = new GetMyIpInfoListener() {
        @Override
        public void responseGetMyIpInfo(boolean isSuccess) {
            LogTool.d(TAG, MessageConstants.socket.WALLET_EXTERNAL_IP + BCAASApplication.getWalletExternalIp());
            //比对当前设备的外网IP与SAN的外网IP
            /*1：得到当前设备的外网IP*/
            String walletExternalIp = BCAASApplication.getWalletExternalIp();
            /*2：得到当前服务器返回的可以连接的SAN的内外网IP&Port*/
            clientIpInfoVO = BCAASApplication.getClientIpInfoVO();
            if (clientIpInfoVO == null) {
                tcpRequestListener.getDataException(MessageConstants.socket.CLIENT_INFO_NULL);
                return;
            }
            /*3：比对当前的APP的外网IP与服务器返回的外网IP，如果相同， 那么就连接内网，否则连接外网*/
            if (StringTool.equals(walletExternalIp, clientIpInfoVO.getExternalIp())) {
                //连接内网IP&Port
                connectInternalIP();
            } else {
                //连接外网IP&Port
                connectExternalIP();
            }
            /*1:創建socket,并且连接*/
            createSocket();
        }

    };


    /*连接内网IP&Port*/
    private void connectExternalIP() {
        isInternal = false;
//        LogTool.d(TAG, MessageConstants.socket.CONNECT_EXTERNAL_IP);
        BCAASApplication.setTcpIp(clientIpInfoVO.getExternalIp());
        BCAASApplication.setTcpPort(clientIpInfoVO.getExternalPort());
        BCAASApplication.setHttpPort(clientIpInfoVO.getRpcPort());
    }

    /*连接外网IP&Port*/
    private void connectInternalIP() {
        isInternal = true;
//        LogTool.d(TAG, MessageConstants.socket.CONNECT_INTERNAL_IP);
        BCAASApplication.setTcpIp(clientIpInfoVO.getInternalIp());
        BCAASApplication.setTcpPort(clientIpInfoVO.getInternalPort());
        BCAASApplication.setHttpPort(clientIpInfoVO.getInternalRpcPort());
    }

    /**
     * 用于向服务端写入数据
     */
    private void writeTOSocket(boolean isHeartBeat) {
        PrintWriter printWriter;
        try {
            if (buildSocket != null && buildSocket.isConnected()) {
                //向服务器端发送数据
//                printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), MessageConstants.socket.CHARSET_NAME));
                printWriter = new PrintWriter(buildSocket.getOutputStream());
                printWriter.write(writeStr + Constants.CHANGE_LINE);
                printWriter.flush();
                LogTool.d(TAG, MessageConstants.socket.SEND_DATA + writeStr);
            }
        } catch (Exception e) {
            LogTool.e(TAG, MessageConstants.socket.CONNECT_EXCEPTION);
            e.printStackTrace();
            if (isHeartBeat) {
                closeSocket(false, "writeTOSocket");
            }
            socketIsConnect = false;
            createSocket();
        }
    }

    public static boolean allowConnect() {
        return !stopSocket;
    }

    /*接受服务端响应数据*/
    private class TCPReceiveThread extends Thread {
        private Socket socket;

        public TCPReceiveThread(Socket client) {
            socket = client;
        }

        public final void run() {
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            TCPReceiveLooper = Looper.myLooper();
            Gson gson = GsonTool.getGson();
            //判斷當前是活著且非阻塞的狀態下才能繼續前行
            while (isKeepAlive() && !isInterrupted()) {
                //开始监控SAN返回连接成功的信息的倒计时
                ObservableTimerTool.startCountDownTCPConnectTimer(observableTimerListener);
                LogTool.d(TAG, MessageConstants.SOCKET_HAD_CONNECTED_START_TO_RECEIVE + socket + stopSocket);
                try {
                    //读取服务器端数据
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), MessageConstants.socket.CHARSET_NAME));
                    try {
                        while (socket.isConnected() && isKeepAlive()) {
                            // TODO: 2018/10/27 暂时不用这个，因为在连接成功的状态下可能会因为这个心跳包发送导致异常，所以不用这个
//                            try {
//                                // 發送心跳包
//                                socket.sendUrgentData(MessageConstants.socket.HEART_BEAT);
//                            } catch (Exception e) {
//                                LogTool.e(TAG, MessageConstants.socket.CONNECT_EXCEPTION + e.getMessage());
//                                break;
//                            }
                            String readLine = bufferedReader.readLine();
                            if (readLine != null && readLine.trim().length() != 0) {
                                LogTool.d(TAG, MessageConstants.socket.TCP_RESPONSE + readLine);
                                ResponseJson responseJson = gson.fromJson(readLine, ResponseJson.class);
                                if (responseJson != null) {
                                    int code = responseJson.getCode();
                                    /*匹配异常code，如果是3006||3008，则是token过期，需要提示其重新登录*/
                                    if (JsonTool.isTokenInvalid(code)) {
                                        //Redis data not found,need logout
                                        LogTool.d(TAG, MessageConstants.socket.STOP_SOCKET_TO_LOGIN);
                                        if (bufferedReader != null) {
                                            bufferedReader.close();
                                        }
                                        if (!stopSocket) {
                                            tcpRequestListener.reLogin();
                                            closeSocket(true, "TCPLogout");
                                        }
                                        break;
                                    }
                                    if (code != MessageConstants.CODE_200) {
                                        tcpRequestListener.getDataException(responseJson.getMessage());
                                        LogTool.d(TAG, MessageConstants.socket.CODE_EXCEPTION + responseJson.getMessage());
                                    }
                                    String methodName = responseJson.getMethodName();
                                    if (StringTool.isEmpty(methodName)) {
                                        LogTool.d(TAG, MessageConstants.METHOD_NAME_IS_NULL);
                                    } else {
                                        switch (methodName) {
                                            /*得到最新的余额*/
                                            case MessageConstants.socket.GET_LATEST_BLOCK_AND_BALANCE_SC:
                                                getLatestBlockAndBalance_SC(responseJson);
                                                break;
                                            /*发送*/
                                            case MessageConstants.socket.GET_SEND_TRANSACTION_DATA_SC:
                                                getSendTransactionData_SC(responseJson);
                                                break;
                                            /*签章Receive*/
                                            case MessageConstants.socket.GET_RECEIVE_TRANSACTION_DATA_SC:
                                                getReceiveTransactionData_SC(responseJson);
                                                break;
                                            /*获取余额*/
                                            case MessageConstants.socket.GET_BALANCE_SC:
                                                /*判断当前code是否是"success":false,"code":2097,"message":"The balance data is synchronizing.","methodName":"getBalance_SC","size":0}*/
                                                if (code == MessageConstants.CODE_2097) {
                                                    //提示"资料同步中"，如果当前提示过一次，那么就不再提示
                                                    if (!hadToastBalanceSync) {
                                                        tcpRequestListener.balanceIsSynchronizing();
                                                        hadToastBalanceSync = true;
                                                    }
                                                } else {
                                                    getBalance_SC(responseJson);
                                                }
                                                break;
                                            /*得到最新的R区块*/
                                            case MessageConstants.socket.GET_WALLET_WAITING_TO_RECEIVE_BLOCK_SC:
                                                getWalletWaitingToReceiveBlock_SC(responseJson);
                                                break;
                                            /*获取最新的Change区块*/
                                            case MessageConstants.socket.GET_LATEST_CHANGE_BLOCK_SC:
                                                getLatestChangeBlock_SC(responseJson);
                                                break;
                                            /*响应Change区块数据*/
                                            case MessageConstants.socket.GET_CHANGE_TRANSACTION_DATA_SC:
                                                getChangeTransactionData_SC(responseJson);
                                                break;
                                            /*成功连接到SAN*/
                                            case MessageConstants.socket.CONNECTION_SUCCESS_SC:
                                                LogTool.d(TAG, MessageConstants.socket.CONNECT_SUCCESS);
                                                //接收到连接成功的信息，关闭倒数计时
                                                ObservableTimerTool.closeCountDownTCPConnectTimer();
                                                //开始背景执行获取「余额」和「未签章区块」
                                                HttpIntervalRequester.startToHttpIntervalRequest(httpASYNTCPResponseListener);
                                                //开始向SAN发送心跳，30s一次
                                                ObservableTimerTool.startHeartBeatByIntervalTimer(observableTimerListener);
                                                break;
                                            /*与SAN建立的心跳，如果10s没有收到此心跳，那么就需要重新reset*/
                                            case MessageConstants.socket.HEARTBEAT_SC:
                                                //取消当前心跳倒计时
                                                ObservableTimerTool.closeStartHeartBeatByIntervalTimer();
                                                break;
                                            /*需要重置AN*/
                                            case MessageConstants.socket.CLOSE_SOCKET_SC:
                                                resetSAN();
                                                break;
                                            default:
                                                LogTool.d(TAG, MessageConstants.METHOD_NAME_ERROR + methodName);
                                                break;
                                        }
                                    }
                                } else {
                                    LogTool.d(TAG, MessageConstants.RESPONSE_IS_NULL);
                                }

                            }
                        }
                    } catch (Exception e) {
                        keepAlive = false;
                        LogTool.e(TAG, e.getMessage());
                        break;
                    } finally {
                        if (bufferedReader != null) {
                            bufferedReader.close();
                        }
                    }
                } catch (Exception e) {
                    keepAlive = false;
                    LogTool.e(TAG, e.getMessage());
                    break;
                } finally {
                    // 判断当前是否是主动断开造成的异常，如果是，就不需要reset
                    LogTool.d(TAG, "isActiveDisconnect:" + isActiveDisconnect());
                    if (!isActiveDisconnect()) {
                        resetSAN();
                    }
                    break;
                }
            }
            Looper.loop();
        }
    }

    /*获取余额*/
    private void getBalance_SC(ResponseJson responseJson) {
        LogTool.d(TAG, "step 2:" + MessageConstants.socket.GET_BALANCE_SC);
        if (responseJson.isSuccess()) {
//            LogTool.d(TAG, MessageConstants.socket.SUCCESS_GET_WALLET_GET_BALANCE);
            if (responseJson.getCode() == MessageConstants.CODE_200) {
                parseWalletVoTOGetBalance(responseJson.getWalletVO());
            }
        } else {
            LogTool.d(TAG, MessageConstants.socket.FAILURE_GET_WALLET_GET_BALANCE);
        }

    }

    /*解析錢包信息。得到服務器返回的餘額*/
    private void parseWalletVoTOGetBalance(WalletVO walletVO) {
        if (walletVO != null) {
            //判斷當前服務器返回的區塊是否和本地的區塊相對應，如果是，才顯示新獲取的餘額
            String blockService = walletVO.getBlockService();
            if (BCAASApplication.getBlockService().equals(blockService)) {
                String walletBalance = walletVO.getWalletBalance();
                //現在Receive區塊沒有返回餘額了。所以判斷但錢餘額為空，就不用顯示，當然，R區塊返回也不用調用這個方法了
                if (StringTool.notEmpty(walletBalance)) {
                    tcpRequestListener.showWalletBalance(walletBalance);

                }
            }
        } else {
            LogTool.d(TAG, MessageConstants.socket.FAILURE_GET_WALLET_GET_BALANCE);
        }
    }

    /**
     * "取得未簽章R區塊的Send區塊 & 取最新的R區塊 & wallet餘額"
     *
     * @param responseJson
     */
    public void getWalletWaitingToReceiveBlock_SC(ResponseJson responseJson) {
        LogTool.d(TAG, "step 2:" + MessageConstants.socket.GET_WALLET_WAITING_TO_RECEIVE_BLOCK_SC);
        //判断当前币种是否是同一个币种
        //判断当前的币种是否匹配
        WalletVO walletVO = responseJson.getWalletVO();
        if (walletVO != null) {
            String blockService = walletVO.getBlockService();
            if (StringTool.notEmpty(blockService)) {
                if (StringTool.equals(BCAASApplication.getBlockService(), blockService)) {
                    //如果当前币种一致才存储数据
                    clearGetReceiveBlockQueue();
                    Gson gson = GsonTool.getGson();
                    List<PaginationVO> paginationVOList = responseJson.getPaginationVOList();
                    if (paginationVOList != null) {
                        PaginationVO paginationVO = paginationVOList.get(0);
                        List<Object> objList = paginationVO.getObjectList();
                        if (ListTool.noEmpty(objList)) {
                            //停止當前背景執行獲取「未簽章區塊」的10s請求
                            HttpIntervalRequester.disposeRequest(HttpIntervalRequester.getReceiveBlockByIntervalDisposable);
                            //有未签章的区块
                            for (Object obj : objList) {
                                TransactionChainVO transactionChainVO = gson.fromJson(gson.toJson(obj), TransactionChainVO.class);
                                getWalletWaitingToReceiveQueue.offer(transactionChainVO);
                            }
                            //开始签章从本地队列取出数据
                            getTransactionVOOfQueue(responseJson, false);
                        }
                        BCAASApplication.setNextObjectId(paginationVO.getNextObjectId());
                    }
                }
            }
        }

    }

    /**
     * 处理线程下面需要处理的R区块
     *
     * @param responseJson
     */
    public void getReceiveTransactionData_SC(ResponseJson responseJson) {
        //关闭当前监听接收成功的计时
        ObservableTimerTool.closeCountDownReceiveBlockResponseTimer();
        int code = responseJson.getCode();
        //如果當前是2028：{"databaseVO":{},"walletVO":{"blockService":"COS"},"success":false,"code":2028,"message":"Transaction already exists.","methodName":"getReceiveTransactionData_SC","size":0}
        if (code == MessageConstants.CODE_200) {
            tcpRequestListener.refreshTransactionRecord();
            //同時本地計算餘額
            calculateAfterReceiveBalance(responseJson);
            //签章返回成功，将当前的send块置空
            currentSendVO = null;
            getTransactionVOOfQueue(responseJson, true);
        } else if (code == MessageConstants.CODE_2028) {
            //签章返回成功，将当前的send块置空
            currentSendVO = null;
            getTransactionVOOfQueue(responseJson, true);
        } else {
            LogTool.d(TAG, MessageConstants.socket.SIGNATURE_FAILED + responseJson);
        }
    }

    //簽章成功之後，通知更新當前的餘額
    private void calculateAfterReceiveBalance(ResponseJson responseJson) {
        WalletVO walletVO = responseJson.getWalletVO();
        String blockService = null;
        String amount = null;
        if (walletVO != null) {
            blockService = walletVO.getBlockService();
        }
        DatabaseVO databaseVO = responseJson.getDatabaseVO();
        if (databaseVO != null) {
            TransactionChainVO transactionChainVONew = databaseVO.getTransactionChainVO();
            if (transactionChainVONew != null) {
                Object object = transactionChainVONew.getTc();
                String objectStr = GsonTool.getGson().toJson(object);
                // 如果當前是Receive Block，那麼需要餘額與交易額度相加得到當前需要顯示的金額
                if (JsonTool.isReceiveBlock(objectStr)) {
                    TransactionChainReceiveVO transactionChainReceiveVO = GsonTool.convert(objectStr, TransactionChainReceiveVO.class);
                    if (transactionChainReceiveVO != null) {
                        amount = transactionChainReceiveVO.getAmount();
                        if (StringTool.notEmpty(amount)) {
                            String walletBalance = BCAASApplication.getWalletBalance();
                            if (StringTool.notEmpty(walletBalance)) {
                                String newBalance = DecimalTool.calculateFirstAddSecondValue(walletBalance, amount);
                                LogTool.d(TAG, MessageConstants.socket.CALCULATE_AFTER_RECEIVE_BALANCE + newBalance);
                                tcpRequestListener.showWalletBalance(newBalance);
                            }
                        }
                    }
                } else if (JsonTool.isOpenBlock(objectStr)) {
                    //如果當前是Open區塊，則不需要去檢查本地餘額是否是空，直接顯示交易額度
                    TransactionChainOpenVO transactionChainOpenVO = GsonTool.convert(objectStr, TransactionChainOpenVO.class);
                    if (transactionChainOpenVO != null) {
                        amount = transactionChainOpenVO.getAmount();
                        if (StringTool.notEmpty(amount)) {
                            LogTool.d(TAG, MessageConstants.socket.CALCULATE_AFTER_RECEIVE_BALANCE + amount);
                            tcpRequestListener.showWalletBalance(amount);
                        }
                    }
                }
            }
        }
        //通知当前界面刷新界面
        if (StringTool.notEmpty(blockService) && StringTool.notEmpty(amount)) {
            tcpRequestListener.showNotification(blockService, amount);
        }
    }

    /**
     * 取得线程池里面需要签章的交易
     *
     * @param responseJson
     * @param isReceive    是否是Receive请求进入,如果是，就需要向首页更新上一笔签收成功的数据
     */
    private void getTransactionVOOfQueue(ResponseJson responseJson, boolean isReceive) {
        //1：判斷當前隊列是否為null
        if (getWalletWaitingToReceiveQueue != null) {
            //2：且size是否>0
            int size = getWalletWaitingToReceiveQueue.size();
            LogTool.d(TAG, MessageConstants.socket.CURRENT_RECEIVE_QUEUE_SIZE + size);
            if (size > 0) {
                try {
                    //3：判斷當前是否有正在簽章的區塊
                    if (currentSendVO == null) {
                        //4：重新取得线程池里面的数据,判断当前签章块是否回传结果
                        currentSendVO = getWalletWaitingToReceiveQueue.poll();
                        if (currentSendVO != null) {
                            //開始請求數據
                            receiveTransaction(currentSendVO, responseJson);
                        }
                    } else {
                        LogTool.d(TAG, MessageConstants.socket.SIGNATURE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogTool.e(TAG, MessageConstants.socket.SIGNATURE);
                }
            } else {
                //設置當前沒有需要簽章的數據，且可以開始執行10背景執行
                HttpIntervalRequester.startGetWalletWaitingToReceiveBlockLoop(httpASYNTCPResponseListener);
            }
        }

    }

    /**
     * "取最新的區塊 &wallet餘額"
     *
     * @param responseJson
     */
    public void getLatestBlockAndBalance_SC(ResponseJson responseJson) {
        // 置空「發送」之後需要計算得到的餘額值
        balanceAfterSend = MessageConstants.Empty;
        LogTool.d(TAG, "step 2:" + MessageConstants.socket.GET_LATEST_BLOCK_AND_BALANCE_SC);
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                // 可能是Send/open区块
                .registerTypeAdapter(TransactionChainVO.class, new TransactionChainVOTypeAdapter())
                .create();
        DatabaseVO databaseVO = responseJson.getDatabaseVO();
        if (databaseVO == null) {
            return;
        }
        String destinationWallet = BCAASApplication.getDestinationWallet();
        String transactionAmount = BCAASApplication.getTransactionAmount();
        if (StringTool.isEmpty(destinationWallet)) {
            LogTool.d(TAG, MessageConstants.DESTINATION_WALLET_IS_NULL);
            return;
        }
        if (StringTool.isEmpty(transactionAmount)) {
            LogTool.d(TAG, MessageConstants.AMOUNT_IS_NULL);
            return;
        }
        try {
            LogTool.d(TAG, "transactionAmount:" + transactionAmount + ",destinationWallet:" + destinationWallet);
            WalletVO walletVO = responseJson.getWalletVO();
            if (walletVO != null) {
                balanceAfterSend = DecimalTool.calculateFirstSubtractSecondValue(walletVO.getWalletBalance(), transactionAmount);
                if (StringTool.equals(balanceAfterSend, MessageConstants.NO_ENOUGH_BALANCE)) {
                    tcpRequestListener.noEnoughBalance();
                    balanceAfterSend = "";
                    return;
                }
                parseWalletVoTOGetBalance(walletVO);
                String previousBlockStr = gson.toJson(databaseVO.getTransactionChainVO());
                LogTool.d(TAG, previousBlockStr);
                String previous = Sha256Tool.doubleSha256ToString(previousBlockStr);
                // 2018/8/22请求AN send请求
                responseJson = HttpTransactionRequester.sendAuthNode(previous, walletVO.getBlockService(), destinationWallet, balanceAfterSend, transactionAmount, walletVO.getRepresentative(), httpASYNTCPResponseListener);

                if (responseJson != null) {
                    int code = responseJson.getCode();
                    if (code == MessageConstants.CODE_200) {
                        LogTool.d(TAG, MessageConstants.HTTP_SEND_SUCCESS);
                    } else if (code == MessageConstants.CODE_2002) {
                        tcpRequestListener.sendTransactionFailure(responseJson.getMessage());
                    } else {
                        tcpRequestListener.sendTransactionFailure(MessageConstants.SEND_HTTP_FAILED);
                    }
                }

            } else {
                tcpRequestListener.tcpResponseDataError(MessageConstants.NULL_WALLET);
                return;
            }

        } catch (Exception e) {
            LogTool.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * TCP響應「發送」交易的結果返回
     *
     * @param walletResponseJson
     */
    public void getSendTransactionData_SC(ResponseJson walletResponseJson) {
        if (walletResponseJson.getCode() == MessageConstants.CODE_200) {
            //發送成功，直接顯示當前自己發送錢計算出來的balance，服務器現在不作餘額返回，為了加快返回數據的速度。
            if (StringTool.notEmpty(balanceAfterSend)) {
                LogTool.d(TAG, MessageConstants.socket.BALANCE_AFTER_SEND + balanceAfterSend);
                tcpRequestListener.showWalletBalance(balanceAfterSend);
            }
            tcpRequestListener.sendTransactionSuccess(MessageConstants.socket.TCP_TRANSACTION_SUCCESS);
        } else {
            tcpRequestListener.sendTransactionFailure(MessageConstants.socket.TCP_TRANSACTION_FAILURE + walletResponseJson.getMessage());
        }

    }

    /**
     * 处理线程下面签章区块
     *
     * @param transactionChainVO
     * @param responseJson
     */
    public void receiveTransaction(TransactionChainVO transactionChainVO, ResponseJson responseJson) {
        LogTool.d(TAG, "step 3:" + responseJson);
        //添加receiverAmount
        String receiverAmount = null;
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeAdapter(GenesisVO.class, new GenesisVOTypeAdapter())//初始块有序
                .registerTypeAdapter(TransactionChainVO.class, new TransactionChainVOTypeAdapter())
                .create();
        WalletVO walletVO = responseJson.getWalletVO();
        //如果当前是「open」需要将其「genesisBlockAccount」取出，然后传递给要签章的Representative
        String representative = walletVO.getRepresentative();
        try {
            String tc = gson.toJson(transactionChainVO.getTc());
            LogTool.d(TAG, tc);
            String sourceTXHash = Sha256Tool.doubleSha256ToString(tc);
            LogTool.d(TAG, "step 4:sourceTXHash:" + sourceTXHash);

            String blockType = Constants.BLOCK_TYPE_RECEIVE;
            String previousDoubleHashStr = "";
            DatabaseVO databaseVO = responseJson.getDatabaseVO();
            if (databaseVO != null) {
                TransactionChainVO transactionChainVONew = databaseVO.getTransactionChainVO();
                if (transactionChainVONew != null) {
                    String tcStr = gson.toJson(transactionChainVONew);
                    LogTool.d(TAG, tcStr);
                    previousDoubleHashStr = Sha256Tool.doubleSha256ToString(tcStr);
                    Object object = transactionChainVONew.getTc();
                    String objectStr = GsonTool.getGson().toJson(object);
                    if (JsonTool.isReceiveBlock(objectStr)) {
                        // Receive Block
                        TransactionChainReceiveVO transactionChainReceiveVO = GsonTool.convert(objectStr, TransactionChainReceiveVO.class);
                        if (transactionChainReceiveVO != null) {
                            receiverAmount = transactionChainReceiveVO.getReceiveAmount();
                        }
                    }
                    if (JsonTool.isOpenBlock(objectStr)) {
                        // Open Block
                        TransactionChainOpenVO transactionChainOpenVO = GsonTool.convert(objectStr, TransactionChainOpenVO.class);
                        if (transactionChainOpenVO != null) {
                            receiverAmount = transactionChainOpenVO.getReceiveAmount();
                        }
                    }
                } else {
                    GenesisVO genesisVONew = databaseVO.getGenesisVO();
                    if (genesisVONew != null) { //这里用Gson转化的时候，会有乱序的现象。现在添加一个TypeAdapter来进行顺序的排列，因为这里是直接toJson的「GenesisVO」，所以
                        String str = gson.toJson(genesisVONew);
                        LogTool.d(TAG, str);
                        previousDoubleHashStr = Sha256Tool.doubleSha256ToString(str);
                        blockType = Constants.BLOCK_TYPE_OPEN;
                        representative = genesisVONew.getGenesisBlockAccount();
                    }

                }

            }
            LogTool.d(TAG, "step 5:previousDoubleHashStr:" + previousDoubleHashStr);

            if (StringTool.isEmpty(previousDoubleHashStr)) {
                LogTool.d(TAG, MessageConstants.PREVIOUS_IS_NULL);
                return;
            }
            String signatureSend = transactionChainVO.getSignature();
            //獲取交易金額
            String amount = gson.fromJson(gson.toJson(currentSendVO.getTc()), TransactionChainSendVO.class).getAmount();
            receiverAmount = DecimalTool.calculateFirstAddSecondValue(receiverAmount, amount);
            //如果当前receiveAmount异常，那么提示金额异常
            if (StringTool.equals(receiverAmount, MessageConstants.AMOUNT_EXCEPTION_CODE)) {
                tcpRequestListener.amountException();
                //清除当前
            } else {
                //否则发送当前签章数据
                HttpTransactionRequester.receiveAuthNode(previousDoubleHashStr, walletVO.getBlockService(),
                        sourceTXHash, amount, signatureSend, blockType,
                        representative, receiverAmount, httpASYNTCPResponseListener, httpTransactionListener);

            }
        } catch (Exception e) {
            LogTool.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * /wallet/getLatestChangeBlock
     * 取最新的更換委託人區塊
     * <p>
     * <p>
     * 1.如果没有Change区块，回传Open区块
     * 2.如果有Change区块则回传Change区块
     * TCP封包回传名称：
     * getLatestChangeBlock_SC
     *
     * @param responseJson
     */
    public void getLatestChangeBlock_SC(ResponseJson responseJson) {
        LogTool.d(TAG, "step 2:" + MessageConstants.socket.GET_LATEST_BLOCK_AND_BALANCE_SC);
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                // 可能是change/open区块
                .registerTypeAdapter(TransactionChainVO.class, new TransactionChainVOTypeAdapter())
                .create();
        if (responseJson == null) {
            return;
        }
        /*1：检测当前code，如果是2026，代表没有创世块,用户不能进行授权代表操作*/
        int code = responseJson.getCode();
        if (code == MessageConstants.CODE_2026) {
            tcpRequestListener.modifyRepresentativeResult(changeStatus, responseJson.isSuccess(), code);
            return;
        }
        /*2：否则獲取交易塊*/
        DatabaseVO databaseVO = responseJson.getDatabaseVO();
        if (databaseVO == null) {
            return;
        }
        TransactionChainVO transactionChainVO = databaseVO.getTransactionChainVO();
        if (transactionChainVO == null) {
            return;
        }
        Object tc = transactionChainVO.getTc();
        if (tc == null) {
            return;
        }
        String representative = null;
        //3：判断tc性質 ,檢查blockType是「Open」還是「Change」 ;根據區塊性質，確認representative的值
        String objectStr = GsonTool.getGson().toJson(tc);
        if (JsonTool.isOpenBlock(objectStr)) {
            /*「open」區塊*/
            //标示当前的状态，如果当前是「open」区块，需要在「change」之后再去拉取本接口以获得同AN相同的height、系统时间
            changeStatus = Constants.CHANGE_OPEN;
            /* 「open」區塊，那麼需要從GenesisVO提取genesisBlockAccount這個數據，得到帳戶*/
            GenesisVO genesisVO = databaseVO.getGenesisVO();
            if (genesisVO == null) {
                representative = Constants.ValueMaps.DEFAULT_REPRESENTATIVE;
            } else {
                representative = genesisVO.getGenesisBlockAccount();
                if (StringTool.isEmpty(representative)) {
                    representative = Constants.ValueMaps.DEFAULT_REPRESENTATIVE;
                }
            }
            tcpRequestListener.getPreviousModifyRepresentative(representative);

        } else if (JsonTool.isChangeBlock(objectStr)) {
            /*「Change」區塊*/
            changeStatus = Constants.CHANGE;
            /*1:取得当前用户输入的代表人的地址*/
            representative = getRepresentativeFromInput();
            LogTool.d(TAG, representative);
            if (StringTool.isEmpty(representative)) {
                /*2：解析返回的数据，取出上一个授权代表*/
                TransactionChainChangeVO transactionChainChangeVO = GsonTool.convert(objectStr, TransactionChainChangeVO.class);
                String representativePrevious = transactionChainChangeVO.getRepresentative();
                tcpRequestListener.getPreviousModifyRepresentative(representativePrevious);
                return;
            }

        }
        // 4：previousDoubleHashStr 交易块
        try {
            String transactionGson = gson.toJson(transactionChainVO);
            LogTool.d(TAG, transactionGson);
            String previousDoubleHashStr = Sha256Tool.doubleSha256ToString(transactionGson);
            LogTool.d(TAG, "step 4:previousDoubleHashStr:" + previousDoubleHashStr);
            if (StringTool.isEmpty(previousDoubleHashStr)) {
                LogTool.d(TAG, MessageConstants.PREVIOUS_IS_NULL);
                return;
            }
            /*5：调用change*/
            HttpTransactionRequester.change(previousDoubleHashStr, representative, httpASYNTCPResponseListener);
        } catch (Exception e) {
            LogTool.e(TAG, e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * 响应change
     *
     * @param responseJson
     */
    private void getChangeTransactionData_SC(ResponseJson responseJson) {
        LogTool.d(TAG, "step 2:" + MessageConstants.socket.GET_CHANGE_TRANSACTION_DATA_SC);
        if (responseJson == null) {
            return;
        }
        int code = responseJson.getCode();
        //如果当前「change」成功
        if (responseJson.isSuccess()) {
            String representative = getRepresentativeFromInput();
            if (StringTool.notEmpty(representative)) {
                //置空当前已经请求过的授权代表地址
                setRepresentativeFromInput(MessageConstants.Empty);
                //代表当前是点击「change」返回
                tcpRequestListener.modifyRepresentativeResult(changeStatus, responseJson.isSuccess(), responseJson.getCode());

            }
        } else {
            //置空当前已经请求过的授权代表地址
            setRepresentativeFromInput(MessageConstants.Empty);
            //如果当前「change」失败
            /*当前授权人地址与上一次一致*/
            /*当前授权人地址错误*/
            if (code == MessageConstants.CODE_2030
                    || code == MessageConstants.CODE_2033) {
                tcpRequestListener.modifyRepresentativeResult(changeStatus, responseJson.isSuccess(), responseJson.getCode());
                return;
            }
        }
    }

    /**
     * 关闭当前socket连接
     *
     * @param isStopSocket 是否停止socket停止
     */
    public static void closeSocket(boolean isStopSocket, String from) {
        LogTool.i(TAG, MessageConstants.socket.CLOSE_SOCKET + "form=>" + from + ";activeDisconnect:" + activeDisconnect);
        stopSocket = isStopSocket;
        keepAlive = false;
        LogTool.d(TAG, MessageConstants.socket.KILL + currentThread());
        try {
            if (buildSocket != null) {
                buildSocket.close();
                buildSocket = null;
            }
        } catch (Exception e) {
            LogTool.e(TAG, MessageConstants.socket.EXCEPTION + e.getMessage());
        }
        //清空授权代表信息
        setRepresentativeFromInput(MessageConstants.Empty);
        //清空未签章区块的接收队列
        clearGetReceiveBlockQueue();
        //关闭TCP接收读取线程
        closeTCPReceiveThread();
        HttpIntervalRequester.disposeRequest(HttpIntervalRequester.getReceiveBlockByIntervalDisposable);
        HttpIntervalRequester.disposeRequest(HttpIntervalRequester.getBalanceIntervalDisposable);
        //关闭心跳timer
        ObservableTimerTool.closeStartHeartBeatByIntervalTimer();
        //关闭TCP倒计时timer
        ObservableTimerTool.closeCountDownTCPConnectTimer();

    }

    /*清空当前未签章区块的队列*/
    private static void clearGetReceiveBlockQueue() {
//        LogTool.i(TAG, MessageConstants.socket.CLEAR_RECEIVE_QUEUE);
        //重置数据
        BCAASApplication.setNextObjectId(MessageConstants.Empty);
        currentSendVO = null;
        if (getWalletWaitingToReceiveQueue != null) {
            getWalletWaitingToReceiveQueue.clear();
        }
    }

    private static void closeTCPReceiveThread() {
        LogTool.i(TAG, MessageConstants.socket.CLOSE_TCP_RECEIVE_THREAD);
        if (TCPReceiveLooper != null) {
            TCPReceiveLooper.quit();
            TCPReceiveLooper = null;
        }
        tcpReceiveThread = null;
    }

    public static boolean isKeepAlive() {
        try {
            return keepAlive && buildSocket.getKeepAlive();
        } catch (SocketException e) {
            e.printStackTrace();
            LogTool.e(TAG, e.getMessage());
        }
        return false;
    }

    private HttpTransactionListener httpTransactionListener = new HttpTransactionListener() {
        @Override
        public void transactionAlreadyExists() {
            //交易记录已经存在了，将当前的send块置空
            currentSendVO = null;
        }

        @Override
        public void receiveBlockHttpSuccess() {
            // 开启对当前未签章区块TCP接口响应的倒计时
            ObservableTimerTool.countDownReceiveBlockResponseTimer(observableTimerListener);
        }

        @Override
        public void receiveBlockHttpFailure() {
            //清空当前队列，重新签章
            clearGetReceiveBlockQueue();
        }

    };

    private HttpASYNTCPResponseListener httpASYNTCPResponseListener = new HttpASYNTCPResponseListener() {

        @Override
        public void getLatestChangeBlockSuccess() {

        }

        @Override
        public void getLatestChangeBlockFailure(String failure) {
            // 执行「Change」的情况返回，返回code=0，然后执行「更改失败」
            tcpRequestListener.modifyRepresentativeResult(changeStatus, false, MessageConstants.CODE_0);
        }

        @Override
        public void resetSuccess() {
            //连接socket
            tcpRequestListener.resetSuccess();
        }

        @Override
        public void resetFailure() {
            LogTool.i(TAG, MessageConstants.socket.DATA_ACQUISITION_ERROR);
        }

        @Override
        public void logout() {
            tcpRequestListener.reLogin();
            closeSocket(true, "TCPLogoutListener");
        }

        @Override
        public void sendFailure() {
            tcpRequestListener.sendTransactionFailure(MessageConstants.Empty);

        }

        @Override
        public void canReset() {
            canReset = true;
        }

        @Override
        public void verifySuccess(String from) {

        }

        @Override
        public void verifyFailure(String from) {

        }
    };

    private void clearQueueAndReceive() {
        LogTool.e(TAG, MessageConstants.socket.CLEAR_QUEUE_AND_RECEIVE);
        //一分钟倒计时到，没有收到服务器响应
        //清空当前队列，重新开始签章
        clearGetReceiveBlockQueue();
        HttpIntervalRequester.startGetWalletWaitingToReceiveBlockLoop(httpASYNTCPResponseListener);
    }

    private ObservableTimerListener observableTimerListener = new ObservableTimerListener() {
        @Override
        public void timeUp(String from) {
            if (StringTool.notEmpty(from)) {
                switch (from) {
                    case Constants.TimerType.COUNT_DOWN_TCP_CONNECT:
                        resetSAN();
                        break;
                    case Constants.TimerType.COUNT_DOWN_TCP_HEARTBEAT:
                        //向SAN发送心跳信息
                        HeartBeatBean heartBeatBean = new HeartBeatBean(MessageConstants.socket.HEART_BEAT_CS);
                        writeStr = GsonTool.string(heartBeatBean);
                        writeTOSocket(true);
                        break;
                    case Constants.TimerType.COUNT_DOWN_RECEIVE_BLOCK_RESPONSE:
                        clearQueueAndReceive();
                        break;
                }
            }
        }
    };
}
