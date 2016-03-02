package com.example.isky.flaggame.role;

import com.amap.api.maps2d.model.LatLng;

/**
 * Created by isky on 2016/1/28.
 * 旗帜,能够被占领
 */
public class Flag extends FixedSign {
    private boolean isOccupied = false;

    public boolean isOccupied() {
        return isOccupied;
    }

    public Flag(LatLng latLng) {
        setLatLng(latLng.latitude, latLng.longitude);
    }

    /**
     * 被某个RoleSign占领
     *
     * @param roleSign 占领者
     */
    public void beOccupied(RoleSign roleSign) {
        if (isOccupied == false) {
            isOccupied = true;
            notifyOnFixedSignOnbeOccupiedListeners(roleSign, this);
        }
    }

    /**
     * 通知所有的监听器被占领了
     */
    private void notifyOnFixedSignOnbeOccupiedListeners(RoleSign roleSign, Flag flag) {
        for (OnSignListener listener : onSignListeners) {
            if (listener instanceof OnFixedSignListener)
                ((OnFixedSignListener) listener).OnBeOccupied(roleSign, flag);
        }
    }

}
