package com.jj.defense.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.jj.defense.Engine.AddressDao;
import com.jj.defense.R;
import com.jj.defense.Utils.ConstantValue;
import com.jj.defense.Utils.SpUtils;

/**
 * Created by Administrator on 2016/8/11.
 */
public class AddressService extends Service {

    private TelephonyManager mTM;
    private MyPhoneStateListener myPhoneStateListener;
    private String tag = "AddressService";
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private View mViewToast;
    private WindowManager mWM;
    private String mAddress;
    private TextView tv_toast;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            tv_toast.setText(mAddress);
        }
    };
    private int[] mToastDrawableIds;
    private int startX;
    private int startY;
    private int mScreenWidth;
    private int mScreenHeight;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        /*第一次开启此服务后就要去管理吐司的显示*/

        //监听电话状态
        //1.获得电话管理者对象
        mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //2.监听电话
        myPhoneStateListener = new MyPhoneStateListener();
        mTM.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        //获得窗体管理者以便管理吐司的显示
        mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mScreenWidth = mWM.getDefaultDisplay().getWidth();
        mScreenHeight = mWM.getDefaultDisplay().getHeight();

        super.onCreate();
    }

    class MyPhoneStateListener extends PhoneStateListener {
        //3.手动重写电话状态发生改变会触发的方法
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    //空闲状态，移除吐司
                    if (mWM != null && mViewToast != null) {
                        mWM.removeView(mViewToast);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //摘机状态
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //响铃状态，跳出吐司
                    showToast(incomingNumber);
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    private void showToast(String incomingNumber) {
        //跳出吐司
        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //让吐司呈现透明样式
        params.format = PixelFormat.TRANSLUCENT;
        //指定吐司的显示级别，在响铃时可显示
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        /*指定吐司的位置*/
        //默认的坐标
        params.gravity = Gravity.LEFT + Gravity.TOP;
        //设定的坐标
        params.x = SpUtils.getInt(getApplicationContext(), ConstantValue.LOCATION_X, 0);
        params.y = SpUtils.getInt(getApplicationContext(), ConstantValue.LOCATION_Y, 0);

        //指定吐司的布局效果,并挂载到windowmanager上(需要权限)
        mViewToast = View.inflate(this, R.layout.toast_view, null);
        tv_toast = (TextView) mViewToast.findViewById(R.id.tv_toast);
        //在窗体管理者上指定吐司布局效果和规则
        mWM.addView(mViewToast, mParams);

        //将吐司背景色图片资源存进数组以供匹配
        mToastDrawableIds = new int[]{R.drawable.call_locate_white,
                R.drawable.call_locate_orange,
                R.drawable.call_locate_blue,
                R.drawable.call_locate_gray,
                R.drawable.call_locate_green};
        //得到sp中存储的吐司背景色的索引值
        int toastStyleIndex = SpUtils.getInt(getApplicationContext(), ConstantValue.TOAST_STYLE, 0);
        tv_toast.setBackgroundResource(mToastDrawableIds[toastStyleIndex]);

        //查询到号码的归属地并显示
        query(incomingNumber);

        mViewToast.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //按下时左上角的坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //移动后左上角的坐标
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();

                        //移动的距离
                        int disX = moveX - startX;
                        int disY = moveY - startY;

                        //1.当前控件所在屏幕的位置
                        params.x = params.x + disX;
                        params.y = params.y + disY;

                        //更新吐司的位置
                        mWM.updateViewLayout(mViewToast,params);

                        //吐司框不能出了activity界面
                        if (params.x < 0) {
                            params.x = 0;
                        }

                        if (params.x > mScreenWidth - mViewToast.getWidth()) {
                            params.x = mScreenWidth - mViewToast.getWidth();
                        }

                        if (params.y < 0) {
                            params.y = 0;
                        }

                        if (params.y > mScreenHeight - 22 - mViewToast.getHeight()) {
                            params.y = mScreenHeight - 22 - mViewToast.getHeight();
                        }


                        //3.重置一次起始坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP:
                        //记录下移动控件的坐标
                        SpUtils.putInt(getApplicationContext(), ConstantValue.LOCATION_X, params.x);
                        SpUtils.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, params.y);
                        break;
                }
                return true;
            }
        });
    }

    private void query(final String incomingNumber) {
        //查询号码为耗时操作
        new Thread() {
            @Override
            public void run() {
                mAddress = AddressDao.getAddress(incomingNumber);
                //查询到后发送空消息让主线程更新UI
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        //销毁服务无法关闭电话监听，需要手动关闭
        if (mTM != null && myPhoneStateListener != null) {
            mTM.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        super.onDestroy();
    }
}
