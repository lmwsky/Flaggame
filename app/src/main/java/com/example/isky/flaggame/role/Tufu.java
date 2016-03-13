package com.example.isky.flaggame.role;

import com.example.isky.flaggame.game.GameConfig;

/**
 * Created by isky on 2016/3/11.
 * 玩家可选角色，攻击范围超大，视野很大，但是无法夺旗，
 */
public class Tufu extends RoleSign {
    public Tufu() {
        this(0);
    }

    public Tufu(int team) {
        setTeam(team);
        setDist_attract(GameConfig.DIST_ATTRACT_TUFU);
        setDist_investigate(GameConfig.DIST_INVESTIGATE_TUFU);
    }

    @Override
    public void skill() {

    }
}
