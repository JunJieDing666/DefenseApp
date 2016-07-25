package com.jj.defense.Activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jj.defense.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/7/19.
 */
public class ContactListActivity extends Activity {
    private ListView lv_contact = null;
    private MyAdapter myAdapter = null;
    //用哈希表做List的成员
    private List<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //8.填充数据适配器
            myAdapter = new MyAdapter();
            lv_contact.setAdapter(myAdapter);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        initUI();
        initData();
    }

    /**
     * 获得系统联系人数据的方法（耗时操作应放到子线程中进行）
     */
    private void initData() {
        new Thread() {
            @Override
            public void run() {
               /* //1.获得内容解析器
                ContentResolver contentResolver = getContentResolver();
                //2.查询系统联系人数据库表的过程（先找到每个联系人对应的id）
                Cursor cursor = contentResolver.query(Uri.parse("content://com.android.contacts/raw_contacts"),
                        new String[]{"_id"}, null, null, null);
                contactList.clear();   //每次列表做添加前都需要清空，避免重复
                //3.做循环，知道没有游标为止
                while (cursor.moveToNext()) {
                    String id = cursor.getString(0);
                    Log.w("message",id);
                    //4.用户根据唯一性id找到对应联系人的姓名和电话
                    Cursor indexCursor = contentResolver.query(Uri.parse("content://com.android.contacts/data"),
                            new String[]{"data1", "mimetype"},
                            "raw_contact_id = ? ",
                            new String[]{id}, null);
                    //5.做循环，将每个联系人的姓名和电话遍历出来,并将其存放在哈希键值对中
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    while (indexCursor.moveToNext()) {
                        String data = indexCursor.getString(0);
                        String type = indexCursor.getString(1);
                        //Log.w("message",data);
                        //Log.w("message",type);
                        //6.区分类型给哈希表做填充
                        if (type.equals("vnd.android.cursor.item/name")) {
                            if (!hashMap.isEmpty()) {
                                hashMap.put("name", data);
                            }
                        } else if (type.equals("vnd.android.cursor.item/phone_v2")) {
                            if (!hashMap.isEmpty()) {
                                hashMap.put("phone", data);
                            }
                        }
                    }
                    indexCursor.close();
                    //得到n个哈希表，每个哈希表都有"name"和"phone"两个键
                    contactList.add(hashMap);
                }
                cursor.close();*/

                //封装好的方法
                Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, null, null, null);
                contactList.clear();
                while (cursor.moveToNext()) {
                    String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    //Log.w("message", displayName);
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("name", displayName);
                    hashMap.put("phone", number);
                    contactList.add(hashMap);
                }
                cursor.close();

                //7.消息机制，控制UI
                mHandler.sendEmptyMessage(0);

            }
        }.start();
    }

    private void initUI() {
        lv_contact = (ListView) findViewById(R.id.lv_contact);

        //设置列表框条目点击事件
        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (myAdapter != null){
                    //获取点击条目所对应的哈希表对象
                    HashMap<String, String> hashMap = myAdapter.getItem(position);
                    //从该对象读取电话
                    String phone = hashMap.get("phone");
                    //在此界面结束时将该电话返回给导航界面3
                    Intent intent = new Intent();
                    intent.putExtra("phone",phone);
                    setResult(0,intent);
                    finish();
                }
            }
        });
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return contactList.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return contactList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.listview_contact_item, null);

            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);

            tv_name.setText(contactList.get(position).get("name"));
            tv_phone.setText(contactList.get(position).get("phone"));

            return view;
        }
    }
}
