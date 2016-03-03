package util;

import com.example.isky.flaggame.role.Mine;
import com.example.isky.flaggame.role.Miner;
import com.example.isky.flaggame.role.Monster;
import com.example.isky.flaggame.role.RoleSign;
import com.example.isky.flaggame.role.Sapper;
import com.example.isky.flaggame.role.Scout;
import com.example.isky.flaggame.role.SignMarkerManager;

import java.util.ArrayList;

/**
 * Created by x1832 on 2016/2/29.
 */
public class GetScore {
    public static void getAttackScore(RoleSign attacker, RoleSign beattacker, int score) {
        if (beattacker instanceof Monster) {
            score += 10;
            attacker.setScore(score);
        } else if (beattacker instanceof Miner) {
            score += 15;
            attacker.setScore(score);
        } else if (beattacker instanceof Sapper) {
            score += 20;
            attacker.setScore(score);
        } else if (beattacker instanceof Scout) {
            score += 25;
            attacker.setScore(score);
        }
    }

    public static void getFlagScore(int team) {
        ArrayList<RoleSign> ownTeam = SignMarkerManager.getInstance().getOwnTeamRoleSign(team);
        for (RoleSign player : ownTeam) {
            int score = player.getScore() + 50;
            player.setScore(score);
        }
    }

    public static void getRebirthScore(RoleSign mainPlayer) {
        int score = mainPlayer.getScore() - 10;
        mainPlayer.setScore(score);
    }

    public static void putMineScore(Miner miner) {
        int score = miner.getScore() + 5;
        miner.setScore(score);
    }

    public static void sweepMineScore(Sapper sapper) {
        int score = sapper.getScore() + 5;
        sapper.setScore(score);
    }

    public static void mineBoomScore(Mine mine) {
        Miner miner = mine.getMiner();
        int score = miner.getScore() + 10;
        miner.setScore(score);
    }

}
