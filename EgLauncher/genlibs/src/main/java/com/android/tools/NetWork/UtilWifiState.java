package com.android.tools.NetWork;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.android.tools.Misc.UtilSystem;

public class UtilWifiState {
	private final String TAG = "UtilWifiState";

	private int mIP_LAST = 0;

	private static UtilWifiState mUtilWifiState = null;
	public static synchronized UtilWifiState getInstance(){
		if(mUtilWifiState == null){
			mUtilWifiState = new UtilWifiState();
		}
		return mUtilWifiState;
	}

	public UtilWifiState(){
		Log.d(TAG,"init");
	}

	public void TaskCheckWifiState(final Context context) {
        new Thread(new Runnable(){
            public void run(){
                while (true){
                    if(context == null){
                        break;
                    }

                    WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                    if(UtilWifiAp.getInstance().getWifiApState(context) == 13){
                        mIP_LAST = 0xffffffff;
                    }

                    if(wifiManager.isWifiEnabled()){
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        if(wifiInfo.getIpAddress() != 0){
                            if(mIP_LAST == 0){//first time startup
                                mIP_LAST = wifiInfo.getIpAddress();
                                Log.d(TAG,"getIpAddress 0:"+mIP_LAST);
                            }
                            if(mIP_LAST == wifiInfo.getIpAddress()){
                                Log.d(TAG,"getIpAddress:"+mIP_LAST);
                            }else{
                                Log.d(TAG,"ip is changed restart service");
                                UtilSystem.getInstance().SendBroadCast(context,"bcom.genlib.wifi_state","changed",wifiInfo.getIpAddress()+"");
                            }
                        }
                    }

                    try{
                        Thread.sleep(2000);
                    } catch(Exception e) {
                        Log.e(TAG,"wait error");
                    }
                }
            }
        }).start();
	}
}
