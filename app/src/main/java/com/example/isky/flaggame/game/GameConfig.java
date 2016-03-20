package com.example.isky.flaggame.game;

import android.graphics.Color;

import com.example.isky.flaggame.R;

/**
 * Created by isky on 2016/2/29.
 * 游戏初始化配置信息，大写的是常量，表示可选的各种参数，小写的实际的值，会根据实际的值进行初始化游戏
 */
public class GameConfig {
    public final static int BITMAP_TUFU_RED = R.drawable.tufured;
    public final static int BITMAP_TUFU_BLUE = R.drawable.tufublue;
    public final static int BITMAP_TUFU_GREEN = R.drawable.tufugreen;
    public final static int BITMAP_TUFU_DEAD = R.drawable.tufudead;

    public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_NORMAL = 1;
    public static final int DIFFICULTY_DIFFICUL = 2;

    public static final int COLOR_ATTRACTCIRCLE_RED = Color.argb(100, 255, 102, 501);
    public static final int COLOR_ATTRACTCIRCLE_BLUE = Color.argb(100, 0, 102, 204);
    public static final int COLOR_ATTRACTCIRCLE_GREEN = Color.argb(100, 0, 205, 0);
    /*大写表示游戏初始化可以选的参数常量*/
    final public static double DIST_FLAG_CLOSED = 0.1;//flag距离玩家的初始距离km
    final public static double DIST_FLAG_NOMAL = 0.5;//flag距离玩家的初始距离km
    final public static double DIST_FLAG_FAR = 1.5;//flag距离玩家的初始距离km
    final public static double DIST_REBIRTHPOINT = 0.1;//重生点随机位置km
    final public static double DIST_MONSTER_CLOSED = DIST_FLAG_CLOSED - 0.05;
    final public static double DIST_MONSTER_NOMAL = DIST_FLAG_NOMAL - 0.05;
    final public static double DIST_MONSTER_FAR = DIST_FLAG_FAR - 0.05;
    final public static int NUM_MONSTER_SMALL = 4;
    final public static int NUM_MONSTER_MIDDLE = 6;
    final public static int NUM_MONSTER_LARGE = 10;
    public static final int GAMETYPE_SINGLEGAME = 0;
    public static final int GAMETYPE_MULTIPLAYER = 1;
    public final static int SINGLEGAME_MAINPLAYERTEAM = 0;
    public final static int SINGLEGAME_MONSTERTEAM = 1;
    public final static int BITMAP_MINER_BLUE = R.drawable.minerblue;
    public final static int BITMAP_MINER_RED = R.drawable.minerred;
    public final static int BITMAP_MINER_GREEN = R.drawable.minergreen;
    public final static int BITMAP_SAPPER_BLUE = R.drawable.sapperblue;
    public final static int BITMAP_SAPPER_RED = R.drawable.sapperred;
    public final static int BITMAP_SAPPER_GREEN = R.drawable.sappergreen;
    public final static int BITMAP_SCOUT_BLUE = R.drawable.scoutblue;
    public final static int BITMAP_SCOUT_RED = R.drawable.scoutred;
    public final static int BITMAP_SCOUT_GREEN = R.drawable.scoutgreen;
    public final static int BITMAP_MONSTER1 = R.drawable.ghost1;
    public final static int BITMAP_MONSTER2 = R.drawable.ghost2;
    public final static int BITMAP_MONSTER3 = R.drawable.ghost3;
    public final static int BITMAP_MONSTER4 = R.drawable.ghost4;
    public final static int BITMAP_MONSTER5 = R.drawable.ghost5;
    public final static int BITMAP_FLAG_RED = R.drawable.redflag;
    public final static int BITMAP_FLAG_BLUE = R.drawable.blueflag;
    public final static int BITMAP_FLAG_GREEN = R.drawable.greenflag;
    public final static int BITMAP_MINE = R.drawable.mine;

    public final static int[] BITMAP_MONSTERARRAY = {BITMAP_MONSTER1, BITMAP_MONSTER2, BITMAP_MONSTER3, BITMAP_MONSTER4, BITMAP_MONSTER5};
    public final static int[] BITMAP_FLAGARRAY = {BITMAP_FLAG_BLUE, BITMAP_FLAG_RED, BITMAP_FLAG_GREEN};
    public final static double DIST_ATTRACT_SAPPER = 30.0;
    public final static double DIST_INVESTIGATE_SAPPER = 80.0;
    public final static double DIST_ATTRACT_SCOUT = 35.0;
    public final static double DIST_INVESTIGATE_SCOUT = 200.0;
    public final static int BITMAP_BOOM[] = {R.drawable.baozha1, R.drawable.baozha2, R.drawable.baozha3, R.drawable.baozha4, R.drawable.baozha5, R.drawable.baozha6, R.drawable.baozha7, R.drawable.baozha8};
    public final static int BITMAP_REBIRTHPOINT = R.drawable.icon_rebirthpoint;
    public final static int ROLE_MINER = 0;
    public final static int ROLE_SAPPER = 1;
    public final static int ROLE_SCOUT = 2;
    public final static int BITMAP_SKILL_MINER = R.drawable.icon_skill_miner;
    public final static int BITMAP_SKILL_SAPPER = R.drawable.icon_skill_sapper;
    public final static int BITMAP_SKILL_SCOUT = 0;
    public final static int BITMAP_SKILL_TUFU = 0;

    public final static int BITMAP_TUFU[][] = {
            {BITMAP_TUFU_BLUE, BITMAP_TUFU_RED, BITMAP_TUFU_GREEN},
            {BITMAP_TUFU_DEAD, BITMAP_TUFU_DEAD, BITMAP_TUFU_DEAD}
    };
    public final static double DIST_MINE_INFLUENCE = 50d;//爆炸影响范围
    public final static double DIST_ATTRACT_MINER = 20.0;//挖雷者的攻击范围
    public final static double DIST_INVESTIGATE_MINER = 50.0;//挖雷者的侦查范围
    public final static int MAX_NUM_MINE = 5;//最多能够放置的地雷数目
    public final static long AIMOVEDELAY = 100;//更新位置的间隔，单位毫秒,可以看做每多少ms一帧动画
    public final static double DIST_INVESTIGATE_TUFU = 80.0;
    public final static double DIST_ATTRACT_TUFU = 80.0;
    public final static int BITMAP_MINER_DEAD = R.drawable.minerdie;
    public final static int BITMAP_MINER[][] = {
            {BITMAP_MINER_BLUE, BITMAP_MINER_RED, BITMAP_MINER_GREEN},
            {BITMAP_MINER_DEAD, BITMAP_MINER_DEAD, BITMAP_MINER_DEAD}
    };
    public static final int ROLE_TUFU = 3;
    public static final int ROLESIGNNUM = 4;
    public static final double AI_RACE_SLOW = 0.005;
    public static final double AI_RACE_NOMAL = 0.007;
    public static final double AI_RACE_FAST = 0.009;
    private static final int BITMAP_SAPPER_DEAD = R.drawable.sapperdie;
    public final static int BITMAP_SAPPER[][] = {
            {BITMAP_SAPPER_BLUE, BITMAP_SAPPER_RED, BITMAP_SAPPER_GREEN},
            {BITMAP_SAPPER_DEAD, BITMAP_SAPPER_DEAD, BITMAP_SAPPER_DEAD}
    };
    private static final int BITMAP_SCOUT_DEAD = R.drawable.sapperdie;
    public final static int BITMAP_SCOUT[][] = {
            {BITMAP_SCOUT_BLUE, BITMAP_SCOUT_RED, BITMAP_SCOUT_GREEN},
            {BITMAP_SCOUT_DEAD, BITMAP_SCOUT_DEAD, BITMAP_SCOUT_DEAD}
    };
    public static int mainplayerroletype = ROLE_MINER;
    /*小写表示的是游戏初始化的各种可变参数*/
    public static double dist_flag = DIST_FLAG_FAR;
    public static int num_monsters = NUM_MONSTER_MIDDLE;
    public static int gametype = GAMETYPE_SINGLEGAME;
    public static int difficulty = DIFFICULTY_EASY;
    public static int flagnum = 1;
    public static double race_ai = 0.005;//km/s
    public static int mainplayerBitmapDie = R.drawable.minerblue;
    public static int mainplayerBitmapLive = R.drawable.location_marker;
    public static double dist_monster = dist_flag;
    public static int bitmap_skill = 0;

    /**
     * 加载图片资源
     */
    public static void initBitmap() {
    }

    /**
     * 设置mainplayer 控制的角色的类型
     *
     * @param mainPlayerRoleType，必须是GameConfig.ROLE_XXX 已经在GameConfig中定义的常量，若不是则会抛出异常
     */
    public static void setMainPlayerRoleSign(int mainPlayerRoleType) {
        switch (mainPlayerRoleType) {
            case ROLE_MINER:
                mainplayerBitmapLive = BITMAP_MINER_BLUE;
                mainplayerBitmapLive = BITMAP_MINER_BLUE;
                bitmap_skill = BITMAP_SKILL_MINER;
                break;
            case ROLE_SAPPER:
                mainplayerBitmapLive = BITMAP_SAPPER_BLUE;
                mainplayerBitmapLive = BITMAP_SAPPER_BLUE;
                bitmap_skill = BITMAP_SKILL_SAPPER;
                break;
            case ROLE_SCOUT:
                mainplayerBitmapLive = BITMAP_SCOUT_BLUE;
                mainplayerBitmapLive = BITMAP_SCOUT_BLUE;
                bitmap_skill = BITMAP_SKILL_SCOUT;
                break;
            case ROLE_TUFU:
                mainplayerBitmapLive = BITMAP_TUFU_BLUE;
                mainplayerBitmapLive = BITMAP_TUFU_BLUE;
                bitmap_skill = BITMAP_SKILL_TUFU;
                break;
            default:
                try {
                    throw new Exception("unknow rolesign type");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        GameConfig.mainplayerroletype = mainPlayerRoleType;
    }
}
