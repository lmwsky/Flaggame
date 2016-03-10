package com.example.isky.flaggame.game;

import android.app.Activity;
import android.view.View;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.LatLng;
import com.example.isky.flaggame.role.Flag;
import com.example.isky.flaggame.role.Mine;
import com.example.isky.flaggame.role.Monster;
import com.example.isky.flaggame.role.OnFixedSignListener;
import com.example.isky.flaggame.role.OnRoleSignListener;
import com.example.isky.flaggame.role.RebirthPoint;
import com.example.isky.flaggame.role.RoleSign;
import com.example.isky.flaggame.role.Sapper;
import com.example.isky.flaggame.role.Sign;
import com.example.isky.flaggame.role.SignFactory;
import com.example.isky.flaggame.role.SignManager;

import java.util.ArrayList;

import util.GetScore;
import util.RandUtil;

/**
 * Created by isky on 2016/1/25.
 * 游戏管理者的接口
 */
public abstract class GameManager {
    public static final int STATE_UNINT = 0;//尚未初始化的状态，上一盘游戏结束也会进入这个状态
    public static final int STATE_INIT = 1;//已经初始化但未开始游戏
    public static final int STATE_START = 2;//游戏进行中
    public static final int STATE_STOP = 3;//游戏暂停
    public static final int STATE_END = 4;//游戏暂停

    public static OnFixedSignListener onFixedSignListener = new FixedSignListener();
    public static OnOtherRoleSignlistener onOtherRolesignlistener = new OnOtherRoleSignlistener();
    public static MainplayerListener onMainplayerListener = new MainplayerListener();
    protected int gamestate = STATE_UNINT;
    protected GameHandler handler;//其他线程消息处理器
    protected Activity activity;

    GameManager(Activity activity, AMap aMap) {
        this.activity = activity;
        this.handler = new GameHandler(this, activity);
        SignManager.getInstance().initMap(aMap);
    }

    /**
     * 对游戏进行初始化工作，包括地图初始化，玩家，标志物等等
     */
    public abstract void InitGame();

    /**
     * 开始游戏
     */
    public abstract void StartGame();

    /**
     * 暂停游戏
     */
    public abstract void StopGame();

    /**
     * 继续游戏
     */
    public abstract void ContinueGame();

    /**
     * 结束游戏，进行结算
     */
    public abstract void EndGame();

    /**
     * 获取游戏目前的状态
     *
     * @return STATE_UNINT=0;尚未初始化的状态，上一盘游戏结束也会进入这个状态
     * STATE_INIT=1;已经初始化但未开始游戏
     * STATE_START=2;游戏进行中
     * STATE_STOP=3;游戏暂停
     */
    public int getGamestate() {
        return gamestate;
    }

    /**
     * 点击监听器，点击就让当前玩家攻击能够攻击的人,必须是游戏已经开始了
     */
    public static class OnAttractBtClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (SignManager.isGameStarting()) {
                RoleSign mainplayer = SignManager.getInstance().getMainPlayer();
                if (mainplayer != null) {
                    ArrayList<RoleSign> otherteam = SignManager.getInstance().getOtherTeamRoleSign(mainplayer.getTeam());
                    mainplayer.attack(otherteam);
                }
            }
        }
    }

    /**
     * 占领按钮监听器，点击就尝试占领,必须是游戏已经开始了
     */
    public static class OnOccupyBtClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (SignManager.isGameStarting()) {
                RoleSign mainplayer = SignManager.getInstance().getMainPlayer();
                if (mainplayer != null) {
                    ArrayList<Flag> otherTeamFlag = SignManager.getInstance().getOtherTeamFlag(mainplayer.getTeam());
                    mainplayer.occupy(otherTeamFlag);
                }
            }
        }
    }

    public static class OnEndGameClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            GameHandler.doGameEventAndSendifNeed(GameEventFactory.produceEndGameEvent());
        }
    }

    /**
     * 使用技能按钮监听器,点击使用对应的技能,必须是游戏已经开始了
     */
    public static class OnSkillBtClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (SignManager.isGameStarting()) {
                RoleSign mainplayer = SignManager.getInstance().getMainPlayer();
                if (mainplayer != null)
                    mainplayer.skill();
            }
        }
    }

    /**
     * 使用技能按钮监听器,点击尝试重生,必须是游戏已经开始了
     */
    public static class OnRebirthBtClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (SignManager.isGameStarting()) {
                RoleSign mainplayer = SignManager.getInstance().getMainPlayer();
                if (mainplayer != null) {
                    ArrayList<RebirthPoint> rebirthPoints = SignManager.getInstance().getRebirthPoint(mainplayer.getTeam());
                    for (RebirthPoint rebirthPoint : rebirthPoints) {
                        if (rebirthPoint.isRebirthable(mainplayer)) {
                            rebirthPoint.rebirth(mainplayer);
                            GetScore.getRebirthScore(mainplayer);
                            return;
                        }
                    }
                }
            }
        }
    }

    public static class MainplayerListener implements OnRoleSignListener {
        /**
         * 改变地图上Sign对应Marker的坐标
         *
         * @param sign   监听的sign对象
         * @param latLng 改变后的sign的坐标
         */
        @Override
        public void onMove(Sign sign, LatLng latLng) {
            //游戏结束后不会发生移动了
            if (SignManager.getInstance().getGAMESTATE() != STATE_END) {
                //主玩家只需要控制攻击圈进行移动
                Circle circle = SignManager.getInstance().getCircle(sign);
                if (circle != null)
                    circle.setCenter(latLng);
                //游戏进行中才进行触雷判断
                if (SignManager.isGameStarting()) {
                    //检查是否触发炸弹
                    ArrayList<Mine> mines = SignManager.getInstance().getOtherTeamMine(sign.getTeam());
                    for (Mine mine : mines) {
                        if (mine.isinfluence(latLng)) {
                            mine.boom();
                            ((RoleSign) sign).die();
                            GameHandler.doGameEventAndSendifNeed(GameEventFactory.produceShowToast(sign.getName() + "触发炸弹~"));
                            return;
                        }
                    }

                }
            }
        }

        @Override
        public void onDielistener(RoleSign roleSign) {
            GameHandler.doGameEventAndSendifNeed(GameEventFactory.produceMakeRolesignDieEvnet(roleSign));
            RebirthPoint rebirthPoint = SignFactory.produceRebirthpoint(
                    RandUtil.randPointerInCircle(roleSign.getLatLng(), GameConfig.DIST_REBIRTHPOINT),
                    SignManager.getInstance().getMainPlayer().getTeam());
            GameHandler.doGameEventAndSendifNeed(GameEventFactory.produceAddFixedSignEvent(rebirthPoint));
        }

        @Override
        public void onRebirthlistener(RoleSign roleSign) {
            GameHandler.doGameEventAndSendifNeed(GameEventFactory.produceMakeRolesignLiveEvnet(roleSign));
        }

        @Override
        public void onBeAttractedlistener(RoleSign attracker, RoleSign roleSign) {
            roleSign.die();
            GameHandler.doGameEventAndSendifNeed(GameEventFactory.produceShowToast("被" + attracker.getName() + "攻击"));
        }
    }

    /**
     * 除了主要玩家之外的角色的监听器
     */
    public static class OnOtherRoleSignlistener implements OnRoleSignListener {
        /**
         * 改变地图上Sign对应Marker的坐标
         *
         * @param sign   监听的sign对象
         * @param latLng 改变后的sign的坐标
         */
        @Override
        public void onMove(Sign sign, LatLng latLng) {
            //游戏结束后不会发生移动了
            if (SignManager.getInstance().getGAMESTATE() != STATE_END) {
                //发送消息给UI线程处理,并且是不需要上传到服务器端的
                GameHandler.doGameEventInLocal(GameEventFactory.produceEventMove(sign));

                //游戏在进行中才检查是否触发炸弹
                if (SignManager.isGameStarting()) {
                    /*检查是否触发炸弹*/
                    ArrayList<Mine> mines = SignManager.getInstance().getOtherTeamMine(sign.getTeam());
                    for (Mine mine : mines) {
                        if (mine.isinfluence(latLng)) {
                            mine.boom();
                            ((RoleSign) sign).die();
                            return;
                        }
                    }
                }

            }
        }

        @Override
        public void onDielistener(RoleSign roleSign) {
            if (roleSign instanceof Monster) {
                GameHandler.doGameEventAndSendifNeed(GameEventFactory.produceShowToast(roleSign.getName() + "死亡~"));
                GameHandler.doGameEventAndSendifNeed(GameEventFactory.produceRevomeSign(roleSign));
            } else {
                GameHandler.doGameEventAndSendifNeed(GameEventFactory.produceMakeRolesignDieEvnet(roleSign));
                RebirthPoint rebirthPoint = SignFactory.produceRebirthpoint(
                        RandUtil.randPointerInCircle(roleSign.getLatLng(), GameConfig.DIST_REBIRTHPOINT),
                        roleSign.getTeam());
                GameHandler.doGameEventAndSendifNeed(GameEventFactory.produceAddFixedSignEvent(rebirthPoint));
            }
        }

        @Override
        public void onRebirthlistener(RoleSign roleSign) {
            GameHandler.doGameEventAndSendifNeed(GameEventFactory.produceMakeRolesignLiveEvnet(roleSign));
        }

        @Override
        public void onBeAttractedlistener(RoleSign attracker, RoleSign roleSign) {
            roleSign.die();
        }
    }

    /**
     * 所有固定标志物的监听器
     */
    private static class FixedSignListener implements OnFixedSignListener {
        @Override
        public void OnRebirth(RebirthPoint rebirthPoint) {
            GameHandler.doGameEventAndSendifNeed(GameEventFactory.produceRevomeSign(rebirthPoint));
        }

        @Override
        public void OnBeOccupied(RoleSign roleSign, Flag flag) {
            GameHandler.doGameEventAndSendifNeed(GameEventFactory.produceOccupyFlagGameEvent(flag));
            if (SignManager.getInstance().isWin(roleSign.getTeam()))
                GameHandler.doGameEventAndSendifNeed(
                        GameEventFactory.produceWinEvent(roleSign));
        }

        @Override
        public void OnBoom(Mine mine) {
            GameHandler.doGameEventAndSendifNeed(GameEventFactory.produceBoomGameEvent(mine));
        }

        @Override
        public void OnSweep(Sapper sapper, Mine mine) {
            GameHandler.doGameEventAndSendifNeed(GameEventFactory.produceSweepMineGameEvent(mine));
        }

        @Override
        public void onMove(Sign sign, LatLng latLng) {

        }
    }
}
