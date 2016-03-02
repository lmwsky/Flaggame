package com.example.isky.flaggame.role;

import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.LatLng;

/**
 * Created by isky on 2016/2/1.
 * 对于sign的子类rolesign实现的接口，
 */
public interface OnRoleSignListener extends OnSignListener {


    /**
     * rolesign的死亡监听器,如果rolesign进入死亡状态则调用这个方法
     * @param roleSign
     */
    void onDielistener(RoleSign roleSign);

    /**
     * rolesign的复活监听器，如果rolesign复活则进行调用
     * @param roleSign
     */
    void onRebirthlistener(RoleSign roleSign);

    /**
     * rolesign的被攻击监听器,如果rolesign被攻击则进行调用
     * @param attracker
     * @param roleSign
     */
    void onBeAttractedlistener(RoleSign attracker, RoleSign roleSign);
}
