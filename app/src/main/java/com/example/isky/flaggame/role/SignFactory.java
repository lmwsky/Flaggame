package com.example.isky.flaggame.role;

import com.amap.api.maps2d.model.LatLng;
import com.example.isky.flaggame.game.GameConfig;

import java.util.ArrayList;

import util.RandUtil;

/**
 * Created by isky on 2016/1/28.
 * Sign的生成工厂
 */
public class SignFactory {


    /**
     * 在距离某个点的一定距离随机产生旗帜
     *
     * @param startpoint 起始点
     * @param dis        距离，单位
     * @param team       队伍
     * @return 旗帜
     */

    public static Flag produceFlag(LatLng startpoint, double dis, int team) {
        LatLng latLng = RandUtil.randPointerOnCircle(startpoint, dis);
        return produceFlag(latLng, team);
    }


    /**
     * 以某个位置为圆心在某个圆内产生怪物
     *
     * @param center
     * @param radius
     * @return
     */
    public static Monster produceMonster(LatLng center, double radius) {
        Monster monster = new Monster(RandUtil.randPointerOnCircle(center, radius));
        monster.setIcon(GameConfig.BITMAP_MONSTER);
        monster.setName("怪物");
        return monster;
    }

    /**
     * 在某个圆范围内产生一定数量的怪物
     *
     * @param num       怪物数目
     * @param center    圆心
     * @param maxradius 半径 单位km
     * @return 产生的怪物
     */
    public static ArrayList<Monster> produceMonster(int num, LatLng center, double maxradius) {
        ArrayList<Monster> monsters = new ArrayList<>();
        for (int i = 0; i < num; i++)
            monsters.add(produceMonster(center, maxradius));

        return monsters;
    }

    public static RebirthPoint produceRebirthpoint(LatLng latLng, int team) {
        RebirthPoint rebirthpoint = new RebirthPoint();
        rebirthpoint.setTeam(team);
        rebirthpoint.setLatLng(latLng);
        rebirthpoint.setIcon(GameConfig.BITMAP_REBIRTHPOINT);
        rebirthpoint.setName("复活点");
        return rebirthpoint;
    }

    /**
     * 在指定坐标生成地雷
     *
     * @param latLng
     * @return
     */
    public static Mine produceMine(Miner miner, LatLng latLng) {
        Mine mine = new Mine();
        mine.setTeam(miner.getTeam());
        mine.setMinerSignature(miner.getSignature());
        mine.setLatLng(latLng);
        mine.setIcon(GameConfig.BITMAP_MINE);
        mine.setName("地雷");
        return mine;
    }

    /**
     * 在指定坐标生成指定队伍的布雷者
     *
     * @param latLng 产生的坐标
     * @param team   布雷者的队伍
     * @return 构造的布雷者对象
     */
    public static Miner produceMiner(LatLng latLng, int team) {
        Miner miner = new Miner(team);
        miner.setLatLng(latLng);
        miner.setIcon(GameConfig.BITMAP_MINER);
        miner.setName("布雷者");
        return miner;
    }

    public static Sapper produceSapper(LatLng latLng, int team) {
        Sapper sapper = new Sapper(team);
        sapper.setLatLng(latLng);
        sapper.setIcon(GameConfig.BITMAP_SAPPER);
        sapper.setName("扫雷者");
        return sapper;
    }

    public static Scout produceScout(LatLng latLng, int team) {
        Scout scout = new Scout(team);
        scout.setLatLng(latLng);
        scout.setIcon(GameConfig.BITMAP_SCOUT);
        scout.setName("侦查兵");
        return scout;
    }

    public static Flag produceFlag(LatLng latLng, int team) {
        Flag flag = new Flag(latLng);
        flag.setIcon(GameConfig.BITMAP_FLAG);
        flag.setTeam(team);
        flag.setName("旗帜");
        return flag;
    }
}
