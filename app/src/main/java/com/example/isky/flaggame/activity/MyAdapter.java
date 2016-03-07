package com.example.isky.flaggame.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.isky.flaggame.R;

import java.util.List;
import java.util.Map;

/**
 * Created by x1832 on 2016/3/4.
 */
public class MyAdapter extends BaseAdapter {

    List<Map<String, String>> list;
    LayoutInflater inflater;

    public MyAdapter(Context context, List<Map<String, String>> list) {
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

        Map map = list.get(position);
        viewHolder.roomName.setText((String) map.get("roomName"));
        viewHolder.realNumber.setText((String) map.get("realNumber"));
        viewHolder.maxNumber.setText((String) map.get("maxNumber"));

        return convertView;
    }

    /**
     * 只添加尚未在列表中存在的
     *
     * @param map
     */
    public void addData(Map<String, String> map) {
        boolean isexist = false;
        for (Map map1 : list) {
            if (map1.get("roomid").equals(map.get("roomid")))
                isexist = true;
        }
        if (isexist == false)
            list.add(map);
    }

    public class ViewHolder {
        TextView roomName;
        TextView realNumber;
        TextView maxNumber;
    }
}
