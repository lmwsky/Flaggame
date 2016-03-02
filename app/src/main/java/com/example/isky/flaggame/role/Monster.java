package com.example.isky.flaggame.role;

import com.amap.api.maps2d.model.LatLng;

import java.util.Random;

/**
 * Created by isky on 2016/2/4.
 * 游戏中的怪物角色
 */
public class Monster extends RoleSign {
    public Monster(LatLng latLng){
        setDist_attract(Math.random()*30+20);
        this.latLng=latLng;
    }
}
