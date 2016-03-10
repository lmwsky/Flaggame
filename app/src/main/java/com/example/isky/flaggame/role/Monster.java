package com.example.isky.flaggame.role;

import com.amap.api.maps2d.model.LatLng;
import com.example.isky.flaggame.game.GameConfig;

import java.util.Random;

/**
 * Created by isky on 2016/2/4.
 * 游戏中的怪物角色
 */
public class Monster extends RoleSign {

    private int icon_index = 0;

    public Monster() {
        this(new LatLng(0, 0));
    }

    public Monster(LatLng latLng) {
        setDist_attract(Math.random() * 30 + 10);
        setLatLng(latLng);
        Random random = new Random();
        icon_index = random.nextInt(GameConfig.BITMAP_MONSTERARRAY.length);
        setTeam(GameConfig.SINGLEGAME_MONSTERTEAM);
    }

    @Override
    public int getIconResouseid() {
        return GameConfig.BITMAP_MONSTERARRAY[icon_index];
    }

    @Override
    public void skill() {

    }
}
