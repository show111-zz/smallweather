package com.example.smallweather.util;

/**
 * Created by lhf on 2015/9/6.
 */
public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
