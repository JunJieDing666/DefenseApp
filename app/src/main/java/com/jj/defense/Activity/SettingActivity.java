package com.jj.defense.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.jj.defense.R;
import com.jj.defense.Utils.ConstantValue;
import com.jj.defense.Utils.SpUtils;
import com.jj.defense.View.SettingItemView;

/**
 * Created by Administrator on 2016/7/9.
 */
public class SettingActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initUpdate();
    }

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
