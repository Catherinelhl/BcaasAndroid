package io.bcaas.tools;

import io.bcaas.base.BCAASApplication;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.listener.ObservableTimerListener;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

import static io.reactivex.Observable.timer;

/**
 * @author catherine.brainwilliam
 * @since 2018/10/20
 * <p>
 * 工具類：时间倒计时、定时管理工具
 */
public class ObservableTimerTool {

    private static String TAG = ObservableTimerTool.class.getSimpleName();

    /*倒计时观察者*/
    private static Disposable countDownTCPConnectDisposable;
    /*关闭通知显示的观察者*/
    private static Disposable countDownNotificationDisposable;
    /*定时发送心跳观察者*/
    private static Disposable heartBeatByIntervalDisposable;
    /*定时刷新*/
    private static Disposable countDownRefreshViewDisposable;
    /*SAN对C的签章区块响应观察*/
    private static Disposable countDownReceiveBlockResponseDisposable;
    /*定时查询当前的内存信息*/
    private static Disposable logDisposable;

    /**
     * 开始TCP连接、心跳响应10s倒计时
     */
    public static void startCountDownTCPConnectTimer(ObservableTimerListener observableTimerListener) {
        LogTool.d(TAG, MessageConstants.socket.START_COUNT_DOWN_TIMER);
        closeCountDownTCPConnectTimer();
        if (BCAASApplication.tokenIsNull()) {
            //如果当前的token为null，那么就停止所有循环
        } else {
            timer(Constants.ValueMaps.COUNT_DOWN_TIME, TimeUnit.SECONDS)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Long>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            countDownTCPConnectDisposable = d;
                        }

                        @Override
                        public void onNext(Long value) {
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                            LogTool.d(TAG, MessageConstants.socket.COUNT_DOWN_OVER);
                            //关闭计时
                            closeCountDownTCPConnectTimer();
                            if (observableTimerListener != null) {
                                //如果10s之后还没有收到收到SAN的信息，那么需要resetSAN
                                observableTimerListener.timeUp(Constants.TimerType.COUNT_DOWN_TCP_CONNECT);
                            }
                        }
                    });
        }
    }

    /**
     * 关闭TCP连接、心跳响应倒计时
     */
    public static void closeCountDownTCPConnectTimer() {
        if (countDownTCPConnectDisposable != null) {
            LogTool.i(TAG, MessageConstants.socket.CLOSE_COUNT_DOWN_TIMER);
            countDownTCPConnectDisposable.dispose();
        }
    }

    /**
     * 开始与SAN心跳30秒定时发送
     *
     * @param observableTimerListener
     */
    public static void startHeartBeatByIntervalTimer(ObservableTimerListener observableTimerListener) {
        LogTool.d(TAG, MessageConstants.socket.START_HEART_BEAT_BY_INTERVAL_TIMER);
        if (BCAASApplication.tokenIsNull()) {
            //如果当前的token为null，那么就停止所有循环
            return;
        }
//        int count_time = 30; //总时间
        Observable.interval(0, Constants.ValueMaps.HEART_BEAT_TIME, TimeUnit.SECONDS)
//                .take(count_time + 1)//设置总共发送的次数
//                .map(new io.reactivex.functions.Function<Long, Long>() {
//                    @Override
//                    public Long apply(Long aLong) throws Exception {
//                        //aLong从0开始
//                        return count_time - aLong;
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        heartBeatByIntervalDisposable = d;
                    }

                    @Override
                    public void onNext(Long value) {
                        if (observableTimerListener != null) {
                            observableTimerListener.timeUp(Constants.TimerType.COUNT_DOWN_TCP_HEARTBEAT);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * 关闭与SAN心跳30秒定时发送器
     */
    public static void closeStartHeartBeatByIntervalTimer() {
        if (heartBeatByIntervalDisposable != null) {
            LogTool.i(TAG, MessageConstants.socket.CLOSE_START_HEART_BEAT_BY_INTERVAL_TIMER);
            heartBeatByIntervalDisposable.dispose();
        }
    }

    /**
     * 关闭计时，通过给定的时间
     *
     * @param stayTime                停留的时间
     * @param observableTimerListener
     */
    public static void countDownTimerBySetTime(long stayTime, ObservableTimerListener observableTimerListener) {
        countDownTimerBySetTime(stayTime, TimeUnit.SECONDS, observableTimerListener);
    }

    public static void countDownTimerBySetTime(long stayTime, TimeUnit timeUnit, ObservableTimerListener observableTimerListener) {
        timer(stayTime, timeUnit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        countDownNotificationDisposable = d;
                    }

                    @Override
                    public void onNext(Long value) {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        if (observableTimerListener != null) {
                            observableTimerListener.timeUp(Constants.TimerType.COUNT_DOWN_NOTIFICATION);
                        }
                        //关闭计时
                        if (countDownNotificationDisposable != null) {
                            countDownNotificationDisposable.dispose();
                        }
                    }
                });
    }


    /**
     * TV版引导页面一个刷新焦点的倒计时
     *
     * @param observableTimerListener
     */
    public static void resetRequestFocus(ObservableTimerListener observableTimerListener) {
        timer(Constants.ValueMaps.COUNT_DOWN_GUIDE_TV, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        countDownRefreshViewDisposable = d;
                    }

                    @Override
                    public void onNext(Long value) {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        if (observableTimerListener != null) {
                            observableTimerListener.timeUp(Constants.TimerType.COUNT_DOWN_REFRESH_VIEW);
                        }
                        //关闭计时
                        if (countDownRefreshViewDisposable != null) {
                            countDownRefreshViewDisposable.dispose();
                        }
                    }
                });
    }

    /**
     * 开始对「receive」发送，等待回应 60s倒计时
     */
    public static void countDownReceiveBlockResponseTimer(ObservableTimerListener observableTimerListener) {
        LogTool.d(TAG, MessageConstants.socket.START_COUNT_DOWN_RECEIVE_BLOCK_RESPONSE_TIMER);
        closeCountDownReceiveBlockResponseTimer();
        timer(Constants.ValueMaps.COUNT_DOWN_RECEIVE_BLOCK_TIME, TimeUnit.MINUTES)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        countDownReceiveBlockResponseDisposable = d;
                    }

                    @Override
                    public void onNext(Long value) {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        LogTool.d(TAG, MessageConstants.socket.COUNT_DOWN_OVER);
                        //关闭计时
                        closeCountDownReceiveBlockResponseTimer();
                        if (observableTimerListener != null) {
                            //如果10s之后还没有收到收到SAN的信息，那么需要resetSAN
                            observableTimerListener.timeUp(Constants.TimerType.COUNT_DOWN_RECEIVE_BLOCK_RESPONSE);
                        }
                    }
                });
    }

    /**
     * 关闭对「receive」发送，等待回应 60s倒计时
     */
    public static void closeCountDownReceiveBlockResponseTimer() {
        if (countDownReceiveBlockResponseDisposable != null) {
            LogTool.i(TAG, MessageConstants.socket.CLOSE_COUNT_DOWN_RECEIVE_BLOCK_RESPONSE_TIMER);
            countDownReceiveBlockResponseDisposable.dispose();
        }
    }


    /**
     * 开始与SAN心跳30秒定时发送
     *
     * @param observableTimerListener
     */
    public static void startLogByIntervalTimer(ObservableTimerListener observableTimerListener) {
        LogTool.d(TAG, "startLogByIntervalTimer");
        closeLogByIntervalTimer();
        Observable.interval(0, Constants.ValueMaps.LOG_TIME, TimeUnit.SECONDS)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        logDisposable = d;
                    }

                    @Override
                    public void onNext(Long value) {
                        if (observableTimerListener != null) {
                            observableTimerListener.timeUp("startLogByIntervalTimer");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * 关闭与SAN心跳30秒定时发送器
     */
    public static void closeLogByIntervalTimer() {
        if (logDisposable != null) {
            LogTool.i(TAG, "closeLogByIntervalTimer");
            logDisposable.dispose();
        }
    }

}
