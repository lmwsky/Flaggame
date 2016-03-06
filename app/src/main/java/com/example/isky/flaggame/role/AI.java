package com.example.isky.flaggame.role;

import com.amap.api.maps2d.model.LatLng;

/**
 * Created by isky on 2016/2/2.
 * AI接口
 */
public interface AI {
    /**
     * 获得下一次的位置，可以通过记录每次调用的时间，利用调用的时间差乘以速度来计算最多能走多少距离
     * @return 下一个位置的坐标
     */
    LatLng getNextLocation();

    /**
     * 准备开始工作
     */
    void startwork();

    /**
     * 暂停工作
     */
    void stopwork();
}
