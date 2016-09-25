package com.jj.defense.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jj.defense.R;

/**
 * Created by Administrator on 2016/9/13.
 */
public class EnterPsdActivity extends Activity {

    private String packageName;
    private ImageView iv_icon;
    private TextView tv_app_name;
    private EditText et_enter_psd;
    private Button btn_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取包名
        packageName = getIntent().getStringExtra("packageName");
        setContentView(R.layout.activity_enter_psd);

        initUI();
        initData();
    }

    private void initData() {
        //通过传递过来的包名设置拦截界面的图标和应用名称
        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
            iv_icon.setBackground(applicationInfo.loadIcon(pm));
            tv_app_name.setText(applicationInfo.loadLabel(pm).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String psd = et_enter_psd.getText().toString();
                if (!TextUtils.isEmpty(psd)) {
                    if (psd.equals("123")) {
                        //解锁成功，进入应用,并发送广播告知看门狗漏过此应用
                        Intent intent = new Intent("android.intent.action.skip");
                        intent.putExtra("packageName", packageName);
                        sendBroadcast(intent);

                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "密码错误", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "请输入解锁密码", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initUI() {
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        tv_app_name = (TextView) findViewById(R.id.tv_app_name);
        et_enter_psd = (EditText) findViewById(R.id.et_enter_psd);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
    }

    @Override
    public void onBackPressed() {
        //在该界面点击回退会回到主界面
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        super.onBackPressed();
    }
}
