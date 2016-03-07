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
import com.example.isky.flaggame.role.RoleSign;
import com.example.isky.flaggame.role.SignFactory;
import com.example.isky.flaggame.role.SignManager;
import com.example.isky.flaggame.server.LocationServiceManager;
import com.example.isky.flaggame.server.Server;

import java.util.ArrayList;
import java.util.List;

import util.ToastUtil;

/**
 * Created by isky on 2016/1/25.
 * 单人游戏的管理，可以初始化游戏，开始游戏，暂停游戏，继续游戏，结束游戏
 */
public class SinglePlayerGame extends GameManager {
    private LocationSource.OnLocationChangedListener initlocationreceivelistener;

    /**
     * 用游戏进行的activity与map进行初始化
     *
     * @param activity 游戏进行的上下文
     * @param aMap     游戏进行的地图
     */
    public SinglePlayerGame(Activity activity, AMap aMap) {
        super(activity, aMap);
        /*初始化三类监听器*/
    }

    @Override
    public void InitGame() {
        if (gamestate != STATE_UNINT)
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
        RoleSign mainPlayer = new Miner(GameConfig.SINGLEGAME_MAINPLAYERTEAM);
        mainPlayer.setLatLng(startlatlng);
        mainPlayer.setIcon(GameConfig.mainplayerBitmapLive);
        GameHandler.doGameEventAndSendifNeed(GameEventFactory.produceAddMainPlayerRoleSign(mainPlayer));

        //生成旗帜
        final Flag flag = SignFactory.produceFlag(startlatlng, GameConfig.dist_flag, GameConfig.SINGLEGAME_MONSTERTEAM);
        GameHandler.doGameEventAndSendifNeed(GameEventFactory.produceAddFixedSignEvent(flag));

        Server.getInstance().queryWalkPath(startlatlng, flag.getLatLng(), new Server.OndatasearchListener() {
            @Override
            public void success(ArrayList<Object> datas) {

            }

            @Override
            public void success(Object object) {
                if (object instanceof WalkPath) {
                    WalkPath walkPath = (WalkPath) object;
                    initMonster(walkPath, flag.getLatLng());
                    initMonster(walkPath, startlatlng);
                    gamestate = STATE_INIT;
                    //完成初始化则开始游戏
                    GameHandler.doGameEventAndSendifNeed(GameEventFactory.produceStartGameEvent());
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
        SignManager.getInstance().setWalkPathList(walkPathList);
        //生成怪物
        ArrayList<Monster> monsterlist = SignFactory.produceMonster(GameConfig.num_monsters, centerpoint, GameConfig.dist_monster);
        for (Monster monster : monsterlist) {
            GameHandler.doGameEventAndSendifNeed(GameEventFactory.produceAddRoleSign(monster, null));
            GameHandler.doGameEventAndSendifNeed(GameEventFactory.produceBindWalkPathAI(monster.getSignature()));
        }
    }

    @Override
    public void StartGame() {
        if (gamestate != STATE_INIT && gamestate != STATE_STOP)
            return;
        gamestate = STATE_START;
        ToastUtil.show(activity, "startGame");
        for (Monster monster : SignManager.getInstance().getAllMonsters()) {
            monster.startmove();
        }
    }

    @Override
    public void StopGame() {
        if (gamestate != STATE_START)
            return;
        gamestate = STATE_STOP;
    }

    @Override
    public void ContinueGame() {
        if (gamestate != STATE_STOP)
            return;
        gamestate = STATE_START;

    }

    @Override
    public void EndGame() {
        gamestate = STATE_END;
        /*摧毁定位服务的实例已经所有监听*/
        LocationServiceManager.getInstance().destory();
//       SignManager.getInstance().initMap();//重新设置地图定位
        for (RoleSign monster : SignManager.getInstance().getAllMonsters()) {
            monster.stopmoving();
        }
        SignManager.getInstance().clear();
        activity.finish();
    }

}
