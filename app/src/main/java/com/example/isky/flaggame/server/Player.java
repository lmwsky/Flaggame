package com.example.isky.flaggame.server;

import com.amap.api.maps2d.model.LatLng;

/**
 * Created by isky on 2016/2/29.
 * 玩家的信息类，玩家名，玩家当前位置
 */
public class Player implements _idQuery{
    private LatLng latLng;
    private String playername;
    private String roomid;

    public String get_id(){
        return BindwithServer.getInstance().get_id(this);
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getPlayername() {
        return playername;
    }

    public void setPlayername(String playername) {
        this.playername = playername;
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }


}
