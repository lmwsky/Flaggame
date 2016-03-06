package com.example.isky.flaggame.role;

import com.amap.api.maps2d.model.LatLng;

/**
 * Created by isky on 2016/2/1.
 * Sign,游戏中的某一角色，Marker，地图上的标记，这个接口需要实现marker对sign的监听，以实现marker与Sign的一致变化
 * 比如Sign的移动,图标的改变
 */
public interface OnSignListener {
    /**
     * 监听sign的位置的变化
     * @param sign 监听的sign对象
     * @param latLng 改变后的sign的坐标
     */
    void onMove(Sign sign,LatLng latLng);


}
