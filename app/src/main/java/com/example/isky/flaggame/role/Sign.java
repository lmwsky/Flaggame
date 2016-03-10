package com.example.isky.flaggame.role;

import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.LatLng;

import java.util.ArrayList;

/**
 * Created by isky on 2016/1/26.
 * 游戏中能够在地图上显示必须继承这个类
 * 必须具有所在经纬度和显示的图片描述
 */
public abstract class Sign {
    protected LatLng latLng;
    transient protected BitmapDescriptor icon;//标志的图片
    protected String name;//标志的名字
    transient protected ArrayList<OnSignListener> onSignListeners = new ArrayList<>();
    protected int team; //所属队伍
    private String signature;//唯一签名，保证能够通过这个来

    public Sign() {
        //利用sign创建的时间作为唯一签名
        this.signature = (Math.random() * 10000 + "").substring(0, 5) + System.currentTimeMillis() + "";
        team = 0;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    /**
     * 获得Sign所在经纬度
     *
     * @return 若尚未初始化则为null
     */
    public LatLng getLatLng() {
        return latLng;
    }

    /**
     * 将当前的Role的坐标设为指定经纬度，并且触发位置改变的监听器
     *
     * @param latLng
     */
    public void setLatLng(LatLng latLng) {
        this.latLng = new LatLng(latLng.latitude, latLng.longitude);
        notifyOnSignMoveListeners();
    }

    /**
     * 将当前的Role的坐标设为指定经纬度，并且触发位置改变的监听器
     *
     * @param latitude  纬度
     * @param longitude 经度
     */
    public void setLatLng(double latitude, double longitude) {
        this.latLng = new LatLng(latitude, longitude);
        notifyOnSignMoveListeners();
    }

    /**
     * 获得Sign的图标描述
     *
     * @return 若尚未初始化则为null
     */
    public BitmapDescriptor getIcon() {
        return icon;
    }

    public void setIcon(BitmapDescriptor icon) {
        this.icon = icon;
    }

    /**
     * 获取标志的文字描述
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * 设置标志的名字
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    public abstract int getIconResouseid();

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    /**
     * 添加对于Role的监听器
     *
     * @param onSignListener
     */
    public void addOnSignListener(OnSignListener onSignListener) {
        onSignListeners.add(onSignListener);
    }

    /**
     * 移除对于Role的所有监听
     */
    public void removeAllOnSignListeners() {
        onSignListeners.clear();
    }

    /**
     * 移除对于Role的某个监听
     *
     * @param onSignListener 要移除的监听器对象
     */
    public void removeOnSignListener(OnSignListener onSignListener) {
        onSignListeners.remove(onSignListener);
    }


    /**
     * 通知所有的监听器位置改变了
     */
    private void notifyOnSignMoveListeners() {
        for (OnSignListener listener : onSignListeners)
            listener.onMove(this, latLng);
    }

}
