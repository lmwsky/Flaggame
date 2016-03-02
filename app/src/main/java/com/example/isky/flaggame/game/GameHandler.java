package com.example.isky.flaggame.game;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.example.isky.flaggame.role.RoleSign;
import com.example.isky.flaggame.role.Sign;
import com.example.isky.flaggame.role.SignMarkerManager;

import util.ToastUtil;

/**
 * Created by isky on 2016/2/29.
 * 用于捕获消息，更新游戏时的UI线程
 */
public class GameHandler extends Handler {
    public final static int MSG_ADDSIGN = 1;//添加sign到地图
    public final static int MSG_REMOVESIGN = 2;//移除sign到地图
    public final static int MSG_SWITCHICONTODIE = 3;//主玩家图标变死
    public static final int MSG_SWITCHICONTOLIVE = 4;//主玩家图标变活
    public static final int MSG_SHOWTOAST = 5;//主玩家图标变活
    public static final int MSG_MOVE = 6;//进行移动
    private static Handler handler;
    private GameManager gameManager;
    private Activity activity;

    /**
     * @param gameManager 游戏的管理
     * @param activity    游戏所在的UI context
     */
    public GameHandler(GameManager gameManager, Activity activity) {
        this.gameManager = gameManager;
        this.activity = activity;
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
    static public <T> void sendMsg(int type, T object) {
        Message message = new Message();
        message.what = type;
        message.obj = object;
        if (handler != null)
            handler.sendMessage(message);
    }

    //让其他线程能通过发送msg来操作UI线程
    @Override
    public void handleMessage(Message msg) {

        // TODO 接收消息并且去更新UI线程上地图的内容，添加更多的事件类型
        switch (msg.what) {
            case MSG_ADDSIGN:
                SignMarkerManager.getInstance().addSignToMap((Sign) (msg.obj));
                break;
            case MSG_REMOVESIGN:
                SignMarkerManager.getInstance().remove((Sign) (msg.obj));
                break;
            case MSG_SWITCHICONTODIE:
                ((RoleSign) (msg.obj)).setIcon(RoleSign.mainplayerBitmapDie);
                SignMarkerManager.getInstance().changeMainplayerIcon(RoleSign.mainplayerBitmapDie);
                break;
            case MSG_SWITCHICONTOLIVE:
                ((RoleSign) (msg.obj)).setIcon(RoleSign.mainplayerBitmapLive);
                SignMarkerManager.getInstance().changeMainplayerIcon(RoleSign.mainplayerBitmapLive);
                break;
            case MSG_MOVE:
                Sign sign = (Sign) (msg.obj);
                SignMarkerManager.getInstance().move(sign);
                break;
            case MSG_SHOWTOAST:
                if(activity!=null)
                ToastUtil.show(activity, ((String) (msg.obj)));
                break;
            default:
                break;
        }
        super.handleMessage(msg);
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}

