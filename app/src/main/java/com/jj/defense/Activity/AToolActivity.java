package com.jj.defense.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.jj.defense.Engine.SmsBackUpDao;
import com.jj.defense.R;

import java.io.File;

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
        //备份短信
        initSmsBackUp();
    }

    private void initSmsBackUp() {
        TextView tv_sms_back_up = (TextView) findViewById(R.id.tv_sms_back_up);
        tv_sms_back_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSmsBackUpDialog();
            }
        });
    }

    private void showSmsBackUpDialog() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.drawable.safe);
        progressDialog.setTitle("备份短信中");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        //直接调用备份短信的方法(可能为耗时操作)
        new Thread() {
            @Override
            public void run() {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "DefenseSms.xml";
                SmsBackUpDao.backUp(getApplicationContext(), new SmsBackUpDao.ProgressCallBack() {
                    @Override
                    public void setMax(int max) {
                        progressDialog.setMax(max);
                    }

                    @Override
                    public void setProgress(int index) {
                        progressDialog.setProgress(index);
                    }
                }, path);
                progressDialog.dismiss();
            }
        }.start();
    }

    private void initPhoneAddress() {
        TextView tv_query_phone_address = (TextView) findViewById(R.id.tv_query_phone_address);
        tv_query_phone_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), QueryAddressActivity.class));
            }
        });
    }
}
