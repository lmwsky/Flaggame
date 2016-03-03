package com.example.isky.flaggame.server;

import android.util.Log;

import com.amap.api.maps2d.model.LatLng;

import java.util.ArrayList;

/**
 * Created by isky on 2016/2/20.
 * 房间管理，能够查询房间，删除房间，创建房间
 */
public class RoomManage {
    private static RoomManage roomManage;

    private RoomManage() {

    }

    public static RoomManage getInstance() {
        if (roomManage == null)
            roomManage = new RoomManage();
        return roomManage;
    }

    public void SearchRoom(LatLng latLng, int radius, BindwithServer.OndatasearchListener ondatasearchListener) {
        BindwithServer.getInstance().getData(BindwithServer.TABLEID_ROOM, latLng, radius, ondatasearchListener);
    }

    /**
     * 建立一个房间
     *
     * @param roomname 房间名
     * @return
     */
    public void createRoom(String roomname, Player player, final OnRoomListener onRoomListener) {

        if (player.get_id() == null) {
            try {
                throw new Exception("player is not exist in the server");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

        }
        /*建立房间对象*/
        final Room room = new Room();
        room.setLatLng(player.getLatLng());
        room.setOwner_name(player.getPlayername());
        room.setPlayersnum(1);
        room.setNeedplayernum(4);
        room.setRoomname(roomname);
        ArrayList<String> arrayList = new ArrayList<>();
        room.setOtherplayersid(arrayList);
        createRoom(room, onRoomListener);
    }

    private void createRoom(final Room room, final OnRoomListener onRoomListener) {
        //绑定房间对象到表room
        BindwithServer.getInstance().bindTablebyName(room, "room");
        //上传房间对象
        BindwithServer.getInstance().createData(room, new BindwithServer.OnCreateDataListener() {
            @Override
            public void success(String _id) {
                onRoomListener.onCreateRoomSuccess(room);
            }

            @Override
            public void fail(String info) {
                onRoomListener.onCreateRoomFail();
            }
        });
    }


    public void deleteRoom(Room room) {
        BindwithServer.getInstance().deleteData(room, new BindwithServer.OnDeleteDataListener() {
            @Override
            public void success(String info) {
                Log.d("hhh", "delete room sucess");
            }

            @Override
            public void fail(String info) {
                Log.d("hhh", "delete room fail");
            }
        });
    }

    /**
     * 获取某个房间里的所有玩家的对象
     *
     * @param room                 房间对象
     * @param ondatasearchListener
     */
    public void getPlayersByRoom(Room room, BindwithServer.OndatasearchListener ondatasearchListener) {
        String roomid = BindwithServer.getInstance().get_id(room);
        if (roomid == null)
            ondatasearchListener.fail("room is not exist");
        else
            BindwithServer.getInstance().getData(BindwithServer.TABLEID_PLAYER, "roomid", roomid, ondatasearchListener);
    }


    public interface OnRoomListener {
        void onCreateRoomSuccess(Room room);

        void onCreateRoomFail();

    }

}
