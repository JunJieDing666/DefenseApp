package com.jj.defense.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.jj.defense.Engine.BlackNumberDao;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2016/8/19.
 */
public class BlackNumberService extends Service {

    private InnerSmsReceiver mInnerSmsReceiver;
    private BlackNumberDao mBlackNumberDao;
    private TelephonyManager mTM;
    private MyPhoneStateListener myPhoneStateListener;
    private MyContentObserver mContentObserver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mBlackNumberDao = BlackNumberDao.getInstance(getApplicationContext());

        //拦截短信
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(1000);

        mInnerSmsReceiver = new InnerSmsReceiver();
        registerReceiver(mInnerSmsReceiver, intentFilter);

        //拦截电话,在响铃状态的时候挂断电话
        //首先要监听电话状态
        //1.获得电话管理者对象
        mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //2.监听电话
        myPhoneStateListener = new MyPhoneStateListener();
        mTM.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        super.onCreate();
    }

    class MyPhoneStateListener extends PhoneStateListener {
        //3.手动重写电话状态发生改变会触发的方法
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    //空闲状态
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //摘机状态
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //响铃状态，挂断黑名单电话,endCall方法放在了aidl文件中
                    endCall(incomingNumber);
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    private void endCall(String incomingNumber) {
        int mode = mBlackNumberDao.getMode(incomingNumber);
        if (mode == 2 || mode == 3) {
            //拦截电话
            try {
                /*ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
                 * ServiceManager对开发者隐藏，需反射调用*/
                //1.获得ServiceManager的字节码
                Class<?> clazz = Class.forName("android.os.ServiceManager");
                //2.获取方法
                Method method = clazz.getMethod("getService", String.class);
                //3.反射调用此方法
                IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
                //4.调用获取aidl文件的方法,即获得ITelephony
                ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
                //5.使用ITelephony结束响铃
                iTelephony.endCall();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //6.删除通话记录
            mContentObserver = new MyContentObserver(new Handler(), incomingNumber);
            getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"), true, mContentObserver);
        }

    }

    @Override
    public void onDestroy() {
        //注销广播
        if (mInnerSmsReceiver != null) {
            unregisterReceiver(mInnerSmsReceiver);
        }
        //注销内容观察者
        if (mContentObserver != null) {
            getContentResolver().unregisterContentObserver(mContentObserver);
        }
        //注销电话监听器
        if (mTM != null) {
            mTM.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        super.onDestroy();
    }

    private class InnerSmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取短信的发送号码，如果此号码在黑名单中，且拦截模式为1、3，则拦截该短信
            //1.得到pdus短信数组
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            //2.循环遍历pdus数组
            for (Object pdu : pdus) {
                //3.获取其中每条短信的对象
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                //4.获取该条短信的发送号码
                String originatingAddress = smsMessage.getOriginatingAddress();
                int mode = mBlackNumberDao.getMode(originatingAddress);
                if (mode == 1 || mode == 3) {
                    //拦截短信
                    abortBroadcast();
                }
            }
        }
    }

    private class MyContentObserver extends ContentObserver {
        private final String incomingNumber;

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler, String incomingNumber) {
            super(handler);
            this.incomingNumber = incomingNumber;
        }

        @Override
        public void onChange(boolean selfChange) {
            //插入一条数据后再进行删除
            getContentResolver().delete(Uri.parse("content://call_log/calls"), "number = ?", new String[]{incomingNumber});
            super.onChange(selfChange);
        }
    }
}
