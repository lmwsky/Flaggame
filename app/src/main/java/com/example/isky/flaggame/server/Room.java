package com.example.isky.flaggame.server;

import com.amap.api.maps2d.model.LatLng;

import java.util.ArrayList;

/**
 * Created by isky on 2016/2/22.
 */
public class Room implements _idQuery {
    private static final int PREPARE = 0;
    private static final int START = 1;

    private int state = PREPARE;
    private LatLng latLng;//房间建立的经纬度
    private String roomname;//房间名
    private int playersnum = 1;//玩家数目
    private int needplayernum = 4;//需要玩家数目
    private String owner_name;//房主的名字
    private String owner_id;//房主的名字
    private ArrayList<String> otherplayersid;//其他玩家的_id

    public void setPlayersnum(int playersnum) {
        this.playersnum = playersnum;
    }

    public int getNeedplayernum() {
        return needplayernum;
    }

    public void setNeedplayernum(int needplayernum) {
        this.needplayernum = needplayernum;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getRoomname() {
        return roomname;
    }

    public void setRoomname(String roomname) {
        this.roomname = roomname;
    }


    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public ArrayList<String> getOtherplayersid() {
        return otherplayersid;
    }

    public void setOtherplayersid(ArrayList<String> otherplayersid) {
        this.otherplayersid = otherplayersid;
    }


    public ArrayList<String> getAllplayers() {
        ArrayList<String> allplayers = new ArrayList<>(otherplayersid);
        allplayers.add(owner_id);
        return allplayers;
    }

    @Override
    public String get_id() {
        return BindwithServer.getInstance().get_id(this);
    }

    /**
     * 某个玩家是否存在
     * @param _id
     * @return
     */
    public boolean isExist(String _id) {
        if (owner_id.equals(_id))
            return true;
        for (String playerid : otherplayersid)
            if (playerid.equals(_id))
                return true;
        return false;
    }

    public void addplayer(String id) {
        otherplayersid.add(id);
    }

    public void removePlayer(String id) {
        for(int i=0;i<otherplayersid.size();i++){
            if(otherplayersid.get(i).equals(id)) {
                otherplayersid.remove(i);
                return;
            }
        }
    }

    public boolean isfull() {
        if(needplayernum==otherplayersid.size()+1)
        return true;
        else
            return false;
    }
}
