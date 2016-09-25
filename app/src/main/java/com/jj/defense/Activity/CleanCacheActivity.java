package com.jj.defense.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jj.defense.DB.Domain.CacheInfo;
import com.jj.defense.R;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Administrator on 2016/9/18.
 */
public class CleanCacheActivity extends Activity {

    private static final int UPDATE_CACHE_UI = 1001;
    private static final int CHECKING_CACHE_APP = 1002;
    private static final int CHECK_FINISH = 1003;
    private static final int DELETE_CACHE = 1004;

    private Button btn_clean_cache;
    private ProgressBar pb_clean_cache;
    private TextView tv_cache_name;
    private LinearLayout ll_add_textview;
    private PackageManager mPm;
    private int mIndex = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_CACHE_UI:
                    //8.新加一条缓存信息进入滚动框
                    View cacheItemView = View.inflate(getApplicationContext(), R.layout.ll_cache_item, null);
                    ImageView iv_icon = (ImageView) cacheItemView.findViewById(R.id.iv_icon);
                    TextView tv_process_name = (TextView) cacheItemView.findViewById(R.id.tv_process_name);
                    TextView tv_process_cache_size = (TextView) cacheItemView.findViewById(R.id.tv_process_cache_size);
                    ImageView iv_delete = (ImageView) cacheItemView.findViewById(R.id.iv_delete);

                    final CacheInfo cacheInfo = (CacheInfo) msg.obj;
                    iv_icon.setBackground(cacheInfo.icon);
                    tv_process_name.setText(cacheInfo.applicationName);
                    tv_process_cache_size.setText(cacheInfo.cacheSize);

                    ll_add_textview.addView(cacheItemView, 0);

                    iv_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                            intent.setData(Uri.parse("package:" + cacheInfo.packageName));
                            startActivity(intent);
                        }
                    });
                    break;
                case CHECKING_CACHE_APP:
                    //将正在检查缓存的应用名显示在文本框上
                    String name = (String) msg.obj;
                    tv_cache_name.setText(name);
                    break;
                case CHECK_FINISH:
                    //扫描完成
                    tv_cache_name.setText("扫描完成");
                    break;
                case DELETE_CACHE:
                    //删除有缓存应用的view
                    ll_add_textview.removeAllViews();
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_cache);

        initUI();
        initData();
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                //1.获得包管理器
                mPm = getPackageManager();
                //2.获得所有应用的包信息
                List<PackageInfo> packageInfoList = mPm.getInstalledPackages(0);
                //3.设置进度条的最大值
                pb_clean_cache.setMax(packageInfoList.size());
                //4.遍历该集合获得每个应用的包名、应用名、图标、缓存大小并通知UI更新
                for (PackageInfo packageInfo : packageInfoList) {
                    String packageName = packageInfo.packageName;
                    getPackageCache(packageName);

                    //每循环一次让进度条+1
                    mIndex++;
                    pb_clean_cache.setProgress(mIndex);

                    try {
                        Thread.sleep((long) (25 + Math.random() * 50));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //显示正在查询缓存的应用名称
                    String name = null;
                    try {
                        name = mPm.getApplicationInfo(packageInfo.packageName, 0).loadLabel(mPm).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    Message msg = Message.obtain();
                    msg.what = CHECKING_CACHE_APP;
                    msg.obj = name;
                    mHandler.sendMessage(msg);
                }

                //遍历完后，显示扫描完成
                Message msg = Message.obtain();
                msg.what = CHECK_FINISH;
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 获得对应包名应用的缓存大小
     *
     * @param packageName 包名
     */
    private void getPackageCache(final String packageName) {
        IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {

            public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {
                //4.缓存指定包名的缓存大小
                long cacheSize = stats.cacheSize;
                String strCacheSize = Formatter.formatFileSize(getApplicationContext(), cacheSize);
                //5.判断缓存大小
                if (cacheSize > 0) {
                    //6.通知主线程更新UI
                    Message msg = Message.obtain();
                    msg.what = UPDATE_CACHE_UI;
                    //7.将对应要传送的信息封装到javabean中
                    CacheInfo cacheInfo = null;
                    try {
                        cacheInfo = new CacheInfo();
                        cacheInfo.packageName = stats.packageName;
                        cacheInfo.applicationName = mPm.getApplicationInfo(packageName, 0).loadLabel(mPm).toString();
                        cacheInfo.icon = mPm.getApplicationInfo(packageName, 0).loadIcon(mPm);
                        cacheInfo.cacheSize = strCacheSize;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    msg.obj = cacheInfo;
                    mHandler.sendMessage(msg);
                }
            }
        };

        //通过反射调用pm中的getPackageSizeInfo方法
        try {
            //1.获得pm的字节码对象
            Class<?> clazz = Class.forName("android.content.pm.PackageManager");
            //2.获得对象身上的方法
            Method methodGetPackageSizeInfo = clazz.getMethod("getPackageSizeInfo",
                    String.class, IPackageStatsObserver.class);
            //3.调用该方法
            methodGetPackageSizeInfo.invoke(mPm, packageName, mStatsObserver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        btn_clean_cache = (Button) findViewById(R.id.btn_clean_cache);
        pb_clean_cache = (ProgressBar) findViewById(R.id.pb_clean_cache);
        tv_cache_name = (TextView) findViewById(R.id.tv_cache_name);
        ll_add_textview = (LinearLayout) findViewById(R.id.ll_add_textview);

        btn_clean_cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //通过反射调用pm中的freeStorageAndNotify方法
                try {
                    //1.获得pm的字节码对象
                    Class<?> clazz = Class.forName("android.content.pm.PackageManager");
                    //2.获得对象身上的方法
                    Method methodGetPackageSizeInfo = clazz.getMethod("freeStorageAndNotify",
                            long.class, IPackageDataObserver.class);
                    //3.调用该方法
                    methodGetPackageSizeInfo.invoke(mPm, Long.MAX_VALUE, new IPackageDataObserver.Stub() {
                        @Override
                        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                            //删除缓存后调用的方法,通知主线程删除有缓存应用的view
                            Message msg = Message.obtain();
                            msg.what = DELETE_CACHE;
                            mHandler.sendMessage(msg);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
