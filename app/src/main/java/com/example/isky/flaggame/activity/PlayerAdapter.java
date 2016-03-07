package com.example.isky.flaggame.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.isky.flaggame.R;
import com.example.isky.flaggame.server.PlayerManager;

import java.util.ArrayList;

/**
 * Created by x1832 on 2016/3/5.
 */
public class PlayerAdapter extends BaseAdapter {

    LayoutInflater inflater;
    private ArrayList<Object> playerlist;

    public PlayerAdapter(Context context, ArrayList<Object> playerlist) {
        this.inflater = LayoutInflater.from(context);
        this.playerlist = playerlist;
    }

    @Override
    public int getCount() {
        return playerlist.size();
    }

    @Override
    public Object getItem(int position) {
        return playerlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_playerinfo, null);
            viewHolder = new ViewHolder();
            viewHolder.playerName = (TextView) convertView.findViewById(R.id.tv_player_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        PlayerManager.Player player = (PlayerManager.Player) playerlist.get(position);
        viewHolder.playerName.setText(player.getPlayername());
        return convertView;
    }

    public void setPlayerlist(ArrayList<Object> playerlist) {
        this.playerlist = playerlist;
    }

    public class ViewHolder {
        TextView playerName;
    }

}
