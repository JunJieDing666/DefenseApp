package com.jj.defense.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.jj.defense.R;
import com.jj.defense.Service.LoctionService;
import com.jj.defense.Utils.ConstantValue;
import com.jj.defense.Utils.SpUtils;

/**
 * Created by Administrator on 2016/7/28.
 */
public class SMSReceiver extends BroadcastReceiver {
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
                if (messageBody.contains("#*alarm*#")){
                    //8.播放音乐
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.setVolume(100,100);
                    mediaPlayer.start();
                }

                //1.判断短信是否包含获取经纬度坐标的信息
                if(messageBody.contains("#*location*#")){
                    //2.开启服务来获取坐标
                    context.startService(new Intent(context, LoctionService.class));
                }
            }
        }
        //
    }
}
