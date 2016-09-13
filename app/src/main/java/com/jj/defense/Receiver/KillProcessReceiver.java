package com.jj.defense.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jj.defense.Engine.ProcessInfoProvider;

/**
 * Created by Administrator on 2016/9/8.
 */
public class KillProcessReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ProcessInfoProvider.killAll(context);
    }
}
