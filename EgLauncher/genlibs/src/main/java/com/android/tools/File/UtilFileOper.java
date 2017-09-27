package com.android.tools.File;

import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UtilFileOper {
	private final String TAG = "UtilFileStream";
	private static UtilFileOper mUtilFileOper = null;

	public static synchronized UtilFileOper getInstance(){
		if(mUtilFileOper == null){
			mUtilFileOper = new UtilFileOper();
		}
		return mUtilFileOper;
	}

    public UtilFileOper(){
		Log.d(TAG,"init...");
    }

	public boolean RenameFile(String srt_from,String str_to)
	{
		File fromfile = new File(srt_from);
		File tofile = new File(str_to);

		if(fromfile.exists()){
			fromfile.renameTo(tofile);
			Log.d(TAG,"file rename:"+srt_from+"->"+str_to);
			return true;
		}else{
			Log.e(TAG,"file not exists:"+srt_from+"->"+str_to);
			return false;
		}
	}

	public boolean CopyFile(File src, File dest) {
		String destPath = "" + dest.getAbsolutePath();
		boolean copysuccess = false;

		int copybyte = 0;
		
		if ((src == null) || (src.exists() == false)){
			return false;
		}

		if (dest.exists() && dest.canWrite()) {
			dest.delete();
		}

		dest = new File(destPath);
		try {
			dest.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}

		try {
			OutputStream outputstream = new FileOutputStream(destPath);
			InputStream inputstream = new FileInputStream(src);
			byte[] buffer = new byte[1024];
			int length = inputstream.read(buffer);
			while (length > 0) {
				outputstream.write(buffer, 0, length);
				copybyte += length;
				length = inputstream.read(buffer);
			}

			outputstream.flush();
			inputstream.close();
			outputstream.close();
			copysuccess = true;
		} catch (Exception e) {
			e.printStackTrace();
			copysuccess = false;
		}
		
		Log.i(TAG, ".copyFile " + src.getAbsolutePath() + " to  " + dest.getAbsoluteFile());
		return (dest.length() == src.length()) && copysuccess && copybyte > 2;
	}

    public boolean CopyFile(String from, String to)
	{
 	    File file_From = new File(from);
 	    if(file_From.exists() == false){
 	    	Log.e(TAG, "CopyFile fromFile is not exists:"+from);
 	    	return false;
 	    }
 	    
 	    File file_To = new File(to);
 	    if(file_To.exists()){
 	    	Log.e(TAG, "CopyFile toFile is exists,delete:"+to);
			file_To.delete();
 	    }

		return CopyFile(file_From,file_To);
	}

	public boolean CopyDir(String fromDir, String toDir)
    {
	   File root = new File(fromDir);

	   if(root.exists() == false) {
		   return false;
	   }
	   File[] currentFiles = root.listFiles();
	   
	   File targetDir = new File(toDir);
	   if(targetDir.exists() == false) {
		   targetDir.mkdirs();
	   }
	   
	   for(int i= 0;i<currentFiles.length;i++) {
		   if(currentFiles[i].isDirectory() == true) {
			   CopyDir(currentFiles[i].getPath() + "/", toDir + currentFiles[i].getName() + "/");
		   } else {
			   CopyFile(currentFiles[i].getPath(), toDir + currentFiles[i].getName());
		   }
	   }
	   
	   return true;
    }
    
    public boolean FileExists(String path)
    {
		File file = new File(path);
		
		if(file.exists()){
			Log.d(TAG,"file is exists:"+path);
			return true;
		}else{
			Log.d(TAG,"file not exists:"+path);
			return false;
		}
    }
    
    public boolean DeleteFile(String path)
    {
		File file = new File(path);
		
		if(file.exists()){
			file.delete();
	    	Log.d(TAG,"file is delete:"+path);
			return true;
		}else{
			Log.e(TAG,"file is not exists:"+path);
			return false;
		}
    }
   
	public boolean DeleteDir(String pathDir) {
		File file = new File(pathDir);

		if((pathDir==null)||(file == null)||(file.exists() == false)){
			return false;
		}

		if (file.isFile() == true) {
			file.delete();
			return true;
		}
		
		if(file.isDirectory()){
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return true;
			}
			
			for (int i = 0; i < childFiles.length; i++) {
				DeleteDir(childFiles[i].getPath());
			}
			file.delete();
		}
		return true;
	}

    public boolean MkDir(String path)
    {
		File file = new File(path);
    	if(!file.exists()){
    		Log.d(TAG,"floder not exists creat it.");
    		file.mkdir();
			return true;
    	}else{
    		Log.e(TAG,"floder exists exit!");
			return false;
    	}
    }
   
    public boolean MergeFile(String file_a,String file_b)
    {
		File File_a = new File(file_a);
		File File_b = new File(file_b);
		
		if(File_a.exists() == false){	  
			Log.d(TAG,"file a not exists:"+file_a);
			return false;
	    }
		
		if(File_b.exists() == false){
			Log.d(TAG,"file b not exists:"+file_b);
			return false;
	    }
		
		try {
			int length = 0;
			File_a.createNewFile();
			RandomAccessFile out = new RandomAccessFile(File_a, "rw");
			out.seek(File_a.length());
			
			Log.d(TAG,"File Len:"+File_a.length());
			
			FileInputStream fis = new FileInputStream(File_b);
            byte[] tmpBytes = new byte[1024];
            while((length = fis.read(tmpBytes)) != -1){
            	out.write(tmpBytes,0,length);
            	//Log.d(TAG,"LEN:"+length);
            }
            
            fis.close();
			out.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
    }

	public File GetSortTimeFile(String path,String ext,int pos)
	{
		final String f_ext = ext;

		File[] files;
		File parentFile = new File(path);

		Log.i(TAG, ".getFileName " + ".path: " + path + ".ext : " + ext + ". pos " + pos);

		if(ext.equals("*") == true){
			files = parentFile.listFiles();
		}else{
			FileFilter fileFilter = new FileFilter(){
				public boolean accept(File file) {
					String tmp = file.getName().toLowerCase();
					if (tmp.endsWith(f_ext)){
						return true;
					}
					return false;
				}
			};

			files = parentFile.listFiles(fileFilter);
		}

		List<File> fileList = Arrays.asList(files);
		Collections.sort(fileList, new FileComparator());
		File[] sortList = (File[])fileList.toArray();

		if(pos > sortList.length){
			Log.e(TAG,"pos is max then list size.");
			return null;
		}

		return sortList[pos];
	}

	public File[] GetFileList(String path,String ext)
	{
		final String f_ext = ext;

		File[] files;
		File parentFile = new File(path);

		Log.i(TAG, ".GetFileList " + ".path: " + path + ".ext : " + ext);

		if(ext.equals("*") == true){
			files = parentFile.listFiles();
		}else{
			FileFilter fileFilter = new FileFilter(){
				public boolean accept(File file) {
					String tmp = file.getName().toLowerCase();
					if (tmp.endsWith(f_ext)){
						return true;
					}
					return false;
				}
			};

			files = parentFile.listFiles(fileFilter);
		}

		if(files != null){
			List<File> fileList = Arrays.asList(files);
			Collections.sort(fileList, new FileComparator());

			return (File[])fileList.toArray();
		}else{
			return null;
		}
	}

	private class FileComparator implements Comparator<File> {
        public int compare(File file1, File file2) {
            return file1.lastModified() < file2.lastModified() ? -1 : 1;
        }
    }
    
	public int GetFileNum(String path,String ext)
	{
    	int iRet = 0;
    	File[] subFile;
    	int iListLength = 0;
    	String filename = "";

		File file = new File(path);
 	    if(file.exists()) {
	    	subFile = file.listFiles();
	    	for (iListLength = 0; iListLength < subFile.length; iListLength++) {
	    		if (!subFile[iListLength].isDirectory()) {
	    			filename = subFile[iListLength].getName();
					if(ext.equals("*") == true){
						iRet++;
					}else{
						if (filename.endsWith(ext)) {//"zip"
							iRet++;
						}
					}
	    		}
	    	}
 	    }
 	    
 	    Log.d(TAG ,"CheckFileNum:" +iRet);
 	    return iRet;
	}
	
	public long GetFloderSize(String path,String ext)
	{
    	long iRet = 0;
    	File[] subFile;
    	int iListLength = 0;
    	String filename = "";

		File file = new File(path);
 	    if(file.exists())
 	    {
	    	subFile = file.listFiles();
	    	for (iListLength = 0; iListLength < subFile.length; iListLength++) {
	    		if (!subFile[iListLength].isDirectory()) {
	    			filename = subFile[iListLength].getName();

					if(ext.equals("*") == true){
						iRet = iRet + subFile[iListLength].length();
					}else{
						if (filename.endsWith(ext)) {//".zip"
							iRet = iRet + subFile[iListLength].length();
						}
					}
	    		}
	    	}
 	    }
 	    
 	    Log.d(TAG ,"CheckFileNum:" +iRet);
 	    return iRet;
	}

	public long GetFileSize(String path)
	{
		long iRet = 0;

		File file = new File(path);
		if(file.exists())
		{
			iRet = file.length();
		}

		Log.d(TAG ,"file length:" +iRet);
		return iRet;
	}
}
