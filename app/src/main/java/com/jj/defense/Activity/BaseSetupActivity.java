package com.jj.defense.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/7/25.
 */
public abstract class BaseSetupActivity extends Activity {
    public GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化一个手势识别器
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            //e1代表触摸起点,e2代表触摸抬起点，velociteX代表水平方向速度,velocityY代表竖直方向速度
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //判断事件是否有效（即过滤无效的事件）
                if (Math.abs(velocityX) < 200) {
                    Toast.makeText(getApplicationContext(), "无效动作，移动太慢", Toast.LENGTH_SHORT).show();
                }

                if ((e2.getRawX() - e1.getRawX()) > 200) {
                    //从左向右滑动，显示上一个界面
                    showPre();
                }

                if ((e1.getRawX() - e2.getRawX()) > 200) {
                    //从右向左滑动，显示下一个界面
                    showNext();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    public abstract void showPre();

    public abstract void showNext();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //分析手势事件
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    //同一处理每个界面上一页和下一页按钮
    public void nextPage(View view){
        //将下一页的代码逻辑交由子类处理
        showNext();
    }

    public void prePage(View view){
        //将下一页的代码逻辑交由子类处理
        showPre();
    }
}
