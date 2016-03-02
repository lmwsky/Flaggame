package com.example.isky.flaggame.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.example.isky.flaggame.game.GameManager;
import com.example.isky.flaggame.server.LocationServiceManager;
import com.example.isky.flaggame.R;
import com.example.isky.flaggame.game.SinglePlayerGame;

public class MapActivity extends AppCompatActivity {
    //声明变量
    private MapView mapView;
    private AMap aMap;
    private GameManager gameManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //在onCreat方法中给aMap对象赋值
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 必须要写
        aMap = mapView.getMap();
        gameManager = new SinglePlayerGame(this, aMap);
        setViewAndListener(this);
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
        Button bt_skill = (Button) (activity.findViewById(R.id.bt_skill));
        bt_skill.setOnClickListener(new SinglePlayerGame.OnSkillBtClickListener());

        Button bt_attract = (Button) (activity.findViewById(R.id.bt_attract));
        bt_attract.setOnClickListener(new SinglePlayerGame.OnAttractBtClickListener());

        Button bt_occupy = (Button) (activity.findViewById(R.id.bt_occupy));
        bt_occupy.setOnClickListener(new SinglePlayerGame.OnOccupyBtClickListener());

        Button bt_rebirth = (Button) (activity.findViewById(R.id.bt_rebirth));
        bt_rebirth.setOnClickListener(new SinglePlayerGame.OnRebirthBtClickListener());

        Button bt_endgame = (Button) (activity.findViewById(R.id.bt_endgame));
        bt_endgame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameManager.EndGame();
            }
        });


    }
}
