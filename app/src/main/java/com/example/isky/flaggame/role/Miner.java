package com.example.isky.flaggame.role;

import android.util.Log;

import com.amap.api.maps2d.model.BitmapDescriptorFactory;
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
    public void setTeam(int team) {
        super.setTeam(team);
        setIcon(BitmapDescriptorFactory.fromResource(GameConfig.BITMAP_MINER[team % 3]));

    }
    @Override
    public void skill() {
        if (isDead == true)
            return;
        if (num_mine >= GameConfig.MAX_NUM_MINE)
            return;
        num_mine++;
        Mine mine = SignFactory.produceMine(this, getLatLng());
        GetScore.putMineScore(this);
        GameHandler.doGameEventAndSendifNeed(GameEventFactory.produceAddFixedSignEvent(mine));
        Log.d("hh", "使用技能");
    }
}
