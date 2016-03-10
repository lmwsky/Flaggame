package com.example.isky.flaggame.server;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.model.LatLng;

import java.util.ArrayList;

import util.GameApplication;

/**
 * Created by isky on 2016/3/2.
 * 玩家管理类，建立玩家并且获得
 */
public class PlayerManager {
    private static PlayerManager playerManager;
    LocationSource.OnLocationChangedListener initlocationreceivelistener;
    private RoomManage.Room currentRoom;
    private Player mainplayer;

    private PlayerManager() {
    }

    public static PlayerManager getInstance() {
        if (playerManager == null)
            playerManager = new PlayerManager();
        return playerManager;
    }

    public Player getMainplayer() {
        return mainplayer;
    }

    /**
     * 将一个玩家对象设置为主玩家
     *
     * @param mainplayer 玩家对象，必须是已经在服务器端已经注册的
     */
    public void setMainplayer(Player mainplayer) {
        if (mainplayer.get_id() == null)
            try {
                throw new Exception("mainplayer is not exist in the server yet");
            } catch (Exception e) {
                e.printStackTrace();
            }
        else
            this.mainplayer = mainplayer;
    }

    /**
     * 是否存在已经注册的玩家id
     *
     * @return
     */
    public boolean isPlayerRegister() {
        String _id = getRegisterPlayerid();
        if (_id == null)
            return false;
        return true;
    }

    public
    @Nullable
    String getRegisterPlayerid() {
        Context cxt = GameApplication.getApplication();
        SharedPreferences sp = cxt.getSharedPreferences("player", Context.MODE_PRIVATE);
        String _id = sp.getString("mainplayerid", null);
        return _id;
    }

    /**
     * 用玩家当前位置在服务器端创建一个新的玩家对象，通过回调函数返回
     *
     * @param playername
     * @param onCreateOrGetPlayerListener
     */
    public void createMainplayer(@NonNull String playername, @Nullable final OnCreateOrGetPlayerListener onCreateOrGetPlayerListener) {
        final Player player = new Player();
        player.setPlayername(playername);

        initlocationreceivelistener = new LocationSource.OnLocationChangedListener() {
            @Override
            public void onLocationChanged(Location location) {
                LocationServiceManager.getInstance().unregisterOnLocationchangelistener(initlocationreceivelistener);
                player.setLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                Server.getInstance().createData(player, new Server.OnCreateDataListener() {
                    @Override
                    public void success(String _id) {
                        Log.i("hh", "create player success" + _id);
                        Context context = GameApplication.getApplication();
                        SharedPreferences sp = context.getSharedPreferences("player", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("mainplayerid", _id);
                        editor.apply();
                        mainplayer = player;
                        if (onCreateOrGetPlayerListener != null) {
                            onCreateOrGetPlayerListener.OnCreateOrGetPlayerSuccess(player);
                        }
                    }

                    @Override
                    public void fail(String info) {
                        if (onCreateOrGetPlayerListener != null) {
                            onCreateOrGetPlayerListener.OnCreatePlayerFail(info);
                        }
                    }
                });

            }
        };
        LocationServiceManager.getInstance().activate(initlocationreceivelistener);
    }

    /**
     * 获取之前创建的玩家，若没有则以playername创建一个
     *
     * @param onCreateOrGetPlayerListener 监听器
     */
    public void getMainplayer(@Nullable final OnCreateOrGetPlayerListener onCreateOrGetPlayerListener) {
        if (mainplayer != null) {
            if (onCreateOrGetPlayerListener != null) {
                onCreateOrGetPlayerListener.OnCreateOrGetPlayerSuccess(mainplayer);
            }
        } else {
            final String _id = getRegisterPlayerid();
            //之前没有创建过
            if (_id == null) {
                final String playname = "无名";
                createMainplayer(playname, onCreateOrGetPlayerListener);
            } else {
                Server.getInstance().getData(Server.TABLEID_PLAYER, _id, new Server.OndatasearchListener() {
                    @Override
                    public void success(ArrayList<Object> datas) {

                    }

                    @Override
                    public void success(Object object) {
                        if (object instanceof Player) {
                            Player player = ((Player) object);
                            mainplayer = player;
                            if (onCreateOrGetPlayerListener != null) {
                                onCreateOrGetPlayerListener.OnCreateOrGetPlayerSuccess(player);
                            }
                        } else {
                            try {
                                throw new Exception("search result is not player");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void fail(String info) {
                        if (onCreateOrGetPlayerListener != null) {
                            onCreateOrGetPlayerListener.OnCreatePlayerFail(info);
                        }
                    }
                });
            }
        }
    }

    /**
     * @return
     */
    public
    @Nullable
    RoomManage.Room getCurrentRoom() {
        if (mainplayer == null || currentRoom == null)
            return null;
        else
            return currentRoom;
    }

    public void setCurrentRoom(RoomManage.Room currentRoom) {
        this.currentRoom = currentRoom;
        if (currentRoom != null)
            mainplayer.setRoomid(currentRoom.get_id());
        else
            mainplayer.setRoomid(null);
    }

    /**
     * 主玩家离开房间
     *
     * @param onEnterOrLeaveRoomListener 玩家的行为的监听器
     */
    public void leaveRoom(final OnEnterOrLeaveRoomListener onEnterOrLeaveRoomListener) {
        //主玩家存在并且在某个房间里
        if (mainplayer != null && currentRoom != null) {
            reverseRoomPlayerState(currentRoom, mainplayer, onEnterOrLeaveRoomListener);
        } else {
            onEnterOrLeaveRoomListener.onLeaveRoomFail();
            try {
                throw new Exception("already leave the room or mainplayer is not exist");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将玩家与房间之间的关系反转，并且更新到服务器，然后调用监听器的对应的回调方法,若更新失败，则人和房间关系不变
     *
     * @param room
     * @param player
     * @param onEnterOrLeaveRoomListener
     */
    public void reverseRoomPlayerState(@NonNull final RoomManage.Room room, @NonNull final Player player, final OnEnterOrLeaveRoomListener onEnterOrLeaveRoomListener) {
        final boolean finalIsInRoom = reverseRoomPlayerState(room, player);
        Server.getInstance().updateData(Server.TABLEID_ROOM, room.get_id(), room, new Server.OnUpdateDataListener() {
            @Override
            public void success(String _id) {
                Server.getInstance().updateData(Server.TABLEID_PLAYER, player.get_id(), "roomid", player.getRoomid(), new Server.OnUpdateDataListener() {

                    @Override
                    public void success(String _id) {
                        if (onEnterOrLeaveRoomListener != null) {
                            if (finalIsInRoom == true)
                                onEnterOrLeaveRoomListener.onLeaveRoomSuccess();
                            else
                                onEnterOrLeaveRoomListener.OnEnterRoomSuccess(mainplayer);
                        }
                    }

                    @Override
                    public void fail(String info) {
                        reverseRoomPlayerState(room, player);
                        if (onEnterOrLeaveRoomListener != null) {
                            if (finalIsInRoom == true)
                                onEnterOrLeaveRoomListener.onLeaveRoomFail();
                            else
                                onEnterOrLeaveRoomListener.OnEnterRoomSuccess(mainplayer);
                        }
                    }
                });

            }

            @Override
            public void fail(String info) {
                reverseRoomPlayerState(room, player);
                if (onEnterOrLeaveRoomListener != null) {
                    if (finalIsInRoom == true) {
                        onEnterOrLeaveRoomListener.onLeaveRoomFail();
                    } else {
                        onEnterOrLeaveRoomListener.OnEnterRoomFail(info);
                    }
                }
            }
        });
    }

    /**
     * 改变与房间之间的关系，在房间变成不在房间，不在房间变成在房间
     *
     * @param room
     * @param player
     * @return 改变之前player是否在room里面，true 在，false 不在
     */
    private boolean reverseRoomPlayerState(@NonNull RoomManage.Room room, @NonNull Player player) {
        boolean isInRoom;
        if (room.isExist(player.get_id())) {
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
     * 主玩家加入某个房间，如果房间满了或者主玩家不存在，失败，否则通过对应的回调函数返回
     *
     * @param room 要加入的房间
     */
    public void enterRoom(final RoomManage.Room room, @Nullable final OnEnterOrLeaveRoomListener onEnterOrLeaveRoomListener) {
        if (mainplayer == null || mainplayer.get_id() == null) {
            try {
                throw new Exception("mainplayer is not exist");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //房间满人
            if (room.isfull()) {
                if (onEnterOrLeaveRoomListener != null) {
                    onEnterOrLeaveRoomListener.OnEnterRoomFail("room is full");
                }
                return;
            }
            String _id = mainplayer.get_id();
            if (room.isExist(_id)) {
                try {
                    throw new Exception("already in the room");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (onEnterOrLeaveRoomListener != null) {
                    onEnterOrLeaveRoomListener.OnEnterRoomFail("already in room");
                }
            } else {
                reverseRoomPlayerState(room, mainplayer, onEnterOrLeaveRoomListener);
            }
        }
    }

    public interface OnEnterOrLeaveRoomListener {
        void OnEnterRoomSuccess(@NonNull Player player);

        void OnEnterRoomFail(String info);

        void onLeaveRoomSuccess();

        void onLeaveRoomFail();

    }

    public interface OnCreateOrGetPlayerListener {
        void OnCreateOrGetPlayerSuccess(@NonNull Player player);

        void OnCreatePlayerFail(String info);
    }

    /**
     * Created by isky on 2016/2/29.
     * 玩家的信息类，玩家名，玩家当前位置
     */
    public static class Player implements _idQuery {
        private LatLng latLng;
        private String playername;
        private String roomid;

        public String get_id() {
            return Server.getInstance().get_id(this);
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

        public boolean isMainPlayer() {
            if (PlayerManager.getInstance().getMainplayer() == this)
                return true;
            else return false;
        }

        /**
         * 将主玩家角色的位置与player进行绑定，每当主玩家角色的位置改变，上传到服务器
         */
        public void bindMainplayerAsLocationSender() {
            LocationSource.OnLocationChangedListener mainplayerlocationsenderlistener = new LocationSource.OnLocationChangedListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (Math.abs(location.getLongitude() - latLng.longitude) < 0.0000000001 && Math.abs(location.getLatitude() - latLng.latitude) < 0.0000000001)
                        return;
                    else {
                        setLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                        Server.getInstance().sendPlayerLocation(Player.this);
                    }
                }
            };
            LocationServiceManager.getInstance().activate(mainplayerlocationsenderlistener);
        }
    }
}
