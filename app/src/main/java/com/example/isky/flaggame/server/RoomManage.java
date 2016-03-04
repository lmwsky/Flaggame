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
    public void createRoom(String roomname, PlayerManager.Player player, final OnRoomListener onRoomListener) {

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

    /**
     * Created by isky on 2016/2/22.
     */
    public static class Room implements _idQuery {
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
         *
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
            for (int i = 0; i < otherplayersid.size(); i++) {
                if (otherplayersid.get(i).equals(id)) {
                    otherplayersid.remove(i);
                    return;
                }
            }
        }

        public boolean isfull() {
            if (needplayernum == otherplayersid.size() + 1)
                return true;
            else
                return false;
        }
    }
}
