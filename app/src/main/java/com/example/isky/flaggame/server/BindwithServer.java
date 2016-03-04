package com.example.isky.flaggame.server;

import android.support.annotation.Nullable;
import android.util.Log;

import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.cloud.CloudItem;
import com.amap.api.services.cloud.CloudItemDetail;
import com.amap.api.services.cloud.CloudResult;
import com.amap.api.services.cloud.CloudSearch;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.example.isky.flaggame.role.RoleSign;
import com.example.isky.flaggame.role.SignMarkerManager;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import util.GameApplication;
import util.JsonUtil;

/**
 * Created by isky on 2016/2/22.
 * 实现了这个接口的类能够和云端的数据进行绑定
 */
public class BindwithServer {
    public static final String TABLEID_ROOM = "56d111d4305a2a32886cd3a3";
    public static final String TABLEID_PLAYER = "56d3ba587bbf197f399b96f1";
    public static final String TABLEID_EVENT = "56d8f970305a2a3288be05a3";
    private static final long UPDATEPLAYERLOCATION_DELAY = 1100;
    private static BindwithServer bindwithServer;
    private HashMap<String, String> tablenamemap = new HashMap<>();//tablename 与 tableid的map
    private HashMap<Object, String> tableidmap = new HashMap<>();//object 与tableid的map
    private HashMap<Object, String> _idmap = new HashMap<>();//object 与 id(在 table中)的map
    private CloudSearch mCloudSearch;
    private OnCloudeSearchlistener onCloudeSearchlistener;

    private HashMap<PlayerManager.Player, Timer> playerTimerHashMap = new HashMap<>();//更新player位置的定时器

    private BindwithServer() {
        addtableid(RoomManage.Room.class.getName(), BindwithServer.TABLEID_ROOM);
        addtableid(PlayerManager.Player.class.getName(), BindwithServer.TABLEID_PLAYER);
        mCloudSearch = new CloudSearch(GameApplication.getApplication());

        onCloudeSearchlistener = new OnCloudeSearchlistener();
        mCloudSearch.setOnCloudSearchListener(onCloudeSearchlistener);
    }

    public static BindwithServer getInstance() {
        if (bindwithServer == null)
            bindwithServer = new BindwithServer();
        return bindwithServer;
    }

    public void queryWalkPath(LatLng s, LatLng e, final OndatasearchListener ondatasearchListener) {

        LatLonPoint startPoint = new LatLonPoint(s.latitude, s.longitude);
        LatLonPoint endPoint = new LatLonPoint(e.latitude, e.longitude);

        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, endPoint);
        RouteSearch routeSearch = new RouteSearch(GameApplication.getApplication());
        routeSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {

            @Override
            public void onBusRouteSearched(BusRouteResult busRouteResult, int statuscode) {
            }

            @Override
            public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int statuscode) {
            }

            @Override
            public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int statuscode) {
                WalkPath walkPath = null;
                if (statuscode == 0 && walkRouteResult != null && walkRouteResult.getPaths() != null
                        && walkRouteResult.getPaths().size() > 0) {
                    walkPath = walkRouteResult.getPaths().get(0);
                    ondatasearchListener.success(walkPath);
                } else {

                    try {
                        throw new Exception("can't not find path....");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ondatasearchListener.fail("unknow");
                }
            }
        });
        RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, RouteSearch.WalkDefault);
        routeSearch.calculateWalkRouteAsyn(query);
    }

    public <T> void deleteData(final T object, final OnDeleteDataListener onDeleteDataListener) {
        String tableid = tableidmap.get(object);
        String _id = _idmap.get(object);
        if (tableid == null || _id == null) {
            try {
                throw new Exception("tableid or _id is not exist");
            } catch (Exception e) {
                e.printStackTrace();
            }
            onDeleteDataListener.fail("NO exist");
        } else {
            RequestParamsFactory requestParamsFactory = new RequestParamsFactory();
            requestParamsFactory.tableid(tableid).ids(_id);
            AsyncClient.post("datamanage/data/delete", requestParamsFactory, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        if (response.getInt("status") != 1)
                            onDeleteDataListener.fail(response.getString("info"));
                        else {
                            tableidmap.remove(object);
                            _idmap.remove(object);
                            onDeleteDataListener.success("");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        onDeleteDataListener.fail("JSONNOTCONTENT STATUS_id");
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    onDeleteDataListener.fail(responseString);
                }
            });
        }
    }

    /**
     * 在某个表中建立一个数据,数据的值来源于object的拥有对应get方法的属性
     *
     * @param object
     * @param onCreateDataListener
     * @param <T>                  支持object为任意泛型，但是要求需要上传的属性拥有get方法
     */
    public <T> void createData(final T object, final OnCreateDataListener onCreateDataListener) {
        String tableid = tableidmap.get(object);
        //若object尚未绑定表，则自动以object.getClass().getSimpleName()为表名创建表,若创建表成功再添加data在这个表中
        if (tableid == null) {
            createTable(object.getClass().getSimpleName(), new OnCreateTableListener() {
                @Override
                public void success(String tableid) {
                    tableidmap.put(object, tableid);//绑定对象
                    createData(object, onCreateDataListener);
                }

                @Override
                public void fail(String info) {
                    onCreateDataListener.fail(info);
                }
            });
        }//若object已经绑定表
        else {
            RequestJsonFactory requestJsonFactory = new RequestJsonFactory();
            requestJsonFactory = requestJsonFactory._name(object.toString());

            //当前类声明的所有private,protect,public属性,只包括当前类，不包括父类

            requestJsonFactory.object(object);
            RequestParamsFactory requestParamsFactory = new RequestParamsFactory();
            requestParamsFactory.tableid(tableid).data(requestJsonFactory);
            AsyncClient.post("datamanage/data/create", requestParamsFactory, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        if (response.getInt("status") != 1)
                            onCreateDataListener.fail(response.getString("info"));
                        else {

                            String _id = response.getString("_id");
                            _idmap.put(object, _id);

                            onCreateDataListener.success(_id);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        onCreateDataListener.fail("JSONNOTCONTENT STATUS_id");
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    onCreateDataListener.fail(responseString);
                }
            });

        }
    }

    /**
     * 建立一个表，表名为name，在回调接口OnCreateTableListener中返回创建结果
     *
     * @param name                  表名
     * @param onCreateTableListener 创建的回调接口，成功或者失败
     */
    public void createTable(final String name, final OnCreateTableListener onCreateTableListener) {
        String tableid = tablenamemap.get(name);
        if (tableid != null) {
            onCreateTableListener.success(tableid);
        } else {
            AsyncClient.post("datamanage/table/create", new RequestParamsFactory().name(name), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        if (response.getInt("status") != 1)
                            onCreateTableListener.fail(response.getString("info"));
                        else {
                            /*自动将创建的表名与tableid映射保存到Map*/
                            String tableid = response.getString("tableid");
                            tablenamemap.put(name, tableid);

                            onCreateTableListener.success(tableid);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        onCreateTableListener.fail("JSONNOTCONTENT STATUS");
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    onCreateTableListener.fail(responseString);
                }
            });
        }
    }

    private String UpdateFirstWord(String word) {
        return (word.charAt(0) + "").toUpperCase() + word.substring(1);
    }

    public <T> boolean bindTablebyName(T object, String name) {
        String tableid = tablenamemap.get(name);
        if (tableid == null) {
            Log.d("hh", "表不存在");
            return false;
        }
        if (tableidmap.containsKey(object)) {
            Log.d("hh", "");
            return false;
        }

        tableidmap.put(object, tableid);
        return true;
    }

    /**
     * 根据tableid,_id来获取数据
     *
     * @param tableid table的id
     * @param _id     data的id
     */
    public void getData(String tableid, String _id, final OndatasearchListener ondatasearchListener) {
        onCloudeSearchlistener.setOndatasearchListener(ondatasearchListener);
        mCloudSearch.searchCloudDetailAsyn(tableid, _id);
    }

    /**
     * @param tableid
     * @param latlng
     * @param radius               搜索半径 单位m
     * @param ondatasearchListener
     */
    public void getData(String tableid, LatLng latlng, int radius, final OndatasearchListener ondatasearchListener) {
        onCloudeSearchlistener.setOndatasearchListener(ondatasearchListener);
        CloudSearch.SearchBound bound = new CloudSearch.SearchBound(new LatLonPoint(
                latlng.latitude, latlng.longitude), radius);

        CloudSearch.Query mQuery = null;
        try {
            mQuery = new CloudSearch.Query(tableid, " ", bound);
            mQuery.setPageSize(10);
            CloudSearch.Sortingrules sorting = new CloudSearch.Sortingrules(
                    "_id", false);
            mQuery.setSortingrules(sorting);
            mCloudSearch.searchCloudAsyn(mQuery);// 异步搜索
        } catch (AMapException e) {
            e.printStackTrace();
        }


    }

    public void getData(String tableid, String key, String keyValue, final OndatasearchListener ondatasearchListener) {
        onCloudeSearchlistener.setOndatasearchListener(ondatasearchListener);

        CloudSearch.Query mQuery = null;
        CloudSearch.SearchBound bound = new CloudSearch.SearchBound("全国");
        try {
            mQuery = new CloudSearch.Query(tableid, "", bound);
            mQuery.addFilterString(key, keyValue);
            mQuery.setPageSize(10);
            CloudSearch.Sortingrules sorting = new CloudSearch.Sortingrules(
                    "_id", false);
            mQuery.setSortingrules(sorting);
            mCloudSearch.searchCloudAsyn(mQuery);// 异步搜索
        } catch (AMapException e) {
            e.printStackTrace();
        }


    }

    public void addtableid(String name, String tableid) {
        tablenamemap.put(name, tableid);
    }

    /**
     * 获取对象在服务器储存的表中的_id
     *
     * @param o
     * @return
     */
    public String get_id(Object o) {
        return _idmap.get(o);
    }

    public void updateData(String tableid, final String _id, String updatekey, String updatevalue, @Nullable final OnUpdateDataListener onUpdateDataListener) {
        RequestJsonFactory requestJsonFactory = new RequestJsonFactory()._id(_id).customerValue(updatekey, updatevalue);
        RequestParamsFactory requestParamsFactory = new RequestParamsFactory();
        requestParamsFactory.tableid(tableid).data(requestJsonFactory);
        AsyncClient.post("datamanage/data/update", requestParamsFactory, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.getInt("status") != 1)
                        if (onUpdateDataListener != null) {
                            onUpdateDataListener.fail(response.getString("info"));
                        } else {

                            if (onUpdateDataListener != null) {
                                onUpdateDataListener.success(_id);
                            }
                        }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (onUpdateDataListener != null) {
                        onUpdateDataListener.fail("JSONNOTCONTENT STATUS");
                    }
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                if (onUpdateDataListener != null) {
                    onUpdateDataListener.fail(responseString);
                }
            }
        });
    }

    public <T> void updateData(String tableid, final String _id, T updateObject, @Nullable final OnUpdateDataListener onUpdateDataListener) {
        Gson gson = new Gson();
        String gsonstr = gson.toJson(updateObject);
        updateData(tableid, _id, "gson", gsonstr, onUpdateDataListener);
    }

    public <T> void updateData(T updateObject, @Nullable final OnUpdateDataListener onUpdateDataListener) {
        String tableid = tableidmap.get(updateObject);
        String _id = _idmap.get(updateObject);
        Gson gson = new Gson();
        String gsonstr = gson.toJson(updateObject);
        updateData(tableid, _id, "gson", gsonstr, onUpdateDataListener);
    }

    public void sendPlayerLocation(PlayerManager.Player player) {
        updateData(TABLEID_PLAYER, player.get_id(), "_location", JsonUtil.get_locationstr(player.getLatLng()), null);
    }

    /**
     * 开始定时任务，每隔一段时间就从服务器获取位置来更新player的位置，以及和player绑定的rolesign的位置
     *
     * @param player
     */
    public void startReceivePlayerLocation(final PlayerManager.Player player) {
        final String _id = player.get_id();
        if (_id == null) {
            try {
                throw new Exception("player id is not exist!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Timer timer = playerTimerHashMap.get(player);
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    getData(TABLEID_PLAYER, _id, new OndatasearchListener() {
                        @Override
                        public void success(ArrayList<Object> datas) {

                        }

                        @Override
                        public void success(Object object) {
                            LatLng latlng = ((PlayerManager.Player) object).getLatLng();
                            player.setLatLng(latlng);
                            RoleSign rolesign = SignMarkerManager.getInstance().getBindingRolesignByPlayer(player);
                            if (rolesign == null)
                                try {
                                    throw new Exception("unbind the player with rolesign");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            else
                                rolesign.OnReceiveLocationInfo(latlng);
                        }

                        @Override
                        public void fail(String info) {

                        }
                    });
                }
            };
            if (timer == null) {
                timer = new Timer();
                playerTimerHashMap.put(player, timer);
            } else {
                timer.cancel();
            }
            //开启移动的更新定时器
            timer.schedule(task, 0, UPDATEPLAYERLOCATION_DELAY);
        }
    }

    public void stopReceivePlayerLocation(final PlayerManager.Player player) {
        Timer timer = playerTimerHashMap.get(player);
        if (timer == null) {
            try {
                throw new Exception("timer is not exist");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            timer.cancel();
    }

    /**
     * 在云端建立数据的回调接口
     */
    public interface OnCreateDataListener {
        void success(String _id);

        void fail(String info);
    }

    /**
     * 在云端建立数据的回调接口
     */
    public interface OnUpdateDataListener {
        void success(String _id);

        void fail(String info);
    }

    /**
     * 在云端建立数据表的回调接口
     */
    public interface OnCreateTableListener {
        void success(String tableid);

        void fail(String info);
    }

    public interface OndatasearchListener {
        //返回数组实例
        void success(ArrayList<Object> datas);

        //返回搜索结果的实例
        void success(Object object);

        void fail(String info);
    }


    public interface OnDeleteDataListener {
        void success(String info);

        void fail(String info);
    }

    private class OnCloudeSearchlistener implements CloudSearch.OnCloudSearchListener {
        private OndatasearchListener ondatasearchListener;

        public void setOndatasearchListener(OndatasearchListener ondatasearchListener) {
            this.ondatasearchListener = ondatasearchListener;
        }

        @Override
        public void onCloudSearched(CloudResult cloudResult, int errorCode) {
            if (errorCode == 0 && cloudResult != null) {
                ArrayList<CloudItem> clouditems = cloudResult.getClouds();
                ArrayList<Object> datas = new ArrayList<>();
                if (cloudResult.getTotalCount() == 0) {
                    ondatasearchListener.success(datas);
                } else {
                    String classname = clouditems.get(0).getCustomfield().get("class");
                    try {
                        Class clss = Class.forName(classname);

                        Gson gson = new Gson();
                        for (CloudItem clouditem : clouditems) {
                            Object item = gson.fromJson(clouditem.getCustomfield().get("gson"), clss);
                            _idmap.put(item, clouditem.getID());//将id映射保存到_idmap
                            datas.add(item);
                            ondatasearchListener.success(datas);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                if (ondatasearchListener != null)
                    ondatasearchListener.fail(errorCode + "");
            }
        }

        @Override
        public void onCloudItemDetailSearched(CloudItemDetail cloudItemDetail, int errorCode) {
            if (errorCode == 0 && cloudItemDetail != null) {
                if (ondatasearchListener != null) {
                    Class clss;
                    try {
                        clss = Class.forName(cloudItemDetail.getCustomfield().get("class"));
                        Gson gson = new Gson();
                        Object item = gson.fromJson(cloudItemDetail.getCustomfield().get("gson"), clss);
                        _idmap.put(item, cloudItemDetail.getID());//将id映射保存到_idmap

                        ondatasearchListener.success(item);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            } else {
                if (ondatasearchListener != null)
                    ondatasearchListener.fail(errorCode + "");
            }
        }
    }

}

