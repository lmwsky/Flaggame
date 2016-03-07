package com.example.isky.flaggame.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.isky.flaggame.R;
import com.example.isky.flaggame.game.GameConfig;
import com.example.isky.flaggame.server.BindwithServer;
import com.example.isky.flaggame.server.PlayerManager;
import com.example.isky.flaggame.server.RoomManage;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by x1832 on 2016/3/5.
 */
public class RoomActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room);

        ListView listView = (ListView) findViewById(R.id.lv_player);
        final PlayerManager.Player mainplayer = PlayerManager.getInstance().getMainplayer();
        final RoomManage.Room currentRoom = PlayerManager.getInstance().getCurrentRoom();

        TextView tv_room_name = (TextView) (findViewById(R.id.tv_room_name));
        tv_room_name.setText(currentRoom.getRoomname());

        TextView tv_real_number = (TextView) (findViewById(R.id.tv_real_number));
        tv_real_number.setText(currentRoom.getPlayersnum() + "");

        TextView tv_max_number = (TextView) (findViewById(R.id.tv_max_number));
        tv_max_number.setText(currentRoom.getNeedplayernum() + "");


        ArrayList<Object> players = new ArrayList<>();
        players.add(mainplayer);
        final PlayerAdapter adapter = new PlayerAdapter(this, players);
        listView.setAdapter(adapter);


        Timer getPlayerTimer = new Timer();
        TimerTask getPlayersTimerTask = new TimerTask() {
            @Override
            public void run() {
                currentRoom.getPlayersByRoom(new BindwithServer.OndatasearchListener() {
                    @Override
                    public void success(ArrayList<Object> datas) {
                        adapter.setPlayerlist(datas);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void success(Object object) {

                    }

                    @Override
                    public void fail(String info) {

                    }
                });
            }
        };
        getPlayerTimer.schedule(getPlayersTimerTask, 0, 1000);
        Button bt_startgame = (Button) findViewById(R.id.bt_startgame);
        bt_startgame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(RoomActivity.this, MapActivity.class);
                GameConfig.gametype = GameConfig.GAMETYPE_MULTIPLAYER;
                startActivityForResult(intent, 0);
            }
        });
        Button bt_leaveroom = (Button) findViewById(R.id.bt_leaveroom);
        bt_leaveroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentRoom.getOwner_id().equals(mainplayer.get_id())) {
                    RoomManage.getInstance().deleteRoom(currentRoom, new BindwithServer.OnDeleteDataListener() {
                        @Override
                        public void success(String info) {
                            PlayerManager.getInstance().setCurrentRoom(null);
                            RoomActivity.this.finish();
                            Log.d("hhh", "delete room sucess");
                        }

                        @Override
                        public void fail(String info) {
                            Log.d("hhh", "delete room fail");
                        }
                    });
                } else {
                    PlayerManager.getInstance().leaveRoom(new PlayerManager.OnEnterOrLeaveRoomListener() {
                        @Override
                        public void OnEnterRoomSuccess(@NonNull PlayerManager.Player player) {

                        }

                        @Override
                        public void OnEnterRoomFail(String info) {

                        }

                        @Override
                        public void onLeaveRoomSuccess() {
                            Intent intent = new Intent(RoomActivity.this, MainActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onLeaveRoomFail() {

                        }
                    });
                }
            }
        });


    }

    /**
     * 拦截activity 的后退键处理
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
