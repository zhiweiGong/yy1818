package com.android.tools.SysPorp;

import android.content.Context;
import android.util.Log;

import com.android.tools.File.UtilAssetFiles;

import java.io.File;
import java.io.IOException;

public class UtilShareDB {
	private final String TAG = "UtilShareDB";
	private static UtilShareDB mUtilShareDB = null;

    private DBaseHelper mDbaseSysHelper;
    private DBaseHelper mDbaseDynHelper;

    public static final String PATH_MAIN = "sdcard/trunk/";
    private static final String mDBase_AbsPath = PATH_MAIN+"dbase.sqlite";

    public static synchronized UtilShareDB getInstance(){
		if(mUtilShareDB == null){
			mUtilShareDB = new UtilShareDB();
		}
		return mUtilShareDB;
	}

    public UtilShareDB(){

        Log.d(TAG,"init...");
    }

    public void InitSystem(Context context)
    {
        /*File file_MainPath = new File(PATH_MAIN);
        if(file_MainPath.exists() == false){
            Log.d(TAG,"file not exit so mkdir it");
            file_MainPath.mkdir();
        }*/
        createDir(PATH_MAIN);
        DBaseContext dbContext = new DBaseContext(context,mDBase_AbsPath);
        mDbaseSysHelper =new DBaseHelper(dbContext,mDBase_AbsPath);
        mDbaseSysHelper.CreateTable("sys_table");
        mDbaseDynHelper =new DBaseHelper(dbContext,mDBase_AbsPath);
        mDbaseDynHelper.CreateTable("dyn_table");

        String Model = android.os.Build.MODEL;
        if(Model.equals("aeon6735_66t_v_l1")){
            WriteSysKey("path_dev_s","/sys/devices/mtk-msdc.0/11230000.MSDC0/mmc_host/mmc0/mmc0:0001/cid");
        }else if(Model.equals("aston")){
            WriteSysKey("path_dev_s","/sys/devices/platform/sunxi-mmc.2/mmc_host/mmc0/mmc0:0001/cid");
        }

        try{
            String[] strLines = UtilAssetFiles.getInstance().ReadAssetFileLine(context,"trunk/FixProperty.txt");
            for(int i = 0; i < strLines.length; i++){
                String[] strVar = strLines[i].split("=");
                if(strVar.length == 2){
                    WriteSysKey(strVar[0],strVar[1]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

		UtilAssetFiles.getInstance().CopyAssetDir(context, "trunk", PATH_MAIN);
    }
    public static boolean createFile(String destFileName) {
        File file = new File(destFileName);
        if(file.exists()) {
            System.out.println("create single file" + destFileName + "failed exit！");
            return false;
        }
        if (destFileName.endsWith(File.separator)) {
            System.out.println("create single file" + destFileName + "failed con't be a dir");
            return false;
        }
        //if the file exit
        if(!file.getParentFile().exists()) {
            //if the dir not exit ,create it
            System.out.println("dir don't exit create it！");
            if(!file.getParentFile().mkdirs()) {
                System.out.println("create dir failed！");
                return false;
            }
        }
        //create the file
        try {
            if (file.createNewFile()) {
                System.out.println("create single file" + destFileName + "success！");
                return true;
            } else {
                System.out.println("create single file" + destFileName + "failed！");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("create single file" + destFileName + "failed！" + e.getMessage());
            return false;
        }
    }
    public boolean createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {
            Log.d(TAG,"Mkdir" + destDirName + "failed exit");
            return false;
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        //mkdir
        if (dir.mkdirs()) {
            Log.d(TAG,"Mkdir" + destDirName + "success！");
            return true;
        } else {
            Log.d(TAG,"Mkdir" + destDirName + "failed！");
            return false;
        }
    }
    public String ReadSysKey(String tag_name,String def)
	{
		String strRet = mDbaseSysHelper.query(tag_name);

		if(strRet == null){
			strRet = def;
            Log.e(TAG,"ReadSysKey is null:"+tag_name);
		}

		return strRet;
	}

	public void WriteSysKey(String tag_name,String tag_value)
    {
        String strItem = mDbaseSysHelper.query(tag_name);
        Log.e(TAG, "DB ok");
        if(strItem == null){
            mDbaseSysHelper.insert(tag_name,tag_value);
        }else{
            if(strItem.equals(tag_value) == true){
                Log.d(TAG,"the item in sys is the same,skip it");
            }else{
                mDbaseSysHelper.update(tag_name,tag_value);
            }
        }
    }

    public String ReadDynKey(String tag_name,String def)
    {
        String strRet = mDbaseDynHelper.query(tag_name);

        if(strRet == null){
            strRet = def;
        }

        return strRet;
    }

    public void WriteDynKey(String tag_name,String tag_value)
    {
        String strItem = mDbaseDynHelper.query(tag_name);
        if(strItem == null){
            mDbaseDynHelper.insert(tag_name,tag_value);
        }else{
            if(strItem.equals(tag_value) == true){
                Log.d(TAG,"the item in dyn is the same,skip it");
            }else{
                mDbaseDynHelper.update(tag_name,tag_value);
            }
        }
    }
}
