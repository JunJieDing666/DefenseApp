package com.jj.defense.DB.Domain;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2016/8/26.
 */
public class AppInfo {
    public String packageName;
    public String applicationName;
    public Drawable icon;
    public boolean isSDcard;
    public boolean isSystemApp;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isSystemApp() {
        return isSystemApp;
    }

    public void setIsSystemApp(boolean isSystemApp) {
        this.isSystemApp = isSystemApp;
    }

    public boolean isSDcard() {
        return isSDcard;
    }

    public void setIsSDcard(boolean isSDcard) {
        this.isSDcard = isSDcard;
    }
}
