package com.example.isky.flaggame.game;

import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.example.isky.flaggame.R;

/**
 * Created by isky on 2016/2/29.
 * 游戏初始化配置信息，大写的是常量，表示可选的各种参数，小写的实际的值，会根据实际的值进行初始化游戏
 */
public class GameConfig {
    /*大写表示游戏初始化可以选的参数常量*/
    final public static double DIST_FLAG_CLOSED = 0.1;//flag距离玩家的初始距离km
    final public static double DIST_FLAG_NOMAL = 0.5;//flag距离玩家的初始距离km
    final public static double DIST_FLAG_FAR = 1.0;//flag距离玩家的初始距离km
    final public static double DIST_REBIRTHPOINT = 0.1;//重生点随机位置km
    final public static double DIST_MONSTER_CLOSED = DIST_FLAG_CLOSED - 0.05;
    final public static double DIST_MONSTER_NOMAL = DIST_FLAG_NOMAL - 0.05;
    final public static double DIST_MONSTER_FAR = DIST_FLAG_FAR - 0.05;

    final public static int NUM_MONSTER_SMALL = 3;
    final public static int NUM_MONSTER_MIDDLE = 6;
    final public static int NUM_MONSTER_LARGE = 10;
    public static final int GAMETYPE_SinglePlayerGame = 0;
    public static final int GAMETYPE_MULTIPLAYER = 1;


    /*小写表示的是游戏初始化的各种可变参数*/
    public static double dist_flag = DIST_FLAG_CLOSED;
    public static double num_monsters = NUM_MONSTER_MIDDLE;
    public static int gametype = GAMETYPE_MULTIPLAYER;

    public static BitmapDescriptor mainplayerBitmapDie = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
    public static BitmapDescriptor mainplayerBitmapLive = BitmapDescriptorFactory.fromResource(R.drawable.location_marker);
    public static BitmapDescriptor otherplayerDying = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);

}
