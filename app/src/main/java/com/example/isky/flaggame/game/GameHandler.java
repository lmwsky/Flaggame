package com.example.isky.flaggame.game;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.isky.flaggame.role.FixedSign;
import com.example.isky.flaggame.role.RoleSign;
import com.example.isky.flaggame.role.Sign;
import com.example.isky.flaggame.role.SignManager;
import com.example.isky.flaggame.role.WalkPath2AI;
import com.example.isky.flaggame.server.LocationServiceManager;
import com.example.isky.flaggame.server.PlayerManager;
import com.example.isky.flaggame.server.Server;
import com.google.gson.Gson;

import util.ToastUtil;

/**
 * Created by isky on 2016/2/29.
 * 用于捕获消息，更新游戏时的UI线程，所有对于游戏的UI修改，必须借助这个类
 */
public class GameHandler extends Handler {
    public final static int MSG_ADDMAINPLAYERROLESIGN_SINGLE = 0;

    /**
     * 添加sign到地图
     */
    public final static int MSG_ADDSIGN_SINGLE = 1;

    /**
     * 从地图移除sign
     */
    public final static int MSG_REMOVESIGN = 2;
    /**
     * 图标变死
     */
    public final static int MSG_SWITCHICONTODIE = 3;
    /**
     * 图标变活
     */
    public static final int MSG_SWITCHICONTOLIVE = 4;
    /**
     * 显示TOAST信息
     */
    public static final int MSG_SHOWTOAST = 5;
    /**
     * 更新最新的位置
     */
    public static final int MSG_MOVESIGN = 6;
    /**
     * 开始游戏
     */
    public static final int MSG_STARTGAME = 8;
    /**
     * 结束游戏
     */
    public static final int MSG_ENDGAME = 9;
    public static final int MSG_BINDWALKPATHAI = 10;
    /**
     * 添加sign到地图
     */
    public final static int MSG_ADDSIGN_MULTI = 11;
    public static final int MSG_ADDSIGN = 12;//通用的addsign

    private static Handler handler;
    private static OnSendGameEventToServerListener onSendGameEventToServerListener = new OnSendGameEventToServerListener();
    private GameManager gameManager;
    private Activity activity;
    private Gson gson;

    /**
     * @param gameManager 游戏的管理
     * @param activity    游戏所在的UI context
     */
    public GameHandler(GameManager gameManager, Activity activity) {
        this.gameManager = gameManager;
        this.activity = activity;
        this.gson = new Gson();
        setHandler(this);
    }

    /**
     * @param gameManager 游戏的管理
     */
    public GameHandler(GameManager gameManager) {
        this.gameManager = gameManager;
        setHandler(this);
    }

    public static void setHandler(Handler handler) {
        GameHandler.handler = handler;
    }


    static public void doGameEventInLocal(GameEventFactory.GameEvent gameEvent) {
        Message message = new Message();
        message.what = gameEvent.getEventtype();
        if (message.what == MSG_ADDSIGN)
            message.what = MSG_ADDSIGN_SINGLE;
        message.obj = gameEvent;
        if (handler != null)
            handler.sendMessage(message);
    }

    /**
     * 执行gameEvent,如果是多人游戏，自动发送到服务器
     *
     * @param gameEvent 游戏事件
     */
    static public void doGameEventAndSendifNeed(GameEventFactory.GameEvent gameEvent) {
        Message message = new Message();

        message.what = gameEvent.getEventtype();
        if (message.what == MSG_ADDSIGN) {
            if (GameConfig.gametype == GameConfig.GAMETYPE_MULTIPLAYER)
                message.what = MSG_ADDSIGN_MULTI;
            else
                message.what = MSG_ADDSIGN_SINGLE;
        }


        message.obj = gameEvent;
        if (handler != null) {
            handler.sendMessage(message);
            if (GameConfig.gametype == GameConfig.GAMETYPE_MULTIPLAYER)
                sendGameEventToServer(gameEvent, onSendGameEventToServerListener);
        }
    }

    /**
     * 执行从服务器获取到的gameEvent, 若gameEvent的来源是当前玩家则不执行
     *
     * @param gameEvent 游戏事件
     */
    public static void doGameEventFromServer(GameEventFactory.GameEvent gameEvent) {
        if (!gameEvent.getSourceplayerid().equals(PlayerManager.getInstance().getMainplayer().get_id())) {
            Message message = new Message();
            message.what = gameEvent.getEventtype();
            if (message.what == MSG_ADDSIGN)
                message.what = MSG_ADDSIGN_MULTI;

            message.obj = gameEvent;
            if (handler != null) {
                Log.d("event", "send server event to handler=" + gameEvent.get_id());
                handler.sendMessage(message);
            }
        } else {
            Log.d("event", "event send from self " + gameEvent.get_id());
        }
    }

    /**
     * 发送游戏事件到服务器
     *
     * @param gameEvent            游戏事件
     * @param onCreateDataListener 发送游戏事件后的监听器
     */
    public static void sendGameEventToServer(GameEventFactory.GameEvent gameEvent, Server.OnCreateDataListener onCreateDataListener) {
        Server.getInstance().createData(gameEvent, onCreateDataListener);
    }

    //让其他线程能通过发送msg来操作UI线程
    @Override
    public void handleMessage(Message msg) {
        // TODO 接收消息并且去更新UI线程上地图的内容，添加更多的事件类型

        GameEventFactory.GameEvent gameEvent = (GameEventFactory.GameEvent) (msg.obj);
        Object gsonObject = gameEvent.obj;
        switch (msg.what) {
            case MSG_ADDMAINPLAYERROLESIGN_SINGLE: {
                RoleSign mainplayer = (RoleSign) gsonObject;
                SignManager.getInstance().addMainPlayerToMap(mainplayer);
                if (mainplayer != null) {
                    mainplayer.addOnSignListener(GameManager.onSinglePlayerMainPlayerlistener);
                }
                //添加为定位的位置接收者
                LocationServiceManager.getInstance().setLocationInfoReceiver(mainplayer);
            }
            break;
            case MSG_ADDSIGN_SINGLE: {
                SignManager.getInstance().addSignToMap(((Sign) gsonObject));
                Sign sign = (Sign) gsonObject;
                if (gsonObject instanceof FixedSign)
                    sign.addOnSignListener(GameManager.onFixedSignListener);
                else
                    sign.addOnSignListener(GameManager.onOtherRolesignlistener);
            }
            break;
            case MSG_ADDSIGN_MULTI: {
                Log.d("event addsign", "prepare add sign to map " + gameEvent.get_id());
                //添加标记，并且要添加对应的监听器
                //如果主玩家id与gameevent的Topalyerid一致，说明这个要添加的rolesign是主玩家
                PlayerManager.Player mainplayer = PlayerManager.getInstance().getMainplayer();
                if (mainplayer != null && mainplayer.get_id().equals(gameEvent.getToplayerid())) {
                    Log.d("event addsign", "mainsign " + gameEvent.get_id());

                    RoleSign mainpalyerrolesign = (RoleSign) gsonObject;
                    SignManager.getInstance().addMainPlayerToMap(mainpalyerrolesign);
                    mainpalyerrolesign.addOnSignListener(GameManager.onSinglePlayerMainPlayerlistener);
                    //添加为定位的位置接收者
                    LocationServiceManager.getInstance().setLocationInfoReceiver(mainpalyerrolesign);
                    //绑定主玩家对象和主玩家角色，主玩家角色加入地图，开始发送位置
                    mainplayer.bindMainplayerAsLocationSender();
                    //添加监听器
                    SignManager.getInstance().bindRoleSignWithPlayerid(mainpalyerrolesign.getSignature(), mainplayer.get_id());
                } else {
                    Log.d("event addsign", "mainsign " + gameEvent.get_id());
                    Sign sign = (Sign) gsonObject;

                    SignManager.getInstance().addSignToMap(sign);
                    if (gsonObject instanceof FixedSign) {
                        sign.addOnSignListener(GameManager.onFixedSignListener);
                        Log.d("event addsign", " add FixedSign " + gameEvent.get_id());


                    } else
                        sign.addOnSignListener(GameManager.onOtherRolesignlistener);

                    if (sign instanceof RoleSign) {
                        //绑定rolesign为player的位置接收者，当player的位置改变时，rolesign也发生变化
                        SignManager.getInstance().bindRoleSignWithPlayerid(sign.getSignature(), gameEvent.getToplayerid());
                        //player开始和服务器端进行同步
                        Server.getInstance().startReceivePlayerLocation(gameEvent.getToplayerid());
                    }

                }
            }
            break;
            case MSG_REMOVESIGN: {
                //gsonObject 存的是要移除的Sign的唯一签名
                SignManager.getInstance().remove((String) gsonObject);
            }
            break;
            case MSG_SWITCHICONTODIE: {
                Sign sign = SignManager.getInstance().getSignBySignature((String) gsonObject);
                SignManager.getInstance().setLastLiveBitDescriptor((String) gsonObject, sign.getIcon());
                sign.setIcon(GameConfig.mainplayerBitmapDie);
            }
            break;
            case MSG_SWITCHICONTOLIVE: {
                Sign sign = SignManager.getInstance().getSignBySignature((String) gsonObject);
                sign.setIcon(SignManager.getInstance().getLastLiveBitDescriptor((String) gsonObject));
            }
            break;
                    /*在地图上显示sign最新的位置*/
            case MSG_MOVESIGN: {
                Sign sign = SignManager.getInstance().getSignBySignature((String) gsonObject);
                SignManager.getInstance().move(sign);
            }
            break;

            case MSG_SHOWTOAST:
                if (activity != null)
                    ToastUtil.show(activity, (String) gsonObject);
                break;
            case MSG_BINDWALKPATHAI: {
                Sign sign = SignManager.getInstance().getSignBySignature((String) gsonObject);
                if (sign != null) {
                    ((RoleSign) sign).setAi(new WalkPath2AI(sign.getLatLng(),
                            SignManager.getInstance().getWalkPathList(), sign.getSignature()));
                }
            }
            break;
            case MSG_STARTGAME:
                gameManager.StartGame();
                break;
            case MSG_ENDGAME:
                gameManager.EndGame();
                break;
            default:
                break;
        }

        super.handleMessage(msg);
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    /**
     * 发送游戏事件到服务器的监听器
     */
    public static class OnSendGameEventToServerListener implements Server.OnCreateDataListener {
        @Override
        public void success(String _id) {
            Log.d("hh", "sendGameevent " + _id);
        }

        @Override
        public void fail(String info) {
            try {
                throw new Exception("send gameevent to server fail");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

