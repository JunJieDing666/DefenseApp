package com.jj.defense.Global;

import android.app.Application;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by Administrator on 2016/9/25.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                //在获取到未捕获的异常后调用的方法(通常是将异常存储在一个文件中放在SD卡中然后上传至公司服务器)
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + "errorDefense.log";
                File file = new File(path);
                PrintWriter printWriter = null;
                try {
                    printWriter = new PrintWriter(file);
                    ex.printStackTrace(printWriter);
                    printWriter.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                //上传公司服务器
                //手动退出应用
                System.exit(0);
            }
        });
    }
}
