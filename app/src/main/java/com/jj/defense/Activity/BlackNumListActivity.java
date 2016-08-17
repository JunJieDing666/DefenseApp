package com.jj.defense.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.os.Handler;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jj.defense.DB.Domain.BlackNumberInfo;
import com.jj.defense.Engine.BlackNumberDao;
import com.jj.defense.R;

import java.util.List;

/**
 * Created by Administrator on 2016/8/15.
 */
public class BlackNumListActivity extends Activity {

    private Button bt_add_black_num;
    private ListView lv_black_num;
    private BlackNumberDao mBlackNumDao;
    private List<BlackNumberInfo> mBlackNumberInfoList;
    private MyBlackNumAdapter myBlackNumAdapter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            myBlackNumAdapter = new MyBlackNumAdapter();
            lv_black_num.setAdapter(myBlackNumAdapter);
        }
    };
    private int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_num_list);

        initUI();
        initData();
    }

    private void initData() {
        //获得数据库中所有的号码(耗时操作)
        new Thread() {
            @Override
            public void run() {
                //1.获得操作黑名单数据库的接口类
                mBlackNumDao = BlackNumberDao.getInstance(getApplicationContext());
                //2.查询所有数据
                mBlackNumberInfoList = mBlackNumDao.queryAll();
                //3.发送空消息告知获取完毕
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initUI() {
        bt_add_black_num = (Button) findViewById(R.id.bt_add_black_num);
        lv_black_num = (ListView) findViewById(R.id.lv_black_num);

        bt_add_black_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(getApplicationContext(), R.layout.dialog_add_black_num, null);
        dialog.setView(view, 0, 0, 0, 0);

        final EditText et_black_num = (EditText) view.findViewById(R.id.et_black_num);
        RadioGroup rg_mode_group = (RadioGroup) view.findViewById(R.id.rg_mode_group);
        Button bt_confirm = (Button) view.findViewById(R.id.bt_confirm);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

        //监听单选框组里条目的切换情况
        rg_mode_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_sms:
                        mode = 1;
                        break;
                    case R.id.rb_phone:
                        mode = 2;
                        break;
                    case R.id.rb_all:
                        mode = 3;
                        break;
                }
            }
        });

        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = et_black_num.getText().toString();
                if (!TextUtils.isEmpty(phone)) {
                    //1.将新电话添加进数据库
                    mBlackNumDao.insert(phone, mode + "");
                    //2.让数据库与集合保持同步,采用手动将新号码加入集合的方式
                    BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                    blackNumberInfo.phone = phone;
                    blackNumberInfo.mode = mode + "";
                    //3.将对象插入集合的顶部
                    mBlackNumberInfoList.add(0, blackNumberInfo);
                    //4.通知数据适配器刷新
                    if (myBlackNumAdapter != null) {
                        myBlackNumAdapter.notifyDataSetChanged();
                    }
                    //5.将对话框关闭
                    dialog.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(), "请输入拦截的号码", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private class MyBlackNumAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mBlackNumberInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return mBlackNumberInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            /*Listview的优化
            * 1.convertview的复用
            * 2.减少findviewbyId的次数采用viewHolder
            * 3.将viewHolder声明为静态占用固定空间*/
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.black_num_listview_item, null);
                viewHolder = new ViewHolder();
                viewHolder.tv_black_phone = (TextView) convertView.findViewById(R.id.tv_black_phone);
                viewHolder.tv_black_mode = (TextView) convertView.findViewById(R.id.tv_black_mode);
                viewHolder.iv_delete_black_num = (ImageView) convertView.findViewById(R.id.iv_delete_black_num);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //删除黑名单号码
            viewHolder.iv_delete_black_num.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //1.删除数据库中的该条
                    mBlackNumDao.delete(mBlackNumberInfoList.get(position).phone);
                    //2.删除集合中的该条
                    mBlackNumberInfoList.remove(position);
                    //3.通知数据适配器刷新
                    if (myBlackNumAdapter != null) {
                        myBlackNumAdapter.notifyDataSetChanged();
                    }
                }
            });

            viewHolder.tv_black_phone.setText(mBlackNumberInfoList.get(position).phone);
            int mode = Integer.parseInt(mBlackNumberInfoList.get(position).mode);
            switch (mode) {
                case 1:
                    viewHolder.tv_black_mode.setText("拦截短信");
                    break;
                case 2:
                    viewHolder.tv_black_mode.setText("拦截电话");
                    break;
                case 3:
                    viewHolder.tv_black_mode.setText("拦截所有");
                    break;
            }
            return convertView;
        }
    }

    static private class ViewHolder {
        TextView tv_black_phone;
        TextView tv_black_mode;
        ImageView iv_delete_black_num;
    }
}
