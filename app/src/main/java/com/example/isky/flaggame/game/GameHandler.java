package com.example.isky.flaggame.game;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.isky.flaggame.role.FixedSign;
import com.example.isky.flaggame.role.RoleSign;
import com.example.isky.flaggame.role.Sign;
import com.example.isky.flaggame.role.SignManager;
import com.example.isky.flaggame.server.BindwithServer;
import com.example.isky.flaggame.server.LocationServiceManager;
import com.example.isky.flaggame.server.PlayerManager;
import com.google.gson.Gson;

import util.ToastUtil;

/**
 * Created by isky on 2016/2/29.
 * 用于捕获消息，更新游戏时的UI线程，所有对于游戏的UI修改，必须借助这个类
 */
public class GameHandler extends Handler {
    public final static int MSG_ADDMAINPLAYERROLESIGN = 0;

    /**
     * 添加sign到地图
     */
    public final static int MSG_ADDSIGN = 1;

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

    /**
     * 传输一个msg，msg.what=type,msg.obj=object
     *
     * @param type   传输的事件的类型，可以通过GameHandler.MSG_...来获得
     * @param object 传输的数据
     * @param <T>    支持任意泛型
     */
    static public <T> void doGameEvent(int type, T object) {
        Message message = new Message();
        message.what = type;
        message.obj = object;
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
        message.obj = gameEvent;
        if (handler != null) {
            handler.sendMessage(message);
            if (GameConfig.gametype == GameConfig.GAMETYPE_MULTIPLAYER)
                sendGameEvent(gameEvent, onSendGameEventToServerListener);
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
            message.obj = gameEvent;
            if (handler != null) {
                handler.sendMessage(message);
            }
        }
    }

    /**
     * 发送游戏事件到服务器
     *
     * @param gameEvent            游戏事件
     * @param onCreateDataListener 发送游戏事件后的监听器
     */
    public static void sendGameEvent(GameEventFactory.GameEvent gameEvent, BindwithServer.OnCreateDataListener onCreateDataListener) {
        BindwithServer.getInstance().createData(gameEvent, onCreateDataListener);
    }

    //让其他线程能通过发送msg来操作UI线程
    @Override
    public void handleMessage(Message msg) {
        // TODO 接收消息并且去更新UI线程上地图的内容，添加更多的事件类型

        Object gsonObject = null;
        GameEventFactory.GameEvent gameEvent = (GameEventFactory.GameEvent) (msg.obj);
        try {

            gsonObject = gson.fromJson(gameEvent.getGson(), Class.forName(gameEvent.getGsonclassname()));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (gsonObject != null) {
            switch (msg.what) {
                case MSG_ADDMAINPLAYERROLESIGN: {
                    RoleSign mainplayer = (RoleSign) gsonObject;
                    SignManager.getInstance().addMainPlayerToMap(mainplayer);
                    mainplayer.addOnSignListener(GameManager.onSinglePlayerMainPlayerlistener);
                    //添加为定位的位置接收者
                    LocationServiceManager.getInstance().setLocationInfoReceiver(mainplayer);
                }
                break;
                case MSG_ADDSIGN: {
                    //添加标记，并且要添加对应的监听器
                    //如果主玩家id与gameevent的Topalyerid一致，说明这个要添加的rolesign是主玩家
                    if (PlayerManager.getInstance().getMainplayer().get_id().equals(gameEvent.getToplayerid())) {
                        RoleSign mainplayer = (RoleSign) gsonObject;
                        SignManager.getInstance().addMainPlayerToMap(mainplayer);
                        mainplayer.addOnSignListener(GameManager.onSinglePlayerMainPlayerlistener);
                        //添加为定位的位置接收者
                        LocationServiceManager.getInstance().setLocationInfoReceiver(mainplayer);


                        //绑定主玩家对象和主玩家角色，主玩家角色加入地图，开始发送位置
                        SignManager.getInstance().bindMainplayerAsLocationSender(PlayerManager.getInstance().getMainplayer());
                        //添加监听器
                        SignManager.getInstance().bindRoleSignWithPlayerid(mainplayer.getSignature(), PlayerManager.getInstance().getMainplayer().get_id());


                    } else {
                        SignManager.getInstance().addSignToMap(((Sign) gsonObject));
                        Sign sign = (Sign) gsonObject;
                        if (gsonObject instanceof FixedSign)
                            sign.addOnSignListener(GameManager.onFixedSignListener);
                        else
                            sign.addOnSignListener(GameManager.onOtherRolesignlistener);

                        if (GameConfig.gametype == GameConfig.GAMETYPE_MULTIPLAYER) {
                            if (sign instanceof RoleSign) {
                                //绑定rolesign为player的位置接收者，当player的位置改变时，rolesign也发生变化
                                SignManager.getInstance().bindRoleSignWithPlayerid(sign.getSignature(), gameEvent.getToplayerid());
                                //player开始和服务器端进行同步
                                BindwithServer.getInstance().startReceivePlayerLocation(SignManager.getInstance().getPlayerByPlayerid(gameEvent.getToplayerid()));
                            }
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
                case MSG_STARTGAME:
                    gameManager.StartGame();
                    break;
                case MSG_ENDGAME:
                    gameManager.EndGame();
                    break;
                default:
                    break;
            }
        }

        super.handleMessage(msg);
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    /**
     * 发送游戏事件到服务器的监听器
     */
    public static class OnSendGameEventToServerListener implements BindwithServer.OnCreateDataListener {
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

