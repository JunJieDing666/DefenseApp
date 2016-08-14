package com.jj.defense.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jj.defense.R;

/**
 * Created by Administrator on 2016/7/9.
 */
public class SettingClickView extends RelativeLayout{
    private TextView tv_setting_item_des=null;
    private TextView tv_setting_item_title;

    public SettingClickView(Context context) {
        this(context, null);
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //将xml转化为view对象,并挂载在此类上以供使用
        View.inflate(context, R.layout.setting_click_view,this);
        tv_setting_item_des = (TextView) findViewById(R.id.tv_setting_item_des);
        tv_setting_item_title = (TextView) findViewById(R.id.tv_setting_item_title);


    }

    /**
     * @param title 设置标题内容
     */
    public void setTitle(String title){
        tv_setting_item_title.setText(title);
    }

    /**
     * @param des   设置描述内容
     */
    public void setDes(String des){
        tv_setting_item_des.setText(des);
    }

}
