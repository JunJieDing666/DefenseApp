package com.jj.defense.Receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.jj.defense.R;
import com.jj.defense.Service.LoctionService;
import com.jj.defense.Utils.ConstantValue;
import com.jj.defense.Utils.SpUtils;

/**
 * Created by Administrator on 2016/7/28.
 */
public class SMSReceiver extends BroadcastReceiver {
    //获得设备政策管理者
    private DevicePolicyManager mDPM = null;
    //判断是否激活权限所需要的组件名
    private ComponentName mDeviceAdminSample = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        //1.判断是否开启了防盗保护
        boolean open_defense = SpUtils.getBoolean(context, ConstantValue.OPEN_DEFENSE, false);
        //2.获取短信内容
        if (open_defense) {
            //3.得到pdus短信数组
                Object[] pdus = (Object[]) intent.getExtras().get("pdus");
                //4.循环遍历pdus数组
                for (Object pdu : pdus) {
                    //5.获取其中每条短信的对象
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                //6.获取该条短信的基本内容
                String originatingAddress = smsMessage.getOriginatingAddress();
                String messageBody = smsMessage.getMessageBody();

                //7.判断该条短信是否包含播放报警音乐的信息
                if (messageBody.contains("#*alarm*#")) {
                    //8.播放音乐
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.setVolume(100, 100);
                    mediaPlayer.start();
                }

                //1.判断短信是否包含获取经纬度坐标的信息
                if (messageBody.contains("#*location*#")) {
                    //2.开启服务来获取坐标
                    context.startService(new Intent(context, LoctionService.class));
                }

                mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                mDeviceAdminSample = new ComponentName(context, DeviceAdmin.class);
                //锁定屏幕，且可以更改锁屏密码
                if (messageBody.contains("#*lockscreen*#")) {
                    if (mDPM.isAdminActive(mDeviceAdminSample)) {
                        //锁定屏幕
                        mDPM.lockNow();
                        //设置锁屏密码
                        mDPM.resetPassword("", 0);
                    } else {
                        Toast.makeText(context, "请激活设备管理器(进入设置-安全-设备管理器勾选)", Toast.LENGTH_SHORT).show();
                    }
                }

                //清除手机数据，还可清除sd卡数据
                if (messageBody.contains("#*wipedata*#")) {
                    if (mDPM.isAdminActive(mDeviceAdminSample)) {
                        //清除手机本机数据
                        mDPM.wipeData(0);
                        //清除手机sd卡数据
                        //mDPM.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
                    } else {
                        Toast.makeText(context, "请激活设备管理器(进入设置-安全-设备管理器勾选)", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }

    }

}
