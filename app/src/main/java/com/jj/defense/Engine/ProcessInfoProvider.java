package com.jj.defense.Engine;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.*;

import com.jj.defense.DB.Domain.ProcessInfo;
import com.jj.defense.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/30.
 */
public class ProcessInfoProvider {
    /**
     * @param ctx 上下文环境
     * @return 获得正在运行的进程数
     */
    public static int getProcessCount(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) ctx.getSystemService(Context.USAGE_STATS_SERVICE);
            long now = System.currentTimeMillis();
            List<UsageStats> usageStatses = usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST, now - 30 * 1000, now);
            return usageStatses.size();
        } else {
            //1.获得活动管理者
            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            //2.调用am的方法获得正在运行的进程集合
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
            //3.返回结果
            return runningAppProcesses.size();
        }
    }

    /**
     * @param ctx 上下文环境
     * @return 返回可用内存大小 bytes
     */
    public static long getAvailSpace(Context ctx) {
        //1.获得活动管理者
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //2.构建存储可用内存信息的对象
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //3.得到可用内存信息
        am.getMemoryInfo(memoryInfo);
        //4.返回可用内存大小
        return memoryInfo.availMem;
    }

    /**
     * @param ctx 上下文环境
     * @return 返回总共的内存大小 bytes
     */
    public static long getTotalSpace(Context ctx) {
        //1.获得活动管理者
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //2.构建存储内存信息的对象
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //3.得到内存信息
        am.getMemoryInfo(memoryInfo);
        //4.返回内存大小
        return memoryInfo.totalMem;
    }

    /*
     * @param ctx 上下文环境
     * @return 返回进程信息的集合
     */
    public static List<ProcessInfo> getProcessInfoList1(Context ctx) {
        List<ProcessInfo> processInfoList = new ArrayList<ProcessInfo>();
        //1.获得活动管理者
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = ctx.getPackageManager();
        //2.调用am的方法获得正在运行的进程集合
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        //3.遍历该集合，得到进程信息
        for (ActivityManager.RunningAppProcessInfo info : runningAppProcesses) {
            ProcessInfo processInfo = new ProcessInfo();
            //4.得到进程的名称 = 应用的包名
            processInfo.packageName = info.processName;
            //5.得到进程的占用内存的大小(先得到该进程pid，再获取其内存信息数组,然后根据索引获取所要进程的内存信息，最后获得该进程已用内存)
            Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});
            Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
            processInfo.memSize = memoryInfo.getTotalPrivateDirty() * 1024;
            try {
                //6.获得应用的名称
                ApplicationInfo applicationInfo = pm.getApplicationInfo(processInfo.packageName, 0);
                processInfo.applicationName = applicationInfo.loadLabel(pm).toString();
                //7.获得应用的图标
                processInfo.icon = applicationInfo.loadIcon(pm);
                //8.判断是否为系统进程
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                    processInfo.isSystem = true;
                } else {
                    processInfo.isSystem = false;
                }
            } catch (PackageManager.NameNotFoundException e) {
                //进程不一定是应用，可能找不到，需要处理该种情况
                processInfo.applicationName = info.processName;
                processInfo.icon = ctx.getResources().getDrawable(R.drawable.safe);
                processInfo.isSystem = true;
                e.printStackTrace();
            }
            processInfoList.add(processInfo);
        }

        return processInfoList;
    }

    /**
     * @param ctx 上下文环境
     * @return 返回进程信息的集合
     */
    public static List<ProcessInfo> getProcessInfoList(Context ctx) {
        List<ProcessInfo> processInfoList = new ArrayList<ProcessInfo>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) ctx.getSystemService(Context.USAGE_STATS_SERVICE);
            long now = System.currentTimeMillis();
            List<UsageStats> usageStatses = usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST, now - 1000, now);

            PackageManager pm = ctx.getPackageManager();
            //3.遍历该集合，得到进程信息
            for (UsageStats usageStats : usageStatses) {
                ProcessInfo processInfo = new ProcessInfo();
                //4.得到进程的名称 = 应用的包名
                processInfo.packageName = usageStats.getPackageName();
                /*//5.得到进程的占用内存的大小
                Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});
                Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
                processInfo.memSize = memoryInfo.getTotalPrivateDirty() * 1024;*/
                try {
                    //6.获得应用的名称
                    ApplicationInfo applicationInfo = pm.getApplicationInfo(processInfo.packageName, 0);
                    processInfo.applicationName = applicationInfo.loadLabel(pm).toString();
                    //7.获得应用的图标
                    processInfo.icon = applicationInfo.loadIcon(pm);
                    //8.判断是否为系统进程
                    if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                        processInfo.isSystem = true;
                    } else {
                        processInfo.isSystem = false;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    //进程不一定是应用，可能找不到，需要处理该种情况
                    processInfo.applicationName = usageStats.getPackageName();
                    processInfo.icon = ctx.getResources().getDrawable(R.drawable.safe);
                    processInfo.isSystem = true;
                    e.printStackTrace();
                }
                processInfoList.add(processInfo);
            }
        }

        return processInfoList;
    }

    /**
     * @param ctx         上下文环境
     * @param processInfo 杀掉的进程
     */
    public static void killProcess(Context ctx, ProcessInfo processInfo) {
        //1.获得活动管理者
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //2.杀掉进程(权限)
        am.killBackgroundProcesses(processInfo.packageName);
    }

    /** 杀死所有进程
     * @param ctx   上下文环境
     */
    public static void killAll(Context ctx) {
        //1.获得活动管理者
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

        List<ProcessInfo> processInfoList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            processInfoList = ProcessInfoProvider.getProcessInfoList(ctx);
        } else {
            processInfoList = ProcessInfoProvider.getProcessInfoList1(ctx);
        }

        for (ProcessInfo processInfo : processInfoList) {
            if (processInfo.packageName.equals(ctx.getPackageName())) {
                continue;
            }
            am.killBackgroundProcesses(processInfo.packageName);
        }
    }
}
