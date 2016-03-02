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

    /**
     * 将某个玩家加入某个房间
     *
     * @param room
     * @param player
     */
    public void enterRoom(final Room room, final Player player, final OnRoomListener onRoomListener) {
        //房间满人
        if (room.isfull() == true) {
            onRoomListener.onEnterRoomFail();
            return;
        }
        String _id = player.get_id();
        if (room.isExist(_id) == true)
            try {
                throw new Exception("already in the room");
            } catch (Exception e) {
                e.printStackTrace();
            }
        else {
            //尚未在服务器上创建player
            if (_id == null) {
                BindwithServer.getInstance().bindTablebyName(player, Player.class.getName());
                BindwithServer.getInstance().createData(player, new BindwithServer.OnCreateDataListener() {
                    @Override
                    public void success(String _id) {
                        reverseRoomPlayerState(room, player, onRoomListener);
                    }

                    @Override
                    public void fail(String info) {
                        onRoomListener.onEnterRoomFail();
                    }
                });
            } else {
                reverseRoomPlayerState(room, player, onRoomListener);
            }
        }
    }

    /**
     * 让某个玩家离开房间
     *
     * @param room
     * @param player
     * @param onRoomListener
     */
    public void leaveRoom(final Room room, final Player player, final OnRoomListener onRoomListener) {
        String _id = player.get_id();
        if (room.isExist(_id) == false)
            try {
                throw new Exception("already leave the room");
            } catch (Exception e) {
                e.printStackTrace();
            }
        else {
            reverseRoomPlayerState(room, player, onRoomListener);
        }
    }

    /**
     * 改变人与房间之间的关系，在房间变成不在房间，不在房间变成在房间
     *
     * @param room
     * @param player
     * @return 改变之前player是否在room里面，true 在，false 不在
     */
    private boolean reverseRoomPlayerState(Room room, Player player) {
        boolean isInRoom;
        if (room.isExist(player.get_id()) == true) {
            isInRoom = true;
            room.removePlayer(player.get_id());
            player.setRoomid(null);
        } else {
            isInRoom = false;
            room.addplayer(player.get_id());
            player.setRoomid(room.get_id());
        }
        return isInRoom;
    }

    /**
     * 将玩家与房间之间的关系反转，并且更新到服务器，然后调用监听器的对应的回调方法,若更新失败，则人和房间关系不变
     *
     * @param room
     * @param player
     * @param onRoomListener
     */
    private void reverseRoomPlayerState(final Room room, final Player player, final OnRoomListener onRoomListener) {
        final boolean finalIsInRoom = reverseRoomPlayerState(room, player);
        BindwithServer.getInstance().updateData(room, new BindwithServer.OnUpdateDataListener() {
            @Override
            public void success(String _id) {
                BindwithServer.getInstance().updateData(BindwithServer.TABLEID_PLAYER, _id, "roomid", player.getRoomid(), new BindwithServer.OnUpdateDataListener() {

                    @Override
                    public void success(String _id) {
                        if (onRoomListener != null) {
                            if (finalIsInRoom == true)
                                onRoomListener.onLeaveRoomSuccess();
                            else
                                onRoomListener.onEnterRoomSuccess();
                        }

                    }

                    @Override
                    public void fail(String info) {
                        reverseRoomPlayerState(room, player);
                        if (onRoomListener != null) {
                            if (finalIsInRoom == true)
                                onRoomListener.onLeaveRoomFail();
                            else
                                onRoomListener.onEnterRoomFail();
                        }
                    }
                });

            }

            @Override
            public void fail(String info) {
                reverseRoomPlayerState(room, player);
                if (onRoomListener != null) {
                    if (finalIsInRoom == true) {
                        onRoomListener.onLeaveRoomFail();
                    } else {
                        onRoomListener.onEnterRoomFail();
                    }
                }
            }
        });
    }


    public interface OnRoomListener {
        void onCreateRoomSuccess(Room room);

        void onCreateRoomFail();

        void onEnterRoomSuccess();

        void onEnterRoomFail();

        void onLeaveRoomSuccess();

        void onLeaveRoomFail();
    }

}
