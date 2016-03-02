package com.example.isky.flaggame.server;

import android.support.annotation.NonNull;

import com.loopj.android.http.RequestParams;

/**
 * Created by isky on 2016/2/21.
 * RequestParams的装配工厂
 * 调用方式new RequestParamsFactory().key("gggg").tableid("xxxx")....
 */
public class RequestParamsFactory {
    private final static String key = "959d885d93f526234bbf6e82c391b890";
    RequestParams requestParams = new RequestParams();
    private String ids = null;

    public RequestParamsFactory(){
        key();//默认秘钥
    }
    private RequestParamsFactory key() {
        requestParams.put("key", key);
        return this;
    }

    public RequestParamsFactory key(@NonNull String key) {
        requestParams.put("key", key);
        return this;
    }

    public RequestParamsFactory tableid(@NonNull String tableid) {
        requestParams.put("tableid", tableid);
        return this;
    }

    public RequestParamsFactory data(@NonNull RequestJsonFactory requestJsonFactory) {
        requestParams.put("data", requestJsonFactory.getJsonstr());
        return this;
    }
    public RequestParamsFactory name(@NonNull String name) {
        requestParams.put("name", name);
        return this;
    }
    /**
     * 要添加的id，可以是多个，但必须逗号隔开
     * @param ids， 一个数字或者 1,2,3
     * @return
     */
    public RequestParamsFactory ids(@NonNull String ids) {
        if (this.ids == null) {
            this.ids = ids;
            requestParams.put("ids", this.ids);
        } else {
            this.ids = this.ids + "," + ids;
            requestParams.remove("ids");
            requestParams.put("ids", this.ids);
        }
        return this;
    }
    public RequestParamsFactory _id(@NonNull String _id){
        requestParams.put("_id", _id);
        return this;
    }
}
