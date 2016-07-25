package com.jj.defense.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jj.defense.R;

/**
 * Created by Administrator on 2016/7/17.
 */
public class Setup1Activity extends BaseSetupActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
    }

    @Override
    public void showPre() {
        //返回空方法
    }

    @Override
    public void showNext() {
        Intent intent = new Intent(getApplicationContext(), Setup2Activity.class);
        startActivity(intent);
        finish();
        //移入移出动画效果
        overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
    }

}
