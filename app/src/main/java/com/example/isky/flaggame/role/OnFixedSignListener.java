package com.example.isky.flaggame.role;

/**
 * Created by isky on 2016/2/16.
 * 监听不会移动的FixedSign的监听器
 */
public interface OnFixedSignListener extends OnSignListener{
    /**
     * rebirthPoint接下来重生活动
     * @param rebirthPoint
     */
    void OnRebirth(RebirthPoint rebirthPoint);

    /**
     * flag被rolesign占领了
     * @param roleSign
     */
    void OnBeOccupied(RoleSign roleSign,Flag flag);

    /**
     * 地雷mine爆炸了
     * @param mine
     */
    void OnBoom(Mine mine);

    /**
     * 地雷被清扫了
     * @param sapper 扫雷者
     * @param mine 地雷
     */
    void OnSweep(Sapper sapper, Mine mine);
}
