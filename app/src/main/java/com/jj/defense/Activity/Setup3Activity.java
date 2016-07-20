package com.jj.defense.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jj.defense.R;

/**
 * Created by Administrator on 2016/7/19.
 */
public class Setup3Activity extends Activity {
    private EditText et_phone_num = null;
    private Button btn_select_num = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);

        initUI();
    }

    private void initUI() {
        //显示电话号码的输入框
        et_phone_num = (EditText) findViewById(R.id.et_phone_num);
        //点击选择联系人的按钮
        btn_select_num = (Button) findViewById(R.id.btn_select_num);

        btn_select_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ContactListActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //处理返回该界面时带回的结果
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void prePage(View view) {
        Intent intent = new Intent(getApplicationContext(), Setup2Activity.class);
        startActivity(intent);
        finish();
    }


    public void nextPage(View view) {
        Intent intent = new Intent(getApplicationContext(), Setup4Activity.class);
        startActivity(intent);
        finish();
    }
}
