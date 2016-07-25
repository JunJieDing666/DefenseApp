package com.jj.defense.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jj.defense.R;
import com.jj.defense.Utils.ConstantValue;
import com.jj.defense.Utils.SpUtils;

/**
 * Created by Administrator on 2016/7/19.
 */
public class Setup3Activity extends BaseSetupActivity {
    private EditText et_phone_num = null;
    private Button btn_select_num = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);

        initUI();
    }

    @Override
    public void showPre() {
        Intent intent = new Intent(getApplicationContext(), Setup2Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
    }

    @Override
    public void showNext() {
        String contact_num = et_phone_num.getText().toString();
        if (!TextUtils.isEmpty(contact_num)) {
            SpUtils.putString(this, ConstantValue.PHONE_NUM, contact_num);
            Intent intent = new Intent(getApplicationContext(), Setup4Activity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        } else {
            Toast.makeText(this, "请输入电话号码", Toast.LENGTH_SHORT).show();
        }
    }

    private void initUI() {
        //显示电话号码的输入框
        et_phone_num = (EditText) findViewById(R.id.et_phone_num);
        //回显存储起来的电话号码
        String phone = SpUtils.getString(this, ConstantValue.PHONE_NUM, "");
        et_phone_num.setText(phone);
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
        if (data != null) {
            //1.返回当前界面时，接收带回结果的数据
            String phone = data.getStringExtra("phone");
            //2.过滤电话号码中的特殊字符
            phone = phone.replace(" ", "").replace("+", "").trim();
            et_phone_num.setText(phone);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
