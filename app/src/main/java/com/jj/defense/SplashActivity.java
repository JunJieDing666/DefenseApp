package com.jj.defense;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jj.defense.Utils.ConstantValue;
import com.jj.defense.Utils.SpUtils;
import com.jj.defense.Utils.VersionUpdateUtils;

public class SplashActivity extends AppCompatActivity {
    /*应用的版本号*/
    private TextView mVersionTV;
    /*本地版本号*/
    private String mVersion;

    private String tag = "SplashActivity";

    private RelativeLayout rl_root =null;

    private final VersionUpdateUtils updateUtils = new VersionUpdateUtils(mVersion, SplashActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        mVersion = getVersion(getApplicationContext());
        initView();
        final VersionUpdateUtils updateUtils = new VersionUpdateUtils(mVersion, SplashActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (SpUtils.getBoolean(getApplicationContext(), ConstantValue.OPEN_UPDATE,false)){
                    //获取服务器版本号
                    updateUtils.getCloudVersion();
                } else {
                    updateUtils.notUpdateEnterHome();
                }
            }
        }).start();
        initUI();
    }

    private void initUI() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(2000);
        rl_root.startAnimation(alphaAnimation);
    }


    /**
     * 获得版本名称
     *
     * @param context
     * @return
     */
    public String getVersion(Context context) {
        /*获得清单文件中所有信息*/
        PackageManager manager = context.getPackageManager();
        /*获取当前程序的包名*/
        try {
            PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 初始化UI
     */
    private void initView() {
        mVersionTV = (TextView) findViewById(R.id.tv_splash_version);
        rl_root = (RelativeLayout) findViewById(R.id.rl_root);
        mVersionTV.setText("版本号:" + mVersion);
    }

    //开启一个activity后，返回结果时调用
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        updateUtils.onActivityResultSendMessage();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
