package com.example.isky.flaggame.server;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by isky on 2015/11/21.
 */
public class AsyncClient {
    private static final String BASE_URL = "http://yuntuapi.amap.com/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void get(String url, RequestParamsFactory params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params.requestParams, responseHandler);
    }
    public static void fullget(String url, RequestParamsFactory params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params.requestParams, responseHandler);
    }
    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParamsFactory params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params.requestParams, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }


}
