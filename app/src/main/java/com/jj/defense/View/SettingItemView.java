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
public class SettingItemView extends RelativeLayout{
    private CheckBox cb_box=null;
    private TextView tv_setting_item_des=null;
    private static final String NAME_SPACE="http://schemas.android.com/apk/res/com.jj.defense";
    private String titledes =null;
    private String deson =null;
    private String desoff =null;

    public SettingItemView(Context context) {
        this(context, null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //将xml转化为view对象,并挂载在此类上以供使用
        View.inflate(context, R.layout.setting_item_view,this);
        tv_setting_item_des = (TextView) findViewById(R.id.tv_setting_item_des);
        TextView tv_setting_item_title = (TextView) findViewById(R.id.tv_setting_item_title);
        cb_box= (CheckBox) findViewById(R.id.cb_box);

        //初始化条目的属性
        initAttrs(attrs);

        //设置条目标题名
        tv_setting_item_title.setText(titledes);
    }

    /** 返回自定义属性
     * @param attrs 构造方法中维护好的属性集合
     */
    private void initAttrs(AttributeSet attrs) {
        //通过名空间和属性名获得属性值
        titledes = attrs.getAttributeValue(NAME_SPACE, "titledes");
        deson = attrs.getAttributeValue(NAME_SPACE, "deson");
        desoff = attrs.getAttributeValue(NAME_SPACE, "desoff");
    }

    /**
     * @return 判断当前自定义组合控件条目是否选中
     */
    public boolean isCheck(){
        return cb_box.isChecked();
    }

    public void setCheck(Boolean isCheck){
        //当前条目在选择状态中，cb_box的选中状态应跟着改变
        cb_box.setChecked(isCheck);
        //描述文字也应跟着改变
        if (isCheck){
            tv_setting_item_des.setText(deson);
        } else {
            tv_setting_item_des.setText(desoff);
        }
    }


}
