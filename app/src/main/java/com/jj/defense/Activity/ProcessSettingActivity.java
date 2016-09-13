package com.jj.defense.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.jj.defense.R;
import com.jj.defense.Service.LockCleanService;
import com.jj.defense.Utils.ConstantValue;
import com.jj.defense.Utils.ServiceUtils;
import com.jj.defense.Utils.SpUtils;

/**
 * Created by Administrator on 2016/9/2.
 */
public class ProcessSettingActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_setting);

        initVisibleSystemPro();
        initLockClean();
    }

    private void initLockClean() {
        final CheckBox cb_lock_clean = (CheckBox) findViewById(R.id.cb_lock_clean);


        //根据服务是否开启选择单选框的显示状态
        boolean isRunning = ServiceUtils.isRunnig(this, "com.jj.defense.Service.LockCleanService");
        if (isRunning) {
            cb_lock_clean.setText("锁屏清理已开启");
        } else {
            cb_lock_clean.setText("锁屏清理已关闭");
        }
        cb_lock_clean.setChecked(isRunning);


        //单选框勾选状态改变的监听
        cb_lock_clean.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //根据是否勾选的状态设置对应文字
                if (isChecked) {
                    cb_lock_clean.setText("锁屏清理已开启");
                    startService(new Intent(getApplicationContext(), LockCleanService.class));
                } else {
                    cb_lock_clean.setText("锁屏清理已关闭");
                    stopService(new Intent(getApplicationContext(), LockCleanService.class));
                }
            }
        });
    }

    private void initVisibleSystemPro() {
        final CheckBox cb_visible_systempro = (CheckBox) findViewById(R.id.cb_visible_systempro);


        //回显勾选状态并设置给单选框
        boolean isChecked = SpUtils.getBoolean(this, ConstantValue.VISIBLE_SYSTEMPRO, false);
        if (isChecked) {
            cb_visible_systempro.setText("显示系统进程");
        } else {
            cb_visible_systempro.setText("隐藏系统进程");
        }
        cb_visible_systempro.setChecked(isChecked);


        //单选框勾选状态改变的监听
        cb_visible_systempro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //根据是否勾选的状态设置对应文字
                if (isChecked) {
                    cb_visible_systempro.setText("显示系统进程");
                } else {
                    cb_visible_systempro.setText("隐藏系统进程");
                }
                //记录勾选状态
                SpUtils.putBoolean(getApplicationContext(), ConstantValue.VISIBLE_SYSTEMPRO, isChecked);
            }
        });
    }
}
