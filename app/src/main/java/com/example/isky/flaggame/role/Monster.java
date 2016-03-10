package com.example.isky.flaggame.role;

import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.example.isky.flaggame.game.GameConfig;

import java.util.Random;

/**
 * Created by isky on 2016/2/4.
 * 游戏中的怪物角色
 */
public class Monster extends RoleSign {
    public Monster() {
        this(new LatLng(0, 0));
    }

    public Monster(LatLng latLng) {
        setDist_attract(Math.random() * 30 + 20);
        setLatLng(latLng);
        Random random = new Random();
        int num = GameConfig.BITMAP_MONSTERARRAY.length;
        setIcon(BitmapDescriptorFactory.fromResource(GameConfig.BITMAP_MONSTERARRAY[random.nextInt(num)]));
    }

    @Override
    public void skill() {

    }
}
