package com.example.isky.flaggame.server;

import android.util.Log;

import com.amap.api.maps2d.model.LatLng;
import com.example.isky.flaggame.game.GameEventFactory;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by isky on 2016/2/21.
 * 组装联网请求中data的json
 */
public class RequestJsonFactory {
    private JSONObject jsonObject = new JSONObject();

    public static String get_locationstr(double latitude, double longitude) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.######");
        return df.format(longitude) + "," + df.format(latitude);
    }

    public static String get_locationstr(LatLng latLng) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.######");
        return df.format(latLng.longitude) + "," + df.format(latLng.latitude);
    }

    /**
     * create data 必选
     *
     * @param _name 设置为名字
     * @return
     * @throws JSONException
     */
    public RequestJsonFactory _name(String _name) {
        try {
            jsonObject.put("_name", _name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * update data 必选
     *
     * @param _id
     * @return
     * @throws JSONException
     */
    public RequestJsonFactory _id(String _id) {

        try {
            jsonObject.put("_id", _id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * create data 必选
     *
     * @param latitude
     * @param longitude
     * @return
     * @throws JSONException
     */
    public RequestJsonFactory _location(double latitude, double longitude) {

        java.text.DecimalFormat df = new java.text.DecimalFormat("#.######");
        try {
            jsonObject.put("_location", get_locationstr(latitude, longitude));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public RequestJsonFactory username(String username) {
        try {
            jsonObject.put("username", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public RequestJsonFactory gson(JsonElement jsonElement) {
        try {
            jsonObject.put("gson", jsonElement.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 将一个gameevent对象加入json
     *
     * @param gameEvent
     * @return
     */
    public RequestJsonFactory gameevent(GameEventFactory.GameEvent gameEvent) {
        /*特殊处理，将其变为自定义属性*/

        RequestJsonFactory requestJsonFactory = this;

        requestJsonFactory = requestJsonFactory.customerValue("roomid", PlayerManager.getInstance().getCurrentRoom().get_id())
                .customerValue("eventtype", gameEvent.getEventtype() + "")
                .customerValue("sourceplayerid", gameEvent.getSourceplayerid())
                .customerValue("toplayerid", gameEvent.getToplayerid())
                ._location(0, 0);
        if (gameEvent.obj != null) {
            Gson gson = new Gson();
            requestJsonFactory = requestJsonFactory.customerValue("gson", gson.toJson(gameEvent.obj))
                    .customerValue("clss", gameEvent.obj.getClass().getName());
        } else
            requestJsonFactory = requestJsonFactory.customerValue("clss", null)
                    .customerValue("gson", null);

        Log.i("hh json", requestJsonFactory.getJsonstr());
        return requestJsonFactory;
    }

    /**
     * 将一个对象加入传输的JSON中，即自动转化为gson的字符串，并且同时上传类名
     *
     * @param object
     * @param <T>
     * @return
     */
    public <T> RequestJsonFactory object(T object) {
        if (object instanceof GameEventFactory.GameEvent)
            return gameevent((GameEventFactory.GameEvent) object);
//
        Gson gson = new Gson();
        JsonElement objectjsonElement = gson.toJsonTree(object);
        JsonObject jsonObject = objectjsonElement.getAsJsonObject();

        if (jsonObject.has("latLng")) {
            JsonElement latlngelement = objectjsonElement.getAsJsonObject().get("latLng");
            com.google.gson.JsonObject latlngjsonObject = latlngelement.getAsJsonObject();
            JsonElement latitude = latlngjsonObject.get("latitude");
            JsonElement longitude = latlngjsonObject.get("longitude");
            _location(latitude.getAsDouble(), longitude.getAsDouble());
        } else
            _location(0.0, 0.0);
        RequestJsonFactory requestJsonFactory = customerValue("clss", object.getClass().getName()).gson(objectjsonElement);
         /*特殊处理，将其变为自定义属性*/
        if (jsonObject.has("roomid")) {
            String roomid = jsonObject.get("roomid").getAsString();
            requestJsonFactory.customerValue("roomid", roomid);
        }
        Log.i("hh json", requestJsonFactory.getJsonstr());
        return requestJsonFactory;
    }


    /**
     * 自定义字段以及其值得添加
     *
     * @param name  字段名字
     * @param value 字段值
     * @return
     */
    public RequestJsonFactory customerValue(String name, Object value) {
        try {
            jsonObject.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public String getJsonstr() {
        return jsonObject.toString();
    }
}
