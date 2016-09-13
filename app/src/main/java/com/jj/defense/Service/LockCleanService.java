package com.jj.defense.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.jj.defense.Engine.ProcessInfoProvider;

/**
 * Created by Administrator on 2016/9/2.
 */
public class LockCleanService extends Service {

    private IntentFilter intentFilter;
    private ScreenOffReceiver screenOffReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        screenOffReceiver = new ScreenOffReceiver();
        registerReceiver(screenOffReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        if (screenOffReceiver != null) {
            unregisterReceiver(screenOffReceiver);
        }
        super.onDestroy();
    }

    private class ScreenOffReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ProcessInfoProvider.killAll(context);
        }
    }
}
