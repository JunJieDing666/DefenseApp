package com.jj.defense.DB.Domain;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/8/16.
 */
public class AppLockOpenHelper extends SQLiteOpenHelper {
    public AppLockOpenHelper(Context context) {
        super(context, "applock", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库中黑名单表的方法
        db.execSQL("create table applock (_id integer " +
                "primary key autoincrement , packagename varchar(50));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
