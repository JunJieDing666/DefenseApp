package com.jj.defense.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.jj.defense.R;
import com.jj.defense.Utils.ConstantValue;
import com.jj.defense.Utils.SpUtils;
import com.jj.defense.View.SettingItemView;

/**
 * Created by Administrator on 2016/7/19.
 */
public class Setup4Activity extends BaseSetupActivity {
    private SettingItemView siv_defense = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);

        initUI();
    }

    @Override
    public void showPre() {
        Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
    }

    @Override
    public void showNext() {
        boolean open_defense = SpUtils.getBoolean(this, ConstantValue.OPEN_DEFENSE, false);
        if (open_defense) {
            Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
            startActivity(intent);
            finish();
            //设置完成后将sp状态设置成成功
            SpUtils.putBoolean(this, ConstantValue.SETUP_OVER, true);
            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        } else {
            Toast.makeText(this, "请开启防盗保护", Toast.LENGTH_SHORT).show();
        }
    }

    private void initUI() {
        /*下述代码中变量open_defense是从sp中取出的记录状态，isCheck返回的是当前条目选择状态*/

        siv_defense = (SettingItemView) findViewById(R.id.siv_defense);
        //1.回显是否选中开启了防盗保护
        final boolean open_defense = SpUtils.getBoolean(this, ConstantValue.OPEN_DEFENSE, false);
        //2.根据记录的状态来显示自定义条目
        siv_defense.setCheck(open_defense);
        //3.设置条目点击事件，每点一次取一次相反状态
        siv_defense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = siv_defense.isCheck();
                siv_defense.setCheck(!isCheck);
                SpUtils.putBoolean(getApplicationContext(), ConstantValue.OPEN_DEFENSE, !isCheck);
            }
        });
    }

}
