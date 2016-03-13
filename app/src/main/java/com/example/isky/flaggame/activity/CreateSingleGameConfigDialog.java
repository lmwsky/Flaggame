package com.example.isky.flaggame.activity;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.isky.flaggame.R;
import com.example.isky.flaggame.game.GameConfig;

/**
 * Created by x1832 on 2016/3/10.
 * 单人游戏的游戏选项，配置一些游戏的参数，比如旗子数目，怪物数目
 */
public class CreateSingleGameConfigDialog extends Dialog {


    private Spinner initialDistance;
    private Spinner monsterNumber;
    private Spinner playerType;
    private Spinner difficulty;
    private EditText flagNumber;
    private Button positiveButton;
    private Button negativeButton;

    public CreateSingleGameConfigDialog(Context context) {
        super(context);
        setSelectDialog();
    }

    private void setSelectDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_select_singlegame_type, null);
        initialDistance = (Spinner) mView.findViewById(R.id.distance);
        monsterNumber = (Spinner) mView.findViewById(R.id.monsters_number);
        playerType = (Spinner) mView.findViewById(R.id.player_type);
        difficulty = (Spinner) mView.findViewById(R.id.difficulty);
        flagNumber = (EditText) mView.findViewById(R.id.flag_number);
        positiveButton = (Button) mView.findViewById(R.id.select_confirm);
        negativeButton = (Button) mView.findViewById(R.id.select_cancel);
        super.setContentView(mView);
    }


    public int getMainPlayerRoleType() {
        int type = GameConfig.ROLE_MINER;
        int position = playerType.getSelectedItemPosition();
        switch (position) {
            case 0:
                type = GameConfig.ROLE_MINER;
                break;
            case 1:
                type = GameConfig.ROLE_SAPPER;
                break;
            case 2:
                type = GameConfig.ROLE_SCOUT;
                break;
            case 3:
                type = GameConfig.ROLE_TUFU;
                break;
            default:
                break;
        }
        return type;
    }

    public int getMonsterNum() {
        int position = monsterNumber.getSelectedItemPosition();
        int num = 4;
        switch (position) {
            case 0:
                num = 4;
                break;
            case 1:
                num = 6;
                break;
            case 2:
                num = 10;
                break;
            default:
                break;
        }
        return num;
    }

    public double getFlagDistance() {
        int position = initialDistance.getSelectedItemPosition();
        double distance = GameConfig.DIST_FLAG_CLOSED;
        switch (position) {
            case 0:
                distance = GameConfig.DIST_FLAG_CLOSED;

                break;
            case 1:
                distance = GameConfig.DIST_FLAG_NOMAL;

                break;
            case 2:
                distance = GameConfig.DIST_FLAG_FAR;
                break;
            default:
                break;
        }
        return distance;
    }


    public void setOnPositiveListener(View.OnClickListener listener) {
        positiveButton.setOnClickListener(listener);
    }

    public void setOnNegativeListener(View.OnClickListener listener) {
        negativeButton.setOnClickListener(listener);
    }
}
