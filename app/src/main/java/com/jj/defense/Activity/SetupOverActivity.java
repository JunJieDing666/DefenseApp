package com.jj.defense.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jj.defense.R;
import com.jj.defense.Utils.ConstantValue;
import com.jj.defense.Utils.SpUtils;

/**
 * Created by Administrator on 2016/7/17.
 */
public class SetupOverActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //从sp取出状态判断该进入哪个界面
        boolean setup_over = SpUtils.getBoolean(this, ConstantValue.SETUP_OVER, false);
        if (setup_over) {
            setContentView(R.layout.activty_setup_over);
        } else {
            Intent intent = new Intent(this, Setup1Activity.class);
            startActivity(intent);
            //开启设置界面后关闭设置完成界面
            finish();
        }
    }
}
