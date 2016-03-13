package com.example.isky.flaggame.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.isky.flaggame.R;
import com.example.isky.flaggame.game.GameConfig;
import com.example.isky.flaggame.server.PlayerManager;

import util.ToastUtil;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);
        GameConfig.initBitmap();
        if (PlayerManager.getInstance().isPlayerRegister()) {
            PlayerManager.getInstance().getMainplayer(new PlayerManager.OnCreateOrGetPlayerListener() {
                @Override
                public void OnCreateOrGetPlayerSuccess(@NonNull PlayerManager.Player player) {
                    ToastUtil.showshortToast(MainActivity.this, "欢迎" + player.getPlayername() + " ~~~");
                    player.bindMainplayerAsLocationSender();
                    setView();
                    Button bt_multi = (Button) MainActivity.this.findViewById(R.id.bt_multi);
                    bt_multi.setOnClickListener(MainActivity.this);
                }

                @Override
                public void OnCreatePlayerFail(String info) {
                    ToastUtil.showshortToast(MainActivity.this, getString(R.string.fail_getplayer));
                    MainActivity.this.setView();
                }
            });
        } else {
            showEnterPlayerNameDialog();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_single: {
                final CreateSingleGameConfigDialog createSingleGameConfigDialog = new CreateSingleGameConfigDialog(this);
                createSingleGameConfigDialog.show();
                createSingleGameConfigDialog.setOnPositiveListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        createSingleGameConfigDialog.dismiss();
                        GameConfig.gametype = GameConfig.GAMETYPE_SINGLEGAME;
                        GameConfig.num_monsters = createSingleGameConfigDialog.getMonsterNum();
                        GameConfig.dist_flag = createSingleGameConfigDialog.getFlagDistance();
                        GameConfig.mainplayerroletype = createSingleGameConfigDialog.getMainPlayerRoleType();

                        switch (GameConfig.mainplayerroletype) {
                            case GameConfig.ROLE_MINER:
                                GameConfig.mainplayerBitmapLive = GameConfig.BITMAP_MINER_BLUE;
                                GameConfig.mainplayerBitmapLive = GameConfig.BITMAP_MINER_BLUE;
                                GameConfig.bitmap_skill = GameConfig.BITMAP_SKILL_MINER;
                                break;
                            case GameConfig.ROLE_SAPPER:
                                GameConfig.mainplayerBitmapLive = GameConfig.BITMAP_SAPPER_BLUE;
                                GameConfig.mainplayerBitmapLive = GameConfig.BITMAP_SAPPER_BLUE;
                                GameConfig.bitmap_skill = GameConfig.BITMAP_SKILL_SAPPER;
                                break;
                            case GameConfig.ROLE_SCOUT:
                                GameConfig.mainplayerBitmapLive = GameConfig.BITMAP_SCOUT_BLUE;
                                GameConfig.mainplayerBitmapLive = GameConfig.BITMAP_SCOUT_BLUE;
                                GameConfig.bitmap_skill = GameConfig.BITMAP_SKILL_SCOUT;
                                break;
                            case GameConfig.ROLE_TUFU:
                                GameConfig.mainplayerBitmapLive = GameConfig.BITMAP_TUFU_BLUE;
                                GameConfig.mainplayerBitmapLive = GameConfig.BITMAP_TUFU_BLUE;
                                GameConfig.bitmap_skill = GameConfig.BITMAP_SKILL_TUFU;
                                break;
                            default:
                                break;
                        }
                        GameConfig.dist_monster = GameConfig.dist_flag;
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, MapActivity.class);
                        startActivityForResult(intent, 0);
                    }
                });
                createSingleGameConfigDialog.setOnNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        createSingleGameConfigDialog.dismiss();
                    }
                });
            }
            break;
            case R.id.bt_multi: {
                Intent intent = new Intent();
                intent.setClass(this, RoomListActivity.class);
                GameConfig.gametype = GameConfig.GAMETYPE_MULTIPLAYER;

                startActivity(intent);
            }
            break;
            case R.id.bt_about:
                ToastUtil.showshortToast(this, getString(R.string.about));
                break;
            case R.id.bt_settings:
                ToastUtil.showshortToast(this, getString(R.string.setting));
                break;
            case R.id.bt_exit:
                finish();
                System.exit(0);
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
            default:
                break;
        }
    }

    public void setView() {
        Button bt_singlegame = (Button) findViewById(R.id.bt_single);
        bt_singlegame.setOnClickListener(this);


        Button button_about = (Button) findViewById(R.id.bt_about);
        button_about.setOnClickListener(this);

        Button button_settings = (Button) findViewById(R.id.bt_settings);
        button_settings.setOnClickListener(this);
    }

    public void showEnterPlayerNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.hint_enterplayername));
        builder.setIcon(android.R.drawable.ic_dialog_info);
        final EditText ed_playername = new EditText(MainActivity.this);
        builder.setView(ed_playername);
        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                String playname = ed_playername.getText().toString();
                if ("".equals(playname)) {
                    ToastUtil.showshortToast(MainActivity.this, getString(R.string.fail_playnamemustnotnull));
                } else {
                    PlayerManager.getInstance().createMainplayer(playname, new PlayerManager.OnCreateOrGetPlayerListener() {
                        @Override
                        public void OnCreateOrGetPlayerSuccess(@NonNull PlayerManager.Player player) {
                            ToastUtil.showshortToast(MainActivity.this, "欢迎" + player.getPlayername() + " ~~~");
                            player.bindMainplayerAsLocationSender();
                            setView();
                            Button bt_multi = (Button) MainActivity.this.findViewById(R.id.bt_multi);
                            bt_multi.setOnClickListener(MainActivity.this);
                            dialogInterface.dismiss();

                        }

                        @Override
                        public void OnCreatePlayerFail(String info) {
                            ToastUtil.showshortToast(MainActivity.this, getString(R.string.fail_couldnotmultigame));
                            MainActivity.this.setView();
                        }
                    });
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.this.finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
