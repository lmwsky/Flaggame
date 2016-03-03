package com.example.isky.flaggame.role;

import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.example.isky.flaggame.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import util.MapUtil;

/**
 * Created by isky on 2016/2/6.
 * 游戏中的各个游戏角色，能够在地图上显示，有侦查范围攻击范围，能够移动，能够设置AI
 */
public class RoleSign extends Sign implements LocationInfoReceiver {
    public static BitmapDescriptor mainplayerBitmapLive = BitmapDescriptorFactory.fromResource(R.drawable.location_marker);
    public static BitmapDescriptor mainplayerBitmapDie = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
    private static long MOVEDELAY = 100;//更新位置的间隔，单位毫秒,可以看做每多少ms一帧动画
    private final Timer timer = new Timer();
    //侦查范围
    protected double dist_investigate = 0.0;
    //攻击范围
    protected double dist_attract = 0.0;
    //得分情况
    protected int score = 0;
    //是否死亡
    protected boolean isDead = false;
    protected AI ai;
    private TimerTask task;


    public RoleSign() {
        //默认队伍为0
        team = 0;
    }

    public RoleSign(int team) {
        this.team = team;
    }

    /**
     * 更新当前RoleSign的位置，具体更新方法由子类实现
     * 其中对于RoleSign的坐标latlng的修改只能通过调用setLatlng()方法
     */
    public void startmove() {
        if (isDead == true)
            return;
        //初始化任务
        if (task == null) {
            ai.startwork();
            task = new TimerTask() {
                @Override
                public void run() {
                    //如果由AI控制，则利用ai更新位置
                    if (ai != null)
                        setLatLng(ai.getNextLocation());
                }
            };
            timer.schedule(task, 0, MOVEDELAY);
        } else {
            timer.cancel();
            //开启移动的更新定时器
            timer.schedule(task, 0, MOVEDELAY);
        }

    }

    /**
     * 暂停移动
     */
    public void stopmoving() {

        //暂停定时器
        timer.cancel();
        ai.stopwork();

    }


    /**
     * 让当前角色死亡,并且触发死亡监听器,只有条件符合才会执行
     */
    public void die() {
        if (isDead == false) {
            isDead = true;
            notifyOnRoleSignDieListeners();
        }
    }

    /**
     * 当前角色重生，并且触发重生监听器，只有条件符合才会执行
     */
    public void rebirth() {
        if (isDead == true) {
            isDead = false;
            notifyOnRoleSignRebirthListeners();
        }
    }

    /**
     * 角色的技能
     */
    public void skill(ArrayList<Sign> signList) {
    }

    public void skill() {
    }

    /**
     * 尝试对对象进行, 攻击成功条件：双方都未死亡且被攻击方在攻击方的攻击范围内,且队伍不同
     *
     * @param roleSign 尝试攻击的对象
     */
    public void attack(RoleSign roleSign) {
        if (!isDead) {
            if (isAttrackable(roleSign))
                roleSign.beattack(this);
        }
    }

    /**
     * 尝试对roleList列表里面的对象进行攻击, 攻击成功条件：双方都未死亡且被攻击方在攻击方的攻击范围内,且队伍不同
     *
     * @param roleList 尝试攻击的对象的列
     */
    public void attack(ArrayList<RoleSign> roleList) {
        if (!isDead) {
            for (RoleSign roleSign : roleList) {
                if (isAttrackable(roleSign))
                    roleSign.beattack(this);
            }
        }
    }


    /**
     * 从侦查列表中选择能够被侦查的进行返回
     *
     * @param signArrayList 需要判断能否被侦查的对象的列表
     * @return 能够被侦查的对象
     */
    public ArrayList<Sign> investigate(ArrayList<Sign> signArrayList) {
        ArrayList<Sign> resultlist = new ArrayList<>();
        for (Sign sign : signArrayList)
            if (isInvestigate(sign))
                resultlist.add(sign);


        return resultlist;
    }

    /**
     * 被rolesign攻击
     *
     * @param roleSign 攻击来源的对象
     */
    public void beattack(RoleSign roleSign) {
        notifyOnRoleSignBeattractListeners(roleSign, this);
    }

    /**
     * 占领其他方的旗帜，要求flag在攻击范围内
     *
     * @param flag 被占领的旗帜
     */
    public void occupy(Flag flag) {
        if (flag.getTeam() != getTeam() && isDead == false && isInAttractCircle(flag))
            flag.beOccupied(this);
    }

    /**
     * sign是否可以被攻击，条件队伍不同，在攻击范围内
     *
     * @param sign 被攻击方
     * @return
     */
    public boolean isAttrackable(Sign sign) {
        if (sign instanceof RoleSign && sign.getTeam() != getTeam() && ((RoleSign) sign).isDead() == false) {
            return isInAttractCircle(sign);

        }
        return false;
    }

    /**
     * sign是否在攻击圈内
     *
     * @param sign
     * @return
     */
    public boolean isInAttractCircle(Sign sign) {
        double distance = MapUtil.getDistance(this, sign);
        if (distance < this.getDist_attract())
            return true;
        return false;
    }

    /**
     * 是否可以被侦查到
     *
     * @param sign 被侦查方
     * @return
     */
    public boolean isInvestigate(Sign sign) {
        if (sign.getTeam() != getTeam()) {
            double distance = MapUtil.getDistance(this, sign);
            if (distance < this.getDist_investigate())
                return true;
        }
        return false;
    }

    /**
     * 通知所有的监听器死亡了
     */
    private void notifyOnRoleSignDieListeners() {
        for (OnSignListener listener : onSignListeners) {
            if (listener instanceof OnRoleSignListener)
                ((OnRoleSignListener) listener).onDielistener(this);
        }
    }

    /**
     * 通知所有的监听器死亡了
     */
    private void notifyOnRoleSignRebirthListeners() {
        for (OnSignListener listener : onSignListeners) {
            if (listener instanceof OnRoleSignListener)
                ((OnRoleSignListener) listener).onRebirthlistener(this);
        }
    }

    /**
     * 通知所有监听器当前rolesign被攻击
     *
     * @param attracker  攻击者
     * @param beatracker 被攻击者
     */
    private void notifyOnRoleSignBeattractListeners(RoleSign attracker, RoleSign beatracker) {
        for (OnSignListener listener : onSignListeners) {
            if (listener instanceof OnRoleSignListener)
                ((OnRoleSignListener) listener).onBeAttractedlistener(attracker, beatracker);
        }
    }

    /**
     * 获取侦查范围
     *
     * @return
     */
    public double getDist_investigate() {
        return dist_investigate;
    }

    public void setDist_investigate(double dist_investigate) {
        this.dist_investigate = dist_investigate;
    }

    /**
     * 获取攻击范围
     *
     * @return
     */
    public double getDist_attract() {
        return dist_attract;
    }

    public void setDist_attract(double dist_attract) {
        this.dist_attract = dist_attract;
    }

    public AI getAi() {
        return ai;
    }

    public void setAi(AI ai) {
        this.ai = ai;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isDead() {
        return isDead;
    }

    @Override
    public void OnReceiveLocationInfo(LatLng latLng) {
        setLatLng(latLng);
    }

    /**
     * 尝试占领列表里面的旗帜
     *
     * @param otherTeamFlag
     */
    public void occupy(ArrayList<Flag> otherTeamFlag) {
        for (Flag flag : otherTeamFlag)
            occupy(flag);
    }
}