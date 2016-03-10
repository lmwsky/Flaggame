package com.example.isky.flaggame.role;

import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.example.isky.flaggame.game.GameConfig;

import java.util.ArrayList;

/**
 * Created by isky on 2016/1/31.
 * 地图上的标志物Marker的配置的生成工厂
 */
public class MarkerOptionsFactory {

    private static ArrayList boombitmaplist;

    /**
     * 根据sign生成MarkerOption
     *
     * @param sign
     * @return
     */
    public static MarkerOptions produceBySign(Sign sign) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(sign.getIconResouseid()))
                .position(sign.getLatLng()).title(sign.getName()).visible(true).anchor(0.5f, 0.7f);
        return markerOptions;
    }

    /**
     * 根据rolesign生成攻击范围的CircleOptions,并且不同队伍颜色不一样
     *
     * @param roleSign
     * @return
     */
    public static CircleOptions produceAttackCircleBySign(RoleSign roleSign) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(roleSign.getLatLng()).radius(roleSign.getDist_attract()).strokeWidth(1.0f).
                visible(true).zIndex(10.0f);
        switch (roleSign.getTeam()) {
            case 0:
                circleOptions.fillColor(GameConfig.COLOR_ATTRACTCIRCLE_BLUE);
                break;
            case 1:
                circleOptions.fillColor(GameConfig.COLOR_ATTRACTCIRCLE_RED);
                break;
            default:
                circleOptions.fillColor(GameConfig.COLOR_ATTRACTCIRCLE_GREEN);
                break;
        }
        return circleOptions;
    }

    /**
     * 在某个坐标初始化一个爆炸动画
     *
     * @param latLng 爆炸坐标
     * @return 添加的动画资源的位置
     */
    public static MarkerOptions produceBoomAnimation(LatLng latLng) {
        if (boombitmaplist == null) {
            boombitmaplist = new ArrayList();
            for (int resouseid : GameConfig.BITMAP_BOOM)
                boombitmaplist.add(BitmapDescriptorFactory.fromResource(resouseid));
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng).icons(boombitmaplist).period(1).anchor(0.5f, 0.8f);
        return markerOptions;
    }
}
