package com.jj.defense.Engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/9.
 */
public class AntiVirusDao {
    //1.指定访问你数据库的路径
    public static String path = "data/data/com.jj.defense/files/antivirus.db";

    /**
     * @return  获取病毒数据库MD5码的方法
     */
    public static List<String> getVirusMd5List() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("datable", new String[]{"md5"}, null, null, null, null, null);
        List<String> virusMd5List = new ArrayList<>();
        while (cursor.moveToNext()) {
            virusMd5List.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return virusMd5List;
    }

}
