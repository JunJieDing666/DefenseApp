package com.jj.defense.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Handler;

import com.jj.defense.Engine.AddressDao;
import com.jj.defense.R;

/**
 * Created by Administrator on 2016/8/9.
 */
public class QueryAddressActivity extends Activity {
    private EditText et_phone = null;
    private TextView tv_query_result = null;
    private Button btn_query = null;
    private String mAddress = null;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            tv_query_result.setText(mAddress);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_address);

        //初始化控件
        initUI();
    }

    private void initUI() {
        et_phone = (EditText) findViewById(R.id.et_phone);
        tv_query_result = (TextView) findViewById(R.id.tv_query_result);
        btn_query = (Button) findViewById(R.id.btn_query);

        //1.查询按钮的点击事件
        btn_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = et_phone.getText().toString();
                //2.查询是耗时操作，开启子线程
                query(phone);
            }
        });
    }

    /** 耗时操作
     *  获取电话号码归属地
     * @param phone 传入的电话号码
     */
    protected void query(final String phone) {
        new Thread(){
            @Override
            public void run() {
                mAddress = AddressDao.getAddress(phone);
                //3.消息机制。告知已经查询到结果
                handler.sendEmptyMessage(0);
            }
        }.start();
    }
}
