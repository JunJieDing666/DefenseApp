package com.jj.defense.Engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2016/8/9.
 */
public class AddressDao {
    //1.指定访问你数据库的路径
    public static String path = "data/data/com.jj.defense/files/address.db";

    private static String mAddress = null;

    /**
     * 传入一个电话号码，开启数据库连接，并返回归属地
     *
     * @param phone 传入的电话号码
     */
    public static String getAddress(String phone) {
        mAddress = "未知号码";
        //通过正则表达式限制输入号码的类型
        String regularExpression = "^1[3-8]\\d{9}";
        if (phone.matches(regularExpression)) {
            //截取输入号码的前7位
            phone = phone.substring(0, 7);
            //2.启动数据库连接
            SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
            //3.查询电话归属地
            Cursor cursor = db.query("data1", new String[]{"outkey"}, "id = ?", new String[]{phone}, null, null, null);
            //查到即可
            if (cursor.moveToNext()) {
                String outkey = cursor.getString(0);
                //4.根据data1查到的外键关联data2里查到的地址
                Cursor indexCursor = db.query("data2", new String[]{"location"}, "id = ?", new String[]{outkey}, null, null, null);
                if (indexCursor.moveToNext()) {
                    //5.获取查询到的归属地
                    mAddress = indexCursor.getString(0);
                }
            }
        } else {
            int length = phone.length();
            switch (length) {
                case 3:
                    mAddress = "报警号码";
                    break;
                case 4:
                    mAddress = "模拟器";
                    break;
                case 5:
                    mAddress = "服务号码";
                    break;
            }
        }
        return mAddress;
    }
}
