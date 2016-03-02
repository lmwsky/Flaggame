package util;


import com.amap.api.maps2d.model.LatLng;

/**
 * Created by isky on 2016/2/27.
 */
public class JsonUtil {
    public static  String get_locationstr(double latitude, double longitude){
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.######");
       return df.format(longitude) + "," + df.format(latitude);
    }
    public static  String get_locationstr(LatLng latLng){
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.######");
        return df.format(latLng.longitude) + "," + df.format(latLng.latitude);
    }
}
