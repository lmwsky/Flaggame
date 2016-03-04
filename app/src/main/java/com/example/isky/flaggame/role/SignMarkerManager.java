package com.example.isky.flaggame.role;

import android.graphics.Color;
import android.location.Location;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.example.isky.flaggame.server.BindwithServer;
import com.example.isky.flaggame.server.LocationServiceManager;
import com.example.isky.flaggame.server.PlayerManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by isky on 2016/2/15.
 * 所有游戏中sign的管理者，和Maeker的管理者,可以通过方法来获得不同的sign和Marker
 * sign游戏中的标记的数据抽象，Marker地图上的图标的抽象
 */
public class SignMarkerManager {

    private static SignMarkerManager signMarkerManager;
    private Map<PlayerManager.Player, RoleSign> playerRoleSignHashMap = new HashMap<>();
    private AMap aMap;
    private ArrayList<Sign> allsignlist = new ArrayList<>();
    private ArrayList<RoleSign> allrolesignlist = new ArrayList<>();

    private ArrayList<Flag> flagArrayList = new ArrayList<>();
    private ArrayList<Mine> mineArrayList = new ArrayList<>();
    private ArrayList<Monster> monsterArrayList = new ArrayList<>();

    private ArrayList<RebirthPoint> rebirthpointArrayList = new ArrayList<>();
    private Map<Sign, Marker> signMarkerMap = new HashMap<>();//将游戏中的角色与地图上的marker进行一一映射的Map
    private Map<Sign, Circle> signCircleMap = new HashMap<>();//将游戏中的角色与地图上的Circle进行一一映射的Map

    private RoleSign mainplayer;
    private MyLocationStyle myLocationStyle;

    public static SignMarkerManager getInstance() {
        if (signMarkerManager == null)
            signMarkerManager = new SignMarkerManager();
        return signMarkerManager;
    }

    public AMap getaMap() {
        return aMap;
    }

    public void setaMap(AMap aMap) {
        this.aMap = aMap;
    }


    public RoleSign getMainPlayer() {
        return mainplayer;
    }

    public ArrayList<Monster> getAllMonsters() {
        return monsterArrayList;
    }

    public void clear() {
        while (allrolesignlist.isEmpty() == false) {
            remove(allsignlist.get(0));
        }
        signMarkerManager = new SignMarkerManager();
    }

    public ArrayList<Mine> getMines() {
        return mineArrayList;
    }

    /**
     * 将sign加入sign的管理
     *
     * @param sign
     */
    public void add(Sign sign) {
        if (sign == null)
            return;
        /*不重复添加*/
        if (getSignByTempID(sign.getTempID()) != null)
            return;

        allsignlist.add(sign);
        if (sign instanceof RoleSign) {
            allrolesignlist.add((RoleSign) sign);

            if (sign instanceof Monster)
                monsterArrayList.add((Monster) sign);


        }
        if (sign instanceof FixedSign) {
            if (sign instanceof Flag)
                flagArrayList.add((Flag) sign);
            if (sign instanceof RebirthPoint)
                rebirthpointArrayList.add((RebirthPoint) sign);
            if (sign instanceof Mine)
                mineArrayList.add((Mine) sign);
        }
    }

    /**
     * 将sign移除管理，并且移除对应的地图Marker
     *
     * @param sign
     */
    public void remove(Sign sign) {
        if (sign == null)
            return;
        allsignlist.remove(sign);
        Marker marker = signMarkerMap.remove(sign);
        if (marker != null)
            marker.remove();
        Circle circle = signCircleMap.remove(sign);
        if (circle != null)
            circle.remove();

        if (sign instanceof RoleSign) {
            allrolesignlist.remove(sign);
            if (sign instanceof Monster)
                monsterArrayList.remove(sign);
        }
        if (sign instanceof FixedSign) {

            if (sign instanceof Flag)
                flagArrayList.remove(sign);

            if (sign instanceof RebirthPoint)
                rebirthpointArrayList.remove(sign);
            if (sign instanceof Mine)
                mineArrayList.remove((Mine) sign);
        }
    }


    /**
     * 将sign与Marker进行绑定,若已经有绑定则接触旧绑定
     *
     * @param sign
     * @param marker
     */
    public void bindSignAndMarker(Sign sign, Marker marker) {
        if (signMarkerMap.containsKey(sign) == true)
            signMarkerMap.remove(sign);
        signMarkerMap.put(sign, marker);
    }

    /**
     * 将sign与其攻击圈的Marker进行绑定,若已经有绑定则接触旧绑定
     *
     * @param sign
     * @param circle
     */
    public void bindSignAndMarker(RoleSign sign, Circle circle) {
        if (signCircleMap.containsKey(sign) == true)
            signCircleMap.remove(sign);
        signCircleMap.put(sign, circle);
    }

    public Marker getMarker(Sign sign) {
        return signMarkerMap.get(sign);
    }

    public Circle getCircle(Sign sign) {
        return signCircleMap.get(sign);
    }

    public ArrayList<Flag> getFlag(int team) {
        ArrayList<Flag> flags = new ArrayList<>();
        for (Flag flag : flagArrayList)
            if (flag.getTeam() == team)
                flags.add(flag);
        return flags;
    }

    public ArrayList<Flag> getOtherTeamFlag(int team) {
        ArrayList<Flag> flags = new ArrayList<>();
        for (Flag flag : flagArrayList)
            if (flag.getTeam() != team)
                flags.add(flag);
        return flags;
    }

    /**
     * 获取其他队的地雷
     *
     * @param team
     * @return
     */
    public ArrayList<Mine> getOtherTeamMine(int team) {
        ArrayList<Mine> mines = new ArrayList<>();
        for (Mine mine : mineArrayList)
            if (mine.getTeam() != team)
                mines.add(mine);
        return mines;
    }

    public ArrayList<RebirthPoint> getRebirthPoint(int team) {
        ArrayList<RebirthPoint> rebirthPoints = new ArrayList<>();
        for (RebirthPoint rebirthPoint : rebirthpointArrayList)
            if (rebirthPoint.getTeam() == team)
                rebirthPoints.add(rebirthPoint);
        return rebirthPoints;
    }

    public void setMainplayer(RoleSign sign) {
        this.mainplayer = sign;
    }

    /**
     * 隐藏所有的攻击圈
     */
    public void hideAttractCircle() {
        for (Circle circle : signCircleMap.values())
            circle.setVisible(false);
    }


    /**
     * 隐藏或显示某个sign，包括其攻击圈
     *
     * @param sign
     * @param state true 显示，false 隐藏
     */
    public void hideOrshowSignAndCiecle(Sign sign, boolean state) {
        Marker marker = getMarker(sign);
        if (marker != null)
            marker.setVisible(state);
        Circle circle = getCircle(sign);
        if (circle != null)
            circle.setVisible(state);
    }

    /**
     * 隐藏所有的攻击圈
     */
    public void showAttractCircle() {
        for (Circle circle : signCircleMap.values())
            circle.setVisible(true);
    }

    /**
     * 返回除了某个队伍之外的所有rolesign角色
     *
     * @param team 被排除的队伍标记
     * @return 角色的结合
     */
    public ArrayList<RoleSign> getOtherTeamRoleSign(int team) {
        ArrayList<RoleSign> otherTeam = new ArrayList<>();
        for (RoleSign sign : allrolesignlist) {
            if (sign.getTeam() != team)
                otherTeam.add(sign);
        }
        return otherTeam;
    }

    public ArrayList<RoleSign> getOwnTeamRoleSign(int team) {
        ArrayList<RoleSign> ownTeam = new ArrayList<>();
        for (RoleSign sign : allrolesignlist) {
            if (sign.getTeam() == team) {
                ownTeam.add(sign);
            }
        }
        return ownTeam;
    }
    /**
     * 返回mainplayer的队伍是否获胜了,只有全部其他方的旗帜占领才算胜利
     *
     * @return
     */
    public boolean isWin() {
        if (mainplayer == null)
            return false;
        return isWin(mainplayer.getTeam());
    }

    /**
     * 根据Sign的tempID查询
     *
     * @param tempID
     * @return
     */
    public Sign getSignByTempID(int tempID) {
        for (Sign sign : allsignlist)
            if (sign.getTempID() == tempID)
                return sign;
        return null;
    }

    public boolean isWin(int team) {
        ArrayList<Flag> flags = getOtherTeamFlag(team);
        for (Flag flag : flags)
            if (flag.isOccupied() == false)
                return false;
        return true;
    }

    /**
     * 返回mainplayer的队伍是否输了,只有己方旗帜都被占领了才算输
     *
     * @return
     */
    public boolean isLose() {
        if (mainplayer == null)
            return false;
        boolean iswin = true;
        ArrayList<Flag> flags = getFlag(mainplayer.getTeam());
        for (Flag flag : flags)
            if (flag.isOccupied() == false)
                return false;
        return true;
    }


    public void remove(int anInt) {
        remove(getSignByTempID(anInt));
    }

    /**
     * 将sign的marker，circle移动到与sign相应的位置
     *
     * @param sign
     */
    public void move(Sign sign) {
        Marker marker = SignMarkerManager.getInstance().getMarker(sign);
        if (marker != null)
            marker.setPosition(sign.getLatLng());
        Circle circle = SignMarkerManager.getInstance().getCircle(sign);
        if (circle != null)
            circle.setCenter(sign.getLatLng());
    }

    /**
     * 负责将一个Sign的对应的Marker添加到地图上，若是能够进行攻击的则把攻击的范围也加到地图上
     * 并建立Sign与Marker的绑定,若被添加到地图上，自动加入SignMarkerManager的管理
     *
     * @param sign Marker对应的sign
     */
    public void addSignToMap(Sign sign) {
        if (aMap == null) {
            try {
                throw new Exception("no map set to SignMarkerManager");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            add(sign);
            MarkerOptions markerOptions = MarkerOptionsFactory.produceBySign(sign);
            Marker marker = aMap.addMarker(markerOptions);
            bindSignAndMarker(sign, marker);

            if (sign instanceof RoleSign) {
                CircleOptions circleOptions = MarkerOptionsFactory.produceAttackCircleBySign((RoleSign) sign);
                Circle circle = aMap.addCircle(circleOptions);
                bindSignAndMarker((RoleSign) sign, circle);
            }
        }
    }

    /**
     * 将主玩家的标记加入地图,以及其攻击圈
     *
     * @param sign 主玩家的sign
     */
    public void addMainPlayerToMap(RoleSign sign) {
        if (aMap == null) {
            try {
                throw new Exception("no map set to SignMarkerManager");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            add(sign);
            setMainplayer(sign);


            CircleOptions circleOptions = MarkerOptionsFactory.produceAttackCircleBySign(sign);
            Circle circle = aMap.addCircle(circleOptions);
            bindSignAndMarker(sign, circle);
        }
    }

    /**
     * 初始化地图，地图上有定位
     *
     * @param aMap
     */
    public void initMap(AMap aMap) {
        this.aMap = aMap;
        initMap();
    }

    public void changeMainplayerIcon(BitmapDescriptor bitmapDescriptor) {
        myLocationStyle = myLocationStyle.myLocationIcon(bitmapDescriptor);
        aMap.setMyLocationStyle(myLocationStyle);
    }

    public void initMap() {
        // 自定义系统定位小蓝点
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(RoleSign.mainplayerBitmapLive);// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.TRANSPARENT);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.TRANSPARENT);// 设置圆形的填充颜色
        myLocationStyle.strokeWidth(0.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(LocationServiceManager.getInstance());// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
    }

    /**
     * 让role的坐标与player的保持一致
     *
     * @param roleSign
     * @param player
     */
    public void bindRoleSignWithLocationReciever(RoleSign roleSign, PlayerManager.Player player) {
        playerRoleSignHashMap.put(player, roleSign);
    }

    /**
     * 将主玩家角色的位置与player进行绑定，每当主玩家角色的位置改变，上传到服务器
     * @param player
     */
    public void bindMainplayerAsLocationSender(final PlayerManager.Player player) {
        if (mainplayer == null) {
            try {
                throw new Exception("Mainplayer is not exist");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            LocationSource.OnLocationChangedListener mainplayerlocationsenderlistener= new LocationSource.OnLocationChangedListener(){
                @Override
                public void onLocationChanged(Location location) {
                    player.setLatLng(new LatLng(location.getLatitude(),location.getLongitude()));
                    BindwithServer.getInstance().sendPlayerLocation(player);
                }
            };
            LocationServiceManager.getInstance().registerOnLocationchangelistener(mainplayerlocationsenderlistener);

        }
    }

    public RoleSign getBindingRolesignByPlayer(PlayerManager.Player player) {
        return playerRoleSignHashMap.get(player);
    }
}
