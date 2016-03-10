package com.example.isky.flaggame.game;

import android.app.Activity;
import android.util.Log;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.model.LatLng;
import com.example.isky.flaggame.role.Flag;
import com.example.isky.flaggame.role.RoleSign;
import com.example.isky.flaggame.role.SignFactory;
import com.example.isky.flaggame.role.SignManager;
import com.example.isky.flaggame.server.LocationServiceManager;
import com.example.isky.flaggame.server.PlayerManager;
import com.example.isky.flaggame.server.RoomManage;
import com.example.isky.flaggame.server.Server;

import java.util.ArrayList;

import util.MapUtil;
import util.RandUtil;
import util.ToastUtil;

/**
 * Created by isky on 2016/1/25.
 * 多人游戏的管理，可以初始化游戏，开始游戏，暂停游戏，继续游戏，结束游戏
 */

public class MultiPlayerGame extends GameManager {

    /**
     * 用游戏进行的activity与map进行初始化
     *
     * @param activity 游戏进行的上下文
     * @param aMap     游戏进行的地图
     */
    public MultiPlayerGame(Activity activity, AMap aMap) {
        super(activity, aMap);
    }

    @Override
    public void InitGame() {
        if (gamestate != STATE_UNINT)
            return;
        ToastUtil.showshortToast(activity, "initgame");
        PlayerManager.Player player = PlayerManager.getInstance().getMainplayer();
        RoomManage.Room room = PlayerManager.getInstance().getCurrentRoom();

        if (player == null || room == null) {
            try {
                throw new Exception("can not find mainplayer or room");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (room.getOwner_id().equals(player.get_id()))
                InitGameByOwner();
            else
                InitGameByOthers();
            gamestate = STATE_INIT;
        }
    }

    @Override
    public void StartGame() {
        if (gamestate != STATE_INIT && gamestate != STATE_STOP)
            return;
        gamestate = STATE_START;
        ToastUtil.showshortToast(activity, "startGame");
    }

    @Override
    public void StopGame() {
        if (gamestate != STATE_START)
            return;
        gamestate = STATE_STOP;
        ToastUtil.showshortToast(activity, "stopGame");
    }

    @Override
    public void ContinueGame() {
        if (gamestate != STATE_STOP)
            return;
        gamestate = STATE_START;
        ToastUtil.showshortToast(activity, "continueGame");
    }

    @Override
    public void EndGame() {
        if (gamestate == STATE_END)
            return;
        gamestate = STATE_END;
        ToastUtil.showshortToast(activity, "endGame");
        /*摧毁定位服务的实例已经所有监听*/
        LocationServiceManager.getInstance().destory();
        Server.getInstance().stopReceiveGameEvent();
        Server.getInstance().stopReceivePlayerLocation();
        SignManager.getInstance().clear();
    }

    /**
     * 以房主的方式来进行初始化游戏
     */
    public void InitGameByOwner() {
        final RoomManage.Room room = PlayerManager.getInstance().getCurrentRoom();

        if (room != null) {
            room.getPlayersInCurrentRoom(new Server.OndatasearchListener() {
                @Override
                public void success(ArrayList<Object> datas) {
                    /*获取了在房间里的所有玩家*/
                    ArrayList<PlayerManager.Player> allplayerlist = new ArrayList<PlayerManager.Player>();
                    PlayerManager.Player mainplayer = PlayerManager.getInstance().getMainplayer();
                    allplayerlist.add(mainplayer);

                    for (Object o : datas) {
                        PlayerManager.Player player = (PlayerManager.Player) o;
                        if (!player.get_id().equals(room.getOwner_id()))
                            allplayerlist.add(player);
                    }
                    SignManager.getInstance().setAllplayer(allplayerlist);

                    ArrayList<RoleSign> roleSigns = getInitRoleSign(allplayerlist);
                    RoleSign mainplayerrolesign = roleSigns.get(0);
                    GameHandler.doGameEventAndSendifNeed(GameEventFactory.produceAddRoleSign(
                            mainplayerrolesign, allplayerlist.get(0).get_id()));

                    ArrayList<Flag> flags = getInitFlag(allplayerlist);

                    //所有其他角色加入地图并且开始接受位置
                    for (int i = 1; i < allplayerlist.size(); i++) {
                        RoleSign roleSign = roleSigns.get(i);
                        PlayerManager.Player player = allplayerlist.get(i);
                        GameHandler.doGameEventAndSendifNeed(GameEventFactory.produceAddRoleSign(roleSign, player.get_id()));
                    }
                    for (Flag flag : flags)
                        GameHandler.doGameEventAndSendifNeed(GameEventFactory.produceAddFixedSignEvent(flag));
                    GameHandler.doGameEventAndSendifNeed(GameEventFactory.produceStartGameEvent());

                    Server.getInstance().startReceiveGameEvent();

                }

                @Override
                public void success(Object object) {

                }

                @Override
                public void fail(String info) {

                }
            });
        }

    }

    /**
     * 根据所有玩家对象产生对应的游戏角色
     *
     * @param playerArrayList
     * @return
     */
    private ArrayList<RoleSign> getInitRoleSign(ArrayList<PlayerManager.Player> playerArrayList) {
        ArrayList<RoleSign> roleSigns = new ArrayList<>();

        int num = playerArrayList.size();
        int oneteamnum = num / 2;

        for (int i = 0; i < num; i++) {
            int team = i / oneteamnum;
            RoleSign roleSign = null;
            PlayerManager.Player player = playerArrayList.get(i);
            if (i % 3 == 0) {
                roleSign = SignFactory.produceMiner(player.getLatLng(), team);
            }
            if (i % 3 == 1) {
                roleSign = SignFactory.produceSapper(player.getLatLng(), team);
            }
            if (i % 3 == 2) {
                roleSign = SignFactory.produceScout(player.getLatLng(), team);
            }
            roleSigns.add(roleSign);
        }

        return roleSigns;
    }

    /**
     * 根据玩家对象产生旗子
     *
     * @param playerArrayList
     * @return
     */
    private ArrayList<Flag> getInitFlag(ArrayList<PlayerManager.Player> playerArrayList) {
        ArrayList<Flag> flagArrayList = new ArrayList<>();
        int num = playerArrayList.size();
        int oneteamnum = num / 2;

        LatLng startlatLng = playerArrayList.get(0).getLatLng();
        LatLng endlatLng = playerArrayList.get(oneteamnum).getLatLng();
        double maxdist = 0.0;

        //寻找距离最远的两个不同队的人，然后在连线上初始化旗帜
        for (int i = 0; i < oneteamnum; i++) {
            LatLng s = playerArrayList.get(i).getLatLng();
            for (int j = oneteamnum; j < num; j++) {
                LatLng e = playerArrayList.get(j).getLatLng();
                double distance = MapUtil.getDistance(s, e);
                if (distance > maxdist) {
                    maxdist = distance;
                    startlatLng = s;
                    endlatLng = e;
                }

            }
        }


        Flag flag1 = SignFactory.produceFlag(RandUtil.moveToGoal(startlatLng, endlatLng, GameConfig.dist_flag * 1000), 0);
        Flag flag2 = SignFactory.produceFlag(RandUtil.moveToGoal(endlatLng, startlatLng, GameConfig.dist_flag * 1000), 1);

        flagArrayList.add(flag1);
        flagArrayList.add(flag2);

        return flagArrayList;
    }

    /**
     * 以房间的普通成员的方式来初始化游戏
     */
    public void InitGameByOthers() {
        Log.d("event", "init game by others");
        final RoomManage.Room room = PlayerManager.getInstance().getCurrentRoom();

        if (room != null) {
            room.getPlayersInCurrentRoom(new Server.OndatasearchListener() {
                @Override
                public void success(ArrayList<Object> datas) {
                    Log.d("event", "get data of all players");

                    /*获取了在房间里的所有玩家*/
                    ArrayList<PlayerManager.Player> allplayerlist = new ArrayList<>();
                    PlayerManager.Player mainplayer = PlayerManager.getInstance().getMainplayer();
                    allplayerlist.add(mainplayer);

                    for (Object o : datas) {
                        PlayerManager.Player player = (PlayerManager.Player) o;
                        if (player.get_id().equals(mainplayer.get_id()))
                            continue;

                        allplayerlist.add(player);

                    }
                    if (mainplayer != null)
                        Log.d("event", "mainpalyer=" + mainplayer.get_id());
                    else
                        Log.d("event", "mainpalyer=" + null);

                    SignManager.getInstance().setAllplayer(allplayerlist);
                    Server.getInstance().startReceiveGameEvent();
                }

                @Override
                public void success(Object object) {

                }

                @Override
                public void fail(String info) {

                }
            });
        }
    }
}
