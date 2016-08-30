package com.jj.defense.Engine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016/8/24.
 */
public class SmsBackUpDao {
    private static int index;
    private static FileOutputStream fileOutputStream = null;
    private static Cursor cursor = null;

    //需要的参数有：上下文环境、进度条对话框、存储短息的路径
    public static void backUp(Context ctx, ProgressCallBack progressCallBack, String path) {
        try {
            //1.获取备份短信要写入的文件
            File file = new File(path);
            //2.获得内容解析器，获取短信数据库中的数据
            cursor = ctx.getContentResolver().query(Uri.parse("content://sms/"),
                    new String[]{"address", "date", "type", "body"}, null, null, null);
            //3.写入数据相应的输出流
            fileOutputStream = new FileOutputStream(file);
            //4.序列化数据库中读取的数据放入xml中
            XmlSerializer xmlSerializer = Xml.newSerializer();
            //5.给此xml做相应设置
            xmlSerializer.setOutput(fileOutputStream, "utf-8");
            //DTD(规范)
            xmlSerializer.startDocument("utf-8", true);
            xmlSerializer.startTag(null, "smss");
            //6.指定备份短信总数目
            if (progressCallBack != null) {
                progressCallBack.setMax(cursor.getCount());
            }
            //7.将数据库中的每一行数据写入xml
            while (cursor.moveToNext()) {
                xmlSerializer.startTag(null, "sms");

                xmlSerializer.startTag(null, "address");
                xmlSerializer.text(cursor.getString(0));
                xmlSerializer.endTag(null, "address");

                xmlSerializer.startTag(null, "date");
                xmlSerializer.text(cursor.getString(1));
                xmlSerializer.endTag(null, "date");

                xmlSerializer.startTag(null, "type");
                xmlSerializer.text(cursor.getString(2));
                xmlSerializer.endTag(null, "type");

                xmlSerializer.startTag(null, "body");
                xmlSerializer.text(cursor.getString(3));
                xmlSerializer.endTag(null, "body");

                xmlSerializer.endTag(null, "sms");

                //8.每循环一次，进度条加一格
                index++;
                Thread.sleep(200);
                if (progressCallBack != null) {
                    progressCallBack.setProgress(index);
                }
            }
            xmlSerializer.endTag(null, "smss");
            xmlSerializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null && cursor != null) {
                try {
                    cursor.close();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public interface ProgressCallBack {

        /**
         * 设置短信总数的方法
         *
         * @param max 短信总数
         */
        public void setMax(int max);

        /**
         * 备份过程中更新百分比的方法
         *
         * @param index 百分比
         */
        public void setProgress(int index);
    }
}
