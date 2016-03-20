package com.example.isky.flaggame.server;

import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.model.LatLng;
import com.example.isky.flaggame.role.LocationInfoSender;

import java.util.ArrayList;

import util.GameApplication;

/**
 * Created by isky on 2016/1/28.
 * 对于定位服务的封装
 */
public class LocationServiceManager extends LocationInfoSender implements LocationSource, AMapLocationListener {
    private static LocationServiceManager locationServiceManager;
    ArrayList<OnLocationChangedListener> onLocationChangedListeners = new ArrayList<>();
    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    private AMapLocationClientOption mLocationOption = null;

    private LocationServiceManager() {
        initClient();
    }

    public static LocationServiceManager getInstance() {
        if (locationServiceManager == null)
            locationServiceManager = new LocationServiceManager();
        return locationServiceManager;
    }

    private void initClient() {
        mLocationClient = new AMapLocationClient(GameApplication.getApplication());
        initLocationOption();
        mLocationClient.setLocationListener(this);
    }

    /**
     * 配置定位的相关参数
     */
    public void initLocationOption() {
        //初始化定位参数
        AMapLocationClientOption locationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        locationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        locationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        locationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        locationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        locationOption.setInterval(1000);
        //给定位客户端对象设置定位参数
        initLocationOption(locationOption);


    }

    public boolean initLocationOption(AMapLocationClientOption mLocationOption) {
        if (mLocationClient == null)
            return false;
        boolean isStarted = mLocationClient.isStarted();
        if (isStarted)
            mLocationClient.stopLocation();
        this.mLocationOption = mLocationOption;
        mLocationClient.setLocationOption(mLocationOption);
        if (isStarted)
            mLocationClient.startLocation();
        return true;
    }

    /**
     * 注册位置改变监听器，可以同时存在多个监听
     *
     * @param onLocationChangedListener
     */
    public void registerOnLocationchangelistener(OnLocationChangedListener onLocationChangedListener) {
        if (onLocationChangedListener != null && !onLocationChangedListeners.contains(onLocationChangedListener))
            synchronized (onLocationChangedListeners) {
                onLocationChangedListeners.add(onLocationChangedListener);
            }
    }

    public void unregisterOnLocationchangelistener(OnLocationChangedListener onLocationChangedListener) {
        if (onLocationChangedListener != null)
            synchronized (onLocationChangedListeners) {
                onLocationChangedListeners.remove(onLocationChangedListener);
            }
    }

    /**
     * 添加监听器并且激活定位
     *
     * @param onLocationChangedListener 若已经被添加则不重复添加,可以为null,若为null则仅仅直接开始获取定位信息
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        if (mLocationClient == null) {
            initClient();
        }
        synchronized (onLocationChangedListeners) {
            if (onLocationChangedListener != null && !onLocationChangedListeners.contains(onLocationChangedListener))

                onLocationChangedListeners.add(onLocationChangedListener);
        }
        if (!mLocationClient.isStarted()) {
            mLocationClient.startLocation();
            Log.d("hhh", "激活定位 size=" + onLocationChangedListeners.size());
        }
    }

    /**
     * 停止定位，但监听器保留
     */
    @Override
    public void deactivate() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                synchronized (onLocationChangedListeners) {
                    java.text.DecimalFormat df = new java.text.DecimalFormat("#.######");
                    //发送地点信息给接收者
                    LatLng latLng = new LatLng(Double.parseDouble(df.format(aMapLocation.getLatitude())),
                            Double.parseDouble(df.format(aMapLocation.getLongitude())));
                    send(latLng);
                    for (OnLocationChangedListener mlistener : onLocationChangedListeners)
                        mlistener.onLocationChanged(aMapLocation);
                }
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    /**
     * 摧毁当前实例，即所有监听失效，当前进行的定位停止
     */
    public void destory() {
        deactivate();
        onLocationChangedListeners.clear();
    }

}
