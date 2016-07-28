package com.jj.defense.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.jj.defense.Utils.ConstantValue;
import com.jj.defense.Utils.SpUtils;

/**
 * Created by Administrator on 2016/7/28.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //1.接收原本sp里存储的sim卡
        String spSimNum = SpUtils.getString(context, ConstantValue.SIM_NUM, "");
        //2.获取开机后当前sim卡
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simNum = tm.getSimSerialNumber();
        //3.将两者作比较
        if (!spSimNum.equals(simNum)) {
            //4.给安全号码发送短信
            SmsManager smsManager = SmsManager.getDefault();
            String phone = SpUtils.getString(context, ConstantValue.PHONE_NUM, "");
            smsManager.sendTextMessage(phone, null, "sim number changed!!!!" + "\nThe new number is " + simNum, null, null);
        }
    }
}
