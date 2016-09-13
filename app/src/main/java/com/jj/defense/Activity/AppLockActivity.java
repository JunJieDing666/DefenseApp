package com.jj.defense.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jj.defense.DB.Domain.AppInfo;
import com.jj.defense.Engine.AppInfoProvider;
import com.jj.defense.Engine.AppLockDao;
import com.jj.defense.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/11.
 */
public class AppLockActivity extends Activity {

    private Button btn_lock, btn_unlock;
    private LinearLayout ll_lock, ll_unlock;
    private TextView tv_lock, tv_unlock;
    private ListView lv_lock, lv_unlock;
    private List<AppInfo> mAppInfoList;
    private List<AppInfo> mLockAppInfo, mUnLockAppInfo;
    private AppLockDao mAppLockDao;
    private MyAdapter myLockedAdapter;
    private MyAdapter myUnlockedAdapter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //接收到消息，为已加锁和未加锁列表填充数据适配器
            myLockedAdapter = new MyAdapter(true);
            lv_lock.setAdapter(myLockedAdapter);

            myUnlockedAdapter = new MyAdapter(false);
            lv_unlock.setAdapter(myUnlockedAdapter);
        }
    };
    private TranslateAnimation mTranslateAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);

        initUI();
        initData();
        initAnimation();
    }

    /**
     * 初始化动画效果
     */
    private void initAnimation() {
        mTranslateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        mTranslateAnimation.setDuration(500);
    }

    /**
     * 获得手机应用信息的集合（区分了已加锁和未加锁的）
     */
    private void initData() {
        new Thread() {
            @Override
            public void run() {
                //1.获取所有应用信息
                mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
                //2.区分已加锁和未加锁应用
                mLockAppInfo = new ArrayList<AppInfo>();
                mUnLockAppInfo = new ArrayList<AppInfo>();
                //3.获取数据库中已加锁应用的名称
                mAppLockDao = AppLockDao.getInstance(getApplicationContext());
                List<String> packagenameList = mAppLockDao.findAll();
                //4.遍历所有应用信息，区分开已加锁和为加锁的应用
                for (AppInfo appInfo : mAppInfoList) {
                    if (packagenameList.contains(appInfo.packageName)) {
                        mLockAppInfo.add(appInfo);
                    } else {
                        mUnLockAppInfo.add(appInfo);
                    }
                }
                //5.告知主线程可以使用应用数据了
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initUI() {
        btn_lock = (Button) findViewById(R.id.btn_lock);
        btn_unlock = (Button) findViewById(R.id.btn_unlock);

        ll_lock = (LinearLayout) findViewById(R.id.ll_lock);
        ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);

        tv_lock = (TextView) findViewById(R.id.tv_lock);
        tv_unlock = (TextView) findViewById(R.id.tv_unlock);

        lv_lock = (ListView) findViewById(R.id.lv_lock);
        lv_unlock = (ListView) findViewById(R.id.lv_unlock);

        btn_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示已加锁应用列表，隐藏未加锁应用列表
                ll_lock.setVisibility(View.VISIBLE);
                ll_unlock.setVisibility(View.GONE);
                //设置加锁按钮的背景
                btn_lock.setBackgroundResource(R.drawable.tab_right_pressed);
                btn_unlock.setBackgroundResource(R.drawable.tab_left_default);
            }
        });

        btn_unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示未加锁应用列表，隐藏已加锁应用列表
                ll_lock.setVisibility(View.GONE);
                ll_unlock.setVisibility(View.VISIBLE);
                //设置加锁按钮的背景
                btn_lock.setBackgroundResource(R.drawable.tab_right_default);
                btn_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
            }
        });
    }

    private class MyAdapter extends BaseAdapter {
        private boolean isLocked;

        //isLocked是用于区分程序是否加锁的标志
        public MyAdapter(boolean isLocked) {
            this.isLocked = isLocked;
        }

        @Override
        public int getCount() {
            if (isLocked) {
                tv_lock.setText("已加锁程序：" + mLockAppInfo.size());
                return mLockAppInfo.size();
            } else {
                tv_unlock.setText("未加锁程序：" + mUnLockAppInfo.size());
                return mUnLockAppInfo.size();
            }
        }

        @Override
        public AppInfo getItem(int position) {
            if (isLocked) {
                return mLockAppInfo.get(position);
            } else {
                return mUnLockAppInfo.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.lv_app_islocked_item, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                viewHolder.tv_app_name = (TextView) convertView.findViewById(R.id.tv_app_name);
                viewHolder.iv_lock = (ImageView) convertView.findViewById(R.id.iv_lock);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final AppInfo appInfo = getItem(position);
            final View animationView = convertView;
            viewHolder.iv_icon.setBackground(appInfo.icon);
            viewHolder.tv_app_name.setText(appInfo.applicationName);
            if (isLocked) {
                viewHolder.iv_lock.setBackgroundResource(R.drawable.applock_icon);
            } else {
                viewHolder.iv_lock.setBackgroundResource(R.drawable.appunlock_icon);
            }
            viewHolder.iv_lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //给条目设置动画
                    animationView.startAnimation(mTranslateAnimation);
                    //为动画设置监听器
                    mTranslateAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            //动画结束时执行
                            if (isLocked) {
                                //从已加锁添加至未加锁
                                //1.从已加锁集合中删除该条目，在未加锁集合中加入该条目
                                mLockAppInfo.remove(appInfo);
                                mUnLockAppInfo.add(appInfo);
                                //2.在数据库中删除该数据
                                mAppLockDao.Delete(appInfo.packageName);
                                //3.通知数据适配器刷新
                                myLockedAdapter.notifyDataSetChanged();
                            } else {
                                //从未加锁添加至已加锁
                                //1.从已加锁集合中加入该条目，在未加锁集合中删除该条目
                                mLockAppInfo.add(appInfo);
                                mUnLockAppInfo.remove(appInfo);
                                //2.在数据库中删除该数据
                                mAppLockDao.Insert(appInfo.packageName);
                                //3.通知数据适配器刷新
                                myUnlockedAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            });
            return convertView;
        }
    }

    static class ViewHolder {
        private ImageView iv_icon;
        private TextView tv_app_name;
        private ImageView iv_lock;
    }
}
