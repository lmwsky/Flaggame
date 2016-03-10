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
import com.example.isky.flaggame.server.PlayerManager;
import com.example.isky.flaggame.server.RoomManage;
import com.example.isky.flaggame.server.Server;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import util.ToastUtil;

/**
 * Created by x1832 on 2016/3/5.
 */
public class RoomActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room);
        setView();
        GameConfig.gametype = GameConfig.GAMETYPE_MULTIPLAYER;
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

    public void setView() {
        ListView listView = (ListView) findViewById(R.id.lv_player);
        final PlayerManager.Player mainplayer = PlayerManager.getInstance().getMainplayer();
        final RoomManage.Room currentRoom = PlayerManager.getInstance().getCurrentRoom();

        TextView tv_room_name = (TextView) (findViewById(R.id.tv_room_name));
        tv_room_name.setText(currentRoom.getRoomname());

        final TextView tv_real_number = (TextView) (findViewById(R.id.tv_real_number));
        tv_real_number.setText(currentRoom.getPlayersnum() + "");

        TextView tv_max_number = (TextView) (findViewById(R.id.tv_max_number));
        tv_max_number.setText(currentRoom.getNeedplayernum() + "");


        ArrayList<Object> players = new ArrayList<>();
        players.add(mainplayer);
        final PlayerAdapter adapter = new PlayerAdapter(this, players);
        listView.setAdapter(adapter);

        final Timer getPlayerTimer = new Timer();
        TimerTask getPlayersTimerTask = new TimerTask() {
            @Override
            public void run() {
                currentRoom.getPlayersInCurrentRoom(new Server.OndatasearchListener() {
                    @Override
                    public void success(ArrayList<Object> datas) {
                        ArrayList<PlayerManager.Player> playerArrayList = new ArrayList<PlayerManager.Player>();
                        adapter.setPlayerlist(datas);
                        for (Object o : datas) {
                            PlayerManager.Player player = (PlayerManager.Player) o;
                            playerArrayList.add(player);
                        }
                        PlayerManager.getInstance().getCurrentRoom().setALLplayerlist(playerArrayList);

                        tv_real_number.setText(datas.size() + "");
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

        final Button bt_startgame = (Button) findViewById(R.id.bt_startgame);
        bt_startgame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final RoomManage.Room currentRoom = PlayerManager.getInstance().getCurrentRoom();

                /*房主有责任通知其他游戏已经，通过修改房间状态*/
                if (currentRoom.getOwner_id().equals(mainplayer.get_id())) {
                    if (currentRoom.isfull() == false) {
                        ToastUtil.show(RoomActivity.this, "房间未满~");
                        return;
                    }
                    currentRoom.startgame(
                            new Server.OnUpdateDataListener() {
                                @Override
                                public void success(String _id) {
                                    getPlayerTimer.cancel();
                                    Intent intent = new Intent();
                                    intent.setClass(RoomActivity.this, MapActivity.class);
                                    GameConfig.gametype = GameConfig.GAMETYPE_MULTIPLAYER;
                                    startActivityForResult(intent, 0);
                                }

                                @Override
                                public void fail(String info) {
                                    ToastUtil.show(RoomActivity.this, "开始游戏发生了一点问题...");
                                    currentRoom.init();
                                }
                            });
                } else {
                    if (currentRoom.isStarting() == false) {
                        ToastUtil.show(RoomActivity.this, "只有房主能够开始游戏~");
                        return;
                    }
                    Intent intent = new Intent();
                    intent.setClass(RoomActivity.this, MapActivity.class);
                    GameConfig.gametype = GameConfig.GAMETYPE_MULTIPLAYER;
                    startActivityForResult(intent, 0);
                }

            }
        });

        final Timer getRoomstateTimer = new Timer();
        TimerTask getRoomstateTask = new TimerTask() {
            @Override
            public void run() {

                Server.getInstance().getData(Server.TABLEID_ROOM, currentRoom.get_id(), new Server.OndatasearchListener() {
                    @Override
                    public void success(ArrayList<Object> datas) {

                    }

                    @Override
                    public void success(Object object) {
                        RoomManage.Room newRoom = (RoomManage.Room) object;
                        if (newRoom.isStarting()) {
                            getRoomstateTimer.cancel();

                            PlayerManager.getInstance().getCurrentRoom().setState(RoomManage.Room.START);
                            bt_startgame.callOnClick();
                        }
                    }

                    @Override
                    public void fail(String info) {

                    }
                });
            }
        };

        getRoomstateTimer.schedule(getRoomstateTask, 0, 1000);


        Button bt_leaveroom = (Button) findViewById(R.id.bt_leaveroom);
        bt_leaveroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentRoom.getOwner_id().equals(mainplayer.get_id())) {
                    currentRoom.abandon(new Server.OnUpdateDataListener() {
                        @Override
                        public void success(String _id) {
                            getRoomstateTimer.cancel();
                            getPlayerTimer.cancel();
                            PlayerManager.getInstance().setCurrentRoom(null);
                            RoomActivity.this.finish();
                            Log.d("hhh", "leave room sucess");
                        }

                        @Override
                        public void fail(String info) {
                            ToastUtil.show(RoomActivity.this, "退出房间失败");
                            currentRoom.init();
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
                            getRoomstateTimer.cancel();
                            getPlayerTimer.cancel();
                            PlayerManager.getInstance().setCurrentRoom(null);
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
}
