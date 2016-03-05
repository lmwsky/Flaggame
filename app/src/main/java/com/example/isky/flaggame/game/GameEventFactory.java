package com.example.isky.flaggame.game;

import com.example.isky.flaggame.role.FixedSign;
import com.example.isky.flaggame.role.RoleSign;
import com.example.isky.flaggame.role.Sign;
import com.example.isky.flaggame.server.PlayerManager;
import com.example.isky.flaggame.server.RoomManage;
import com.google.gson.Gson;

/**
 * Created by isky on 2016/3/5.
 * 产生游戏事件的对象的工厂
 */
public class GameEventFactory {
    private static Gson gson = new Gson();

    public static GameEvent produceAddFixedSignEvent(FixedSign fixedSign) {
        GameEvent gameEvent = new GameEvent();
        gameEvent.setEventtype(GameHandler.MSG_ADDSIGN);
        gameEvent.setGson(gson.toJson(fixedSign));
        gameEvent.setGsonclassname(fixedSign.getClass().getName());
        return gameEvent;
    }

    public static GameEvent produceSwithBitMapToDie(Sign sign) {
        GameEvent gameEvent = new GameEvent();
        gameEvent.setEventtype(GameHandler.MSG_SWITCHICONTODIE);
        gameEvent.setGson(sign.getSignature());
        gameEvent.setGsonclassname(String.class.getName());
        return gameEvent;
    }

    public static GameEvent produceEventMove(Sign sign) {
        GameEvent gameEvent = new GameEvent();
        gameEvent.setEventtype(GameHandler.MSG_MOVESIGN);
        gameEvent.setGson(sign.getSignature());
        gameEvent.setGsonclassname(String.class.getName());
        return gameEvent;
    }

    public static GameEvent produceShowToast(String str) {
        GameEvent gameEvent = new GameEvent();
        gameEvent.setEventtype(GameHandler.MSG_SHOWTOAST);
        gameEvent.setGson(str);
        gameEvent.setGsonclassname(String.class.getName());
        return gameEvent;
    }

    public static GameEvent produceRevomeSign(Sign sign) {
        GameEvent gameEvent = new GameEvent();
        gameEvent.setEventtype(GameHandler.MSG_REMOVESIGN);
        gameEvent.setGson(sign.getSignature());
        gameEvent.setGsonclassname(String.class.getName());
        return gameEvent;
    }

    public static GameEvent produceSwithBitMapToLive(Sign sign) {
        GameEvent gameEvent = new GameEvent();
        gameEvent.setEventtype(GameHandler.MSG_SWITCHICONTOLIVE);
        gameEvent.setGson(sign.getSignature());
        gameEvent.setGsonclassname(String.class.getName());
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
        gameEvent.setGson(gson.toJson(roleSign));
        gameEvent.setGsonclassname(roleSign.getClass().getName());
        //将rolesign对应的playerid设置为Toplayerid
        gameEvent.setToplayerid(rolesignplayerid);
        return gameEvent;
    }

    /**
     * 单人游戏中添加主玩家角色
     *
     * @param roleSign
     * @return
     */
    public static GameEvent produceAddMainPlayerRoleSign(RoleSign roleSign) {
        PlayerManager.Player player = PlayerManager.getInstance().getMainplayer();
        String rolesignplayerid = null;
        if (player != null)
            rolesignplayerid = player.get_id();

        GameEvent gameEvent = new GameEvent();
        gameEvent.setEventtype(GameHandler.MSG_ADDMAINPLAYERROLESIGN);
        gameEvent.setGson(gson.toJson(roleSign));
        gameEvent.setGsonclassname(roleSign.getClass().getName());
        //将rolesign对应的playerid设置为Toplayerid
        gameEvent.setToplayerid(rolesignplayerid);
        return gameEvent;
    }

    public static GameEvent produceBindWalkPathAI(String rolesignsignature) {
        GameEvent gameEvent = new GameEvent();
        gameEvent.setEventtype(GameHandler.MSG_BINDWALKPATHAI);
        gameEvent.setGson(gson.toJson(rolesignsignature));
        gameEvent.setGsonclassname(String.class.getName());
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

    /**
     * Created by isky on 2016/3/4.
     * 事件类
     */
    public static class GameEvent {
        private int eventtype;
        private String gson;
        private String gsonclassname;
        private String sourceplayerid;
        private String toplayerid;
        private String roomid;

        /**
         * 默认构造事件时会添加玩家id和房间id
         */
        public GameEvent() {

            if (GameConfig.gametype == GameConfig.GAMETYPE_MULTIPLAYER) {
                RoomManage.Room room = PlayerManager.getInstance().getCurrentroom();
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

        public String getGson() {
            return gson;
        }

        public void setGson(String gson) {
            this.gson = gson;
        }

        public String getGsonclassname() {
            return gsonclassname;
        }

        public void setGsonclassname(String gsonclassname) {
            this.gsonclassname = gsonclassname;
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
    }
}
