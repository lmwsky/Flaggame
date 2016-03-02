package util;

import android.app.Application;

/**
 * Created by isky on 2016/1/25.
 */
public class GameApplication extends Application {
    private static Application myApplication;
    @Override
    public void onCreate() {
        super.onCreate();
        myApplication=this;
    }

    /**
     * 获取一个全局的context
     * @return 一个全局的context
     */
    public static Application getApplication(){
        return myApplication;
    }
}
