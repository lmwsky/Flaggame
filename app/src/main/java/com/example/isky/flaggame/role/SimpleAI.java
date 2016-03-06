package com.example.isky.flaggame.role;

import com.amap.api.maps2d.model.LatLng;

import util.RandUtil;

/**
 * Created by isky on 2016/2/7.
 * 简单AI，只会随机移动
 */
public class SimpleAI implements AI {
    private static long UNMOVE = -1;
    private final String roleSignSignature;
    private double race = 0.001;//移动速度 km/s
    private LatLng nowLocation;
    private long lastMoveTime = UNMOVE;//上一次获取位置的时间，尚未获取则为UNMOVE

    public SimpleAI(LatLng latLng, String roleSignSignature) {
        nowLocation = new LatLng(latLng.latitude, latLng.longitude);
        this.roleSignSignature = roleSignSignature;
    }

    @Override
    public LatLng getNextLocation() {
        if (lastMoveTime == UNMOVE)
            lastMoveTime = System.currentTimeMillis();
        else {
            double detatime = (System.currentTimeMillis() - lastMoveTime) / 1000;//转化间隔时间为以秒为单位
            double distance = detatime * race;//移动距离，单位km
            nowLocation = RandUtil.randPointerOnCircle(nowLocation, distance);
        }
        Sign roleSign = SignManager.getInstance().getSignBySignature(roleSignSignature);
        if (roleSign != null)
            /*移动完尝试攻击*/
            ((RoleSign) roleSign).attack(SignManager.getInstance().getOtherTeamRoleSign(roleSign.getTeam()));
        return nowLocation;
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
