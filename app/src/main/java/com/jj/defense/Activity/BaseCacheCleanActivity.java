package com.jj.defense.Activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.jj.defense.R;

/**
 * Created by Administrator on 2016/9/20.
 */
public class BaseCacheCleanActivity extends TabActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_cache_clean);

        initUI();
    }

    private void initUI() {
        TabHost tabHost = getTabHost();
        tabHost.addTab(tabHost.newTabSpec("clear_cache").setIndicator("清理缓存").setContent(new Intent(this, CleanCacheActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("clear_sd_cache").setIndicator("清理sd卡缓存").setContent(new Intent(this, CleanSDCacheActivity.class)));
        /*//1.获得选项卡一、二
        TabHost.TabSpec tabSpec1 = getTabHost().newTabSpec("clear_cache").setIndicator("缓存清理");
        TabHost.TabSpec tabSpec2 = getTabHost().newTabSpec("clear_sd_cache").setIndicator("sd卡清理");
        //2.告知选中选项卡后序操作，即显示什么内容
        tabSpec1.setContent(new Intent(this, CleanCacheActivity.class));
        tabSpec1.setContent(new Intent(this, CleanSDCacheActivity.class));
        //3.将此两个选项卡维护到宿主上去
        getTabHost().addTab(tabSpec1);
        getTabHost().addTab(tabSpec2);*/
    }
}
