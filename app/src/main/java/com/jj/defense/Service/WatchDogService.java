package com.jj.defense.Service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.jj.defense.Activity.EnterPsdActivity;
import com.jj.defense.Engine.AppLockDao;

import java.util.List;

/**
 * Created by Administrator on 2016/9/13.
 */
public class WatchDogService extends Service {

    private boolean isWatching;
    private String packageName;
    private AppLockDao mAppLockDao;

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
        watch();
        super.onCreate();
    }

    private void watch() {
        //1.开启一个子线程，并做可控的死循环
        new Thread() {
            @Override
            public void run() {
                List<String> mPackageNameList = mAppLockDao.findAll();
                while (isWatching) {
                    //2.监听正在开启的应用,首先要获得活动管理者
                    ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                    //做手机版本的判断
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        //3.获得正在运行的任务栈
                        List<ActivityManager.AppTask> appTasks = am.getAppTasks();
                        ActivityManager.AppTask appTask = appTasks.get(0);
                        //4.获取该任务栈的信息，并获得该应用包名
                        ActivityManager.RecentTaskInfo taskInfo = appTask.getTaskInfo();
                        packageName = taskInfo.topActivity.getPackageName();
                    } else {
                        //3.获得正在运行的任务栈
                        List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
                        //4.获取该任务栈的信息，并获得该应用包名
                        ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
                        packageName = runningTaskInfo.topActivity.getPackageName();
                    }
                    //5.拿到已加锁的应用集合做对比，如果匹配上了就打开锁码界面
                    if (mPackageNameList.contains(packageName)) {
                        Intent intent = new Intent(getApplicationContext(),EnterPsdActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("packageName", packageName);
                        startActivity(intent);
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
