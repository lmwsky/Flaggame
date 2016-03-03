package com.example.isky.flaggame.role;

import java.util.ArrayList;

import util.GetScore;

/**
 * Created by Administrator on 2016/2/6.
 * 扫雷者
 */
public class Sapper extends RoleSign {
    public static double DIST_ATTRACT = 40.0;
    public static double DIST_INVESTIGATE = 50.0;

    public Sapper(int team) {
        super(team);
        setDist_attract(DIST_ATTRACT);
        setDist_investigate(DIST_INVESTIGATE);
    }

    @Override
    /**
     * 技能:扫雷,扫除所有攻击范围内的敌方地雷
     */
    public void skill() {
        if (isDead == true)
            return;
        ArrayList<Mine> mineArrayList = SignMarkerManager.getInstance().getMines();
        for (Mine mine : mineArrayList) {
            if (isAttrackable(mine)) {
                mine.sweep(this);
                GetScore.sweepMineScore(this);
            }
        }
    }

}
