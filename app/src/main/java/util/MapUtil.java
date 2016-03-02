package util;

import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.example.isky.flaggame.role.Sign;

/**
 * Created by isky on 2016/2/7.
 * 与地图有关的工具，比如计算两点的距离
 */
public class MapUtil {
    /**
     * 返回两个经纬度之间的距离
     * @param startLatlng
     * @param endLatlng
     * @return 单位，米
     */
    public static double getDistance(LatLng startLatlng,LatLng endLatlng){

        return AMapUtils.calculateLineDistance(startLatlng, endLatlng);

    }
    public static double getDistance(Sign sign1,Sign sign2){
        return AMapUtils.calculateLineDistance(sign1.getLatLng(), sign2.getLatLng());
    }
}
