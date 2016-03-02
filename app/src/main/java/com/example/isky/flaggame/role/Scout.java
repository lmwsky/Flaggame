package com.example.isky.flaggame.role;

/**
 * Created by Administrator on 2016/2/6.
 * 游戏角色 侦查员,拥有被动技能技能，侦查范围远大于其他角色
 */
public class Scout extends RoleSign {
    public static double DIST_ATTRACT=40.0;
    public static double DIST_INVESTIGATE=120.0;

    public Scout(int team) {
        super(team);
        setDist_attract(DIST_ATTRACT);
        setDist_investigate(DIST_INVESTIGATE);
    }

}
