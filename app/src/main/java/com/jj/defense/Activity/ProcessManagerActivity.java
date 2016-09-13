package com.jj.defense.Activity;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jj.defense.DB.Domain.ProcessInfo;
import com.jj.defense.Engine.ProcessInfoProvider;
import com.jj.defense.R;
import com.jj.defense.Utils.ConstantValue;
import com.jj.defense.Utils.SpUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/30.
 */
public class ProcessManagerActivity extends Activity implements View.OnClickListener {
    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1101;
    private static final int SYSTEM_RPOCESS_IS_VISIBLE = 0;

    private TextView tv_total_process;
    private TextView tv_memory_info;
    private ListView lv_process;
    private Button bt_select_all;
    private Button bt_select_reverse;
    private Button bt_clear;
    private Button bt_setting;
    private int mProcessCount;
    private List<ProcessInfo> mProcessInfoList;
    private ArrayList<ProcessInfo> mSystemAppList;
    private ArrayList<ProcessInfo> mCustomerAppList;

    private MyAdapter mAdapter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mAdapter == null) {
                mAdapter = new MyAdapter();
                lv_process.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }
            if (tv_des != null && mCustomerAppList != null) {
                tv_des.setText("用户进程（" + mCustomerAppList.size() + "）");
            }
        }
    };
    private TextView tv_des;
    private ProcessInfo mProcessInfo;
    private long mAvailSpace;
    private long mTotalSpace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!hasPermission()) {
                //用对话框询问是否开启权限
                //若用户未开启权限，则引导用户开启“Apps with usage access”权限
                startActivityForResult(
                        new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                        MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
            }
        }

        initUI();
        initData();
        initProcessList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //通知数据适配器刷新
        if (requestCode == SYSTEM_RPOCESS_IS_VISIBLE) {
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
        //判断是否开启了权限
        if (requestCode == MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS) {
            if (!hasPermission()) {
                //若用户未开启权限，则引导用户开启“Apps with usage access”权限
                startActivityForResult(
                        new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                        MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
            }
        }
    }

    /**
     * @return 判断是否拥有获取进程的权限
     */
    private boolean hasPermission() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private void initProcessList() {
        //准备数据
        getData();

        //常驻悬浮框的实现
        lv_process.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //滑动过程中替换掉标题
                if (mCustomerAppList != null && mSystemAppList != null) {
                    if (firstVisibleItem >= mCustomerAppList.size() + 1) {
                        tv_des.setText("系统进程（" + mSystemAppList.size() + "）");
                    } else {
                        tv_des.setText("用户进程（" + mCustomerAppList.size() + "）");
                    }
                }
            }
        });

        //设置条目点击事件
        lv_process.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == mCustomerAppList.size() + 1) {
                    return;
                } else {
                    if (position < mCustomerAppList.size() + 1) {
                        mProcessInfo = mCustomerAppList.get(position - 1);
                    } else {
                        mProcessInfo = mSystemAppList.get(position - mCustomerAppList.size() - 2);
                    }

                    if (mProcessInfo != null) {
                        if (!mProcessInfo.packageName.equals(getPackageName())) {
                            //1.状态取反
                            mProcessInfo.isCheck = !mProcessInfo.isCheck;
                            //2.设置给当前条目
                            CheckBox cb_box = (CheckBox) view.findViewById(R.id.cb_box);
                            cb_box.setChecked(mProcessInfo.isCheck);
                        }
                    }
                }
            }
        });


    }

    public void getData() {
        new Thread() {
            @Override
            public void run() {
                //找到所有进程信息的集合(耗时)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mProcessInfoList = ProcessInfoProvider.getProcessInfoList(getApplicationContext());
                } else {
                    mProcessInfoList = ProcessInfoProvider.getProcessInfoList1(getApplicationContext());
                }
                mSystemAppList = new ArrayList<ProcessInfo>();
                mCustomerAppList = new ArrayList<ProcessInfo>();
                for (ProcessInfo processInfo : mProcessInfoList) {
                    if (processInfo.isSystem) {
                        mSystemAppList.add(processInfo);
                    } else {
                        mCustomerAppList.add(processInfo);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initData() {
        //获得总的进程数
        mProcessCount = ProcessInfoProvider.getProcessCount(this);
        tv_total_process.setText("进程总数：" + mProcessCount);
        //获得可用内存大小和总内存大小,并格式化
        mAvailSpace = ProcessInfoProvider.getAvailSpace(this);
        String strAvailSpace = Formatter.formatFileSize(this, mAvailSpace);
        mTotalSpace = ProcessInfoProvider.getTotalSpace(this);
        String strTotalSpace = Formatter.formatFileSize(this, mTotalSpace);
        tv_memory_info.setText("剩余/总共：" + strAvailSpace + "/" + strTotalSpace);
    }

    private void initUI() {
        tv_total_process = (TextView) findViewById(R.id.tv_total_process);
        tv_memory_info = (TextView) findViewById(R.id.tv_memory_info);
        tv_des = (TextView) findViewById(R.id.tv_des);

        lv_process = (ListView) findViewById(R.id.lv_process);

        bt_select_all = (Button) findViewById(R.id.bt_select_all);
        bt_select_reverse = (Button) findViewById(R.id.bt_select_reverse);
        bt_clear = (Button) findViewById(R.id.bt_clear);
        bt_setting = (Button) findViewById(R.id.bt_setting);

        bt_select_all.setOnClickListener(this);
        bt_select_reverse.setOnClickListener(this);
        bt_clear.setOnClickListener(this);
        bt_setting.setOnClickListener(this);
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
            boolean sysProIsVisible = SpUtils.getBoolean(getApplicationContext(), ConstantValue.VISIBLE_SYSTEMPRO, false);
            if (sysProIsVisible) {
                return mSystemAppList.size() + mCustomerAppList.size() + 2;
            } else {
                return mCustomerAppList.size() + 1;
            }
        }

        @Override
        public ProcessInfo getItem(int position) {
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
                    viewTitleHolder.tv_app_title.setText("用户进程（" + mCustomerAppList.size() + "）");
                } else {
                    viewTitleHolder.tv_app_title.setText("系统进程（" + mSystemAppList.size() + "）");
                }
                return convertView;
            } else {
                ViewHolder viewHolder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.lv_process_item, null);
                    viewHolder = new ViewHolder();
                    viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                    viewHolder.tv_process_name = (TextView) convertView.findViewById(R.id.tv_process_name);
                    viewHolder.tv_process_mem_size = (TextView) convertView.findViewById(R.id.tv_process_mem_size);
                    viewHolder.cb_box = (CheckBox) convertView.findViewById(R.id.cb_box);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                viewHolder.iv_icon.setBackgroundDrawable(getItem(position).icon);
                viewHolder.tv_process_name.setText(getItem(position).applicationName);
                String strMemSize = Formatter.formatFileSize(getApplicationContext(), getItem(position).getMemSize());
                viewHolder.tv_process_mem_size.setText("内存占用：" + strMemSize);
                //如果是本进程则隐藏单选框
                if (getItem(position).packageName.equals(getPackageName())) {
                    viewHolder.cb_box.setVisibility(View.GONE);
                } else {
                    viewHolder.cb_box.setVisibility(View.VISIBLE);
                }
                viewHolder.cb_box.setChecked(getItem(position).isCheck);

                return convertView;
            }
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_process_name;
        TextView tv_process_mem_size;
        CheckBox cb_box;
    }

    static class ViewTitleHolder {
        TextView tv_app_title;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_select_all:
                selectAll();
                break;
            case R.id.bt_select_reverse:
                selectReverse();
                break;
            case R.id.bt_clear:
                clearAll();
                break;
            case R.id.bt_setting:
                setting();
                break;
        }
    }

    /**
     * 开启进程设置界面
     */
    private void setting() {
        Intent intent = new Intent(this, ProcessSettingActivity.class);
        startActivityForResult(intent, SYSTEM_RPOCESS_IS_VISIBLE);
    }


    /**
     * 一键清理,清理选中进程
     */
    private void clearAll() {
        //1.获取选中的进程集合，并将这些进程加入一个待杀掉的进程集合中
        List<ProcessInfo> killProcessInfos = new ArrayList<>();
        for (ProcessInfo processInfo : mCustomerAppList) {
            if (processInfo.packageName.equals(getPackageName())) {
                continue;
            }
            if (processInfo.isCheck) {
                //如果被选中，则加入killProcessInfos中
                killProcessInfos.add(processInfo);
            }
        }
        for (ProcessInfo processInfo : mSystemAppList) {
            if (processInfo.isCheck) {
                //如果被选中，则加入killProcessInfos中
                killProcessInfos.add(processInfo);
            }
        }
        //2.循环待杀集合，从两个集合中去除对应名称的对象,同时在系统中将这些进程杀掉
        long totalReleaseMem = 0;
        for (ProcessInfo processInfo : killProcessInfos) {
            if (mCustomerAppList.contains(processInfo)) {
                mCustomerAppList.remove(processInfo);
            }
            if (mSystemAppList.contains(processInfo)) {
                mSystemAppList.remove(processInfo);
            }
            //杀掉进程
            ProcessInfoProvider.killProcess(this, processInfo);
            //记录释放的空间
            totalReleaseMem += processInfo.memSize;
        }
        //3.通知数据适配器更新
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        //4.更新进程总数和可用内存数
        mProcessCount -= killProcessInfos.size();
        mAvailSpace += totalReleaseMem;
        String strTotalReleaseMem = Formatter.formatFileSize(this, totalReleaseMem);
        String strAvailSpace = Formatter.formatFileSize(this, mAvailSpace);
        String strTotalSpace = Formatter.formatFileSize(this, mTotalSpace);
        tv_total_process.setText("进程总数：" + mProcessCount);
        tv_memory_info.setText("剩余/总共：" + strAvailSpace + "/" + strTotalSpace);
        //5.通过吐司告知用户杀掉多少进程，且释放了多少空间
        Toast.makeText(getApplicationContext(), "杀死了" + killProcessInfos.size() +
                "个进程，释放了" + strTotalReleaseMem + "空间", Toast.LENGTH_SHORT).show();
    }

    /**
     * 反选逻辑
     */
    private void selectReverse() {
        //1.将所有集合中的对象的Ischeck属性设置为相反的数值，排除当前应用
        for (ProcessInfo processInfo : mCustomerAppList) {
            if (processInfo.packageName.equals(getPackageName())) {
                continue;
            }
            processInfo.isCheck = !processInfo.isCheck;
        }
        for (ProcessInfo processInfo : mSystemAppList) {
            processInfo.isCheck = !processInfo.isCheck;
        }
        //2.通知数据适配器刷新
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 全选逻辑
     */
    private void selectAll() {
        //1.将所有集合中的对象的Ischeck属性设置为true，排除当前应用
        for (ProcessInfo processInfo : mCustomerAppList) {
            if (processInfo.packageName.equals(getPackageName())) {
                continue;
            }
            processInfo.isCheck = true;
        }
        for (ProcessInfo processInfo : mSystemAppList) {
            processInfo.isCheck = true;
        }
        //2.通知数据适配器刷新
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
