package com.jj.defense.View;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/7/5.
 * 能自动获取焦点的textview
 */
public class FocusTextView extends TextView {
    //在通过使用java代码创建时调用
    public FocusTextView(Context context) {
        super(context);
    }

    //写xml文件后由系统调用（带属性+上下文）
    public FocusTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //写xml文件后由系统调用（带属性+上下文+布局文件中样式文件构造方法）
    public FocusTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //重写获得焦点的方法
    @Override
    public boolean isFocused() {
        return true;
    }
}
