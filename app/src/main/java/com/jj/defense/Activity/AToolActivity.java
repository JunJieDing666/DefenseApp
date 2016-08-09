package com.jj.defense.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jj.defense.R;

/**
 * Created by Administrator on 2016/8/9.
 */
public class AToolActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atool);

        //归属地查询
        initPhoneAddress();
    }

    private void initPhoneAddress() {
        TextView tv_query_phone_address = (TextView) findViewById(R.id.tv_query_phone_address);
        tv_query_phone_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),QueryAddressActivity.class));
            }
        });
    }
}
