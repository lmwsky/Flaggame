package com.example.isky.flaggame.role;

import com.amap.api.maps2d.model.LatLng;
import com.example.isky.flaggame.game.GameConfig;

import util.MapUtil;

/**
 * Created by Administrator on 2016/2/10.
 * 地雷 固定标记
 */
public class Mine extends FixedSign {
    private double influence;
    private String minerSignature;

    public Mine() {
        setInfluence(GameConfig.DIST_MINE_INFLUENCE);
        setLatLng(new LatLng(0, 0));
        setIcon(GameConfig.BITMAP_MINE);

    }

    public String getMinerSignature() {
        return minerSignature;
    }

    public void setMinerSignature(String minerSignature) {
        this.minerSignature = minerSignature;
    }

    /**
     * 获取地雷的影响半径
     *
     * @return
     */
    public double getInfluence() {
        return influence;
    }

    public void setInfluence(double influence) {
        this.influence = influence;
    }

    /**
     * 地雷爆炸
     */
    public void boom() {
        notifyOnFixedSignOnBoomListeners();
    }

    /**
     * 当前地雷被扫除
     *
     * @param sapper 扫雷者
     */
    public void sweep(Sapper sapper) {
        notifyOnFixedSignOnSweepListeners(sapper);
    }

    /**
     * 通知所有的监听器地雷爆炸了
     */
    private void notifyOnFixedSignOnBoomListeners() {
        for (OnSignListener listener : onSignListeners) {
            if (listener instanceof OnFixedSignListener)
                ((OnFixedSignListener) listener).OnBoom(this);
        }
    }

    /**
     * 通知所有的监听器当前地雷被扫除
     */
    private void notifyOnFixedSignOnSweepListeners(Sapper sapper) {
        for (OnSignListener listener : onSignListeners) {
            if (listener instanceof OnFixedSignListener)
                ((OnFixedSignListener) listener).OnSweep(sapper, this);
        }
    }

    /**
     * 某地点是否在影响范围内
     *
     * @param latLng
     * @return
     */
    public boolean isinfluence(LatLng latLng) {
        if (MapUtil.getDistance(latLng, getLatLng()) < influence)
            return true;
        else
            return false;
    }
}
