package com.example.isky.flaggame.role;

import com.amap.api.maps2d.model.LatLng;
import com.example.isky.flaggame.game.GameConfig;

/**
 * Created by isky on 2016/1/28.
 * 旗帜,能够被占领
 */
public class Flag extends FixedSign {
    private boolean isOccupied = false;

    public Flag() {
        setLatLng(0, 0);
        setIcon(GameConfig.BITMAP_FLAG);

    }

    public Flag(LatLng latLng) {
        setLatLng(latLng.latitude, latLng.longitude);
        setIcon(GameConfig.BITMAP_FLAG);

    }

    public boolean isOccupied() {
        return isOccupied;
    }

    /**
     * 若尚未占领，被占领，并且通知所有的监听器
     *
     * @param roleSign 占领者
     */
    public void beOccupied(RoleSign roleSign) {
        if (!isOccupied) {
            isOccupied = true;
            notifyOnFixedSignOnbeOccupiedListeners(roleSign, this);
        }
    }

    /**
     * 通知所有的监听器被占领了
     *
     * @param roleSign 占领者
     * @param flag     被占领的旗帜
     */
    private void notifyOnFixedSignOnbeOccupiedListeners(RoleSign roleSign, Flag flag) {
        for (OnSignListener listener : onSignListeners) {
            if (listener instanceof OnFixedSignListener)
                ((OnFixedSignListener) listener).OnBeOccupied(roleSign, flag);
        }
    }

}
