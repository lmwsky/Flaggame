package com.example.isky.flaggame.role;

import com.example.isky.flaggame.game.GameConfig;

/**
 * Created by Administrator on 2016/2/6.
 * 游戏角色 侦查员,拥有被动技能技能，侦查范围远大于其他角色
 */
public class Scout extends RoleSign {

    public Scout() {
        setDist_attract(GameConfig.DIST_ATTRACT_SCOUT);
        setDist_investigate(GameConfig.DIST_INVESTIGATE_SCOUT);
        setIcon(GameConfig.BITMAP_SCOUT);
    }

    public Scout(int team) {
        setDist_attract(team);
        setDist_attract(GameConfig.DIST_ATTRACT_SCOUT);
        setDist_investigate(GameConfig.DIST_INVESTIGATE_SCOUT);
    }

    @Override
    public void skill() {

    }

}
