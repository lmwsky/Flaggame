package util;

import android.app.Application;

/**
 * Created by isky on 2016/1/25.
 * 用来在程序的任何地方获得一个全局的上下文对象context
 */
public class GameApplication extends Application {
    private static Application myApplication;

    /**
     * 获取一个全局的context
     *
     * @return 一个全局的context
     */
    public static Application getApplication() {
        return myApplication;
    }

    /**
     * 程序创建的时候会运行这个方法，应该是先于所有其他的工作，可以进行一些初始化
     */
    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
    }
}
