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
import com.example.isky.flaggame.game.GameConfig;
import com.example.isky.flaggame.server.PlayerManager;
import com.example.isky.flaggame.server.RoomManage;
import com.example.isky.flaggame.server.Server;

import java.util.ArrayList;
import java.util.List;

import util.ToastUtil;

/**
 * Created by x1832 on 2016/3/4.
 * 房间列表
 */
public class RoomListActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private Server.OndatasearchListener loaddatalistener;
    private CreateRoomDialog createRoomDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roomlist);
        setView();
        loadRoomData();
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
                        Log.d("hh", "enter room belong to " + room.getOwner_id());
                        Intent intent = new Intent();
                        intent.setClass(RoomListActivity.this, RoomActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void OnEnterRoomFail(String info) {
                        Log.d("hh", "start enter room fail " + info);

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
     * 异步加载房间列表信息
     */
    public void loadRoomData() {
        PlayerManager.Player mainpalyer = PlayerManager.getInstance().getMainplayer();
        if (mainpalyer != null) {
            RoomManage.getInstance().SearchRoom(mainpalyer.getLatLng(), 14000, loaddatalistener);
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
        final List<RoomManage.Room> list = new ArrayList<>();
        final RoomlistAdapter adapter = new RoomlistAdapter(this, list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        loaddatalistener = new Server.OndatasearchListener() {
            @Override
            public void success(ArrayList<Object> datas) {
                ArrayList<RoomManage.Room> roomArrayList = new ArrayList<>();
                for (Object o : datas) {
                    RoomManage.Room room = (RoomManage.Room) o;
                    if (room.isAbandon() || room.isStarting())
                        continue;
                    roomArrayList.add(room);
                }
                RoomManage.getInstance().addroomlist(roomArrayList);
                Log.d("hh", "getRoonList SIZE :" + RoomManage.getInstance().getRoomlist().size());
                adapter.setRoomlist(RoomManage.getInstance().getRoomlist());
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
                    //TODO 快速加入功能待完成
                    final RoomManage.Room room = roomArrayList.get(0);
                    PlayerManager.getInstance().enterRoom(room, new PlayerManager.OnEnterOrLeaveRoomListener() {

                        @Override
                        public void OnEnterRoomSuccess(@NonNull PlayerManager.Player player) {
                            GameConfig.gametype = GameConfig.GAMETYPE_MULTIPLAYER;
                            PlayerManager.getInstance().setCurrentRoom(room);
                            Intent intent = new Intent();
                            intent.setClass(RoomListActivity.this, RoomActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void OnEnterRoomFail(String info) {
                            ToastUtil.showshortToast(RoomListActivity.this, "进入房间失败啦啦");

                        }

                        @Override
                        public void onLeaveRoomSuccess() {

                        }

                        @Override
                        public void onLeaveRoomFail() {

                        }
                    });

                } else {
                    ToastUtil.showshortToast(RoomListActivity.this, getString(R.string.noavailible));
                }

            }
            break;
            case R.id.bt_createroom: {
                showCreateRoomDialog();
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

    private void showCreateRoomDialog() {
        if (createRoomDialog == null) {
            createRoomDialog = new CreateRoomDialog(this);

            createRoomDialog.setTitle(R.string.dialogtitle_createroom);
            createRoomDialog.setOnNegativeListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createRoomDialog.dismiss();
                }
            });
            createRoomDialog.setOnPositiveListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final PlayerManager.Player mainplayer = PlayerManager.getInstance().getMainplayer();
                    if (mainplayer != null) {
                      /*建立房间对象*/
                        final RoomManage.Room room = new RoomManage.Room();
                        room.setLatLng(mainplayer.getLatLng());
                        room.setOwner_name(mainplayer.getPlayername());
                        room.setOwner_id(mainplayer.get_id());
                        room.setNeedplayernum(createRoomDialog.getRoomSize());
                        room.setRoomname(createRoomDialog.getRoomname());
                        ArrayList<String> arrayList = new ArrayList<>();
                        room.setOtherplayersid(arrayList);

                        RoomManage.getInstance().createRoom(room, new RoomManage.OnRoomListener() {
                            @Override
                            public void onCreateRoomSuccess(RoomManage.Room room) {
                                RoomManage.getInstance().setCreateRoom(room);
                                PlayerManager.getInstance().setCurrentRoom(room);

                                Server.getInstance().updateData(Server.TABLEID_PLAYER, mainplayer.get_id(), getString(R.string.columnname_roomid), mainplayer.getRoomid(), new Server.OnUpdateDataListener() {
                                    @Override
                                    public void success(String _id) {
                                        createRoomDialog.dismiss();
                                        Intent intent = new Intent();
                                        intent.setClass(RoomListActivity.this, RoomActivity.class);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void fail(String info) {
                                        createRoomDialog.dismiss();
                                        ToastUtil.showshortToast(RoomListActivity.this, "创建房间失败" + info);
                                    }
                                });

                            }

                            @Override
                            public void onCreateRoomFail() {
                                createRoomDialog.dismiss();
                                ToastUtil.showshortToast(RoomListActivity.this, getString(R.string.fail_createroom));
                            }
                        });
                    } else {
                        ToastUtil.showshortToast(RoomListActivity.this, getString(R.string.fail_createroomwithoutmainplayer));
                    }
                }
            });
        }
        if (createRoomDialog.isShowing() == false)
            createRoomDialog.show();
    }
}
