package com.jj.defense.Engine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jj.defense.DB.BlackNumberOpenHelper;
import com.jj.defense.DB.Domain.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/16.
 */
public class BlackNumberDao {

    private final BlackNumberOpenHelper blackNumberOpenHelper;

    //采用单例模式
    //1.私有化构造方法
    private BlackNumberDao(Context context) {
        blackNumberOpenHelper = new BlackNumberOpenHelper(context);
    }

    //2.声明一个当前类的对象
    private static BlackNumberDao blackNumberDao = null;

    //3.提供一个方法，如果当前类的对象为空，创建一个新的对象
    public static BlackNumberDao getInstance(Context context) {
        if (blackNumberDao == null) {
            blackNumberDao = new BlackNumberDao(context);
        }
        return blackNumberDao;
    }

    /**
     * 增加一个条目
     *
     * @param phone 拦截的电话号码
     * @param mode  拦截的模式
     */
    public void insert(String phone, String mode) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("phone", phone);
        contentValues.put("mode", mode);
        db.insert("blacknumber", null, contentValues);
        db.close();
    }

    /**
     * 删除一个条目
     *
     * @param phone 删除的电话号码
     */
    public void delete(String phone) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

        db.delete("blacknumber", "phone = ?", new String[]{phone});

        db.close();
    }

    /**
     * 更新一个条目
     *
     * @param phone 要更新的电话号码
     * @param mode  更改目标号码的拦截模式
     */
    public void update(String phone, String mode) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("mode", mode);
        db.update("blacknumber", contentValues, "phone = ?", new String[]{phone});

        db.close();
    }

    /**
     * 查询出所有条目
     */
    public List<BlackNumberInfo> queryAll() {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

        Cursor cursor = db.query("blacknumber", new String[]{"phone", "mode"}, null, null, null, null, "_id desc");
        List<BlackNumberInfo> blackNumberInfoList = new ArrayList<BlackNumberInfo>();
        while (cursor.moveToNext()) {
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.phone = cursor.getString(0);
            blackNumberInfo.mode = cursor.getString(1);
            blackNumberInfoList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();

        return blackNumberInfoList;
    }

    /**
     * 查询部分条目的方法
     *
     * @param index 查询方法起始位置的索引值
     * @return
     */
    public List<BlackNumberInfo> query(int index) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("select phone,mode from blacknumber order by _id desc limit ?,20;", new String[]{index + ""});
        List<BlackNumberInfo> blackNumberInfoList = new ArrayList<BlackNumberInfo>();
        while (cursor.moveToNext()) {
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.phone = cursor.getString(0);
            blackNumberInfo.mode = cursor.getString(1);
            blackNumberInfoList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();

        return blackNumberInfoList;
    }

    /**
     * @return 获取数据库中条目总数的方法，返回0代表异常或没查到数据
     */
    public int getCount() {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("select count(*) from blacknumber;", null);
        int count = 0;
        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    /**
     * 获得指定号码的拦截模式
     *
     * @param phone 拦截号码
     */
    public int getMode(String phone) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

        Cursor cursor = db.query("blacknumber", new String[]{"mode"}, "phone = ?", new String[]{phone}, null, null, null);
        int mode = 0;
        while (cursor.moveToNext()) {
            mode = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return mode;
    }
}
