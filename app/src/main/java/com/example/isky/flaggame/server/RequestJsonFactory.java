package com.example.isky.flaggame.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import util.JsonUtil;

/**
 * Created by isky on 2016/2/21.
 * 组装联网请求中data的json
 */
public class RequestJsonFactory {
    private JSONObject jsonObject = new JSONObject();

    /**
     * create data 必选
     *
     * @param _name
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
            jsonObject.put("_location", JsonUtil.get_locationstr(latitude, longitude));
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
     * 将一个对象加入传输的JSON中，即自动转化为gson的字符串，并且同时上传类名
     *
     * @param object
     * @param <T>
     * @return
     */
    public <T> RequestJsonFactory object(T object) {
//
        Gson gson = new Gson();
        JsonElement objectjsonElement = gson.toJsonTree(object);
        JsonObject jsonObject = objectjsonElement.getAsJsonObject();

        if (jsonObject.has("latlng")) {
            JsonElement latlngelement = objectjsonElement.getAsJsonObject().get("latlng");
            com.google.gson.JsonObject latlngjsonObject = latlngelement.getAsJsonObject();
            JsonElement latitude = latlngjsonObject.get("latitude");
            JsonElement longitude = latlngjsonObject.get("longitude");
            _location(latitude.getAsDouble(), longitude.getAsDouble());
        } else
            _location(0.0, 0.0);


        customerValue("class", object.getClass().getName());
        gson(objectjsonElement);
         /*特殊处理，将其变为自定义属性*/
        if (jsonObject.has("roomid")) {
            String roomid = jsonObject.get("roomid").getAsString();
            customerValue("roomid", roomid);
        }
        return this;
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
