package com.jj.defense.Service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import com.jj.defense.Engine.ProcessInfoProvider;
import com.jj.defense.R;
import com.jj.defense.Receiver.MyAppWidgetProvider;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/9/8.
 */
public class UpdateWidgetService extends Service {

    private Timer mTimer;
    private InnerScreenStateReceiver mInnerScreenStateReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //管理窗体小部件的UI更新（定时器）
        startTimer();

        //注册一个监听开关屏幕的广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);

        mInnerScreenStateReceiver = new InnerScreenStateReceiver();
        registerReceiver(mInnerScreenStateReceiver,intentFilter);

        super.onCreate();
    }

    private void startTimer() {
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateAppWidget();
                //Log.i("screen","5秒打印一次。。。。。。。。。。。。。。");
            }
        }, 0, 5000);
    }

    private void updateAppWidget() {
        //1.获得AppWidget的管理者
        AppWidgetManager aWM = AppWidgetManager.getInstance(this);
        //2.获得窗体小部件的view
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget);
        //3.给view对象的内部控件赋值
        remoteViews.setTextViewText(R.id.tv_process_count, "进程总数：" + ProcessInfoProvider.getProcessCount(this));
        String strAvailSpace = android.text.format.Formatter.formatFileSize(this, ProcessInfoProvider.getAvailSpace(this));
        remoteViews.setTextViewText(R.id.tv_process_memory, "可用内存:" + strAvailSpace);
        //4.点击小部件进入应用
        Intent intent = new Intent("android.intent.action.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_root, pendingIntent);
        //5.点击一件清理清理进程
        Intent broadcastIntent = new Intent("android.intent.action.KILL_PROCESS");
        PendingIntent broadcastPendingIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_clear, broadcastPendingIntent);
        //6.更新小部件
        ComponentName componentName = new ComponentName(this, MyAppWidgetProvider.class);
        aWM.updateAppWidget(componentName, remoteViews);
    }

    @Override
    public void onDestroy() {
        if (mInnerScreenStateReceiver != null) {
            unregisterReceiver(mInnerScreenStateReceiver);
        }
        //如果关闭了此服务，就不需要定时任务了
        endTimer();
        super.onDestroy();
    }

    private class InnerScreenStateReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                //开启定时任务
                startTimer();
            } else {
                //关闭定时任务
                endTimer();
            }
        }
    }

    private void endTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            //让垃圾回收机制回收定时器
            mTimer = null;
        }
    }
}
