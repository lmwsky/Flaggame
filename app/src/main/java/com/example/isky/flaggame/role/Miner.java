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
    private int max_mine = GameConfig.MAX_NUM_MINE;

    public Miner() {
        this(0);
    }

    public Miner(int team) {
        setTeam(team);
        setDist_investigate(GameConfig.DIST_INVESTIGATE_MINER);
        setDist_attract(GameConfig.DIST_ATTRACT_MINER);
    }

    /**
     * 技能：放置地雷，同时存在的地雷不能超过上限
     */
    @Override
    public void skill() {
        if (isDead)
            return;
        if (num_mine >= max_mine)
            return;
        num_mine++;
        Mine mine = SignFactory.produceMine(this, getLatLng());
        GetScore.putMineScore(this);
        GameHandler.doGameEventAndSendifNeed(GameEventFactory.produceAddFixedSignEvent(mine));
    }

    /**
     * 减少一个目前放置的炸弹的数目
     */
    public void reduceExistMineNum() {
        if (num_mine > 0)
            num_mine--;
    }

    /**
     * 增加一个最多能同时存在的炸弹数目
     */
    public void addMax_mine() {
        max_mine++;
    }
}
