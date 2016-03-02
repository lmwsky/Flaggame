package com.example.isky.flaggame.role;

import com.example.isky.flaggame.game.GameHandler;
import com.example.isky.flaggame.game.GameManager;

import util.GameApplication;

import java.util.ArrayList;
import java.util.List;

import util.ToastUtil;

/**
 * Created by Administrator on 2016/2/6.
 * 布雷者
 */
public class Miner extends RoleSign {
    public static double DIST_ATTRACT = 30.0;
    public static double DIST_INVESTIGATE = 20.0;
    private int num_mine = 0;
    private static int max_mine = 5;
    private List<Mine> mineList = new ArrayList<Mine>();   //全局变量

    public Miner(int team) {
        super(team);
        setDist_investigate(DIST_INVESTIGATE);
        setDist_attract(DIST_ATTRACT);
    }

    @Override
    public void skill() {
        if (num_mine >= max_mine)
            return;
        num_mine++;
        Mine mine = SignFactory.produceMine(this, getLatLng());
        mineList.add(mine);
        mine.addOnSignListener(GameManager.onFixedSignListener);

        GameHandler.sendMsg(GameHandler.MSG_ADDSIGN, mine);

        ToastUtil.show(GameApplication.getApplication(), "放置炸弹");
    }
}
