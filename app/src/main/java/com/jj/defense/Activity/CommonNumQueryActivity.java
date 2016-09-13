package com.jj.defense.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.jj.defense.Engine.CommonNumDao;
import com.jj.defense.R;

import java.util.List;

/**
 * Created by Administrator on 2016/9/4.
 */
public class CommonNumQueryActivity extends Activity {

    private ExpandableListView elv_common_num_query;
    private List<CommonNumDao.Group> groupList;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_num_query);

        initUI();
        initData();
    }

    /**
     * 给可扩展的列表框准备并填充数据
     */
    private void initData() {
        CommonNumDao commonNumDao = new CommonNumDao();
        groupList = commonNumDao.getGroup();

        mAdapter = new MyAdapter();
        elv_common_num_query.setAdapter(mAdapter);

        elv_common_num_query.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                startCall(v.findViewById(R.id.tv_number).toString());
                return false;
            }
        });
    }

    private void startCall(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }

    private void initUI() {
        elv_common_num_query = (ExpandableListView) findViewById(R.id.elv_common_num_query);
    }

    private class MyAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return groupList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return groupList.get(groupPosition).childList.size();
        }

        @Override
        public CommonNumDao.Group getGroup(int groupPosition) {
            return groupList.get(groupPosition);
        }

        @Override
        public CommonNumDao.Child getChild(int groupPosition, int childPosition) {
            return groupList.get(groupPosition).childList.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView textView = new TextView(getApplicationContext());
            textView.setText("         " + getGroup(groupPosition).name);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            return textView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.common_num_child_item, null);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            TextView tv_number = (TextView) view.findViewById(R.id.tv_number);
            tv_name.setText(getChild(groupPosition, childPosition).name);
            tv_number.setText(getChild(groupPosition, childPosition).number);
            return view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
