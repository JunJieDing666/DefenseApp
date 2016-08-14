package com.jj.defense.Utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by Administrator on 2016/8/11.
 */
public class ServiceUtils {
    /**
     * @param ctx         上下文环境
     * @param serviceName 服务的名称
     * @return 判断该服务是否开启
     */
    public static boolean isRunnig(Context ctx, String serviceName) {
        //1.获取activitymanager来获得所有正在运行的服务
        ActivityManager mAM = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //2.获得所有服务信息的一个集合
        List<ActivityManager.RunningServiceInfo> runningServices = mAM.getRunningServices(1000);
        //3.遍历所有服务，与要判断的服务名作比较
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            if (serviceName.equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
