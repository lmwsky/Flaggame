package com.example.isky.flaggame.server;

import com.amap.api.maps2d.model.LatLng;

import java.util.ArrayList;

/**
 * Created by isky on 2016/2/20.
 * 房间管理，能够查询房间，删除房间，创建房间
 */
public class RoomManage {
    private static RoomManage roomManage;
    private ArrayList<Room> roomlist;
    private Room createRoom;

    private RoomManage() {

    }

    public static RoomManage getInstance() {
        if (roomManage == null)
            roomManage = new RoomManage();
        return roomManage;
    }

    public void SearchRoom(LatLng latLng, int radius, Server.OndatasearchListener ondatasearchListener) {

        Server.getInstance().getData(Server.TABLEID_ROOM, latLng, radius, ondatasearchListener);
    }


    public void createRoom(final Room room, final OnRoomListener onRoomListener) {

        //上传房间对象
        Server.getInstance().createData(room, new Server.OnCreateDataListener() {
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


    public void deleteRoom(Room room, Server.OnDeleteDataListener onDeleteDataListener) {
        Server.getInstance().deleteData(room, onDeleteDataListener);
    }

    public ArrayList<Room> getRoomlist() {
        return roomlist;
    }

    public void setRoomlist(ArrayList<Room> roomlist) {
        this.roomlist = roomlist;
    }

    public Room getCreateRoom() {
        return createRoom;
    }

    public void setCreateRoom(Room createRoom) {
        this.createRoom = createRoom;
    }


    public interface OnRoomListener {
        void onCreateRoomSuccess(Room room);

        void onCreateRoomFail();

    }

    /**
     * Created by isky on 2016/2/22.
     */
    public static class Room implements _idQuery {
        public static final int PREPARE = 0;
        public static final int START = 1;
        public static final int ABANDON = 2;

        private int state = PREPARE;
        private LatLng latLng;//房间建立的经纬度
        private String roomname;//房间名
        private int playersnum = 1;//玩家数目
        private int needplayernum = 4;//需要玩家数目
        private String owner_name;//房主的名字
        private String owner_id;//房主的名字
        private ArrayList<String> otherplayersid;//其他玩家的_id

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public boolean isStarting() {
            if (state == START)
                return true;
            else return false;
        }

        public boolean isAbandon() {
            if (state == ABANDON)
                return false;
            else return true;
        }

        /**
         * 房间状态设置为准备
         */
        public void init() {
            state = PREPARE;
        }

        /**
         * 废弃当前房间,并且上传到服务器
         */
        public void abandon(Server.OnUpdateDataListener onUpdateDataListener) {
            state = ABANDON;
            Server.getInstance().updateData(Server.TABLEID_ROOM, get_id(),
                    "state", state + "", onUpdateDataListener);
        }

        /**
         * 开始当前房间,并且上传到服务器
         */
        public void startgame(Server.OnUpdateDataListener onUpdateDataListener) {
            state = START;
            Server.getInstance().updateData(Server.TABLEID_ROOM, get_id(),
                    "state", state + "", onUpdateDataListener);
        }


        public int getPlayersnum() {
            return otherplayersid.size() + 1;
        }

        public void setPlayersnum(int playersnum) {
            this.playersnum = playersnum;
        }

        public String getOwner_id() {
            return owner_id;
        }

        public void setOwner_id(String owner_id) {
            this.owner_id = owner_id;
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
            return Server.getInstance().get_id(this);
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

        /**
         * 获取某个房间里的所有玩家的对象
         *
         * @param ondatasearchListener
         */
        public void getPlayersByRoom(Server.OndatasearchListener ondatasearchListener) {
            String roomid = Server.getInstance().get_id(this);
            if (roomid == null)
                ondatasearchListener.fail("room is not exist");
            else
                Server.getInstance().getData(Server.TABLEID_PLAYER, "roomid", roomid, ondatasearchListener);
        }
    }
}
