package com.example.isky.flaggame.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.isky.flaggame.R;
import com.example.isky.flaggame.game.GameConfig;
import com.example.isky.flaggame.server.PlayerManager;

import util.ToastUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);
        if (PlayerManager.getInstance().isPlayerRegister()) {
            PlayerManager.getInstance().getMainplayer(new PlayerManager.OnCreateOrGetPlayerListener() {
                @Override
                public void OnCreateOrGetPlayerSuccess(@NonNull PlayerManager.Player player) {
                    ToastUtil.show(MainActivity.this, "欢迎" + player.getPlayername() + " ~~~");
                    player.bindMainplayerAsLocationSender();
                    setView();
                    Button bt_multi = (Button) MainActivity.this.findViewById(R.id.bt_multi);
                    bt_multi.setOnClickListener(MainActivity.this);
                }

                @Override
                public void OnCreatePlayerFail(String info) {
                    ToastUtil.show(MainActivity.this, "获取用户失败，无法多人游戏");
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
                Intent intent = new Intent();
                intent.setClass(this, MapActivity.class);
                GameConfig.gametype = GameConfig.GAMETYPE_SINGLEGAME;
                startActivityForResult(intent, 0);
            }
            break;
            case R.id.bt_multi: {
                Intent intent = new Intent();
                intent.setClass(this, RoomListActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.bt_about:
                Toast.makeText(this, "关于", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_settings:
                Toast.makeText(this, "设置", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_exit:
                finish();
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
        builder.setTitle("请输入玩家名");
        builder.setIcon(android.R.drawable.ic_dialog_info);
        final EditText ed_playername = new EditText(MainActivity.this);
        builder.setView(ed_playername);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                String playname = ed_playername.getText().toString();
                if ("".equals(playname)) {
                    ToastUtil.show(MainActivity.this, "玩家名不能为空！");
                } else {
                    PlayerManager.getInstance().createMainplayer(playname, new PlayerManager.OnCreateOrGetPlayerListener() {
                        @Override
                        public void OnCreateOrGetPlayerSuccess(@NonNull PlayerManager.Player player) {
                            ToastUtil.show(MainActivity.this, "欢迎" + player.getPlayername() + " ~~~");
                            player.bindMainplayerAsLocationSender();
                            setView();
                            Button bt_multi = (Button) MainActivity.this.findViewById(R.id.bt_multi);
                            bt_multi.setOnClickListener(MainActivity.this);
                            dialogInterface.dismiss();

                        }

                        @Override
                        public void OnCreatePlayerFail(String info) {
                            ToastUtil.show(MainActivity.this, "获取用户失败，无法多人游戏");
                            MainActivity.this.setView();
                        }
                    });
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.this.finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
