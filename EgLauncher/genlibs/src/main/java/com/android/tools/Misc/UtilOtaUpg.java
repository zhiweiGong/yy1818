package com.android.tools.Misc;

import android.content.Context;
import android.os.RecoverySystem;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class UtilOtaUpg {
	private final String TAG = "UtilOtaUpg";
	private static UtilOtaUpg mUtilOtaUpg = null;

	public static synchronized UtilOtaUpg getInstance(){
		if(mUtilOtaUpg == null){
			mUtilOtaUpg = new UtilOtaUpg();
		}
		return mUtilOtaUpg;
	}

    public UtilOtaUpg(){
		Log.d(TAG,"init...");
    }

    public boolean excuteUpdateZip(Context context,String filepath)
    {
		File f = new File(filepath);
		
    	if (!f.exists()){
    		return false;
    	}
		try {
			Log.d(TAG,"start upgrade");
			RecoverySystem.installPackage(context, f);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
    }
}
