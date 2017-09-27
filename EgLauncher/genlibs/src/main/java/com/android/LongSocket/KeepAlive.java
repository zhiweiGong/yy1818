package com.android.LongSocket;

import android.util.Log;

public class KeepAlive {
    private static final String TAG = "KeepAlive";
    
    private int  mKeepAliveTimeInterval = 180000;//3 min
    
    private long mlastKeepAliveOkTime = 0;
    	
	public KeepAlive()
	{
		mlastKeepAliveOkTime = System.currentTimeMillis();
	}

	public void checkIsAlive(LongSocketClient longSocketClient)
	{
		long currentTime = System.currentTimeMillis();
		if ((currentTime - mlastKeepAliveOkTime) > mKeepAliveTimeInterval) {
			SendHeartPackage(longSocketClient);
			mlastKeepAliveOkTime = currentTime;
		}
	}
	
	private void SendHeartPackage(LongSocketClient longSocketClient){
		boolean b = false;
		if(longSocketClient != null){
			 b = longSocketClient.SendCmd("ask","ok");
		}
		Log.e(TAG, " SendHeartPackage " + b);
	}
}
