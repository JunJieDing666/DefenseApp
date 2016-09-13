package com.jj.defense.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jj.defense.DB.Domain.AppInfo;
import com.jj.defense.Engine.AppInfoProvider;
import com.jj.defense.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/26.
 */
public class AppManagerActivity extends Activity implements View.OnClickListener {
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mAdapter == null) {
                mAdapter = new MyAdapter();
                lv_app.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }
            if (tv_des != null && mCustomerAppList != null) {
                tv_des.setText("用户应用（" + mCustomerAppList.size() + "）");
            }
        }
    };
    private ListView lv_app;
    private MyAdapter mAdapter;
    private List<AppInfo> appInfoList;
    private List<AppInfo> mSystemAppList;
    private List<AppInfo> mCustomerAppList;
    private TextView tv_des;
    private AppInfo mAppInfo;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        initAvailableMemory();
        initList();

    }

    @Override
    protected void onResume() {
        //重弄新获取数据
        getData();
        super.onResume();
    }


    /**
     * 初始化列表框
     */
    private void initList() {
        lv_app = (ListView) findViewById(R.id.lv_app);
        tv_des = (TextView) findViewById(R.id.tv_des);


        //getData();

        //常驻悬浮框的实现
        lv_app.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //滑动过程中替换掉标题
                if (mCustomerAppList != null && mSystemAppList != null) {
                    if (firstVisibleItem >= mCustomerAppList.size() + 1) {
                        tv_des.setText("系统应用（" + mSystemAppList.size() + "）");
                    } else {
                        tv_des.setText("用户应用（" + mCustomerAppList.size() + "）");
                    }
                }
            }
        });

        lv_app.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == mCustomerAppList.size() + 1) {
                    return;
                } else {
                    if (position < mCustomerAppList.size() + 1) {
                        mAppInfo = mCustomerAppList.get(position - 1);
                    } else {
                        mAppInfo = mSystemAppList.get(position - mCustomerAppList.size() - 2);
                    }
                    showPopupWindow(view);
                }
            }
        });
    }

    private void showPopupWindow(View view) {
        View popupView = View.inflate(this, R.layout.popupwindow, null);

        TextView tv_uninstall = (TextView) popupView.findViewById(R.id.tv_uninstall);
        TextView tv_start = (TextView) popupView.findViewById(R.id.tv_start);
        TextView tv_share = (TextView) popupView.findViewById(R.id.tv_share);

        tv_uninstall.setOnClickListener(this);
        tv_start.setOnClickListener(this);
        tv_share.setOnClickListener(this);

        //1.创建一个popupWindow，并设置宽高
        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        //2.设置一个透明背景
        popupView.setBackgroundResource(R.drawable.local_popup_bg);
        //3.设置显示的位置
        popupWindow.showAsDropDown(view, 200, -view.getHeight());
        //4.为popupWindow设置动画(透明、缩放)
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);

        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(500);
        scaleAnimation.setFillAfter(true);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);

        popupView.startAnimation(animationSet);

    }


    /**
     * 初始化可用空间大小
     */
    private void initAvailableMemory() {
        TextView tv_memory = (TextView) findViewById(R.id.tv_memory);
        TextView tv_sd_memory = (TextView) findViewById(R.id.tv_sd_memory);
        //1.获得手机内存空间路径和可用大小
        String path = Environment.getDataDirectory().getAbsolutePath();
        String availableSpace = android.text.format.Formatter.formatFileSize(this, getAvailableMemory(path));
        //2.获得sd卡空间路径和可用大小
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String availableSdSpace = android.text.format.Formatter.formatFileSize(this, getAvailableMemory(sdPath));

        tv_memory.setText("本地内存可用：" + availableSpace);
        tv_sd_memory.setText("sd卡可用：" + availableSdSpace);
    }

    /**
     * 获得指定路径可用空间大小(bytes)
     *
     * @param path
     */
    private long getAvailableMemory(String path) {
        //获取可得到可用磁盘大小的类
        StatFs statFs = new StatFs(path);
        //获得可用区块的个数
        long availableBlocks = statFs.getAvailableBlocks();
        //获得每个区块的大小
        long blockSize = statFs.getBlockSize();
        //相乘得可用空间大小(bytes)
        return availableBlocks * blockSize;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_uninstall:
                if (mAppInfo.isSystemApp) {
                    Toast.makeText(getApplicationContext(), "此为系统应用，不可卸载", Toast.LENGTH_SHORT);
                } else {
                    Intent intent = new Intent("android.intent.action.DELETE");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse("package:" + mAppInfo.getPackageName()));
                    startActivity(intent);
                }
                break;
            case R.id.tv_start:
                //通过桌面去启动指定包名的应用
                PackageManager pm = getPackageManager();
                Intent launchIntentForPackage = pm.getLaunchIntentForPackage(mAppInfo.getPackageName());
                if (launchIntentForPackage != null) {
                    startActivity(launchIntentForPackage);
                } else {
                    Toast.makeText(getApplicationContext(), "此应用不能被打开", Toast.LENGTH_SHORT);
                }
                break;
            case R.id.tv_share:
                //此处仅通过短信分享
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "分享一个应用，应用名称为：" + mAppInfo.getApplicationName());
                intent.setType("text/plain");
                startActivity(intent);
                break;
        }

        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    public void getData() {
        new Thread() {
            @Override
            public void run() {
                //找到所有应用信息的集合(耗时)
                appInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
                mSystemAppList = new ArrayList<AppInfo>();
                mCustomerAppList = new ArrayList<AppInfo>();
                for (AppInfo appinfo : appInfoList) {
                    if (appinfo.isSystemApp) {
                        mSystemAppList.add(appinfo);
                    } else {
                        mCustomerAppList.add(appinfo);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }


    private class MyAdapter extends BaseAdapter {
        //两种类型的条目
        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mCustomerAppList.size() + 1) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int getCount() {
            return mSystemAppList.size() + mCustomerAppList.size() + 2;
        }

        @Override
        public AppInfo getItem(int position) {
            if (position == 0 || position == mCustomerAppList.size() + 1) {
                return null;
            } else {
                if (position < mCustomerAppList.size() + 1) {
                    return mCustomerAppList.get(position - 1);
                } else {
                    return mSystemAppList.get(position - mCustomerAppList.size() - 2);
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            if (type == 0) {
                ViewTitleHolder viewTitleHolder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.lv_app_item_title, null);
                    viewTitleHolder = new ViewTitleHolder();
                    viewTitleHolder.tv_app_title = (TextView) convertView.findViewById(R.id.tv_app_title);
                    convertView.setTag(viewTitleHolder);
                } else {
                    viewTitleHolder = (ViewTitleHolder) convertView.getTag();
                }
                if (position == 0) {
                    viewTitleHolder.tv_app_title.setText("用户应用（" + mCustomerAppList.size() + "）");
                } else {
                    viewTitleHolder.tv_app_title.setText("系统应用（" + mSystemAppList.size() + "）");
                }
                return convertView;
            } else {
                ViewHolder viewHolder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.lv_app_item, null);
                    viewHolder = new ViewHolder();
                    viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                    viewHolder.tv_app_name = (TextView) convertView.findViewById(R.id.tv_app_name);
                    viewHolder.tv_app_path = (TextView) convertView.findViewById(R.id.tv_app_path);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                viewHolder.iv_icon.setBackgroundDrawable(getItem(position).icon);
                viewHolder.tv_app_name.setText(getItem(position).applicationName);
                if (getItem(position).isSDcard) {
                    viewHolder.tv_app_path.setText("SD卡应用");
                } else {
                    viewHolder.tv_app_path.setText("手机内存应用");
                }
                return convertView;
            }
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_app_name;
        TextView tv_app_path;
    }

    static class ViewTitleHolder {
        TextView tv_app_title;
    }
}
