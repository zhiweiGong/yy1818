package com.android.EgLauncher.file;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.android.tools.SysPorp.UtilShareDB;

public class FileManager implements Runnable{
	public static final String TAG = "FileManager";
	private static FileManager mInstance = new FileManager();
	public File mFile;
	//private String mSdcardRootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
	private String mSdcardRootPath =  UtilShareDB.PATH_MAIN+"1818record/"; //"";//"/sdcard" + "/";
	private String mWAVrootDirPrefix = "/";
	private String mWAVrootDir = mSdcardRootPath + mWAVrootDirPrefix;
	
	private ArrayList<FileNode> mFileList = new ArrayList<FileNode>();
//	private boolean mHasInit = false;
	private Thread mFileManagerThread = null;
//	private long  mSleepTime = 1 * 1000; //3min to check once;
	private long  mSleepTime = 180 * 1000; //3min to check once;
	private DiskSpaceRecyle mDiskSpaceRecyle = new DiskSpaceRecyle();

	private FileManager(){
		init();		
	}
	public long getWAVFreeSpace(){
		if (mFile == null) {
			mFile = new File(mWAVrootDir);
			if (!mFile.exists()) {
				if (mFile.mkdirs()) {
					Log.d(TAG,"Mkdir" + mWAVrootDir + " success!");
					//return true;
				} else {
					Log.d(TAG,"Mkdir" + mWAVrootDir + " failed!");
					//return false;
				}
			}
		}
		return mFile.getFreeSpace();
	}
	
	public static FileManager getInstance(){
		return mInstance;
	}
	public String getWAVrootDirPath(){
		return mWAVrootDir;
	}
	public File getWAVrootDir(){
		if (mFile == null) {
			mFile = new File(mWAVrootDir);
			if (!mFile.exists()) {
				if (mFile.mkdirs()) {
					Log.d(TAG,"Mkdir" + mWAVrootDir + " success!");
					//return true;
				} else {
					Log.d(TAG,"Mkdir" + mWAVrootDir + " failed!");
					//return false;
				}
			}
		}
		return mFile;
	}
	public String genNewRecodFileName(){
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss"); 
		String fileName = formatter.format(date) + ".wav";
		return fileName;
	}
		
	private void init(){
        Log.e(TAG,"FileManager init");
		if (mFile == null) {
			Log.e(TAG,"mFile == null");
			mFile = new File(mWAVrootDir);
			if (!mFile.exists()) {
				if (mFile.mkdirs()) {
					Log.d(TAG,"Mkdir" + mWAVrootDir + " success!");
					//return true;
				} else {
					Log.d(TAG,"Mkdir" + mWAVrootDir + " failed!");
					//return false;
				}
				Log.e(TAG,"mkdir" + mWAVrootDir);
			} else {
				Log.e(TAG,"mFile exist !!!"+ mFile.toString());
			}
		} else {
			Log.e(TAG,"mFile != null");
		}
		
		initFileList(mFile);
		
		mFileManagerThread = new Thread(this);
		mFileManagerThread.setName(TAG);
		mFileManagerThread.start();
	}
	
	public void initFileList(File dir){
		File[] files = dir.listFiles();
	//	clearFile();
		for (File f : files) {
			if (!f.isDirectory() && f.isFile()) {
				FileNode filenode = new FileNode(f.getAbsolutePath(),
						FileNode.FILE_STATE_IDLE);
				filenode.setLastModifyTime(f.lastModified());
				mFileList.add(filenode);
			}
		}
		Collections.sort(mFileList, new SortByTime());
		
		for (int i = 0; i < mFileList.size(); i++){
			Log.i(TAG, ".i " + mFileList.get(i));
		}
	}
	public void syncFile() {
		ArrayList<FileNode> mSyncFileList = new ArrayList<FileNode>();
		File[] files = mFile.listFiles();
		//	clearFile();
		for (File f : files) {
			if (!f.isDirectory() && f.isFile()) {
				FileNode filenode = new FileNode(f.getAbsolutePath(),
						FileNode.FILE_STATE_IDLE);
				filenode.setLastModifyTime(f.lastModified());
				mSyncFileList.add(filenode);
			}
		}
		Collections.sort(mSyncFileList, new SortByTime());

		for (int i = 0; i < mSyncFileList.size(); i++){
			Log.i(TAG, ".i " + mSyncFileList.get(i));
		}
		mFileList =mSyncFileList;
	}
	public ArrayList<FileNode> getFileList(){

		return mFileList;
	}

	private void fileScan(String file,Context context){
		Log.e(TAG,"scan file " + file);
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(mWAVrootDir + file))));
	}

	private void folderScan(String path,Context context){
		File file = new File(path);

		if(file.exists() && file.isDirectory()){
			File[] array = file.listFiles();
			if (array.length > 0) {
				for(int i=0;i<array.length;i++){
					File f = array[i];

					if(f.isFile()){//FILE TYPE
						String name = f.getName();

						if(name.endsWith(".wav")){
							fileScan(f.getAbsolutePath(),context);
						}
					}
					else {//FOLDER TYPE
						folderScan(f.getAbsolutePath(),context);
					}
				}
			} //else {
			//	context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(mWAVrootDir))));
			//}
		}
	}

	public void addWavRootMTP(Context context){
		if (context == null){
			return;
		}
		Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);//MEDIA_SCANNER_SCAN_DIR
		if (mFile == null) {
			mFile = new File(mWAVrootDir);
			if (!mFile.exists()) {
				//mFile.mkdirs();
				if (mFile.mkdirs()) {
					Log.d(TAG,"Mkdir" + mWAVrootDir + " success!");
					//return true;
				} else {
					Log.d(TAG,"Mkdir" + mWAVrootDir + " failed!");
					//return false;
				}
			}
		}
		folderScan(mWAVrootDir, context);
	//	scanIntent.setData(Uri.fromFile(new File(mWAVrootDir)));
	//	context.sendBroadcast(scanIntent);
	}
	public void addWavFileMTP(Context context, File f){
		if (context == null){
			return;
		}
		Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		scanIntent.setData(Uri.fromFile(f));
		context.sendBroadcast(scanIntent);
	}
	
	/*
	 * sort by time.
	 */
	private class SortByTime implements Comparator<FileNode>{
		@Override
		public int compare(FileNode arg0, FileNode arg1) {
			return ((arg1.getLastModifyTime() - arg0.getLastModifyTime()) > 0L) ? 1 : -1 ;
		}
	}
	
	private boolean addFileNode(File newfile){
		if (!newfile.exists()){
			return false;
		}
		FileNode filenode = new FileNode(newfile.getAbsolutePath(), FileNode.FILE_STATE_IDLE);
		filenode.setLastModifyTime(newfile.lastModified());
		mFileList.add(filenode); 
		Collections.sort(mFileList, new SortByTime());
		return true;
	}
	
	private int mFilePos = 0;
	public FileNode getOldestFile(){
		if (mFileList.size() > 0){
			mFilePos = 0;
			return mFileList.get(0);
		}
		return null;
	}
	
	public FileNode getNewestFile(){
		if (mFileList.size() > 0){
			mFilePos = mFileList.size() - 1;
			return mFileList.get(mFilePos);
		}
		return null;
	}
	
	public FileNode getNextFile(){
		if (mFilePos + 1 < mFileList.size()){
			return mFileList.get(++mFilePos);
		}
		return getOldestFile();
	}
	
	public FileNode getPreFile(){
		if (mFilePos - 1 > 0){
			return mFileList.get(--mFilePos);
		}
		return getNewestFile();
	}
	public FileNode getNumFile(int pos){
		if (pos < mFileList.size())
			return mFileList.get(pos);
		else
			return getNewestFile();
	}
	public boolean delFile(String filePath){
		for (int i = 0; i < mFileList.size(); i++){
			if (mFileList.get(i).getFilePath().equals(filePath)){
				//check file status ?. and del it 
				mFileList.get(i).delete();
				mFileList.remove(i);
				return true;
			}
		}
		
		return false;
	}
	public boolean clearFile(){
		for (int i = 0; i < mFileList.size(); i++){
			//if (mFileList.get(i).getFilePath().equals(filePath)){
				//check file status ?. and del it
			//	mFileList.get(i).delete();
				mFileList.remove(i);
				return true;
			//}
		}
		return false;
	}
	@Override
	public void run() {
		while (true){
			if (mDiskSpaceRecyle.isDiskLowSpace()){
				mDiskSpaceRecyle.deleteOldestFile(mFileList);
			}
			
			try {
				Thread.sleep(mSleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
		
	
}
