package com.example.isky.flaggame.role;

import com.example.isky.flaggame.game.GameConfig;
import com.example.isky.flaggame.game.GameEventFactory;
import com.example.isky.flaggame.game.GameHandler;

import util.GetScore;

/**
 * Created by Administrator on 2016/2/6.
 * 布雷者
 */
public class Miner extends RoleSign {
    private int num_mine = 0;

    public Miner() {
        this(0);
    }

    public Miner(int team) {
        setTeam(team);
        setDist_investigate(GameConfig.DIST_INVESTIGATE_MINER);
        setDist_attract(GameConfig.DIST_ATTRACT_MINER);
    }


    @Override
    public void skill() {
        if (isDead)
            return;
        if (num_mine >= GameConfig.MAX_NUM_MINE)
            return;
        num_mine++;
        Mine mine = SignFactory.produceMine(this, getLatLng());
        GetScore.putMineScore(this);
        GameHandler.doGameEventAndSendifNeed(GameEventFactory.produceAddFixedSignEvent(mine));
    }
}
