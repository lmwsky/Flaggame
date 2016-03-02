package com.example.isky.flaggame.role;

import com.amap.api.maps2d.model.LatLng;

import util.RandUtil;

/**
 * Created by isky on 2016/2/7.
 * 简单AI，只会随机移动
 */
public class SimpleAI  implements AI  {
    private double race=0.001;//移动速度 km/s
    private static long UNMOVE=-1;
    private LatLng nowLocation;
    private long lastMoveTime=UNMOVE;//上一次获取位置的时间，尚未获取则为UNMOVE
    public SimpleAI(LatLng latLng){
        nowLocation=new LatLng(latLng.latitude,latLng.longitude);
    }
    @Override
    public LatLng getNextLocation() {
        if(lastMoveTime==UNMOVE)
            lastMoveTime=System.currentTimeMillis();
        else{
            double detatime=(System.currentTimeMillis()-lastMoveTime)/1000;//转化间隔时间为以秒为单位
            double distance=detatime*race;//移动距离，单位km
            nowLocation= RandUtil.randPointerOnCircle(nowLocation, distance);
        }
        return nowLocation;
    }

    @Override
    public double getRace() {
        return race;
    }

    @Override
    public void startwork() {
        lastMoveTime=System.currentTimeMillis();
    }

    @Override
    public void stopwork() {
        lastMoveTime=UNMOVE;
    }

    /**
     * 设置速度
     * @param race 单位km/s
     */
    public void setRace(double race){
        this.race=race;

    }
}
