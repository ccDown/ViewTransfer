package com.soullistener.viewtransfer.utils;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.soullistener.viewtransfer.utils.cockroach.Cockroach;
import com.soullistener.viewtransfer.utils.cockroach.ExceptionHandler;

/**
 * @author kuan
 * Created on 2018/12/19.
 * @description
 */
public class ViewTransferApplication extends Application {

//    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();

        Utils.init(this);

        //Logger初始化
        initLogger();

        //LeakCanary初始化
//        refWatcher= setupLeakCanary();

        //防crash工具
        cockroachInstall();
    }

    private void initLogger() {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)      //（可选）是否显示线程信息。 默认值为true
                .methodCount(2)               // （可选）要显示的方法行数。 默认2
                .methodOffset(7)               // （可选）设置调用堆栈的函数偏移值，0的话则从打印该Log的函数开始输出堆栈信息，默认是0
                .tag("ViewTransfer")                  //（可选）每个日志的全局标记。 默认PRETTY_LOGGER（如上图）
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));

    }

    private void cockroachInstall() {
        Cockroach.install(new ExceptionHandler() {
            @Override
            protected void onUncaughtExceptionHappened(final Thread thread, final Throwable throwable) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showLong("发生错误:"+throwable.getMessage());
                        Logger.e("发生错误"+throwable.toString());
                        throwable.printStackTrace();//打印警告级别log
                    }
                });
            }

            @Override
            protected void onBandageExceptionHappened(final Throwable throwable) {
                //打印警告级别log，该throwable可能是最开始的bug导致的，无需关心
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
//                        ToastUtils.showLong("发生错误:"+throwable.getMessage());
                        Logger.e("再次发生错误"+throwable.toString());
                        throwable.printStackTrace();//打印警告级别log
                    }
                });


                throwable.printStackTrace();
            }

            @Override
            protected void onEnterSafeMode() {
                ToastUtils.showLong("onEnterSafeMode");

            }

            @Override
            protected void onMayBeBlackScreen(Throwable e) {
                Thread thread = Looper.getMainLooper().getThread();
                Log.e("AndroidRuntime", "--->onUncaughtExceptionHappened:" + thread + "<---", e);
                //黑屏时建议直接杀死app

//                sysExcepHandler.uncaughtException(thread, new RuntimeException("black screen"));
            }

        });

    }

}
