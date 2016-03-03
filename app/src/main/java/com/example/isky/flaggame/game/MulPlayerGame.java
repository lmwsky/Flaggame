package com.example.isky.flaggame.game;

import android.app.Activity;
import android.location.Location;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.LocationSource;
import com.example.isky.flaggame.InitGameEvent;
import com.example.isky.flaggame.role.Flag;
import com.example.isky.flaggame.role.Monster;
import com.example.isky.flaggame.role.OnRoleSignListener;
import com.example.isky.flaggame.role.RoleSign;
import com.example.isky.flaggame.role.SignMarkerManager;
import com.example.isky.flaggame.server.LocationServiceManager;

import java.util.ArrayList;

import util.ToastUtil;

/**
 * Created by isky on 2016/1/25.
 * 多人游戏的管理，可以初始化游戏，开始游戏，暂停游戏，继续游戏，结束游戏
 */
public class MulPlayerGame extends GameManager {
    private RoleSign mainPlayer;
    private String mainplayer_id;//主玩家的在服务器端的_id，可以借此查询到主玩家的位置

    private LocationSource.OnLocationChangedListener initlocationreceivelistener;
    private OnRoleSignListener mainplayerlistener;


    /**
     * 用游戏进行的activity与map进行初始化
     *
     * @param activity 游戏进行的上下文
     * @param aMap     游戏进行的地图
     */
    public MulPlayerGame(Activity activity, AMap aMap) {
        super(activity, aMap);
        /*初始化三类监听器*/
        mainplayerlistener = new SinglePlayerMainPlayerlistener();
    }

    @Override
    public void InitGame() {
        if (GAMESTATE != STATE_UNINT)
            return;
        ToastUtil.show(activity, "initgame");

        initlocationreceivelistener = new LocationSource.OnLocationChangedListener() {
            @Override
            public void onLocationChanged(Location location) {
                LocationServiceManager.getInstance().unregisterOnLocationchangelistener(initlocationreceivelistener);
                // InitGame(new LatLng(location.getLatitude(), location.getLongitude()));
            }
        };
        LocationServiceManager.getInstance().registerOnLocationchangelistener(initlocationreceivelistener);
    }

    /**
     * 通过初始化事件进行初始化，添加各种标志，为各种标志添加监听器
     *
     * @param initGameEvent 一个初始化的信息对象，初始游戏的各种信息,
     */
    private void InitGame(InitGameEvent initGameEvent) {

        ArrayList<Flag> flags = initGameEvent.getFlags();
        ArrayList<RoleSign> roleSigns = initGameEvent.getRoleSigns();

        for (Flag flag : flags) {
            flag.addOnSignListener(onFixedSignListener);
            SignMarkerManager.getInstance().addSignToMap(flag);
        }
        for (RoleSign roleSign : roleSigns) {
            //主玩家
            if (roleSign.get_id().equals(mainplayer_id)) {
                roleSign.addOnSignListener(mainplayerlistener);
                SignMarkerManager.getInstance().addMainPlayerToMap(roleSign);
            }
            //其他标志
            else {
                roleSign.addOnSignListener(onOtherRolesignlistener);
                SignMarkerManager.getInstance().addSignToMap(roleSign);
            }
        }
    }


    @Override
    public void StartGame() {
        if (GAMESTATE != STATE_INIT && GAMESTATE != STATE_STOP)
            return;
        GAMESTATE = STATE_START;
        ToastUtil.show(activity, "startGame");
        for (Monster monster : SignMarkerManager.getInstance().getAllMonsters()) {
            monster.startmove();
        }
    }

    @Override
    public void StopGame() {
        if (GAMESTATE != STATE_START)
            return;
        GAMESTATE = STATE_STOP;
        ToastUtil.show(activity, "stopGame");
    }

    @Override
    public void ContinueGame() {
        if (GAMESTATE != STATE_STOP)
            return;
        GAMESTATE = STATE_START;

        ToastUtil.show(activity, "continueGame");
    }

    @Override
    public void EndGame() {
        //if(GAMESTATE!=STATE_START)
        //    return;
        if (GAMESTATE == STATE_UNINT)
            return;
        GAMESTATE = STATE_UNINT;
        ToastUtil.show(activity, "endGame");
        /*摧毁定位服务的实例已经所有监听*/
        LocationServiceManager.getInstance().destory();

        SignMarkerManager.getInstance().initMap();//重新设置地图定位
        mainPlayer = null;
        SignMarkerManager.getInstance().clear();
    }

    /**
     * 获取游戏目前的状态
     *
     * @return STATE_UNINT=0;尚未初始化的状态，上一盘游戏结束也会进入这个状态
     * STATE_INIT=1;已经初始化但未开始游戏
     * STATE_START=2;游戏进行中
     * STATE_STOP=3;游戏暂停
     */
    public int getGAMESTATE() {
        return GAMESTATE;
    }

    public void InitGameByOwner() {

    }

    public void InitGameByOthers() {

    }
}
