package com.example.isky.flaggame.activity;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.ImageView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.example.isky.flaggame.R;
import com.example.isky.flaggame.game.GameConfig;
import com.example.isky.flaggame.game.GameManager;
import com.example.isky.flaggame.game.MultiPlayerGame;
import com.example.isky.flaggame.game.SinglePlayerGame;
import com.example.isky.flaggame.role.SignManager;
import com.example.isky.flaggame.server.LocationServiceManager;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.ArrayList;

public class MapActivity extends Activity {
    //声明变量
    private MapView mapView;
    private AMap aMap;
    private GameManager gameManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_map);

        //在onCreat方法中给aMap对象赋值
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 必须要写
        aMap = mapView.getMap();
        SignManager.getInstance().setaMap(aMap);
        setViewAndListener(this);

        if (GameConfig.gametype == GameConfig.GAMETYPE_SINGLEGAME)
            gameManager = new SinglePlayerGame(this, aMap);
        else
            gameManager = new MultiPlayerGame(this, aMap);
        SignManager.getInstance().setGameManage(gameManager);

        //游戏进行初始化
        gameManager.InitGame();

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

        ArrayList<SubActionButton> subActionButtonlist = new ArrayList();

        ImageView icon = new ImageView(this);
        Resources resources = this.getResources();
        Drawable drawable = resources.getDrawable(R.drawable.actionbar);
        icon.setImageDrawable(drawable);

        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        // repeat many times:
        ImageView icon_attract = new ImageView(this);
        Drawable drawable_attract = resources.getDrawable(R.drawable.icon_attract);
        icon_attract.setImageDrawable(drawable_attract);
        SubActionButton bt_attract = itemBuilder.setContentView(icon_attract).build();
        bt_attract.setOnClickListener(new GameManager.OnAttractBtClickListener());


        subActionButtonlist.add(bt_attract);
        ImageView icon_skill = new ImageView(this);
        int skillresourceid = GameConfig.bitmap_skill;
        if (skillresourceid != 0) {
            Drawable drawable_skill = resources.getDrawable(skillresourceid);
            icon_skill.setImageDrawable(drawable_skill);
            SubActionButton bt_skill = itemBuilder.setContentView(icon_skill).build();
            bt_skill.setOnClickListener(new GameManager.OnSkillBtClickListener());
            subActionButtonlist.add(bt_skill);
        }

        ImageView icon_occupy = new ImageView(this);
        Drawable drawable_occupy = resources.getDrawable(R.drawable.icon_occupy);
        icon_occupy.setImageDrawable(drawable_occupy);
        SubActionButton bt_occupy = itemBuilder.setContentView(icon_occupy).build();
        bt_occupy.setOnClickListener(new GameManager.OnOccupyBtClickListener());

        subActionButtonlist.add(bt_occupy);

        ImageView icon_rebirth = new ImageView(this);
        Drawable drawable_rebirth = resources.getDrawable(R.drawable.icon_rebirth);
        icon_rebirth.setImageDrawable(drawable_rebirth);
        SubActionButton bt_rebirth = itemBuilder.setContentView(icon_rebirth).build();
        bt_rebirth.setOnClickListener(new GameManager.OnRebirthBtClickListener());

        subActionButtonlist.add(bt_rebirth);

        ImageView icon_endgame = new ImageView(this);
        Drawable drawable_endgame = resources.getDrawable(R.drawable.icon_endgame);
        icon_endgame.setImageDrawable(drawable_endgame);
        SubActionButton bt_endgame = itemBuilder.setContentView(icon_endgame).build();
        bt_endgame.setOnClickListener(new GameManager.OnEndGameClickListener());
        subActionButtonlist.add(bt_endgame);

        FloatingActionMenu.Builder builder = new FloatingActionMenu.Builder(this);
        for (SubActionButton subActionButton : subActionButtonlist)
            builder.addSubActionView(subActionButton);
        FloatingActionMenu actionMenu = builder.attachTo(actionButton).build();

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
