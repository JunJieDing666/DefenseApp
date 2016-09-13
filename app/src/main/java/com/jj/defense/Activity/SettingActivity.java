package com.jj.defense.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jj.defense.R;
import com.jj.defense.Service.AddressService;
import com.jj.defense.Service.BlackNumberService;
import com.jj.defense.Service.WatchDogService;
import com.jj.defense.Utils.ConstantValue;
import com.jj.defense.Utils.ServiceUtils;
import com.jj.defense.Utils.SpUtils;
import com.jj.defense.View.SettingClickView;
import com.jj.defense.View.SettingItemView;

/**
 * Created by Administrator on 2016/7/9.
 */
public class SettingActivity extends Activity {

    private String[] mToastStyle;
    private int toast_style;
    private SettingClickView scv_toast_style;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initUpdate();
        initAddress();
        initToastStyle();
        initToastLocation();
        initBlackNumber();
        initAppLock();
    }

    /**
     * 管理程序锁服务
     */
    private void initAppLock() {
        final SettingItemView siv_app_lock = (SettingItemView) findViewById(R.id.siv_app_lock);
        boolean isRunning = ServiceUtils.isRunnig(this, "com.jj.defense.Service.WatchDogService");
        siv_app_lock.setCheck(isRunning);

        siv_app_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = siv_app_lock.isCheck();
                siv_app_lock.setCheck(!isCheck);
                if (!isCheck) {
                    //开启看门狗服务，一直监听任务栈所打开的程序
                    startService(new Intent(getApplicationContext(), WatchDogService.class));
                } else {
                    //关闭服务
                    stopService(new Intent(getApplicationContext(), WatchDogService.class));
                }
            }
        });
    }

    /**
     * 拦截黑名单电话和短信
     */
    private void initBlackNumber() {
        final SettingItemView siv_black_num = (SettingItemView) findViewById(R.id.siv_black_num);
        boolean isRunning = ServiceUtils.isRunnig(this, "com.jj.defense.Service.BlackNumberService");
        siv_black_num.setCheck(isRunning);

        siv_black_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = siv_black_num.isCheck();
                siv_black_num.setCheck(!isCheck);
                if (!isCheck) {
                    //开启服务
                    startService(new Intent(getApplicationContext(), BlackNumberService.class));
                } else {
                    //关闭服务
                    stopService(new Intent(getApplicationContext(), BlackNumberService.class));
                }
            }
        });
    }

    /**
     * 初始化吐司显示位置的方法
     */
    private void initToastLocation() {
        SettingClickView scv_toast_location = (SettingClickView) findViewById(R.id.scv_toast_location);
        scv_toast_location.setTitle("归属地提示框的位置");
        scv_toast_location.setDes("    设置归属地提示框的位置");

        scv_toast_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ToastLocationActivity.class));
            }
        });
    }

    /**
     * 初始化吐司样式条目的方法
     */
    private void initToastStyle() {
        scv_toast_style = (SettingClickView) findViewById(R.id.scv_toast_style);
        scv_toast_style.setTitle("设置归属地显示风格");
        //1.创建一个作为描述文字的吐司样式数组
        mToastStyle = new String[]{"    透明", "    橙色", "    蓝色", "    灰色", "    绿色"};
        //2.从sp中获取之前设置的样式值
        toast_style = SpUtils.getInt(this, ConstantValue.TOAST_STYLE, 0);
        //3.通过索引值获得条目描述文字
        scv_toast_style.setDes(mToastStyle[toast_style]);
        //4.设置点击事件监听器
        scv_toast_style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //5.点击后显示一个样式选择对话框
                showToastStyleDialog();
            }
        });
    }

    private void showToastStyleDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.safe);
        builder.setTitle("请选择归属地样式");

        toast_style = SpUtils.getInt(this, ConstantValue.TOAST_STYLE, 0);
        /*设置单个选择条目的监听器
        * 1.第一个代表条目名的数组
        * 2.第二个代表弹出时选中的单个条目的索引值
        * 3.第三个代表条目点击监听器*/
        builder.setSingleChoiceItems(mToastStyle, toast_style, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //1.记录选中的条目索引值
                SpUtils.putInt(getApplicationContext(), ConstantValue.TOAST_STYLE, which);
                //2.对话框消失
                dialog.dismiss();
                //3.将选中的色值显示在scv上
                scv_toast_style.setDes(mToastStyle[which]);
            }
        });

        //设置取消按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //显示对话框
        builder.show();
    }

    /**
     * 是否开启归属地监听的条目
     */
    private void initAddress() {
        final SettingItemView siv_address = (SettingItemView) findViewById(R.id.siv_address);

        //判断是否开启了归属地服务
        boolean isRunning = ServiceUtils.isRunnig(this, "com.jj.defense.Service.AddressService");
        siv_address.setCheck(isRunning);

        //点击过程中切换开启状态
        siv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断当前条目的状态
                boolean isCheck = siv_address.isCheck();
                //设置点击后条目的状态（应该取反）
                siv_address.setCheck(!isCheck);
                //根据选中状态决定是否开启管理归属地吐司的服务
                if (!isCheck) {
                    startService(new Intent(getApplicationContext(), AddressService.class));
                } else {
                    stopService(new Intent(getApplicationContext(), AddressService.class));
                }
            }
        });
    }

    /**
     * 是否开启更新的条目
     */
    private void initUpdate() {
        final SettingItemView siv_update = (SettingItemView) findViewById(R.id.siv_update);
        //获取已有的开关状态用作显示
        boolean open_update = SpUtils.getBoolean(this, ConstantValue.OPEN_UPDATE, false);
        siv_update.setCheck(open_update);

        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断当前条目的状态
                boolean isCheck = siv_update.isCheck();
                //设置点击后条目的状态（应该取反）
                siv_update.setCheck(!isCheck);

                //将取反后的状态存储到sp对象中
                SpUtils.putBoolean(getApplicationContext(), ConstantValue.OPEN_UPDATE, !isCheck);
            }
        });
    }
}
