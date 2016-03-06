package com.example.isky.flaggame.role;

import com.example.isky.flaggame.game.GameConfig;

import java.util.ArrayList;

import util.GetScore;

/**
 * Created by Administrator on 2016/2/6.
 * 扫雷者
 */
public class Sapper extends RoleSign {

    public Sapper() {
        setDist_attract(GameConfig.DIST_ATTRACT_SAPPER);
        setDist_investigate(GameConfig.DIST_INVESTIGATE_SAPPER);
        setIcon(GameConfig.BITMAP_SAPPER);

    }

    public Sapper(int team) {
        setTeam(team);
        setDist_attract(GameConfig.DIST_ATTRACT_SAPPER);
        setDist_investigate(GameConfig.DIST_INVESTIGATE_SAPPER);
        setIcon(GameConfig.BITMAP_SAPPER);

    }

    @Override
    /**
     * 技能:扫雷,扫除所有攻击范围内的敌方地雷
     */
    public void skill() {
        if (isDead)
            return;
        ArrayList<Mine> mineArrayList = SignManager.getInstance().getMines();
        for (Mine mine : mineArrayList) {
            if (isAttrackable(mine)) {
                mine.sweep(this);
                GetScore.sweepMineScore(this);
            }
        }
    }

}
