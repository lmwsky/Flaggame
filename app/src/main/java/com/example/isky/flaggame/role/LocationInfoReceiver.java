package com.example.isky.flaggame.role;

import com.amap.api.maps2d.model.LatLng;

/**
 * Created by isky on 2016/2/17.
 */
public interface LocationInfoReceiver {
    /**
     * 收到地点信息
     * @param latLng
     */
    void OnReceiveLocationInfo(LatLng latLng);
}
