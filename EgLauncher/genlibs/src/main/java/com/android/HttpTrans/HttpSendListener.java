package com.android.HttpTrans;

/**
 * Created by hsienwu.chow on 2016/11/29.
 */

public interface HttpSendListener {
    String TAG = "HttpSendListener";
    void OnState(String state, String value);
    void OnSize(int value);
}
