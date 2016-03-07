package com.example.isky.flaggame.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.isky.flaggame.R;
import com.example.isky.flaggame.server.PlayerManager;
import com.example.isky.flaggame.server.RoomManage;
import com.example.isky.flaggame.server.Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.ToastUtil;

/**
 * Created by x1832 on 2016/3/4.
 * 房间列表
 */
public class RoomListActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private Server.OndatasearchListener loaddatalistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roomlist);
        setView();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ArrayList<RoomManage.Room> roomlist = RoomManage.getInstance().getRoomlist();
        if (roomlist.size() - 1 >= position) {
            final RoomManage.Room room = roomlist.get(position);
            if (room != null) {
                PlayerManager.getInstance().enterRoom(room, new PlayerManager.OnEnterOrLeaveRoomListener() {
                    @Override
                    public void OnEnterRoomSuccess(@NonNull PlayerManager.Player player) {
                        PlayerManager.getInstance().setCurrentRoom(room);
                        Intent intent = new Intent();
                        intent.setClass(RoomListActivity.this, RoomActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void OnEnterRoomFail(String info) {

                    }

                    @Override
                    public void onLeaveRoomSuccess() {

                    }

                    @Override
                    public void onLeaveRoomFail() {

                    }
                });
            }
        }

    }

    /**
     * 加载房间信息
     */
    public void loadRoomData() {
        PlayerManager.Player mainpalyer = PlayerManager.getInstance().getMainplayer();
        if (mainpalyer != null) {
            RoomManage.getInstance().SearchRoom(mainpalyer.getLatLng(), 4000, loaddatalistener);
        }

    }

    public void setView() {
        Button bt_quickgame = (Button) findViewById(R.id.bt_quickgame);
        bt_quickgame.setOnClickListener(this);

        Button bt_createroom = (Button) findViewById(R.id.bt_createroom);
        bt_createroom.setOnClickListener(this);

        Button bt_refresh = (Button) findViewById(R.id.bt_refresh);
        bt_refresh.setOnClickListener(this);

        Button bt_back = (Button) findViewById(R.id.bt_back);
        bt_back.setOnClickListener(this);


        ListView lv = (ListView) findViewById(R.id.lv_room);
        final List<Map<String, String>> list = new ArrayList<>();
        final MyAdapter adapter = new MyAdapter(this, list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        loaddatalistener = new Server.OndatasearchListener() {
            @Override
            public void success(ArrayList<Object> datas) {
                ArrayList<RoomManage.Room> roomArrayList = new ArrayList<>();
                for (Object o : datas) {
                    RoomManage.Room room = (RoomManage.Room) o;
                    roomArrayList.add(room);
                    Map<String, String> map = new HashMap<>();
                    map.put("roomName", room.getRoomname());
                    map.put("realNumber", room.getPlayersnum() + "");
                    map.put("maxNumber", room.getNeedplayernum() + "");
                    map.put("roomid", room.get_id() + "");

                    adapter.addData(map);
                }
                RoomManage.getInstance().setRoomlist(roomArrayList);

                adapter.notifyDataSetChanged();

            }

            @Override
            public void success(Object object) {

            }

            @Override
            public void fail(String info) {
                Log.d("hh", "getRoonList fail :" + info);
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_quickgame: {
                ArrayList<RoomManage.Room> roomArrayList = RoomManage.getInstance().getRoomlist();
                if (roomArrayList != null && roomArrayList.size() > 0) {
                    final RoomManage.Room room = roomArrayList.get(0);
                    PlayerManager.getInstance().enterRoom(room, new PlayerManager.OnEnterOrLeaveRoomListener() {

                        @Override
                        public void OnEnterRoomSuccess(@NonNull PlayerManager.Player player) {
                            PlayerManager.getInstance().setCurrentRoom(room);
                            Intent intent = new Intent();
                            intent.setClass(RoomListActivity.this, RoomActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void OnEnterRoomFail(String info) {
                            ToastUtil.show(RoomListActivity.this, "进入房间失败啦啦");

                        }

                        @Override
                        public void onLeaveRoomSuccess() {

                        }

                        @Override
                        public void onLeaveRoomFail() {

                        }
                    });

                } else {
                    ToastUtil.show(RoomListActivity.this, "找不到可用房间，要不你刷新一下？");
                }

            }
            break;
            case R.id.bt_createroom: {
                Intent intent = new Intent();
                intent.setClass(this, CreateRoomActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.bt_back: {
                Intent intent = new Intent();
                intent.setClass(this, MainActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.bt_refresh:
                loadRoomData();
                break;
            default:
                break;
        }
    }
}
