package com.example.isky.flaggame.role;

import android.graphics.Color;

import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.example.isky.flaggame.role.RoleSign;
import com.example.isky.flaggame.role.Sign;

/**
 * Created by isky on 2016/1/31.
 * 地图上的标志物Marker的配置的生成工厂
 */
public class MarkerOptionsFactory {

    /**
     * 根据sign生成MarkerOption
     * @param sign
     * @return
     */
    public static MarkerOptions produceBySign(Sign sign){
        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.icon(sign.getIcon())
                .position(sign.getLatLng()).title(sign.getName()).visible(true);
        return markerOptions;
    }

    /**
     * 根据rolesign生成攻击范围的CircleOptions
     * @param roleSign
     * @return
     */
    public static CircleOptions produceAttackCircleBySign(RoleSign roleSign){
        CircleOptions circleOptions=new CircleOptions();
        circleOptions.center(roleSign.getLatLng()).radius(roleSign.getDist_attract()).strokeWidth(1.0f).
                fillColor(Color.argb(100,250,0,0)).visible(true).zIndex(10.0f);
        return circleOptions;
    }

}
