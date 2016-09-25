package com.jj.defense;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jj.defense.Activity.AToolActivity;
import com.jj.defense.Activity.AntiVirusActivity;
import com.jj.defense.Activity.AppManagerActivity;
import com.jj.defense.Activity.BaseCacheCleanActivity;
import com.jj.defense.Activity.BlackNumListActivity;
import com.jj.defense.Activity.CleanCacheActivity;
import com.jj.defense.Activity.ProcessManagerActivity;
import com.jj.defense.Activity.SettingActivity;
import com.jj.defense.Activity.SetupOverActivity;
import com.jj.defense.Activity.TestActivity;
import com.jj.defense.Activity.TrafficActivity;
import com.jj.defense.Utils.ConstantValue;
import com.jj.defense.Utils.Md5Utils;
import com.jj.defense.Utils.SpUtils;

/**
 * Created by Administrator on 2016/7/4.
 */
public class HomeActivity extends Activity {
    private GridView gv_home = null;
    private String[] mTitleStr = null;
    private int[] mDrawableIds = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        int a = 10/0;

        //初始化UI
        initUI();
        //初始化数据
        initData();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void initData() {
        mTitleStr = new String[]{"手机防盗", "通信卫士", "软件管理", "进程管理", "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"};

        mDrawableIds = new int[]{R.drawable.safe, R.drawable.callmsgsafe, R.drawable.app, R.drawable.taskmanager,
                R.drawable.netmanager, R.drawable.trojan, R.drawable.sysoptimize, R.drawable.atools,
                R.drawable.settings};

        gv_home.setAdapter(new MyAdapter());

        //设置九宫格的点击事件
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        showDialog();
                        break;
                    case 1:
                        startActivity(new Intent(getApplicationContext(), BlackNumListActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(getApplicationContext(), AppManagerActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(getApplicationContext(), ProcessManagerActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(getApplicationContext(), TrafficActivity.class));
                        break;
                    case 5:
                        startActivity(new Intent(getApplicationContext(), AntiVirusActivity.class));
                        break;
                    case 6:
                        startActivity(new Intent(getApplicationContext(), BaseCacheCleanActivity.class));
                        break;
                    case 7:
                        startActivity(new Intent(getApplicationContext(), AToolActivity.class));
                        break;
                    case 8:
                        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    protected void showDialog() {
        //判断sp是否已经存储了密码
        String psd = SpUtils.getString(this, ConstantValue.DEFENSE_PSD, "");
        if (TextUtils.isEmpty(psd)) {
            //弹出设置密码对话框
            showSetPsdDialog();
        } else {
            //弹出确认密码对话框
            showConfirmPsdDialog();
        }
    }

    private void showConfirmPsdDialog() {
        //创建对话框
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        //因为对话框的样式需要自己设定，所以调用setView()方法
        final View view = View.inflate(this, R.layout.dialog_confirm_psd, null);
        dialog.setView(view);
        dialog.show();

        //设置对话框按钮的点击事件
        Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

        //确认键的点击事件
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);

                String confirmPsd = et_confirm_psd.getText().toString();

                String psd = SpUtils.getString(getApplicationContext(), ConstantValue.DEFENSE_PSD, "");

                if (!TextUtils.isEmpty(confirmPsd)) {
                    if (psd.equals(Md5Utils.encode(confirmPsd))) {
                        //进入手机防盗模块
                        //Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                        Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
                        startActivity(intent);
                        //防止按返回键后对话框还在，需要解散对话框
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "确认密码错误", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //弹出吐司指出错误
                    Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //取消键的点击事件
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void showSetPsdDialog() {
        //创建对话框
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        //因为对话框的样式需要自己设定，所以调用setView()方法
        final View view = View.inflate(this, R.layout.dialog_set_psd, null);
        dialog.setView(view);
        dialog.show();

        //设置对话框按钮的点击事件
        Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

        //确认键的点击事件
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_set_psd = (EditText) view.findViewById(R.id.et_set_psd);
                EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);

                String psd = et_set_psd.getText().toString();
                String confirmPsd = et_confirm_psd.getText().toString();

                if (!TextUtils.isEmpty(psd) && !TextUtils.isEmpty(confirmPsd)) {
                    if (psd.equals(confirmPsd)) {
                        //进入手机防盗模块
                        //Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                        Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
                        startActivity(intent);
                        //防止按返回键后对话框还在，需要解散对话框
                        dialog.dismiss();

                        //将用户设置的密码存储在sp中
                        SpUtils.putString(getApplicationContext(), ConstantValue.DEFENSE_PSD, Md5Utils.encode(psd));
                    } else {
                        Toast.makeText(getApplicationContext(), "确认密码错误", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //弹出吐司指出错误
                    Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //取消键的点击事件
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private void initUI() {
        gv_home = (GridView) findViewById(R.id.gv_home);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Home Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.jj.defense/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Home Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.jj.defense/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mTitleStr.length;
        }

        @Override
        public Object getItem(int position) {
            return mTitleStr[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //注意适配器中findViewById()方法前要加上特定的view
            View view = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
            ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            TextView tv_title = (TextView) view.findViewById(R.id.tv_title);

            tv_title.setText(mTitleStr[position]);
            iv_icon.setBackgroundResource(mDrawableIds[position]);
            return view;
        }
    }
}
