package com.example.isky.flaggame.role;

import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.WalkStep;

import java.util.List;

import util.MapUtil;
import util.RandUtil;

/**
 * Created by wenkai on 2016/2/7.
 * 沿着给定的路径进行移动
 */
public class WalkPathAI implements AI {
    /**
     * 单位m，AI与目标点小于这个距离，就发生转向
     */
    public static final double DIST_TURNDIRECTION = 5.0;

    public static final int POSITIVEDIRECTION = 1;
    private static final int NEGATIVEDIRECTION = -1;
    private static long UNMOVE = -1;
    private final String roleSignSignature;
    private final double PROBABILITY_CHANGEDRECTION = 0.4;//转向概率
    private double race;//移动速度 km/s

    private LatLng nowLocation;
    private long lastMoveTime = UNMOVE;//上一次获取位置的时间，尚未获取则为UNMOVE
    private List<WalkStep> walkStepList = null;
    private LatLng goal;//目前移动的目标点
    private int goal_indexinwalksteplist;
    private int goal_indexinPolyLine;
    private int num_stepnum = 0;
    private int direction = POSITIVEDIRECTION;

    /**
     * 构造一个会沿着道路移动的AI
     *
     * @param latLng            起始坐标
     * @param walkStepList      沿着走的路径的标志
     * @param roleSignSignature 控制的rolesign的签名
     * @param race              移动速度，单位km/s
     */
    public WalkPathAI(LatLng latLng, List<WalkStep> walkStepList, String roleSignSignature, double race) {
        nowLocation = new LatLng(latLng.latitude, latLng.longitude);
        this.race = race;
        this.walkStepList = walkStepList;
        this.roleSignSignature = roleSignSignature;
        num_stepnum = walkStepList.size();
        ComputeInitGoal();
    }

    @Override
    public LatLng getNextLocation() {
        if (lastMoveTime == UNMOVE)
            lastMoveTime = System.currentTimeMillis();
        else {
            double detatime = (System.currentTimeMillis() - lastMoveTime) / 1000;//转化间隔时间为以秒为单位
            double distance = detatime * race;//移动距离，单位km

            double tempLength = MapUtil.getDistance(nowLocation, goal);
            if (tempLength < DIST_TURNDIRECTION) {

                boolean isOut = false;

                double random = Math.random();
                if (random < PROBABILITY_CHANGEDRECTION) {
                    direction = direction * -1;
                }
                if (goal_indexinPolyLine == 0 && goal_indexinwalksteplist == 0) {
                    direction = POSITIVEDIRECTION;
                    isOut = true;
                }
                if (goal_indexinwalksteplist == num_stepnum - 1 && goal_indexinPolyLine == walkStepList.get(goal_indexinwalksteplist).getPolyline().size() - 1) {
                    direction = NEGATIVEDIRECTION;
                    isOut = true;
                }
                goal_indexinPolyLine += direction;

                if (!isOut) {
                    //到下一段路
                    if (goal_indexinPolyLine >= walkStepList.get(goal_indexinwalksteplist).getPolyline().size()) {
                        goal_indexinPolyLine = 0;
                        goal_indexinwalksteplist++;
                    }
                    //到上一段路
                    if (goal_indexinPolyLine == -1) {
                        goal_indexinwalksteplist--;
                        goal_indexinPolyLine = walkStepList.get(goal_indexinwalksteplist).getPolyline().size() - 1;
                    }
                }
                goal = new LatLng(walkStepList.get(goal_indexinwalksteplist).getPolyline().get(goal_indexinPolyLine).getLatitude(),
                        walkStepList.get(goal_indexinwalksteplist).getPolyline().get(goal_indexinPolyLine).getLongitude());
            }

            nowLocation = RandUtil.moveToGoal(nowLocation, goal, distance);

              /*移动完尝试攻击*/
            Sign roleSign = SignManager.getInstance().getSignBySignature(roleSignSignature);
            if (roleSign != null)
                ((RoleSign) roleSign).attack(SignManager.getInstance().getOtherTeamRoleSign(roleSign.getTeam()));
        }
        return nowLocation;
    }

    /**
     * 设定初始目标是走到最近的节点上
     */
    private void ComputeInitGoal() {
        goal_indexinwalksteplist = calculateShortestPoint(walkStepList, nowLocation);
        goal_indexinPolyLine = 0;
        goal = new LatLng(walkStepList.get(goal_indexinwalksteplist).getPolyline().get(goal_indexinPolyLine).getLatitude(), walkStepList.get(goal_indexinwalksteplist).getPolyline().get(goal_indexinPolyLine).getLongitude());
        if (Math.random() > 0.5)
            direction = POSITIVEDIRECTION;
        else
            direction = NEGATIVEDIRECTION;
    }


    private int calculateShortestPoint(List<WalkStep> walkStepList, LatLng latlng) {
        double shortest = Double.MAX_VALUE;
        List<LatLonPoint> latLonPointList;
        LatLng temp;
        double distance;
        int index = -1;
        for (int i = 0; i < walkStepList.size(); i++) {
            latLonPointList = walkStepList.get(i).getPolyline();
            temp = new LatLng(latLonPointList.get(0).getLatitude(), latLonPointList.get(0).getLongitude());
            distance = MapUtil.getDistance(latlng, temp);
            if (distance <= shortest) {
                shortest = distance;
                index = i;
            }
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
