package com.example.isky.flaggame.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.isky.flaggame.R;
import com.example.isky.flaggame.server.PlayerManager;
import com.example.isky.flaggame.server.RoomManage;

import java.util.ArrayList;

import util.ToastUtil;

/**
 * Created by x1832 on 2016/3/4.
 * 创建房间的activty
 */
public class CreateRoomActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createroom);
        setView();
    }

    private void setView() {
        final EditText ed_roomname = (EditText) findViewById(R.id.et_room_name);
        final RadioGroup radioGroup = (RadioGroup) (findViewById(R.id.rg_room_number));

        Button bt_confirm = (Button) findViewById(R.id.bt_confirm);

        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayerManager.Player mainplayer = PlayerManager.getInstance().getMainplayer();
                if (mainplayer != null) {

                      /*建立房间对象*/
                    final RoomManage.Room room = new RoomManage.Room();
                    room.setLatLng(mainplayer.getLatLng());
                    room.setOwner_name(mainplayer.getPlayername());
                    room.setOwner_id(mainplayer.get_id());
                    room.setPlayersnum(1);
                    room.setNeedplayernum(4);
                    room.setRoomname(ed_roomname.getText().toString());
                    ArrayList<String> arrayList = new ArrayList<>();
                    room.setOtherplayersid(arrayList);

                    int radioButtonId = radioGroup.getCheckedRadioButtonId();
                    int roomnum = 2;
                    switch (radioButtonId) {
                        case R.id.rb_room_two:
                            roomnum = 2;
                            break;
                        case R.id.rb_room_six:
                            roomnum = 6;
                            break;
                        case R.id.rb_room_ten:
                            roomnum = 10;
                            break;
                        default:
                            break;
                    }
                    room.setNeedplayernum(roomnum);
                    RoomManage.getInstance().createRoom(room, new RoomManage.OnRoomListener() {
                        @Override
                        public void onCreateRoomSuccess(RoomManage.Room room) {
                            RoomManage.getInstance().setCreateRoom(room);
                            PlayerManager.getInstance().setCurrentRoom(room);
                            Intent intent = new Intent();
                            intent.setClass(CreateRoomActivity.this, RoomActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onCreateRoomFail() {

                        }
                    });
                } else {
                    ToastUtil.show(CreateRoomActivity.this, "不能建立房间");
                }
            }
        });
        Button bt_cancel = (Button) findViewById(R.id.bt_cancel);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateRoomActivity.this.finish();
            }
        });

    }
}
