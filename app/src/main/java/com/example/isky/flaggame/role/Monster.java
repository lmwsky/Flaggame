package com.example.isky.flaggame.role;

import com.amap.api.maps2d.model.LatLng;
import com.example.isky.flaggame.game.GameConfig;

/**
 * Created by isky on 2016/2/4.
 * 游戏中的怪物角色
 */
public class Monster extends RoleSign {
    public Monster() {
        setDist_attract(Math.random() * 30 + 20);
        setLatLng(new LatLng(0, 0));
        setIcon(GameConfig.BITMAP_MONSTER);
    }

    public Monster(LatLng latLng) {
        setDist_attract(Math.random() * 30 + 20);
        setLatLng(latLng);
        setIcon(GameConfig.BITMAP_MONSTER);

    }

    @Override
    public void skill() {

    }
}
