package com.android.tools.Misc;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.android.tools.File.UtilFileOper;
import com.android.tools.SysPorp.UtilShareDB;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class UtilStorage {
	private final String TAG = "UtilStorage";
	private static UtilStorage mUtilStorage = null;

	public static synchronized UtilStorage getInstance(){
		if(mUtilStorage == null){
			mUtilStorage = new UtilStorage();
		}
		return mUtilStorage;
	}

    public UtilStorage(){
		Log.d(TAG,"init...");
    }

	public List<String> getInsertInStorageLocations() {
		List<String> mMounts = new ArrayList<String>(10);
		try {
			File mountFile = new File("/proc/mounts");
			if (mountFile.exists()) {
				Scanner scanner = new Scanner(mountFile);
				while (scanner.hasNext()) {
					String line = scanner.nextLine();
						String[] lineElements = line.split(" ");
						if (lineElements.length > 2){
						String element = lineElements[1];
						if (element.contains("sd") && !element.equals(Environment.getExternalStorageDirectory().toString())) {
							mMounts.add(element);
						}
					}
				}
				scanner.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mMounts;
	}

	/*
	 让图片,视频可以立即显示在MTP中.
	 */
	public void addMediaFileOrDirToMTP(Context context, String fileOrDirPath){
		if (context == null || fileOrDirPath == null || fileOrDirPath.equals("")){
			return;
		}
		Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(fileOrDirPath);
		scanIntent.setData(Uri.fromFile(f));
		Log.i(TAG, "addMediaFileOrDirToMTP " + fileOrDirPath);
		context.sendBroadcast(scanIntent);
	}

	public void StorageLimitCopy(String fromPath,String toDir,String toName,int limitSize)
	{
		long floder_size = 0;
		String lost_file = "";

		File toFile = new File(toDir);
		if(toFile.exists() == false){
			toFile.mkdir();
		}

		floder_size = UtilFileOper.getInstance().GetFloderSize(toDir, "*");
		if(floder_size > limitSize){
			lost_file = UtilFileOper.getInstance().GetSortTimeFile(toDir, "*",0).getPath();
			UtilFileOper.getInstance().DeleteFile(lost_file);
			Log.d(TAG,"lost file is externed max,I will delete the oldest file:"+lost_file);
		}

		UtilFileOper.getInstance().CopyFile(fromPath,toDir+toName);
	}

    public long getAvailableSpace() {
        File tFile = new File("/mnt/sdcard/");

        return tFile.getFreeSpace();
    }

    public long LoopRecycleStorage(String path,String ext)
    {
        long iRet = 0;
        File[] subFile;
        String subfilepath = "";
        long modify_time = 0;
        File file_recycle = null;

        File file = new File(path);
        if(file.exists())
        {
            subFile = file.listFiles();
            for(int iListLength = 0; iListLength < subFile.length; iListLength++) {
                if(!subFile[iListLength].isDirectory()) {
                    subfilepath = subFile[iListLength].getPath();
                    if(subfilepath.endsWith(ext)) {//"mp4"
                        File file_sub = new File(subfilepath);
                        if(modify_time == 0) {
                            modify_time = file_sub.lastModified();
                            file_recycle = file_sub;
                        }

                        if(modify_time > file_sub.lastModified()){
                            modify_time = file_sub.lastModified();
                            file_recycle = file_sub;
                        }
                    }
                }
            }
        }

        if(file_recycle != null){
            iRet = file_recycle.length();
            Log.d(TAG,"recycle:"+file_recycle.getPath()+",len:"+iRet);
            file_recycle.delete();
        }

        return iRet;
    }

    public String GenerateFilenameByTime(String dir_path,String ext) {
        File dir = new File(dir_path);
        if(dir.exists() == false){
            dir.mkdirs();
            Log.d(TAG, "create success:" + dir.getPath());
        }

        long dateTaken = System.currentTimeMillis();
        Date date = new Date(dateTaken);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String filename = dateFormat.format(date) + "." +ext;

        String path = dir_path + '/' + filename;

        Log.v(TAG, "New video filename: " + path);
        return path;
    }


    public String  GenerateFileNameWithStorageCheck(String folder,String ext){
        String strDirPath  = UtilShareDB.PATH_MAIN;
        String filePath = UtilStorage.getInstance().GenerateFilenameByTime(strDirPath, ext);

        String mMaxNum = UtilShareDB.getInstance().ReadSysKey(folder+"_max","200");
        String mKeepFree  = UtilShareDB.getInstance().ReadSysKey("storage_keepfree","100000000");

        long iKeepFree = 100000000;
        int iMaxNum = 200;

        try{
            iKeepFree = Long.parseLong(mKeepFree);
            iMaxNum = Integer.parseInt(mMaxNum);
        }catch(NumberFormatException e){
            Log.e(TAG,"fail");
        }

        if(UtilFileOper.getInstance().GetFileNum(strDirPath,ext) > iMaxNum){
            UtilStorage.getInstance().LoopRecycleStorage(strDirPath,ext);
        }

        if(UtilStorage.getInstance().getAvailableSpace() < iKeepFree){
            UtilStorage.getInstance().LoopRecycleStorage(strDirPath,ext);
        }

        return filePath;
    }
}
