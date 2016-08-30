package com.jj.defense.Engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.jj.defense.DB.Domain.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/26.
 */
public class AppInfoProvider {
    public static List<AppInfo> getAppInfoList(Context ctx) {
        //1.获取包管理者
        PackageManager packageManager = ctx.getPackageManager();
        //2.获得安装在手机上的应用的包的相关信息集合
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //3.遍历该集合获得所有需要的信息(包名，应用名，图标，所在位置（手机内存、sd卡），应用类型（系统应用、用户应用）)
        List<AppInfo> appInfoList = new ArrayList<>();
        for (PackageInfo packageInfo : packageInfos) {
            AppInfo appInfo = new AppInfo();
            //4.获得应用包名
            appInfo.packageName = packageInfo.packageName;
            //5.获得应用应用名
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            appInfo.applicationName = applicationInfo.loadLabel(packageManager).toString();
            //6.获得图标
            appInfo.icon = applicationInfo.loadIcon(packageManager);
            //7.判断是否存储在sd卡里
            if ((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
                //在sd卡里
                appInfo.isSDcard = true;
            } else {
                //不在sd卡里
                appInfo.isSDcard = false;
            }
            //判断是否为系统应用
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                //是系统应用
                appInfo.isSystemApp = true;
            } else {
                //不是系统应用
                appInfo.isSystemApp = false;
            }
            appInfoList.add(appInfo);
        }
        return appInfoList;
    }
}
