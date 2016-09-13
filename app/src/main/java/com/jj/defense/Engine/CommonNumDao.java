package com.jj.defense.Engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/9.
 */
public class CommonNumDao {
    //1.指定访问你数据库的路径
    public static String path = "data/data/com.jj.defense/files/commonnum.db";

    //2.开启数据库获得每个组
    public List<Group> getGroup() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("classlist", new String[]{"name", "idx"}, null, null, null, null, null);
        List<Group> groupList = new ArrayList<>();
        while (cursor.moveToNext()) {
            Group group = new Group();
            group.name = cursor.getString(0);
            group.idx = cursor.getString(1);
            group.childList = getChild(group.idx);
            groupList.add(group);
        }
        cursor.close();
        db.close();
        return groupList;
    }

    //3.获得每个组中孩子结点
    public List<Child> getChild(String idx) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery("select * from table" + idx + ";", null);
        List<Child> childList = new ArrayList<>();
        while (cursor.moveToNext()) {
            Child child = new Child();
            child._id = cursor.getString(0);
            child.number = cursor.getString(1);
            child.name = cursor.getString(2);
            childList.add(child);
        }
        cursor.close();
        db.close();
        return childList;
    }

    public class Group {
        public String name;
        public String idx;
        public List<Child> childList;
    }

    public class Child {
        public String _id;
        public String number;
        public String name;
    }
}
