package com.android.tools.File;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class UtilFileZip {
	private final String TAG = "UtilFileZip";
	private static UtilFileZip mUtilFileZip = null;

	private static final int BUFFER = 2048;

	public static synchronized UtilFileZip getInstance(){
		if(mUtilFileZip == null){
			mUtilFileZip = new UtilFileZip();
		}
		return mUtilFileZip;
	}

    public UtilFileZip(){
		Log.d(TAG,"init...");
    }

	public void ZipFile(String src_url) {
		String src_FilePath = src_url.substring(0,src_url.lastIndexOf("/"));
		String src_FileName = src_url.substring(src_url.lastIndexOf("/") + 1);
		String zip_File = src_FilePath+"/"+src_FileName.substring(0,src_FileName.lastIndexOf("."))+".zip";
		BufferedInputStream origin = null; 

		byte data[] = new byte[BUFFER]; 
		File src_file = new File(src_url);
		
		Log.d(TAG,"src_url:"+src_url);
		Log.d(TAG,"zip_File:"+zip_File);
		
		if(src_file.exists() == false){
			Log.d(TAG,"file is not exists");
			return;
		}
		
		try{ 
			FileOutputStream dest = new FileOutputStream(zip_File); 
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest)); 

			FileInputStream fi = new FileInputStream(src_file); 
			origin = new BufferedInputStream(fi, BUFFER); 
			ZipEntry entry = new ZipEntry(src_FileName); 
			out.putNextEntry(entry); 
			int count; 
			while ((count = origin.read(data, 0, BUFFER)) != -1) { 
				out.write(data, 0, count); 
			} 
			origin.close(); 
			out.close(); 
			Log.d(TAG,"zip:"+src_FileName+",success");
		}catch(Exception e){ 
			e.printStackTrace(); 
		} 
	} 

	public void ZipDir(String folder_url) {
    	File[] subFile;
    	
    	File fd_folder = new File(folder_url);
 	    if(fd_folder.exists() == false)
 	    {
 	    	Log.e(TAG,"folder not exists:"+folder_url);
 	    	return;
 	    }
 	    
    	if(fd_folder.isDirectory() == false){
    		Log.e(TAG,"not a folder exists:"+folder_url);
    		return;
    	}
    	
		try {
			String zip_url = fd_folder.getParent()+"/"+fd_folder.getName()+".zip";
			
			FileOutputStream dest = new FileOutputStream(zip_url);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest)); 
	    	subFile = fd_folder.listFiles();
	    	
	    	for (int iLen = 0; iLen < subFile.length; iLen++) {
	    		if (!subFile[iLen].isDirectory()) {
	    			String filename = subFile[iLen].getName(); 
	    			Zip_In(folder_url+filename,out);
	    		}
	    	}
	    	
	    	out.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}  catch (IOException e) {
			e.printStackTrace();
		} 
		
	} 
	
	public void Zip_In(String file_path,ZipOutputStream out)
	{
		int count; 
		byte data[] = new byte[BUFFER]; 
		File fd_file = new File(file_path);
		
 	    if(fd_file.exists() == false)
 	    {
 	    	Log.e(TAG,"file not exists:"+file_path);
 	    	return;
 	    }
 	    
		try{ 
			FileInputStream fi = new FileInputStream(fd_file); 
			BufferedInputStream origin = new BufferedInputStream(fi, BUFFER); 
			ZipEntry entry = new ZipEntry(fd_file.getName()); 
			out.putNextEntry(entry); 
			while ((count = origin.read(data, 0, BUFFER)) != -1) { 
				out.write(data, 0, count); 
			} 
			origin.close(); 
			Log.d(TAG,"zip:"+file_path+",success");
		}catch(Exception e){ 
			e.printStackTrace(); 
		} 
	}
}
