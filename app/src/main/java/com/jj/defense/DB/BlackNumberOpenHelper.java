package com.jj.defense.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/8/16.
 */
public class BlackNumberOpenHelper extends SQLiteOpenHelper {
    public BlackNumberOpenHelper(Context context) {
        super(context, "blacknumber", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库中黑名单表的方法
        db.execSQL("create table blacknumber (_id integer " +
                "primary key autoincrement , phone varchar(20), mode varchar(5));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
