package com.example.isky.flaggame;

/**
 * Created by x1832 on 2016/3/1.
 */
public class InitialValue {
    public static final int GAMETYPE_SinglePlayerGame = 0;
    public static final int GAMETYPE_MULTIPLAYER = 1;

    public static int GameType = GAMETYPE_MULTIPLAYER;
    public static double DIST_FLAG = 0.3;               //flag距离玩家的初始距离
    public static double DIST_REBIRTHPOINT = 0.1;       //重生点随机位置
    public static int NUM_MONSTERS = 3;                 //怪物数量
}
