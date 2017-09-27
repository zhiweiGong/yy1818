package com.android.tools.Misc;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class UtilHandler {
	private final String TAG = "UtilHandler";
	private static UtilHandler mUtilHandler = null;

	public static synchronized UtilHandler getInstance(){
		if(mUtilHandler == null){
			mUtilHandler = new UtilHandler();
		}
		return mUtilHandler;
	}

    public UtilHandler(){
		Log.d(TAG,"init...");
    }
	
    public void sendMsg(Handler handler, int iwhat)
	{
		if(handler != null) {
			Message mMsg = new Message();
			mMsg.what = iwhat;
			handler.sendMessage(mMsg);
		}else{
			Log.e(TAG,"handler is null");
		}
	}
	
	public void sendMsg(Handler handler, int iwhat, long delay)
	{
		if(handler != null) {
			Message mMsg = new Message();
			mMsg.what = iwhat;
			handler.sendMessageDelayed(mMsg, delay);
		}else{
			Log.e(TAG,"handler is null");
		}
	}
	
	public void sendMsg(Handler handler, int iwhat, Object iobj)
	{
		if(handler != null) {
			Message mMsg = new Message();
			mMsg.obj = iobj;
			mMsg.what = iwhat;
			handler.sendMessage(mMsg);
		}else{
			Log.e(TAG,"handler is null");
		}
	}
	
	public void sendMsg(Handler handler, int iwhat, Object iobj,long delay)
	{
		if(handler != null) {
			Message mMsg = new Message();
			mMsg.obj = iobj;
			mMsg.what = iwhat;
			handler.sendMessageDelayed(mMsg, delay);
		}else{
			Log.e(TAG,"handler is null");
		}
	}

	public void sendMsg(Handler handler,int iwhat,int arg1,int arg2)
	{
		if(handler != null) {
			Message mMsg = new Message();
			mMsg.what = iwhat;
			mMsg.arg1 = arg1;
			mMsg.arg2 = arg2;
			handler.sendMessage(mMsg);
		}else{
			Log.e(TAG,"handler is null");
		}
	}

    public void sendMsg(Handler handler,int iwhat,int arg1,int arg2,long delay)
    {
		if(handler != null) {
			Message mMsg = new Message();
	    	mMsg.what = iwhat;
	    	mMsg.arg1 = arg1;
	    	mMsg.arg2 = arg2;
			handler.sendMessageDelayed(mMsg, delay);
		}else{
			Log.e(TAG,"handler is null");
		}
    }
    
    public void removeMsg(Handler handler,int iwhat)
    {
		if(handler != null) {
			handler.removeMessages(iwhat);
		}else{
			Log.e(TAG,"handler is null");
		}
    }
}
