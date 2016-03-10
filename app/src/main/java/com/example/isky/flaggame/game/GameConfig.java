package com.example.isky.flaggame.game;

import android.graphics.Color;

import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.example.isky.flaggame.R;

/**
 * Created by isky on 2016/2/29.
 * 游戏初始化配置信息，大写的是常量，表示可选的各种参数，小写的实际的值，会根据实际的值进行初始化游戏
 */
public class GameConfig {

    public static final int COLOR_ATTRACTCIRCLE_RED = Color.argb(100, 250, 0, 0);
    public static final int COLOR_ATTRACTCIRCLE_BLUE = Color.argb(100, 0, 0, 250);
    public static final int COLOR_ATTRACTCIRCLE_GREEN = Color.argb(100, 0, 250, 0);


    /*大写表示游戏初始化可以选的参数常量*/
    final public static double DIST_FLAG_CLOSED = 0.1;//flag距离玩家的初始距离km
    final public static double DIST_FLAG_NOMAL = 0.5;//flag距离玩家的初始距离km
    final public static double DIST_FLAG_FAR = 1.5;//flag距离玩家的初始距离km
    final public static double DIST_REBIRTHPOINT = 0.1;//重生点随机位置km
    final public static double DIST_MONSTER_CLOSED = DIST_FLAG_CLOSED - 0.05;
    final public static double DIST_MONSTER_NOMAL = DIST_FLAG_NOMAL - 0.05;
    final public static double DIST_MONSTER_FAR = DIST_FLAG_FAR - 0.05;

    final public static int NUM_MONSTER_SMALL = 3;
    final public static int NUM_MONSTER_MIDDLE = 6;
    final public static int NUM_MONSTER_LARGE = 10;
    public static final int GAMETYPE_SINGLEGAME = 0;
    public static final int GAMETYPE_MULTIPLAYER = 1;

    public final static int SINGLEGAME_MAINPLAYERTEAM = 1;
    public final static int SINGLEGAME_MONSTERTEAM = 0;
    public final static int BITMAP_MINER_BLUE = R.drawable.minerblue;
    public final static int BITMAP_MINER_RED = R.drawable.minerred;
    public final static int BITMAP_MINER_GREEN = R.drawable.mineryellow;
    public final static int BITMAP_MINER[] = {BITMAP_MINER_BLUE, BITMAP_MINER_RED, BITMAP_MINER_GREEN};

    public final static int BITMAP_SAPPER_BLUE = R.drawable.sapperblue;
    public final static int BITMAP_SAPPER_RED = R.drawable.sapperred;
    public final static int BITMAP_SAPPER_GREEN = R.drawable.sapperyellow;
    public final static int BITMAP_SAPPER[] = {BITMAP_SAPPER_BLUE, BITMAP_SAPPER_RED, BITMAP_SAPPER_GREEN};

    public final static int BITMAP_SCOUT_BLUE = R.drawable.scoutblue;
    public final static int BITMAP_SCOUT_RED = R.drawable.scoutred;
    public final static int BITMAP_SCOUT_GREEN = R.drawable.scoutyellow;

    public final static int BITMAP_SCOUT[] = {BITMAP_SCOUT_BLUE, BITMAP_SCOUT_RED, BITMAP_SCOUT_GREEN};
    public final static int BITMAP_MONSTER1 = R.drawable.ghost1;
    public final static int BITMAP_MONSTER2 = R.drawable.ghost2;
    public final static int BITMAP_MONSTER3 = R.drawable.ghost3;
    public final static int BITMAP_MONSTER4 = R.drawable.ghost4;
    public final static int BITMAP_MONSTER5 = R.drawable.ghost5;
    public final static int BITMAP_FLAG_RED = R.drawable.redflag;
    public final static int BITMAP_FLAG_BLUE = R.drawable.blueflag;
    public final static int BITMAP_FLAG_GREEN = R.drawable.greenflag;
    public static BitmapDescriptor BITMAP_REBIRTHPOINT;
    public static BitmapDescriptor BITMAP_MINE;
    public static int[] BITMAP_MONSTERARRAY = {BITMAP_MONSTER1, BITMAP_MONSTER2, BITMAP_MONSTER3, BITMAP_MONSTER4, BITMAP_MONSTER5};
    public static int[] BITMAP_FLAGARRAY = {BITMAP_FLAG_BLUE, BITMAP_FLAG_RED, BITMAP_FLAG_GREEN};


    /*小写表示的是游戏初始化的各种可变参数*/
    public static double dist_flag = DIST_FLAG_FAR;
    public static int num_monsters = NUM_MONSTER_MIDDLE;
    public static int gametype = GAMETYPE_SINGLEGAME;
    public static BitmapDescriptor mainplayerBitmapDie = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
    public static BitmapDescriptor mainplayerBitmapLive = BitmapDescriptorFactory.fromResource(R.drawable.location_marker);
    public static BitmapDescriptor otherplayerDying = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
    public static double dist_monster = DIST_MONSTER_NOMAL;
    public static double DIST_MINE_INFLUENCE = 30d;//爆炸影响范围
    public static double DIST_ATTRACT_MINER = 20.0;//挖雷者的攻击范围
    public static double DIST_INVESTIGATE_MINER = 50.0;//挖雷者的侦查范围
    public static int MAX_NUM_MINE = 5;//最多能够放置的地雷数目
    public static long AIMOVEDELAY = 100;//更新位置的间隔，单位毫秒,可以看做每多少ms一帧动画
    public static double DIST_ATTRACT_SAPPER = 30.0;
    public static double DIST_INVESTIGATE_SAPPER = 80.0;
    public static double DIST_ATTRACT_SCOUT = 35.0;
    public static double DIST_INVESTIGATE_SCOUT = 200.0;

    /**
     * 加载图片资源
     */
    public static void initBitmap() {
        BITMAP_REBIRTHPOINT = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
        BITMAP_MINE = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);
    }
}
