package com.example.isky.flaggame.role;

import com.amap.api.maps2d.model.LatLng;
import com.example.isky.flaggame.server.BindwithServer;
import com.example.isky.flaggame.server.Player;

/**
 * Created by isky on 2016/2/17.
 * 地点信息的发送者，能够注册接收者
 */
public class LocationInfoSender {
    private LocationInfoReceiver locationInfoReceiver;

    /**
     * 注册接收者
     *
     * @param locationInfoReceiver
     */
    public void setLocationInfoReceiver(LocationInfoReceiver locationInfoReceiver) {
        this.locationInfoReceiver = locationInfoReceiver;
    }

    /**
     * 发送地点信息
     *
     * @param latLng
     */
    public void send(LatLng latLng) {
        if (locationInfoReceiver != null)
            locationInfoReceiver.OnReceiveLocationInfo(latLng);
    }


}
