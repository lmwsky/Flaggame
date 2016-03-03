package com.example.isky.flaggame.activity;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.example.isky.flaggame.R;
import com.example.isky.flaggame.game.GameManager;
import com.example.isky.flaggame.game.MulPlayerGame;
import com.example.isky.flaggame.game.SinglePlayerGame;
import com.example.isky.flaggame.server.LocationServiceManager;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

public class MapActivity extends Activity {
    //声明变量
    private MapView mapView;
    private AMap aMap;
    private GameManager gameManager;
    private int gametype = 1;
    private boolean isRoomOwner = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_map);

        //在onCreat方法中给aMap对象赋值
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 必须要写
        aMap = mapView.getMap();
        setViewAndListener(this);

        if (gametype == 0) {
            gameManager = new SinglePlayerGame(this, aMap);
            //游戏进行初始化
            gameManager.InitGame();
        } else {
            gameManager = new MulPlayerGame(this, aMap);
            if (isRoomOwner == true)
                ((MulPlayerGame) gameManager).InitGameByOwner();
            else
                ((MulPlayerGame) gameManager).InitGameByOthers();
        }

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        gameManager.ContinueGame();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        LocationServiceManager.getInstance().deactivate();
        gameManager.StopGame();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        gameManager.EndGame();
    }

    private void setViewAndListener(Activity activity) {

        ImageView icon = new ImageView(this);
        Resources resources = this.getResources();
        Drawable drawable = resources.getDrawable(R.drawable.button_action);
        icon.setImageDrawable(drawable);

        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
// repeat many times:
        ImageView itemIcon1 = new ImageView(this);
        Drawable drawable1 = resources.getDrawable(R.drawable.button_sub_action);
        itemIcon1.setImageDrawable(drawable1);
        SubActionButton button1 = itemBuilder.setContentView(itemIcon1).build();
        button1.setOnClickListener(new SinglePlayerGame.OnSkillBtClickListener());

        ImageView itemIcon2 = new ImageView(this);
        Drawable drawable2 = resources.getDrawable(R.drawable.button_sub_action);
        itemIcon2.setImageDrawable(drawable1);
        SubActionButton button2 = itemBuilder.setContentView(itemIcon2).build();
        button2.setOnClickListener(new SinglePlayerGame.OnAttractBtClickListener());

        ImageView itemIcon3 = new ImageView(this);
        Drawable drawable3 = resources.getDrawable(R.drawable.button_sub_action);
        itemIcon3.setImageDrawable(drawable1);
        SubActionButton button3 = itemBuilder.setContentView(itemIcon3).build();
        button3.setOnClickListener(new SinglePlayerGame.OnOccupyBtClickListener());

        ImageView itemIcon4 = new ImageView(this);
        Drawable drawable4 = resources.getDrawable(R.drawable.button_sub_action);
        itemIcon4.setImageDrawable(drawable4);
        SubActionButton button4 = itemBuilder.setContentView(itemIcon4).build();
        button4.setOnClickListener(new SinglePlayerGame.OnRebirthBtClickListener());

        ImageView itemIcon5 = new ImageView(this);
        Drawable drawable5 = resources.getDrawable(R.drawable.button_sub_action);
        itemIcon5.setImageDrawable(drawable1);
        SubActionButton button5 = itemBuilder.setContentView(itemIcon5).build();
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameManager.EndGame();
            }
        });

        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(button1)
                .addSubActionView(button2)
                .addSubActionView(button3)
                .addSubActionView(button4)
                .addSubActionView(button5)
                        // ...
                .attachTo(actionButton)
                .build();

    }
}
