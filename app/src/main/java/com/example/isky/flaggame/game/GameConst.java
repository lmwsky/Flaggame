package com.example.isky.flaggame.game;

/**
 * Created by isky on 2016/2/29.
 * 游戏需要用到的一些常量
 */
public class GameConst
{
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
}
