package com.android.tools.Misc;

import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tools.File.UtilFileStream;
import com.android.tools.SysPorp.UtilShareDB;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UtilSystem {
	private final String TAG = "UtilSystem";
	private static UtilSystem mUtilSystem = null;

	public static synchronized UtilSystem getInstance(){
		if(mUtilSystem == null){
			mUtilSystem = new UtilSystem();
		}
		return mUtilSystem;
	}

    public UtilSystem(){
		Log.d(TAG,"init...");
    }

	public String getShortCid(String str_cid){
		String Ret = "0000";

		String str_cid_tmp = "";
		int    i_cid_tmp = 0;

		int    i_cid_all = 0;
		int    i_cid_xor = 0;
		int    i_cid_len = 0;

		if(str_cid == null){
			return Ret;
		}

		Log.d(TAG,"str_dev:"+str_cid);

		char[] Arr_Tmp = str_cid.toCharArray();
		int[] Arr_Cid = new int[32];

		i_cid_len = Arr_Tmp.length-1;

		if(i_cid_len == 32){
			try{
				for(int i = 0; i < i_cid_len; i++){
					Arr_Cid[i] = Integer.parseInt(String.valueOf(Arr_Tmp[i]),16);
					i_cid_all = i_cid_all+Arr_Cid[i];
				}
				//Log.d(TAG,"i_cid_all:"+i_cid_all);
				for(int j = 0; j < i_cid_len;j=j+4){
					str_cid_tmp = str_cid.substring(j,j+4);
					//Log.d(TAG,"str_cid_tmp:"+str_cid_tmp+",j:"+j);
					i_cid_tmp   = Integer.parseInt(str_cid_tmp,16);
					//Log.d(TAG,"i_cid_tmp1:"+i_cid_tmp);

					i_cid_tmp = i_cid_tmp^i_cid_all;
					//Log.d(TAG,"i_cid_tmp2:"+i_cid_tmp);

					i_cid_xor = i_cid_xor+i_cid_tmp;
					//Log.d(TAG,"i_cid_xor:"+i_cid_xor);
				}

				i_cid_xor = i_cid_xor&0xffff;

				Ret = String.format("%04x", i_cid_xor);
			}catch(NumberFormatException e){
				e.printStackTrace();
			}
		}

		return Ret;
	}

	public String getSystemCid(){
		String strCid = "00000001000000020000000300000004x";

		String path = UtilShareDB.getInstance().ReadSysKey("path_dev_s","/sys/devices/mtk-msdc.0/11230000.MSDC0/mmc_host/mmc0/mmc0:0001/cid");

		if(path != null) {
			String Value = UtilFileStream.getInstance().ReadRawFile(path);

			if(Value != null) {
				if(Value.length() > 32) {
					strCid = Value.substring(0, 32);
				}
			}
		}

		if(strCid == null){
			strCid = "00000001000000020000000300000004";
		}
		return strCid;
	}

	public String getSimSerialNumber(Context context,String str_get)
	{
		String  ret = "emputy";
		
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		
		if(str_get.equals("imei")){
			ret = tm.getDeviceId();
			Log.d(TAG, "IMEI:"+ret);
		}
		if(str_get.equals("iccid")){
			ret =tm.getSimSerialNumber();
			Log.d(TAG, "ICCID:"+ret);
		}
		if(str_get.equals("imsi")){
			ret =tm.getSubscriberId();
			Log.d(TAG, "IMSI:"+ret);
		}
        
		return ret;
	}

    public void sendKeyCode(final int keyCode){
        Log.d(TAG,"Find keyCode:"+keyCode);//also can use: bcom.android.sys_sendkey--->key_code
        new Thread () {
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(keyCode);
                } catch (Exception e) {
                    Log.e(TAG,"Exception when sendPointerSync");
                }
            }
        }.start();
    }
	
    private Process mProcess;
    private int mTimeOut = 0;
    public void runCommand(String arg,int timeOut) {
		String[] command = arg.split(" {1,}");
		mProcess = null;
		mTimeOut = timeOut;
		
		if(mProcess != null){
			mProcess.destroy();
			mProcess = null;
		}
		
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(command);
			mProcess = processBuilder.start();
		}catch (Exception e) {
			e.printStackTrace(); 
		} 
		
		if(timeOut != 0){
	        new Thread(){
	            @Override
	            public void run()
	            {
	                try{
	                    Thread.sleep(mTimeOut);
	            		if(mProcess != null)
	            			mProcess.destroy();
	            		mProcess = null;
	                }catch(InterruptedException e){
	                    e.printStackTrace();
	                }
	            }
	        }.start();
		}else{
			mProcess.destroy();
			mProcess = null;
		}
	}
    
	public String execCommand(String arg) {
		String result = "";
		
		BufferedReader successResult;
		BufferedReader errorResult;
		Process process = null;
		boolean isPminstallcmd = false;
		boolean isPminstallsuccess = false;

		String[] command = arg.split(" {1,}");
		
		if (command.length > 1){
			if (command[0].equalsIgnoreCase("pm") && command[1].equalsIgnoreCase("install")){
				isPminstallcmd = true;
			}
		}

		try {
			ProcessBuilder processBuilder = new ProcessBuilder(command);
			process = processBuilder.start();

			successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
			errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			
			while ((result = successResult.readLine()) != null) {
				Log.e(TAG, "successResult" + result);
				if (result.contains("Success") && isPminstallcmd){
					isPminstallsuccess = true;
				}
			}

			while ((result = errorResult.readLine()) != null && !isPminstallsuccess) {
				Log.e(TAG, "errorResult" + result);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (process != null)
				process.destroy();
		}
		
		Log.e(TAG, "the cmd is " + arg);
		for (int i = 0; i < command.length; i++){
			Log.e(TAG, "" + command[i]);
		}
		
		Log.e(TAG, " result:"+result);
		
		return result;
	}
	
	public String execCommandPureResult(String arg) {
		String result = "";
		
		BufferedReader successResult;
		BufferedReader errorResult;
		Process process = null;
		
		String[] command = arg.split(" {1,}");

		try {
			ProcessBuilder processBuilder = new ProcessBuilder(command);
			process = processBuilder.start();

			successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
			errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String s_tmp;
			
			while ((s_tmp = successResult.readLine()) != null) {
				result = s_tmp;
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (process != null)
				process.destroy();
		}
		
		Log.d(TAG, "execCommand:"+command+",result:"+result);
		return result;
	}

    public AlertDialog showAlertDialogEx(Context context,int layoutId,int styleId,int titleId, String strText)
    {
        View view = View.inflate(context, layoutId, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context, styleId);
        builder.setView(view);
        builder.setCancelable(false);
        TextView title = (TextView) view.findViewById(titleId);
        if (title != null)
            title.setText(strText);
        AlertDialog alertDialogEx = builder.create();
        alertDialogEx.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialogEx.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialogEx.show();
        return alertDialogEx;
    }

    public AlertDialog showAlertDialogEx(Context context,int layoutId,int styleId,int titleId, int textId){
        String text = context.getString(textId);
        return showAlertDialogEx(context,layoutId,styleId,titleId, text);
    }

	public void showToast(Context context, String text,int layoutId,int textId)
    {
		Toast toast;
		LayoutInflater inflater = LayoutInflater.from(context);
		View layout = inflater.inflate(layoutId, null);
		TextView textview = (TextView) layout.findViewById(textId);
		textview.setText(text);
		toast = new Toast(context);
		toast.setGravity(Gravity.CENTER, 10, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		toast.show();
	}

	public void showToast(Context context, int resId,int layoutId,int textId)
    {
		String text = context.getString(resId);
		showToast(context, text, layoutId, textId);
	}

	public void SendBroadCast(Context context,String bCast){
		if((context != null)&&(bCast != null)) {
			Intent intent = new Intent(bCast);
			context.sendBroadcast(intent);
		}
	}

	public void SendBroadCast(Context context,String bCast,String putTag,String putExtra)
    {
		if((context != null)&&(bCast != null)){
			Intent intent = new Intent(bCast);
			intent.putExtra(putTag, putExtra);
			context.sendBroadcast(intent);
		}
	}

	public void SendBroadCast(Context context,String bCast,String putTag,byte[] putExtra)
    {
		if((context != null)&&(bCast != null)){
			Intent intent = new Intent(bCast);
			intent.putExtra(putTag, putExtra);
			context.sendBroadcast(intent);
		}
	}

    public void SendBroadCast(final Context context,final String bCast,int delay){
        if((context != null)&&(bCast != null)){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(bCast);
                    context.sendBroadcast(intent);
                }
            }, delay);
        }
    }

    public void SendBroadCast(final Context context,final String bCast,final String putTag,final String putExtra,int delay)
    {
        if((context != null)&&(bCast != null)){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(bCast);
                    intent.putExtra(putTag, putExtra);
                    context.sendBroadcast(intent);
                }
            }, delay);
        }
    }

    public void SendBroadCast(final Context context,final String bCast,final String putTag,final byte[] putExtra,int delay)
    {
        if((context != null)&&(bCast != null)){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(bCast);
                    intent.putExtra(putTag, putExtra);
                    context.sendBroadcast(intent);
                }
            }, delay);
        }
    }
}
