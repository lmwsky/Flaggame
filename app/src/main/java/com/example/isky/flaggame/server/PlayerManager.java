package com.example.isky.flaggame.server;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
    private Room currentroom;
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
     * 用玩家当前位置在服务器端创建一个新的玩家对象，通过回调函数返回
     *
     * @param playername
     * @param onPlayerManagelistener
     */
    public void createMainplayer(@NonNull String playername, @Nullable final OnPlayerManagelistener onPlayerManagelistener) {
        final Player player = new Player();
        player.setPlayername(playername);

        initlocationreceivelistener = new LocationSource.OnLocationChangedListener() {
            @Override
            public void onLocationChanged(Location location) {
                LocationServiceManager.getInstance().unregisterOnLocationchangelistener(initlocationreceivelistener);
                player.setLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                BindwithServer.getInstance().createData(player, new BindwithServer.OnCreateDataListener() {
                    @Override
                    public void success(String _id) {
                        Context context = GameApplication.getApplication();
                        SharedPreferences sp = context.getSharedPreferences("player", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("mainplayerid", _id);
                        editor.apply();
                        mainplayer = player;
                        if (onPlayerManagelistener != null) {
                            onPlayerManagelistener.OnCreateOrGetPlayerSuccess(player);
                        }


                    }

                    @Override
                    public void fail(String info) {
                        if (onPlayerManagelistener != null) {
                            onPlayerManagelistener.OnCreatePlayerFail(info);
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
     * @param playname               玩家名
     * @param onPlayerManagelistener
     */
    public void getMainplayer(@NonNull final String playname, @Nullable final OnPlayerManagelistener onPlayerManagelistener) {
        if (mainplayer != null) {
            if (onPlayerManagelistener != null) {
                mainplayer.setPlayername(playname);
                onPlayerManagelistener.OnCreateOrGetPlayerSuccess(mainplayer);
            }
        } else {
            Context cxt = GameApplication.getApplication();
            SharedPreferences sp = cxt.getSharedPreferences("player", Context.MODE_PRIVATE);
            String _id = sp.getString("mainplayerid", null);
            //之前没有创建过
            if (_id == null) {
                createMainplayer(playname, onPlayerManagelistener);
            } else {
                BindwithServer.getInstance().getData(BindwithServer.TABLEID_PLAYER, _id, new BindwithServer.OndatasearchListener() {
                    @Override
                    public void success(ArrayList<Object> datas) {

                    }

                    @Override
                    public void success(Object object) {
                        if (object instanceof Player) {
                            Player player = ((Player) object);
                            player.setPlayername(playname);
                            mainplayer = player;
                            if (onPlayerManagelistener != null) {
                                onPlayerManagelistener.OnCreateOrGetPlayerSuccess(player);
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
                        if (onPlayerManagelistener != null) {
                            onPlayerManagelistener.OnCreatePlayerFail(info);
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
    Room getCurrentroom() {
        if (mainplayer == null || currentroom == null)
            return null;
        else
            return currentroom;
    }

    /**
     * 主玩家离开房间
     *
     * @param onPlayerManagelistener 玩家的行为的监听器
     */
    public void leaveRoom(final OnPlayerManagelistener onPlayerManagelistener) {
        //主玩家存在并且在某个房间里
        if (mainplayer != null && currentroom != null) {
            reverseRoomPlayerState(currentroom, mainplayer, onPlayerManagelistener);
        } else {
            onPlayerManagelistener.onLeaveRoomFail();
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
     * @param onPlayerManagelistener
     */
    public void reverseRoomPlayerState(@NonNull final Room room, @NonNull final Player player, final OnPlayerManagelistener onPlayerManagelistener) {
        final boolean finalIsInRoom = reverseRoomPlayerState(room, player);
        BindwithServer.getInstance().updateData(room, new BindwithServer.OnUpdateDataListener() {
            @Override
            public void success(String _id) {
                BindwithServer.getInstance().updateData(BindwithServer.TABLEID_PLAYER, _id, "roomid", player.getRoomid(), new BindwithServer.OnUpdateDataListener() {

                    @Override
                    public void success(String _id) {
                        if (onPlayerManagelistener != null) {
                            if (finalIsInRoom == true)
                                onPlayerManagelistener.onLeaveRoomSuccess();
                            else
                                onPlayerManagelistener.OnEnterRoomSuccess(mainplayer);
                        }

                    }

                    @Override
                    public void fail(String info) {
                        reverseRoomPlayerState(room, player);
                        if (onPlayerManagelistener != null) {
                            if (finalIsInRoom == true)
                                onPlayerManagelistener.onLeaveRoomFail();
                            else
                                onPlayerManagelistener.OnEnterRoomSuccess(mainplayer);
                        }
                    }
                });

            }

            @Override
            public void fail(String info) {
                reverseRoomPlayerState(room, player);
                if (onPlayerManagelistener != null) {
                    if (finalIsInRoom == true) {
                        onPlayerManagelistener.onLeaveRoomFail();
                    } else {
                        onPlayerManagelistener.OnEnterRoomFail(info);
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
    private boolean reverseRoomPlayerState(@NonNull Room room, @NonNull Player player) {
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
     * 主玩家加入某个房间，如果房间满了或者主玩家不存在，失败，否则通过对应的回调函数返回
     *
     * @param room 要加入的房间
     */
    public void enterRoom(final Room room, final OnPlayerManagelistener onPlayerManagelistener) {
        if (mainplayer == null || mainplayer.get_id() == null) {
            try {
                throw new Exception("mainplayer is not exist");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //房间满人
            if (room.isfull() == true) {
                onPlayerManagelistener.OnEnterRoomFail("room is full");
                return;
            }
            String _id = mainplayer.get_id();
            if (room.isExist(_id) == true) {
                try {
                    throw new Exception("already in the room");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                onPlayerManagelistener.OnEnterRoomFail("already in room");
            } else {
                reverseRoomPlayerState(room, mainplayer, onPlayerManagelistener);
            }
        }
    }

    public interface OnPlayerManagelistener {
        void OnCreateOrGetPlayerSuccess(@NonNull Player player);

        void OnCreatePlayerFail(String info);

        void OnEnterRoomSuccess(@NonNull Player player);

        void OnEnterRoomFail(String info);

        void onLeaveRoomSuccess();

        void onLeaveRoomFail();

    }
}
