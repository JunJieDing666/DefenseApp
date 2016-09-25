package com.jj.defense.Engine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.jj.defense.DB.Domain.AppLockOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/16.
 */
public class AppLockDao {

    private final AppLockOpenHelper appLockOpenHelper;
    private Context context;

    //采用单例模式
    //1.私有化构造方法
    private AppLockDao(Context context) {
        this.context = context;
        appLockOpenHelper = new AppLockOpenHelper(context);
    }

    //2.声明一个当前类的对象
    private static AppLockDao appLockDao = null;

    //3.提供一个方法，如果当前类的对象为空，创建一个新的对象
    public static AppLockDao getInstance(Context context) {
        if (appLockDao == null) {
            appLockDao = new AppLockDao(context);
        }
        return appLockDao;
    }

    //插入
    public void Insert(String packagename) {
        SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("packagename", packagename);
        db.insert("applock", null, contentValues);
        db.close();
        context.getContentResolver().notifyChange(Uri.parse("content://applock/change"),null);
    }

    //删除
    public void Delete(String packagename) {
        SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
        db.delete("applock", "packagename = ?", new String[]{packagename});
        db.close();
        context.getContentResolver().notifyChange(Uri.parse("content://applock/change"), null);
    }

    //查询所有条目
    public List<String> findAll() {
        SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
        List<String> LockedAppList = new ArrayList<String>();
        Cursor cursor = db.query("applock", new String[]{"packagename"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            LockedAppList.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return LockedAppList;
    }


}
