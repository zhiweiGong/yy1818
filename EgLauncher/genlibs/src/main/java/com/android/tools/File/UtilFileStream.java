package com.android.tools.File;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.security.MessageDigest;

public class UtilFileStream {
	private final String TAG = "UtilFileStream";
	private static UtilFileStream mUtilFileStream = null;
	
	public static synchronized UtilFileStream getInstance(){
		if(mUtilFileStream == null){
			mUtilFileStream = new UtilFileStream();
		}
		return mUtilFileStream;
	}

    public UtilFileStream(){
		Log.d(TAG,"init...");
    }

	public String ReadRawFile(String path){
		String srtRawFile = "";
		String line = "";

		File file = new File(path);

		try{
			if (file.exists() && file.length() > 0){
				InputStream unputstream = new FileInputStream(file);
				if (unputstream != null)
				{
					InputStreamReader inputstreamreader = new InputStreamReader(unputstream);
					BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

					while (( line = bufferedreader.readLine()) != null) {
						//Log.i(TAG, "line:"+line);
						srtRawFile = srtRawFile+line+"\r\n";//dangerous not change it!!!
					}
					bufferedreader.close();
					inputstreamreader.close();
					unputstream.close();
				}
			}else{
				//Log.e(TAG,"file not exists or emputy");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Log.i(TAG, "ReadFile path:"+path+",readlen:"+srtFile);
		return srtRawFile;
	}

	public boolean WriteRawFile(String to_path,String f_data,boolean f_new){
        boolean ret = true;

		if(f_data.length() == 0){
			Log.i(TAG,"f_data is null exit.");
			return false;
		}

		File toFile = new File(to_path);
		if(toFile.exists() == false){
			Log.d(TAG,"file not exists.");
			try {
				toFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		try {
			long i_seek = 0;

			if(f_new == true){
				toFile.delete();
				toFile.createNewFile();
				i_seek = 0;
			}else{
				i_seek = toFile.length();
			}
			RandomAccessFile out = new RandomAccessFile(toFile, "rw");
			out.seek(i_seek);
			out.write(f_data.getBytes(), 0, f_data.getBytes().length);
			out.close();
			//Log.i(TAG, "SaveToStorage path:"+file_path+",len:"+f_data.length()/*+"\n"+f_data*/);
            ret = true;
		} catch (IOException e) {
			e.printStackTrace();
            ret = false;
		}

		return ret;
	}

	public String ReadInfoFile(String strFileAbsPath,String stag)
	{
		int sub_a;
		String sRet = null;

		String[] content = ReadFileLine(strFileAbsPath);

		if(content == null){
			return null;
		}

		for(int i = 0; i < content.length; i++){
			if(content[i].startsWith(stag) == true){
				sub_a = stag.length();
				if(content[i].length() > sub_a){
					sRet = content[i].substring(sub_a+1);
				}else{
                    sRet = content[i];
                }
			}
			//Log.d(TAG,"content["+i+"]:"+content[i]);
		}
		//Log.d(TAG,"filePath:"+filePath+",sRet:"+sRet+",stag:"+stag);

		return sRet;
	}

	public String[] ReadFileLine(String strFileAbsPath)
	{
		int i_len = 0;

		String[] content = new String[1024];
		File file = new File(strFileAbsPath);

		//Log.d(TAG,"ReadFileLine filepath:"+filepath);
		try {
			InputStream instream = new FileInputStream(file);
			if (instream != null) {
				InputStreamReader inputstreamreader = new InputStreamReader(instream);
				BufferedReader buffreader = new BufferedReader(inputstreamreader);
				String line;
				while (( line = buffreader.readLine()) != null) {
					if(i_len < 1024){
						content[i_len] = line;
						i_len++;
						//Log.d(TAG,"line:"+line);
					}else{
						break;
					}
				}
				buffreader.close();
				inputstreamreader.close();
				instream.close();
			}
		} catch (FileNotFoundException e) {
			Log.d(TAG,"The File doesn't not exist:"+strFileAbsPath);
		} catch (IOException e) {
			Log.d(TAG,"IOException:"+strFileAbsPath+",:"+e);
		}

		String[] ret = null;
		if (i_len > 0) {
			ret = new String[i_len];

			for (int i = 0; i < i_len; i++) {
				ret[i] = content[i];
			}
		}
		return ret;
	}

	public boolean WriteInfoFile(String strFileAbsPath,String stag,String value)
	{
		String file_read = "";

		File file = new File(strFileAbsPath);
		//Log.d(TAG,"filePath:"+filePath+",stag:"+stag+",value:"+value);
		try {
			InputStream instream = new FileInputStream(file);
			if (instream != null) {
				InputStreamReader inputreader = new InputStreamReader(instream);
				BufferedReader buffreader = new BufferedReader(inputreader);
				String line;
				while (( line = buffreader.readLine()) != null) {
					if(line.startsWith(stag) == true){
						file_read = file_read+stag + "=" +value+"\n";
					}else{
						file_read = file_read+line+"\n";
					}
				}
				buffreader.close();
				inputreader.close();
				instream.close();

				RandomAccessFile out = new RandomAccessFile(file, "rw");
				out.seek(0);
				out.write(file_read.getBytes(), 0, file_read.getBytes().length);
				out.close();

				return true;
			}
		}catch (FileNotFoundException e){
			Log.e(TAG,"The File doesn't not exist,stag:"+stag+",value:"+value);
		}catch (IOException e) {
			Log.e(TAG,"IOException,stag:"+stag+",value:"+value);
		}

		return false;
	}

	public String md5sum(String strFileAbsPath) {
		String ret = "000";
		InputStream fis;
		byte[] buffer = new byte[1024];
		int numRead = 0;
		MessageDigest md5;
		char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9','a', 'b', 'c', 'd', 'e', 'f' };

		try {
			fis = new FileInputStream(strFileAbsPath);
			md5 = MessageDigest.getInstance("MD5");
			while((numRead=fis.read(buffer)) > 0) {
				md5.update(buffer,0,numRead);
			}
			fis.close();
			byte[] b = md5.digest();
			StringBuilder sb = new StringBuilder(b.length * 2);

			for(int i = 0; i < b.length; i++) {
				sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
				sb.append(HEX_DIGITS[b[i] & 0x0f]);
			}
			ret = sb.toString();
			Log.d(TAG,strFileAbsPath+",MD5:"+ret);
		}catch(Exception e) {
			Log.d(TAG,"md5 gen fail:"+strFileAbsPath);
		}

		return ret;
	}
}
