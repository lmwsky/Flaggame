package com.example.isky.flaggame.game;

import android.app.Activity;
import android.location.Location;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkStep;
import com.example.isky.flaggame.role.Flag;
import com.example.isky.flaggame.role.Miner;
import com.example.isky.flaggame.role.Monster;
import com.example.isky.flaggame.role.OnRoleSignListener;
import com.example.isky.flaggame.role.RoleSign;
import com.example.isky.flaggame.role.SignFactory;
import com.example.isky.flaggame.role.SignMarkerManager;
import com.example.isky.flaggame.role.WalkPathAI;
import com.example.isky.flaggame.server.BindwithServer;
import com.example.isky.flaggame.server.LocationServiceManager;

import java.util.ArrayList;
import java.util.List;

import util.ToastUtil;

/**
 * Created by isky on 2016/1/25.
 * 单人游戏的管理，可以初始化游戏，开始游戏，暂停游戏，继续游戏，结束游戏
 */
public class SinglePlayerGame extends GameManager {
    private RoleSign mainPlayer;
    private LocationSource.OnLocationChangedListener initlocationreceivelistener;
    private OnRoleSignListener mainplayerlistener;

    /**
     * 用游戏进行的activity与map进行初始化
     *
     * @param activity 游戏进行的上下文
     * @param aMap     游戏进行的地图
     */
    public SinglePlayerGame(Activity activity, AMap aMap) {
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
                InitGame(new LatLng(location.getLatitude(), location.getLongitude()));
            }
        };
        LocationServiceManager.getInstance().registerOnLocationchangelistener(initlocationreceivelistener);
    }

    /**
     * 以某个坐标为起始点初始化游戏
     *
     * @param startlatlng 玩家起始位置
     */
    private void InitGame(final LatLng startlatlng) {
        /*生成各种sign及相应的marker，将其加入SignMarkerManager的管理*/
        mainPlayer = new Miner(1);
        mainPlayer.setLatLng(startlatlng);
        mainPlayer.setIcon(RoleSign.mainplayerBitmapLive);
        mainPlayer.addOnSignListener(mainplayerlistener);

        SignMarkerManager.getInstance().addMainPlayerToMap(mainPlayer);

        //添加为定位的位置接收者
        LocationServiceManager.getInstance().setLocationInfoReceiver(mainPlayer);

        //生成旗帜
        final Flag flag = SignFactory.produceFlag(mainPlayer.getLatLng(), GameConst.DIST_FLAG_CLOSED);
        flag.addOnSignListener(onFixedSignListener);
        SignMarkerManager.getInstance().addSignToMap(flag);


        BindwithServer.getInstance().queryWalkPath(startlatlng, flag.getLatLng(), new BindwithServer.OndatasearchListener() {
            @Override
            public void success(ArrayList<Object> datas) {

            }

            @Override
            public void success(Object object) {
                if (object instanceof WalkPath) {
                    WalkPath walkPath = (WalkPath) object;
                    initMonster(walkPath, flag.getLatLng());
                    initMonster(walkPath, startlatlng);
                    GAMESTATE = STATE_INIT;
                    //完成初始化则开始游戏
                    StartGame();
                } else {
                    try {
                        throw new Exception("query return is not Walkpath");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void fail(String info) {

            }
        });

    }

    /**
     * 根据路径和中心点初始化怪物
     *
     * @param walkPath
     * @param centerpoint
     */
    private void initMonster(WalkPath walkPath, LatLng centerpoint) {
        List<WalkStep> walkPathList = walkPath.getSteps();
        //生成怪物
        ArrayList<Monster> monsterlist = SignFactory.produceMonster(GameConst.NUM_MONSTER_SMALL, centerpoint, GameConst.DIST_MONSTER_CLOSED);
        for (Monster monster : monsterlist) {
            monster.setAi(new WalkPathAI(monster.getLatLng(), walkPathList));//设置怪物AI为简单AI
            monster.addOnSignListener(onOtherRolesignlistener);
            SignMarkerManager.getInstance().addSignToMap(monster);
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

        for (RoleSign monster : SignMarkerManager.getInstance().getAllMonsters()) {
            monster.stopmoving();
        }
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

}
