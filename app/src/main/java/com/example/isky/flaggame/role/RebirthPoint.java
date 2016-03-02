package com.example.isky.flaggame.role;

/**
 * Created by isky on 2016/2/10.
 * 复活点标记，可以让某个rolesign复活
 */
public class RebirthPoint extends FixedSign {
    public void rebirth(RoleSign roleSign) {
        if (isRebirthable(roleSign)){
            roleSign.rebirth();
            notifyOnFixedSignOnRebirthListeners();
        }
    }

    /**
     * roleSign是否可以被重生
     *
     * @param roleSign
     * @return
     */
    public boolean isRebirthable(RoleSign roleSign) {
        if (roleSign.isDead == true && roleSign.getTeam() == getTeam()) {
            if (roleSign.isInAttractCircle(this))
                return true;
        }
        return false;
    }

    /**
     * 通知所有的监听器被占领了
     */
    private void notifyOnFixedSignOnRebirthListeners() {
        for (OnSignListener listener : onSignListeners) {
            if (listener instanceof OnFixedSignListener)
                ((OnFixedSignListener) listener).OnRebirth(this);
        }
    }
}
