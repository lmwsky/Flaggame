package com.example.isky.flaggame.role;

import android.util.Log;

import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.WalkStep;

import java.util.List;

import util.MapUtil;
import util.RandUtil;

/**
 * Created by isky on 2016/2/7.
 * 简单AI，只会随机移动
 */
public class WalkPathAI implements AI {
    private static long UNMOVE = -1;
    private final String roleSignSignature;
    private double race = 0.05;//移动速度 km/s
    private LatLng nowLocation;
    private long lastMoveTime = UNMOVE;//上一次获取位置的时间，尚未获取则为UNMOVE
    private List<WalkStep> walkStepList = null;
    private LatLng goal;
    private int nowstep_index;
    private int num_stepnum = 0;

    public WalkPathAI(LatLng latLng, List<WalkStep> walkStepList, String roleSignSignature) {
        nowLocation = new LatLng(latLng.latitude, latLng.longitude);
        this.walkStepList = walkStepList;
        this.roleSignSignature = roleSignSignature;
        num_stepnum = walkStepList.size();
        getGoal();
    }

    @Override
    public LatLng getNextLocation() {
        if (lastMoveTime == UNMOVE)
            lastMoveTime = System.currentTimeMillis();
        else {
            double detatime = (System.currentTimeMillis() - lastMoveTime) / 1000;//转化间隔时间为以秒为单位
            double distance = detatime * race;//移动距离，单位km

            double tempLength = MapUtil.getDistance(nowLocation, goal);
            if (tempLength < 5.0) {
                //Todo num_stepnum is could be 0
                if (nowstep_index == 0) {
                    goal = new LatLng(walkStepList.get(1).getPolyline().get(0).getLatitude(), walkStepList.get(1).getPolyline().get(0).getLongitude());
                    nowstep_index = 1;
                } else if (nowstep_index == -1) {
                    goal = new LatLng(walkStepList.get(walkStepList.size() - 2).getPolyline().get(0).getLatitude(), walkStepList.get(walkStepList.size() - 2).getPolyline().get(0).getLongitude());
                    nowstep_index = walkStepList.size() - 2;
                } else {
                    double random = Math.random();
                    if (random >= 0.5) {
                        goal = new LatLng(walkStepList.get(nowstep_index + 1).getPolyline().get(0).getLatitude(), walkStepList.get(nowstep_index + 1).getPolyline().get(0).getLongitude());
                        nowstep_index = nowstep_index + 1;
                    } else {
                        goal = new LatLng(walkStepList.get(nowstep_index - 1).getPolyline().get(0).getLatitude(), walkStepList.get(nowstep_index - 1).getPolyline().get(0).getLongitude());
                        nowstep_index = nowstep_index - 1;
                    }
                }
            }
            nowLocation = RandUtil.moveToGoal(nowLocation, goal, distance);


            Sign roleSign = SignManager.getInstance().getSignBySignature(roleSignSignature);
            if (roleSign != null)
                ((RoleSign) roleSign).attack(SignManager.getInstance().getOtherTeamRoleSign(roleSign.getTeam()));


        }

        return nowLocation;
    }

    private void getGoal() {
        String index = calculateShortestPoint(walkStepList, nowLocation);
        nowstep_index = Integer.parseInt(String.valueOf(index.charAt(0)));
        goal = new LatLng(walkStepList.get(nowstep_index).getPolyline().get(0).getLatitude(), walkStepList.get(nowstep_index).getPolyline().get(0).getLongitude());
    }


    private String calculateShortestPoint(List<WalkStep> walkStepList, LatLng latlng) {
        double shortest = Double.MAX_VALUE;
        List<LatLonPoint> latLonPointList;
        LatLng temp;
        double distance;
        String index = "";
        Log.i("xwk", "walkstepList size:" + walkStepList.size());
        for (int i = 0; i < walkStepList.size(); i++) {
            latLonPointList = walkStepList.get(i).getPolyline();
            //    Log.nowstep_index("xwk", "latLonPointList size:" + latLonPointList.size());
            //  for(int j = 0; j < latLonPointList.size(); j++){
            temp = new LatLng(latLonPointList.get(0).getLatitude(), latLonPointList.get(0).getLongitude());
            distance = MapUtil.getDistance(latlng, temp);
            if (distance <= shortest) {
                shortest = distance;
                //      index = nowstep_index + "" + j;
                index = "" + i;
            }
            //     }
        }
        return index;
    }


    @Override
    public void startwork() {
        lastMoveTime = System.currentTimeMillis();
    }

    @Override
    public void stopwork() {
        lastMoveTime = UNMOVE;
    }
}
