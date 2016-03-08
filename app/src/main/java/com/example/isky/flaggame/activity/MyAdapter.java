package com.example.isky.flaggame.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.isky.flaggame.R;
import com.example.isky.flaggame.server.RoomManage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by x1832 on 2016/3/4.
 */
public class MyAdapter extends BaseAdapter {

    List<RoomManage.Room> list;
    LayoutInflater inflater;

    public MyAdapter(Context context, List<RoomManage.Room> list) {
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.room_item, null);
            viewHolder = new ViewHolder();
            viewHolder.roomName = (TextView) convertView.findViewById(R.id.room_name);
            viewHolder.realNumber = (TextView) convertView.findViewById(R.id.real_number);
            viewHolder.maxNumber = (TextView) convertView.findViewById(R.id.max_number);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        RoomManage.Room room = list.get(position);

        viewHolder.roomName.setText(room.getRoomname());
        viewHolder.realNumber.setText(room.getPlayersnum() + "");
        viewHolder.maxNumber.setText(room.getNeedplayernum() + "");

        return convertView;
    }

    /**
     * 只添加尚未在列表中存在的
     *
     * @param room
     */
    public void addData(RoomManage.Room room) {
        boolean isExist = false;
        for (RoomManage.Room room1 : list) {
            if (room1.get_id().equals(room)) {
                isExist = true;
                break;
            }
        }
        if (isExist == false)
            list.add(room);

    }

    public void setRoomlist(ArrayList<RoomManage.Room> roomlist) {
        this.list = roomlist;
    }

    public class ViewHolder {
        TextView roomName;
        TextView realNumber;
        TextView maxNumber;
    }
}
