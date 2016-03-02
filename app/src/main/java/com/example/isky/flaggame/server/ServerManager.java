package com.example.isky.flaggame.server;


import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * Created by isky on 2015/11/21.
 */
public class ServerManager {
    private static ServerManager myServerManager=null;
    private final static String apikey="959d885d93f526234bbf6e82c391b890";//调用云储存的API的key

    private static final String URL_TABLE_CREATE="table/create";

    public static ServerManager getInstance(){
        if(myServerManager==null)
        {
            myServerManager=new ServerManager();
        }
        return myServerManager;
    }
    private ServerManager(){

    }

    /**
     * 创建表
     * @param name 表的名字，支持任意中英文字符、数字、下划线
     * @param jsonHttpResponseHandler 回调接口，创建结束会调用
     */
    public static void createTable(String name,JsonHttpResponseHandler jsonHttpResponseHandler){
        AsyncClient.post(URL_TABLE_CREATE,new RequestParamsFactory().name(name),jsonHttpResponseHandler);
    }

}
