package com.example.isky.flaggame.game;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.LatLng;
import com.example.isky.flaggame.role.Flag;
import com.example.isky.flaggame.role.Mine;
import com.example.isky.flaggame.role.OnFixedSignListener;
import com.example.isky.flaggame.role.OnRoleSignListener;
import com.example.isky.flaggame.role.RebirthPoint;
import com.example.isky.flaggame.role.RoleSign;
import com.example.isky.flaggame.role.Sapper;
import com.example.isky.flaggame.role.Sign;
import com.example.isky.flaggame.role.SignFactory;
import com.example.isky.flaggame.role.SignMarkerManager;

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
    public static OnFixedSignListener onFixedSignListener;
    public static OnOtherRoleSignlistener onOtherRolesignlistener;
    protected int GAMESTATE = STATE_UNINT;
    protected GameHandler handler;//其他线程消息处理器
    protected Activity activity;
    GameManager(Activity activity, AMap aMap) {
        this.activity=activity;
        this.handler = new GameHandler(this,activity);
        SignMarkerManager.getInstance().initMap(aMap);
       onFixedSignListener = new FixedSignListener();
        onOtherRolesignlistener = new OnOtherRoleSignlistener();
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
     * 点击监听器，点击就让当前玩家攻击能够攻击的人
     */
    public static class OnAttractBtClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            RoleSign mainplayer = SignMarkerManager.getInstance().getMainPlayer();
            if (mainplayer == null)
                return;
            ArrayList<RoleSign> otherteam = SignMarkerManager.getInstance().getOtherTeamRoleSign(mainplayer.getTeam());
            mainplayer.attack(otherteam);
        }
    }

    /**
     * 占领按钮监听器，点击就尝试占领
     */
    public static class OnOccupyBtClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            RoleSign mainplayer = SignMarkerManager.getInstance().getMainPlayer();
            if (mainplayer == null)
                return;
            ArrayList<Flag> otherTeamFlag = SignMarkerManager.getInstance().getOtherTeamFlag(mainplayer.getTeam());
            mainplayer.occupy(otherTeamFlag);
        }
    }

    /**
     * 使用技能按钮监听器,点击使用对应的技能
     */
    public static class OnSkillBtClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            RoleSign mainplayer = SignMarkerManager.getInstance().getMainPlayer();
            if (mainplayer == null)
                return;
            mainplayer.skill();
        }
    }

    /**
     * 使用技能按钮监听器,点击尝试重生
     */
    public static class OnRebirthBtClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            RoleSign mainplayer = SignMarkerManager.getInstance().getMainPlayer();
            if (mainplayer == null)
                return;
            ArrayList<RebirthPoint> rebirthPoints = SignMarkerManager.getInstance().getRebirthPoint(mainplayer.getTeam());
            for (RebirthPoint rebirthPoint : rebirthPoints) {
                if (rebirthPoint.isRebirthable(mainplayer)) {
                    rebirthPoint.rebirth(mainplayer);
                    GetScore.getRebirthScore(mainplayer);

                    return;
                }
            }
        }
    }

    public class SinglePlayerMainPlayerlistener implements OnRoleSignListener {
        /**
         * 改变地图上Sign对应Marker的坐标
         *
         * @param sign   监听的sign对象
         * @param latLng 改变后的sign的坐标
         */
        @Override
        public void onMove(Sign sign, LatLng latLng) {
            //主玩家只需要控制攻击圈进行移动
            Circle circle = SignMarkerManager.getInstance().getCircle(sign);
            if (circle != null)
                circle.setCenter(latLng);

            //检查是否触发炸弹
            ArrayList<Mine> mines = SignMarkerManager.getInstance().getOtherTeamMine(sign.getTeam());
            for (Mine mine : mines) {
                if (mine.isinfluence(latLng)) {
                    mine.boom();
                    ((RoleSign) sign).die();

                    GameHandler.sendMsg(GameHandler.MSG_SHOWTOAST, "某人触发炸弹~");
                    Log.d("hit", "某人触发炸弹~");
                    return;
                }
            }
        }

        /**
         * 改变地图上Sign对应Marker的图片
         *
         * @param sign             监听的sign对象
         * @param bitmapDescriptor 改变后的sign的图标，
         */
        @Override
        public void onIconChange(Sign sign, BitmapDescriptor bitmapDescriptor) {

        }


        @Override
        public void onDielistener(RoleSign roleSign) {
            Log.d("hit", "死亡，需到复活点处复活~");

            RebirthPoint rebirthPoint = SignFactory.produceRebirthpoint(
                    RandUtil.randPointerInCircle(roleSign.getLatLng(), GameConst.DIST_REBIRTHPOINT),
                    SignMarkerManager.getInstance().getMainPlayer().getTeam());
            rebirthPoint.addOnSignListener(onFixedSignListener);
            SignMarkerManager.getInstance().add(rebirthPoint);

            GameHandler.sendMsg(GameHandler.MSG_ADDSIGN, rebirthPoint);

            GameHandler.sendMsg(GameHandler.MSG_SWITCHICONTODIE, roleSign);

            GameHandler.sendMsg(GameHandler.MSG_SHOWTOAST, "死亡，需到复活点处复活~");

        }

        @Override
        public void onRebirthlistener(RoleSign roleSign) {
            GameHandler.sendMsg(GameHandler.MSG_SWITCHICONTOLIVE, roleSign);
            GameHandler.sendMsg(GameHandler.MSG_SHOWTOAST, "已经重生~");

            Log.d("hit", "已经重生!");
        }

        @Override
        public void onBeAttractedlistener(RoleSign attracker, RoleSign roleSign) {
            roleSign.die();

            GameHandler.sendMsg(GameHandler.MSG_SHOWTOAST, "被攻击~");
            Log.d("hit", "被攻击!");
        }
    }

    /**
     * 除了主要玩家之外的角色的监听器
     */
    public class OnOtherRoleSignlistener implements OnRoleSignListener {
        /**
         * 改变地图上Sign对应Marker的坐标
         *
         * @param sign   监听的sign对象
         * @param latLng 改变后的sign的坐标
         */
        @Override
        public void onMove(Sign sign, LatLng latLng) {
            //发送消息给UI线程处理
            GameHandler.sendMsg(GameHandler.MSG_MOVE, sign);

            /*检查是否触发炸弹*/
            ArrayList<Mine> mines = SignMarkerManager.getInstance().getOtherTeamMine(sign.getTeam());
            for (Mine mine : mines) {
                if (mine.isinfluence(latLng)) {
                    mine.boom();
                    ((RoleSign) sign).die();
                    return;
                }
            }
            /*移动完尝试攻击*/
            ((RoleSign) sign).attack(SignMarkerManager.getInstance().getOtherTeamRoleSign(sign.getTeam()));

        }

        /**
         * 改变地图上Sign对应Marker的图片
         *
         * @param sign             监听的sign对象
         * @param bitmapDescriptor 改变后的sign的图标，
         */
        @Override
        public void onIconChange(Sign sign, BitmapDescriptor bitmapDescriptor) {
            //SignMarkerManager.getInstance().getMarker(sign).setIcon(bitmapDescriptor);
        }


        @Override
        public void onDielistener(RoleSign roleSign) {
            GameHandler.sendMsg(GameHandler.MSG_SHOWTOAST, "怪物死亡~");
            Log.d("hit", "怪物死亡~");
            GameHandler.sendMsg(GameHandler.MSG_REMOVESIGN, roleSign);
        }

        @Override
        public void onRebirthlistener(RoleSign roleSign) {
            //roleSign.rebirth();
            //复活即显示标志
            //SignMarkerManager.getInstance().getMarker(roleSign).setVisible(true);
            //SignMarkerManager.getInstance().getCircle(roleSign).setVisible(true);
        }

        @Override
        public void onBeAttractedlistener(RoleSign attracker, RoleSign roleSign) {
            roleSign.die();
        }
    }

    /**
     * 所有固定标志物的监听器
     */
    private class FixedSignListener implements OnFixedSignListener {

        @Override
        public void OnRebirth(RebirthPoint rebirthPoint) {
            GameHandler.sendMsg(GameHandler.MSG_REMOVESIGN, rebirthPoint);
        }

        @Override
        public void OnBeOccupied(RoleSign roleSign, Flag flag) {
            if (SignMarkerManager.getInstance().isWin(roleSign.getTeam())) {
                GameHandler.sendMsg(GameHandler.MSG_SHOWTOAST, "游戏胜利啦！！！");
                EndGame();
            }
        }

        @Override
        public void OnBoom(Mine mine) {
            GameHandler.sendMsg(GameHandler.MSG_REMOVESIGN, mine);
            Log.d("hit", "地雷爆炸啦");
        }

        @Override
        public void OnSweep(Sapper sapper, Mine mine) {
            Log.d("hit", "地雷被扫掉啦");
            GameHandler.sendMsg(GameHandler.MSG_REMOVESIGN, mine);
        }

        @Override
        public void onMove(Sign sign, LatLng latLng) {

        }

        @Override
        public void onIconChange(Sign sign, BitmapDescriptor bitmapDescriptor) {

        }
    }

}
