package com.example.isky.flaggame.game;

import com.example.isky.flaggame.role.FixedSign;
import com.example.isky.flaggame.role.Flag;
import com.example.isky.flaggame.role.Mine;
import com.example.isky.flaggame.role.RoleSign;
import com.example.isky.flaggame.role.Sign;
import com.example.isky.flaggame.server.PlayerManager;
import com.example.isky.flaggame.server.RoomManage;
import com.example.isky.flaggame.server.Server;
import com.example.isky.flaggame.server._idQuery;

/**
 * Created by isky on 2016/3/5.
 * 产生游戏事件的对象的工厂
 */
public class GameEventFactory {
    public static GameEvent produceAddFixedSignEvent(FixedSign fixedSign) {
        GameEvent gameEvent = new GameEvent();
        gameEvent.setEventtype(GameHandler.MSG_ADDSIGN);
        gameEvent.obj = fixedSign;
        return gameEvent;
    }

    /**
     * 需要签名
     *
     * @param sign
     * @return
     */
    public static GameEvent produceEventMove(Sign sign) {
        GameEvent gameEvent = new GameEvent();
        gameEvent.setEventtype(GameHandler.MSG_MOVESIGN);

        gameEvent.obj = sign.getSignature();

        return gameEvent;
    }

    public static GameEvent produceShowToast(String str) {
        GameEvent gameEvent = new GameEvent();
        gameEvent.setEventtype(GameHandler.MSG_SHOWTOAST);
        gameEvent.obj = str;

        return gameEvent;
    }

    /**
     * 并且去掉从地图上去掉对应的地雷，产生某个地雷爆炸的动画
     *
     * @param mine 爆炸的地雷
     * @return 事件
     */
    public static GameEvent produceBoomGameEvent(Mine mine) {
        GameEvent gameEvent = new GameEvent();
        gameEvent.setEventtype(GameHandler.MSG_BOOM);
        gameEvent.obj = mine.getSignature();
        return gameEvent;
    }

    /**
     * 产生某个队赢得游戏的事件
     *
     * @param roleSign 赢得游戏的人
     * @return 事件
     */
    public static GameEvent produceWinEvent(RoleSign roleSign) {
        GameEvent gameEvent = new GameEvent();
        gameEvent.setEventtype(GameHandler.MSG_WINGAME);
        gameEvent.obj = roleSign.getSignature();
        return gameEvent;
    }

    public static GameEvent produceRevomeSign(Sign sign) {
        GameEvent gameEvent = new GameEvent();
        gameEvent.setEventtype(GameHandler.MSG_REMOVESIGN);
        gameEvent.obj = sign.getSignature();
        return gameEvent;
    }

    /**
     * 产生一个添加某个玩家的某个角色的事件
     *
     * @param roleSign
     * @param rolesignplayerid
     * @return
     */
    public static GameEvent produceAddRoleSign(RoleSign roleSign, String rolesignplayerid) {
        GameEvent gameEvent = new GameEvent();
        gameEvent.setEventtype(GameHandler.MSG_ADDSIGN);

        //将rolesign对应的playerid设置为Toplayerid
        gameEvent.setToplayerid(rolesignplayerid);
        gameEvent.obj = roleSign;

        return gameEvent;
    }

    /**
     * 单人游戏中添加主玩家角色
     *
     * @param roleSign
     * @return
     */
    public static GameEvent produceAddMainPlayerRoleSignInSingleGame(RoleSign roleSign) {
        PlayerManager.Player player = PlayerManager.getInstance().getMainplayer();
        String rolesignplayerid = null;
        if (player != null)
            rolesignplayerid = player.get_id();

        GameEvent gameEvent = new GameEvent();
        gameEvent.setEventtype(GameHandler.MSG_SINGLE_ADDMAINPLAYERROLESIGN);
        //将rolesign对应的playerid设置为Toplayerid
        gameEvent.setToplayerid(rolesignplayerid);
        gameEvent.obj = roleSign;
        return gameEvent;
    }

    public static GameEvent produceBindWalkPathAI(String rolesignsignature) {
        GameEvent gameEvent = new GameEvent();
        gameEvent.setEventtype(GameHandler.MSG_BINDWALKPATHAI);
        gameEvent.obj = rolesignsignature;
        return gameEvent;
    }

    /**
     * 产生开始游戏的事件
     *
     * @return
     */
    public static GameEvent produceStartGameEvent() {
        GameEvent gameEvent = new GameEvent();
        gameEvent.setEventtype(GameHandler.MSG_STARTGAME);
        return gameEvent;
    }

    public static GameEvent produceEndGameEvent() {
        GameEvent gameEvent = new GameEvent();
        gameEvent.setEventtype(GameHandler.MSG_ENDGAME);
        return gameEvent;
    }

    public static GameEvent produceSweepMineGameEvent(Mine mine) {
        GameEvent gameEvent = new GameEvent();
        gameEvent.setEventtype(GameHandler.MSG_BOOMSWEEP);
        gameEvent.obj = mine.getSignature();

        return gameEvent;
    }

    /**
     * 产生一个占领旗帜的事件
     *
     * @param flag 被占领的旗帜
     * @return 产生的事件
     */
    public static GameEvent produceOccupyFlagGameEvent(Flag flag) {
        GameEvent gameEvent = new GameEvent();
        gameEvent.setEventtype(GameHandler.MSG_OCCUPYFLAGE);
        gameEvent.obj = flag.getSignature();
        return gameEvent;
    }

    public static GameEvent produceMakeRolesignDieEvnet(RoleSign roleSign) {
        GameEvent gameEvent = new GameEvent();
        gameEvent.setEventtype(GameHandler.MSG_MAKEROLESIGNDIE);
        gameEvent.obj = roleSign.getSignature();
        return gameEvent;
    }

    public static GameEvent produceMakeRolesignLiveEvnet(RoleSign roleSign) {
        GameEvent gameEvent = new GameEvent();
        gameEvent.setEventtype(GameHandler.MSG_MAKEROLESIGNLIVE);
        gameEvent.obj = roleSign.getSignature();
        return gameEvent;
    }

    /**
     * Created by isky on 2016/3/4.
     * 事件类
     */
    public static class GameEvent implements _idQuery {
        public transient Object obj;
        private int eventtype;
        private String sourceplayerid;
        private String toplayerid;
        private String roomid;

        /**
         * 默认构造事件时会添加玩家id和房间id
         */
        public GameEvent() {

            if (GameConfig.gametype == GameConfig.GAMETYPE_MULTIPLAYER) {
                RoomManage.Room room = PlayerManager.getInstance().getCurrentRoom();
                if (room != null)
                    setRoomid(room.get_id());
                PlayerManager.Player player = PlayerManager.getInstance().getMainplayer();
                if (player != null)
                    setSourceplayerid(player.get_id());
            }

        }

        public String getToplayerid() {
            return toplayerid;
        }

        /**
         * 设置接受事件的人的id，或者说事件影响的人的id，比如添加的rolesign是属于哪个玩家的，被攻击者
         *
         * @param toplayerid 玩家id
         */
        public void setToplayerid(String toplayerid) {
            this.toplayerid = toplayerid;
        }

        public int getEventtype() {
            return eventtype;
        }

        public void setEventtype(int eventtype) {
            this.eventtype = eventtype;
        }

        public String getSourceplayerid() {
            return sourceplayerid;
        }

        /**
         * 设置发布事件的人的id
         *
         * @param sourceplayerid 发布事件的人的id 可以为null
         */
        public void setSourceplayerid(String sourceplayerid) {
            this.sourceplayerid = sourceplayerid;
        }

        public String getRoomid() {
            return roomid;
        }

        public void setRoomid(String roomid) {
            this.roomid = roomid;
        }

        @Override
        public String get_id() {
            return Server.getInstance().get_id(this);
        }
    }
}
