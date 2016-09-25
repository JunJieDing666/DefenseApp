package com.jj.defense.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jj.defense.DB.Domain.VirusInfo;
import com.jj.defense.Engine.AntiVirusDao;
import com.jj.defense.R;
import com.jj.defense.Utils.Md5Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.RandomAccess;

/**
 * Created by Administrator on 2016/9/17.
 */
public class AntiVirusActivity extends Activity {
    private static final int SCANNING = 100;
    private static final int SCAN_FINISH = 101;

    private ImageView iv_scanning;
    private TextView tv_scanning_app_name;
    private ProgressBar pb_check_virus;
    private LinearLayout ll_add_textview;
    private int index = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //判断扫描状态更新UI
            switch (msg.what) {
                case SCANNING:
                    VirusInfo virusInfo = (VirusInfo) msg.obj;
                    //1.将文本框设置成应用名称
                    tv_scanning_app_name.setText(virusInfo.applicationName);
                    //2.新加一个文本框进入滚动框
                    TextView textView = new TextView(getApplicationContext());
                    if (virusInfo.isVirus) {
                        //文字为红色
                        textView.setTextColor(Color.RED);
                        textView.setText("发现病毒：" + virusInfo.applicationName);
                    } else {
                        //文字为黑色
                        textView.setTextColor(Color.BLACK);
                        textView.setText("扫描安全：" + virusInfo.applicationName);
                    }
                    ll_add_textview.addView(textView, 0);
                    break;
                case SCAN_FINISH:
                    tv_scanning_app_name.setText("扫描完成");
                    //停止动画
                    iv_scanning.clearAnimation();
                    //告知用户卸载有病毒的应用
                    uninstallVirus();
                    break;
            }
        }
    };
    private List<VirusInfo> mVirusInfoList;

    private void uninstallVirus() {
        for (VirusInfo virusInfo : mVirusInfoList) {
            Intent intent = new Intent("android.intent.action.DELETE");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse("package:" + virusInfo.packageName));
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_virus);

        initUI();
        initAnimation();
        checkVirus();
    }

    /**
     * 查杀病毒的方法
     */
    private void checkVirus() {
        new Thread() {
            @Override
            public void run() {
                //0.从病毒库获得所有病毒MD5码,初始化病毒应用信息集合
                List<String> virusMd5List = AntiVirusDao.getVirusMd5List();
                mVirusInfoList = new ArrayList<VirusInfo>();
                //1.获得包管理者
                PackageManager pm = getPackageManager();
                //2.获得所有应用的签名文件（安装的+删除后残余的）,初始化进度条总长度
                List<PackageInfo> packageInfoList = pm.getInstalledPackages(PackageManager.GET_SIGNATURES
                        + PackageManager.GET_UNINSTALLED_PACKAGES);
                pb_check_virus.setMax(packageInfoList.size());
                //3.遍历包的信息集合，获得签名文件
                for (PackageInfo packageInfo : packageInfoList) {
                    Signature[] signatures = packageInfo.signatures;
                    Signature signature = signatures[0];
                    String strSignature = signature.toCharsString();
                    //4.将获得的签名MD5化
                    String md5Signature = Md5Utils.encode(strSignature);
                    //5.将上述签名与病毒库的签名做比对,如果是病毒就同一放进一个集合
                    VirusInfo virusInfo = new VirusInfo();
                    if (virusMd5List.contains(md5Signature)) {
                        virusInfo.isVirus = true;
                        mVirusInfoList.add(virusInfo);
                    } else {
                        virusInfo.isVirus = false;
                    }
                    //6.将应用的3个信息存储好
                    virusInfo.packageName = packageInfo.packageName;
                    virusInfo.applicationName = packageInfo.applicationInfo.loadLabel(pm).toString();
                    //7.每扫描一个应用进度条加1
                    index++;
                    pb_check_virus.setProgress(index);
                    //8.通知主线程去更新UI（1.更新文本框显示为应用名  2.让滚动框加入一个文本框）
                    Message msg = Message.obtain();
                    msg.what = SCANNING;
                    msg.obj = virusInfo;
                    mHandler.sendMessage(msg);
                }
                //让线程睡眠
                try {
                    Thread.sleep(50 + (long) Math.random() * 100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //9.查杀完毕后需要更新UI
                Message msg = Message.obtain();
                msg.what = SCAN_FINISH;
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 雷达旋转的动画
     */
    private void initAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        //一直旋转
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        //保持终止时的样子
        rotateAnimation.setFillAfter(true);
        //开启动画
        iv_scanning.startAnimation(rotateAnimation);
    }

    private void initUI() {
        iv_scanning = (ImageView) findViewById(R.id.iv_scanning);
        tv_scanning_app_name = (TextView) findViewById(R.id.tv_scanning_app_name);
        pb_check_virus = (ProgressBar) findViewById(R.id.pb_check_virus);
        ll_add_textview = (LinearLayout) findViewById(R.id.ll_add_textview);
    }
}
