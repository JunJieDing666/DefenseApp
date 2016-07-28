package com.jj.defense.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jj.defense.R;
import com.jj.defense.Utils.ConstantValue;
import com.jj.defense.Utils.SpUtils;

/**
 * Created by Administrator on 2016/7/17.
 */
public class SetupOverActivity extends Activity {
    private ToggleButton tbtn_open_defense;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //从sp取出状态判断该进入哪个界面
        boolean setup_over = SpUtils.getBoolean(this, ConstantValue.SETUP_OVER, false);
        if (setup_over) {
            setContentView(R.layout.activty_setup_over);
            initUI();
        } else {
            Intent intent = new Intent(this, Setup1Activity.class);
            startActivity(intent);
            //开启设置界面后关闭设置完成界面
            finish();
        }
    }

    private void initUI() {
        //显示选择的安全号码
        TextView tv_safe_num = (TextView) findViewById(R.id.tv_safe_num);
        String phone = SpUtils.getString(this, ConstantValue.PHONE_NUM, "");
        tv_safe_num.setText(phone);

        //为重新进入设置向导的textview设置一个点击事件
        TextView tv_reset = (TextView) findViewById(R.id.tv_reset);
        tv_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Setup1Activity.class));
                finish();
            }
        });

        //根据用户选择，看是否开启防盗保护
        tbtn_open_defense = (ToggleButton) findViewById(R.id.tbtn_open_defense);
        tbtn_open_defense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = tbtn_open_defense.isChecked();
                SpUtils.putBoolean(getApplicationContext(), ConstantValue.OPEN_DEFENSE, isChecked);
            }
        });

    }
}
