package com.jj.defense.Service;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.jj.defense.Activity.EnterPsdActivity;
import com.jj.defense.Engine.AppLockDao;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Administrator on 2016/9/13.
 */
public class WatchDogService extends Service {

    private boolean isWatching;
    private String packageName;
    private AppLockDao mAppLockDao;
    private List<String> mPackageNameList;
    private SkipPackageInnerReceiver mSkipPackageInnerReceiver;
    private String mSkipPackageName;
    private MyContentObserver myContentObserver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        isWatching = true;
        mAppLockDao = AppLockDao.getInstance(this);
        //开启任务栈监听，判断是否为程序锁中要拦截的应用
        if (!hasPermission()) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), "该功能需要开启权限", Toast.LENGTH_SHORT).show();
        }
        watch();

        //接收广播，内容为可以跳过看门狗循环的应用包名
        IntentFilter intentFilter = new IntentFilter("android.intent.action.skip");
        mSkipPackageInnerReceiver = new SkipPackageInnerReceiver();
        registerReceiver(mSkipPackageInnerReceiver, intentFilter);

        //注册内容观察者，监听对象内容的变化
        myContentObserver = new MyContentObserver(new Handler());
        getContentResolver().registerContentObserver(Uri.parse("content://applock/change"), true, myContentObserver);
        super.onCreate();
    }

    /**
     * @return 判断是否拥有获取进程的权限
     */
    private boolean hasPermission() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }


    private void watch() {
        //1.开启一个子线程，并做可控的死循环
        new Thread() {
            @Override
            public void run() {
                mPackageNameList = mAppLockDao.findAll();
                while (isWatching) {
                    //2.监听正在开启的应用,首先要获得活动管理者
                    ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                    //做手机版本的判断
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                        //4.
                        UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
                        long time = System.currentTimeMillis();

                        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);

                        if (stats != null) {
                            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                            for (UsageStats usageStats : stats) {
                                mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                            }
                            if (mySortedMap != null && !mySortedMap.isEmpty()) {
                                packageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                            }
                        }
                    } else {
                        //3.获得正在运行的任务栈
                        List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
                        //4.获取该任务栈的信息，并获得该应用包名
                        ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
                        packageName = runningTaskInfo.topActivity.getPackageName();
                    }
                    //5.拿到已加锁的应用集合做对比，如果匹配上了就打开锁码界面
                    if (mPackageNameList.contains(packageName)) {
                        if (!packageName.equals(mSkipPackageName)) {
                            Intent intent = new Intent(getApplicationContext(), EnterPsdActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("packageName", packageName);
                            startActivity(intent);
                        }
                    }
                    //时间片轮转
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        //销毁服务时应该做的
        //1.停止关门狗
        isWatching = false;
        //2.注销广播接收者
        if (mSkipPackageInnerReceiver != null) {
            unregisterReceiver(mSkipPackageInnerReceiver);
        }
        //3.注销内容观察者
        if (myContentObserver != null) {
            getContentResolver().unregisterContentObserver(myContentObserver);
        }
        super.onDestroy();
    }

    private class SkipPackageInnerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mSkipPackageName = intent.getStringExtra("packageName");
        }
    }

    private class MyContentObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            //一旦数据库内容发生改变，就重新获取加锁应用包名所在集合数据
            new Thread() {
                @Override
                public void run() {
                    mPackageNameList = mAppLockDao.findAll();
                }
            }.start();
            super.onChange(selfChange);
        }
    }
}
