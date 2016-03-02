package com.example.isky.flaggame.role;

import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;

import java.util.ArrayList;

import util.RandUtil;

/**
 * Created by isky on 2016/1/28.
 * Sign的生成工厂
 */
public class SignFactory {


    /**
     * 在距离某个点的某个位置产生旗帜
     *
     * @param startpoint
     * @param dis 单位km
     * @return
     */
    public static Flag produceFlag(LatLng startpoint, double dis) {
        LatLng latLng = RandUtil.randPointerOnCircle(startpoint, dis);
        Flag flag = new Flag(latLng);
        flag.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        flag.setName("旗帜");
        return flag;
    }


    /**
     * 以某个位置为圆心在某个圆内产生怪物
     * @param center
     * @param radius
     * @return
     */
    public static Monster produceMonster(LatLng center, double radius) {
        Monster monster = new Monster(RandUtil.randPointerOnCircle(center, radius));
        monster.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        monster.setName("怪物");
        return monster;
    }

    /**
     * 在某个圆范围内产生一定数量的怪物
     * @param num 怪物数目
     * @param center 圆心
     * @param maxradius 半径 单位km
     * @return 产生的怪物
     */
    public static ArrayList<Monster> produceMonster(int num, LatLng center, double maxradius) {
        ArrayList<Monster> monsters = new ArrayList<>();
        for (int i = 0; i < num; i++)
            monsters.add(produceMonster(center, maxradius));

        return monsters;
    }

    public static RebirthPoint produceRebirthpoint(LatLng latLng,int team) {
        RebirthPoint rebirthpoint=new RebirthPoint();
        rebirthpoint.setTeam(team);
        rebirthpoint.setLatLng(latLng);
        rebirthpoint.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        rebirthpoint.setName("复活点");
        return rebirthpoint;
    }

    /**
     * 在指定坐标生成地雷
     * @param latLng
     * @return
     */
    public static Mine produceMine(Miner miner,LatLng latLng) {
        Mine mine=new Mine(miner,latLng.latitude,latLng.longitude,Mine.DIST_INFLUENCE);
        mine.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        mine.setName("地雷");
        return mine;
    }
}
