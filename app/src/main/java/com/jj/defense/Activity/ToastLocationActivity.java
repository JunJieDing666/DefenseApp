package com.jj.defense.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jj.defense.R;
import com.jj.defense.Utils.ConstantValue;
import com.jj.defense.Utils.SpUtils;

/**
 * Created by Administrator on 2016/8/12.
 */
public class ToastLocationActivity extends Activity {

    private ImageView iv_drag;
    private Button bt_top;
    private Button bt_bottom;
    private int startY;
    private int startX;
    private WindowManager mWM;
    private int mScreenWidth;
    private int mScreenHeight;
    private long[] mHits = new long[2];
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast_location);

        initUI();
    }

    private void initUI() {
        iv_drag = (ImageView) findViewById(R.id.iv_drag);
        bt_top = (Button) findViewById(R.id.bt_top);
        bt_bottom = (Button) findViewById(R.id.bt_bottom);

        //获得屏幕的宽高
        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
        mScreenWidth = mWM.getDefaultDisplay().getWidth();
        mScreenHeight = mWM.getDefaultDisplay().getHeight();

        int locationX = SpUtils.getInt(getApplicationContext(), ConstantValue.LOCATION_X, 0);
        int locationY = SpUtils.getInt(getApplicationContext(), ConstantValue.LOCATION_Y, 0);

        //iv_drag在相对布局中，其所在位置规则需要相对布局指定
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = locationX;
        layoutParams.topMargin = locationY;
        //将规则指定于相应控件
        iv_drag.setLayoutParams(layoutParams);

        //每次开启该界面时根据吐司位置判断描述文字显示的位置
        if (locationY > mScreenHeight / 2) {
            bt_top.setVisibility(View.VISIBLE);
            bt_bottom.setVisibility(View.INVISIBLE);
        } else {
            bt_top.setVisibility(View.INVISIBLE);
            bt_bottom.setVisibility(View.VISIBLE);
        }

        //监听吐司位置图片框的双击事件
        iv_drag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //谷歌提供的API，可以用于响应多击事件
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[mHits.length - 1] - mHits[0] < 500) {
                    //满足双击调用图像框的居中代码
                    int left = mScreenWidth / 2 - iv_drag.getWidth() / 2;
                    int right = mScreenWidth / 2 + iv_drag.getWidth() / 2;
                    int top = mScreenHeight / 2 - iv_drag.getHeight() / 2;
                    int bottom = mScreenHeight / 2 + iv_drag.getHeight() / 2;

                    //按以上规则显示
                    iv_drag.layout(left, top, right, bottom);

                    //存储最终位置
                    SpUtils.putInt(getApplicationContext(), ConstantValue.LOCATION_X, iv_drag.getLeft());
                    SpUtils.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, iv_drag.getTop());
                }
            }
        });

        //监听吐司位置图片框的拖拽事件
        iv_drag.setOnTouchListener(new View.OnTouchListener() {
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
                        int left = iv_drag.getLeft() + disX;
                        int top = iv_drag.getTop() + disY;
                        int right = iv_drag.getRight() + disX;
                        int bottom = iv_drag.getBottom() + disY;

                        //吐司框不能出了activity界面
                        if (left < 0 || right > mScreenWidth || top < 0 || bottom > mScreenHeight - 22) {
                            return true;
                        }


                        //2.告知移动的控件显示的坐标
                        iv_drag.layout(left, top, right, bottom);

                        //3.重置一次起始坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        //4.描述文字的位置随吐司移动改变
                        if (top > mScreenHeight / 2) {
                            bt_top.setVisibility(View.VISIBLE);
                            bt_bottom.setVisibility(View.INVISIBLE);
                        } else {
                            bt_top.setVisibility(View.INVISIBLE);
                            bt_bottom.setVisibility(View.VISIBLE);
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        //记录下移动控件的坐标
                        SpUtils.putInt(getApplicationContext(), ConstantValue.LOCATION_X, iv_drag.getLeft());
                        SpUtils.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, iv_drag.getTop());
                        break;
                }
                //仅响应拖拽事件则返回true，若既要响应拖拽又要响应点击事件则返回false
                return false;
            }
        });
    }
}
