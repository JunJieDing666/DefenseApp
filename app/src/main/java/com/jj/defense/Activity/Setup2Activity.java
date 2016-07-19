package com.jj.defense.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jj.defense.R;
import com.jj.defense.Utils.ConstantValue;
import com.jj.defense.Utils.SpUtils;
import com.jj.defense.View.SettingItemView;

/**
 * Created by Administrator on 2016/7/19.
 */
public class Setup2Activity extends Activity {
    private SettingItemView siv_sim_bound = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);

        initUI();
    }

    private void initUI() {
        //获取控件
        siv_sim_bound = (SettingItemView) findViewById(R.id.siv_sim_bound);
        //1.回显（根据已有状态用作显示，即看看sp中是否储存了sim卡序列号）
        String sim_num = SpUtils.getString(this, ConstantValue.SIM_NUM, "");
        //2.判断当前序列号是否为空
        if (TextUtils.isEmpty(sim_num)) {
            siv_sim_bound.setCheck(false);
        } else {
            siv_sim_bound.setCheck(true);
        }

        //设置条目的点击事件
        siv_sim_bound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //3.获取条目原有状态
                boolean isCheck = siv_sim_bound.isCheck();
                //4.将已有状态取反并赋给当前条目
                siv_sim_bound.setCheck(!isCheck);
                if (!isCheck) {
                    //如若取反后的状态为true，则存储序列号
                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String simSerialNumber = telephonyManager.getSimSerialNumber();
                    SpUtils.putString(getApplicationContext(), ConstantValue.SIM_NUM, simSerialNumber);
                } else {
                    //从sp中删除该结点
                    SpUtils.remove(getApplicationContext(), ConstantValue.SIM_NUM);
                }
            }
        });
    }

    public void prePage(View view) {
        Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
        startActivity(intent);
        finish();
    }


    public void nextPage(View view) {
        if (siv_sim_bound.isCheck()) {
            Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "请绑定序列卡号", Toast.LENGTH_SHORT).show();
        }
    }

}
