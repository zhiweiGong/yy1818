package com.android.LongSocket;

/**
 * Created by hsienwu.chow on 2016/11/29.
 */

public interface DealResposeListener {
    String TAG = "DealResposeListener";
    void OnSocketCmd(String tag,String value);
    void OnSocketOper(String value, int index, int total, byte[] buf_data);
    void OnSocketFile(String value, int index, int total, byte[] buf_data);
    void OnSocketData(String value, int index, int total, byte[] buf_data);
}
