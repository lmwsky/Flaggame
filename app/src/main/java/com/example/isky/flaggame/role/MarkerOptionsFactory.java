package com.example.isky.flaggame.role;

import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.MarkerOptions;
import com.example.isky.flaggame.game.GameConfig;

/**
 * Created by isky on 2016/1/31.
 * 地图上的标志物Marker的配置的生成工厂
 */
public class MarkerOptionsFactory {


    /**
     * 根据sign生成MarkerOption
     *
     * @param sign
     * @return
     */
    public static MarkerOptions produceBySign(Sign sign) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(sign.getIcon())
                .position(sign.getLatLng()).title(sign.getName()).visible(true);
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
                circleOptions.fillColor(GameConfig.COLOR_ATTRACTCIRCLE_RED);
                break;
            case 1:
                circleOptions.fillColor(GameConfig.COLOR_ATTRACTCIRCLE_BLUE);
                break;
            default:
                circleOptions.fillColor(GameConfig.COLOR_ATTRACTCIRCLE_GREEN);
                break;
        }
        return circleOptions;
    }

}
