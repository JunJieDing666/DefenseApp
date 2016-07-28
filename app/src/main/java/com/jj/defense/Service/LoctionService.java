package com.jj.defense.Service;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;

import com.jj.defense.Utils.ConstantValue;
import com.jj.defense.Utils.SpUtils;

/**
 * Created by Administrator on 2016/7/28.
 */
public class LoctionService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //获取手机的经纬度
        //1.获取位置管理者
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //2.以最优的方式获取坐标
        Criteria criteria = new Criteria();
        //指定获取坐标的精度
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //允许花费
        criteria.setCostAllowed(true);
        String bestProvider = locationManager.getBestProvider(criteria, true);
        MyLocationListener myLocationListener = new MyLocationListener();
        //3.每时每刻获取经纬度
        locationManager.requestLocationUpdates(bestProvider, 0, 0, myLocationListener);
    }

    //写一个实现位置监听器的接口
    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            //获得经度
            double longitude = location.getLongitude();
            //获得纬度
            double latitude = location.getLatitude();

            //4.在获取经纬度后发送短信给安全号码
            SmsManager smsManager = SmsManager.getDefault();
            String phone = SpUtils.getString(getApplicationContext(), ConstantValue.PHONE_NUM, "");
            smsManager.sendTextMessage(phone, null, "lontitude = " + longitude + "\nlatitude = " + latitude, null, null);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
