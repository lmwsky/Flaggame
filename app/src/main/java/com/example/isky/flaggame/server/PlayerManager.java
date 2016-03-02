package com.example.isky.flaggame.server;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

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

    private PlayerManager() {
    }

    public static PlayerManager getInstance() {
        if (playerManager == null)
            playerManager = new PlayerManager();
        return playerManager;
    }

    /**
     * 用玩家当前位置在服务器端创建一个新的玩家对象，通过回调函数返回
     *
     * @param playername
     * @param onPlayerManagelistener
     */
    public void createMainplayer(String playername, final OnPlayerManagelistener onPlayerManagelistener) {
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

                        onPlayerManagelistener.OnCreateOrGetPlayerSuccess(player);


                    }

                    @Override
                    public void fail(String info) {
                        onPlayerManagelistener.OnCreatePlayerFail(info);
                    }
                });

            }
        };
        LocationServiceManager.getInstance().activate(initlocationreceivelistener);
    }

    /**
     * 获取之前创建的玩家，若没有则以playername创建一个
     *
     * @param playname
     * @param onPlayerManagelistener
     */
    public void getMainplayer(final String playname, final OnPlayerManagelistener onPlayerManagelistener) {
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
                        onPlayerManagelistener.OnCreateOrGetPlayerSuccess(player);
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
                    onPlayerManagelistener.OnCreatePlayerFail(info);
                }
            });
        }

    }

    public interface OnPlayerManagelistener {
        void OnCreateOrGetPlayerSuccess(Player player);

        void OnCreatePlayerFail(String info);
    }
}
