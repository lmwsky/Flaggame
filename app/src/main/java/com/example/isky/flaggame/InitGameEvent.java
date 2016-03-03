package com.example.isky.flaggame;

import com.example.isky.flaggame.role.Flag;
import com.example.isky.flaggame.role.RoleSign;

import java.util.ArrayList;

/**
 * Created by isky on 2016/2/29.
 *
 */
public class InitGameEvent {
    private ArrayList<RoleSign> roleSigns;
    private ArrayList<Flag> flags;

    public ArrayList<RoleSign> getRoleSigns() {
        return roleSigns;
    }
    public void setRoleSigns(ArrayList<RoleSign> roleSigns) {
        this.roleSigns = roleSigns;
    }

    public ArrayList<Flag> getFlags() {
        return flags;
    }

    public void setFlags(ArrayList<Flag> flags) {
        this.flags = flags;
    }
}
