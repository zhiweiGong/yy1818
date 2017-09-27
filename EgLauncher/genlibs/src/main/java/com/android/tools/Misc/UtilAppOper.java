package com.android.tools.Misc;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.util.List;

public class UtilAppOper {
	private final String TAG = "UtilAppOper";
	private static UtilAppOper mUtilAppOper = null;
	
	public static synchronized UtilAppOper getInstance(){
		if(mUtilAppOper == null){
			mUtilAppOper = new UtilAppOper();
		}
		return mUtilAppOper;
	}

    public UtilAppOper(){
		Log.d(TAG,"init...");
    }

	public String getPackageVersion(Context context,String pkg_name,int iflag) {
		String version = "";
		
	    try {
	        PackageManager manager = context.getPackageManager();
	        PackageInfo info = manager.getPackageInfo(pkg_name, iflag);
	        version = info.versionName;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    return version;
	}
	
    public String getTopActivityClassName(Context context){
        String topActivityClassName=null;
        ActivityManager activityManager = (ActivityManager)(context.getSystemService(android.content.Context.ACTIVITY_SERVICE)) ;
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1) ;

        if(runningTaskInfos != null){
            ComponentName f=runningTaskInfos.get(0).topActivity;
            topActivityClassName=f.getClassName();
        }else{
            Log.e(TAG, "topActivityClassName not found!!!!!!! ");
        }

        return topActivityClassName;
    }

	public boolean isRunningForeground(Context context,String pkgName){
		String topActivityClassName=getTopActivityClassName(context);
		Log.d(TAG,"isRunningForeground check pkgName="+pkgName+",get topActivityClassName="+topActivityClassName);

		if(topActivityClassName!=null&&topActivityClassName.startsWith( pkgName)) {
			Log.d(TAG,pkgName+"---> is RunningForeGround");
			return true;
		} else {
			Log.d(TAG,pkgName+"---> is not RunningForeGround");
			return false;
		}
	}

    public boolean isAppProcessRunning(Context context,String pkgName){
    	boolean isAppRunning = false;
    	
    	ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
    	List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();  

    	for (RunningAppProcessInfo appProcess : appProcesses) {
    		if (appProcess.processName.equals(pkgName) == true) {
    			isAppRunning = true;
    			break;
    		}
    	}
    	
    	return isAppRunning;
    }
    
	public boolean isServiceWork(Context context,String package_name) {
		ActivityManager mActivityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> currentService = mActivityManager.getRunningServices(100);
		final String igrsClassName = package_name;
		boolean b = igrsBaseServiceIsStart(currentService, igrsClassName);
		return b;
	}

	private boolean igrsBaseServiceIsStart(List<ActivityManager.RunningServiceInfo> mServiceList,String className) {
		for (int i = 0; i < mServiceList.size(); i++) {
			ActivityManager.RunningServiceInfo t = mServiceList.get(i);
			if (className.equals(t.service.getClassName()) && t.pid > 150) {
				return true;
			}
		}
		return false;
	}

    public boolean startActivity(Context context,Class<?> clz){
        try {
            Intent intent = new Intent(context,clz);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return  true;
    }

	public boolean startActivity(Context context,String pkgName) {
		try {
			Intent resolveIntent = context.getPackageManager().getLaunchIntentForPackage(pkgName);
			context.startActivity(resolveIntent);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return  true;
	}

    public boolean startActivity(Context context,String pkg_name,String class_name) {
        Intent newIntent = new Intent();
        newIntent.setClassName(pkg_name, class_name);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			context.startActivity(newIntent);
			Log.d(TAG, "Start Activity:"+class_name);
		} catch (ActivityNotFoundException ex) {
			Log.e(TAG, "Activity not found");
            return false;
		}
        return  true;
    }
    
	public String PackageInstall(Context context,String pkg_path) {
		String ret = "";
		Uri mApkUri = Uri.fromFile(new File(pkg_path));
		
		Log.e(TAG,"file install:"+pkg_path);
		
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(mApkUri,"application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("zminstall", "quiet");
		context.startActivity(intent);

	    return ret;
	}
	
	public String PackageUnInstall(Context context,String pkg_path){
		String ret = "";
		
		Intent intent = new Intent();
	    intent.setAction(Intent.ACTION_DELETE);
	    intent.setData(Uri.parse("package:"+pkg_path));
	    context.startActivity(intent);
	    
	    Log.d(TAG,"apk uninstall:"+pkg_path);
	    
	    return ret;
	}
	
	public String GetApkInstallPath(Context context,String apkPackageName) {
		String str_install_path = "";
		
		try {
			str_install_path = context.getPackageManager().getApplicationInfo(apkPackageName, 0).sourceDir;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		return str_install_path;
	}
}
