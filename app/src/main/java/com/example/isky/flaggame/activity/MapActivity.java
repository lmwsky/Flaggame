package com.example.isky.flaggame.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.example.isky.flaggame.R;
import com.example.isky.flaggame.game.GameConfig;
import com.example.isky.flaggame.game.GameManager;
import com.example.isky.flaggame.game.MultiPlayerGame;
import com.example.isky.flaggame.game.SinglePlayerGame;
import com.example.isky.flaggame.role.SignManager;
import com.example.isky.flaggame.server.LocationServiceManager;

public class MapActivity extends Activity {
    //声明变量
    private MapView mapView;
    private AMap aMap;
    private GameManager gameManager;
    private ActionMenu actionMenu;

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
        actionMenu = new ActionMenu(activity);
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
