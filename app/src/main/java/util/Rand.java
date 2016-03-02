package util;

import com.amap.api.maps2d.model.LatLng;

/**
 * Created by isky on 2016/1/28.
 * 随机工具，可以随机点的坐标等
 */
public class Rand {
    private static double radiusEarth = 6372.796924;//地球半径，单位km
    private static double DEGREE=57.29577951;//一弧度等于多少角度

    /**
     * 随机出以center为圆心，radius为半径的圆上的的点
     *
     * @param center 圆心中心的经纬度，弧度制
     * @param radius 距离，单位km
     * @return 随机出的点的坐标
     */
    public static LatLng randPointerOnCircle(LatLng center, double radius) {
        //java.util.Random random=new java.util.Random(System.currentTimeMillis());
        //Convert all latitudes and longitudes to radians.

        double startlat = center.latitude/DEGREE;
        double startlon = center.longitude/DEGREE;
        double longitude = 0, latitudes = 0;
        //Convert maximum distance to radians.
        double dist = radius / radiusEarth;
        //Compute a random bearing from 0 to 2*PI radians (0 to 360 degrees),
        // with all bearings having an equal probability of being chosen.
        double brg = 2 * Math.PI * Math.random();

        //Use the starting point,
        //random distance and random bearing to calculate the coordinates of the final random point.
        latitudes = Math.asin(Math.sin(startlat) * Math.cos(dist) + Math.cos(startlat) * Math.sin(dist) * Math.cos(brg));
        longitude = startlon + Math.atan2(Math.sin(brg) * Math.sin(dist) * Math.cos(startlat),
                Math.cos(dist) - Math.sin(startlat) * Math.sin(latitudes));

        //If lon is less than -PI then:
        if (longitude < (Math.PI * -1))
            longitude = longitude + 2 * Math.PI;
        //If lon is greater than PI then:
        if (longitude > Math.PI)
            longitude = longitude - 2 * Math.PI;

        //Convert all latitudes and longitudes to degree.
        longitude=longitude*DEGREE;
        latitudes=latitudes*DEGREE;
        return new LatLng(latitudes, longitude);
    }

    public static LatLng moveToGoal(LatLng start, LatLng goal, double speed){
        double startLat = start.latitude/DEGREE;
        double startLog = start.longitude/DEGREE;

        double goalLat = goal.latitude/DEGREE;
        double goalLog = goal.longitude/DEGREE;

        double deltaLat = goalLat - startLat;
        double deltaLog = goalLog - startLog;

        double distance = MapUtil.getDistance(start, goal);

        double latitude = 0, longitude = 0;

        latitude = (speed / distance * deltaLat + startLat) * DEGREE;
        longitude = (speed / distance * deltaLog + startLog) * DEGREE;
        return new LatLng(latitude, longitude);
    }


    /**
     * 随机出以center为圆心，radius为半径的圆内的的点
     *
     * @param center 圆心中心的经纬度，弧度制
     * @param radius 距离，单位km
     * @return 随机出的点的坐标
     */
    public static LatLng randPointerInCircle(LatLng center, double radius) {
        //java.util.Random random=new java.util.Random(System.currentTimeMillis());
        //Convert all latitudes and longitudes to radians.

        double startlat = center.latitude/DEGREE;
        double startlon = center.longitude/DEGREE;
        double longitude = 0, latitudes = 0;
        //Convert maximum distance to radians.
        double maxdist = radius / radiusEarth;
        //Compute a random distance from 0 to maxdist scaled so that points on larger circles have a greater probability of being chosen than points on smaller circles as described earlier.
        maxdist = Math.acos(Math.random() * (Math.cos(maxdist) - 1) + 1);
        //Compute a random bearing from 0 to 2*PI radians (0 to 360 degrees),
        // with all bearings having an equal probability of being chosen.
        double brg = 2 * Math.PI * Math.random();
        //Use the starting point,
        //random distance and random bearing to calculate the coordinates of the final random point.
        latitudes = Math.asin(Math.sin(startlat) * Math.cos(maxdist) + Math.cos(startlat) * Math.sin(maxdist) * Math.cos(brg));
        longitude = startlon + Math.atan2(Math.sin(brg) * Math.sin(maxdist) * Math.cos(startlat),
                Math.cos(maxdist) - Math.sin(startlat) * Math.sin(latitudes));

        //If lon is less than -PI then:
        if (longitude < (Math.PI * -1))
            longitude = longitude + 2 * Math.PI;
        //If lon is greater than PI then:
        if (longitude > Math.PI)
            longitude = longitude - 2 * Math.PI;

        //Convert all latitudes and longitudes to degree.

        longitude=longitude*DEGREE;
        latitudes=latitudes*DEGREE;
        return new LatLng(latitudes, longitude);
    }

    /**
     * 在一个矩形区域内随机点
     * @param northlimit_lat 最北纬度，弧度制
     * @param southlimit_lat 最南纬度，弧度制
     * @param westlimit_lon 最西纬度，弧度制
     * @param eastlimit_lon 最东纬度，弧度制
     * @return 随机出的点的经纬度
     */
    public static LatLng randInRectangular(double northlimit_lat,double southlimit_lat,double westlimit_lon,double eastlimit_lon){

        //Convert all latitudes and longitudes to radians.
        //Given the initial latitudes northlimit and southlimit, and the longitudes westlimit and eastlimit.
        // Compute a random latitude such that points on longer latitude lines in the rectangle are more likely to be chosen than points on shorter latitude lines.
        double latitudes = Math.asin(Math.random() * (Math.sin(northlimit_lat) - Math.sin(southlimit_lat)) + Math.sin(southlimit_lat));
        //Find the width of the rectangular region.
                double width = eastlimit_lon - westlimit_lon;
        //If width is less than 0 then:
        width = width + 2*Math.PI;
        //Compute the random longitude between westlimit and eastlimit with all longitudes having equal probability of being chosen.
                double longitude = westlimit_lon + width*Math.random();
        //If lon is less than -PI then:
        if (longitude < (Math.PI * -1))
        longitude = longitude + 2*Math.PI;
        //If lon is greater than PI then:
        if (longitude > Math.PI)
        longitude = longitude - 2*Math.PI;

        //Convert all latitudes and longitudes to degree.
        longitude=longitude*DEGREE;
        latitudes=latitudes*DEGREE;
        return new LatLng(latitudes, longitude);
    }
}
