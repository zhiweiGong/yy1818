package com.android.HttpTrans;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpSend {
	private final String TAG = "HttpSend";

	private HttpSendListener mHttpSendListener;
	public void SetHttpSendListener(HttpSendListener listener) {
		mHttpSendListener = listener;
	}

    public HttpSend(){
		Log.d(TAG,"init...");
    }

    public String HttpSendString(String httpUrl,String sendStr)
    {
    	String RetLine = "";

		if(sendStr.equals("")){
			return RetLine;
		}
		
    	try{
        	URL url = new URL(httpUrl);
			
	    	HttpURLConnection conn = (HttpURLConnection)url.openConnection();
	    	conn.setConnectTimeout(6000);
	    	conn.setReadTimeout(60000);// 1 min read out
	    	conn.setDoOutput(true);
	    	conn.setDoInput(true);
	    	conn.setRequestMethod("POST");
	    	conn.setRequestProperty("User-Agent", "Fiddler");
	    	conn.setRequestProperty("Content-Type", "text/plain");
	    	conn.setRequestProperty("Charset", "UTF-8"); 
	    	conn.connect();
	    	OutputStream os = conn.getOutputStream();
	    	DataOutputStream out = new DataOutputStream(os);
	    	out.writeBytes(sendStr);
	    	out.flush();
	    	out.close();

	    	int code = conn.getResponseCode();
	    	if(code == 200){
	    		Log.d(TAG,"Response Success,Code:"+code);
		    	InputStream php_is = conn.getInputStream();
		    	RetLine = dealResponseResult(php_is);
		    	php_is.close();
	    	}else{
	    		Log.d(TAG,"Response Fail,Code:"+code);
	    	}
	    	
            conn.disconnect();
    	}catch (Exception e){
    		e.printStackTrace();
    	}
    	
    	Log.i(TAG, "HttpSendString httpUrl:"+httpUrl+",sendlen:"+sendStr.length());
    	return RetLine;
	}

	public void SendFileThread(String strHttpUrl, String strFilePath){
		final String mHttpUrl = strHttpUrl;
		final String mFilePath = strFilePath;

		new Thread(){
			@Override
			public void run(){
				SendFile(mHttpUrl,mFilePath);
			}
		}.start();
	}

	public String SendFile(String strHttpUrl, String strFilePath)
	{
		String RetLine = "";
		int    responseCode = 0;
		
		File fd_send = new File(strFilePath);
		
		if(fd_send.exists() == false){
			Log.e(TAG,"file is emputy");
			return RetLine;
		}
		
		if(fd_send.length() == 0){
			Log.e(TAG,"file is emputy");
			return RetLine;
		}
		
    	try{
        	URL url = new URL(strHttpUrl);
			
	    	HttpURLConnection conn = (HttpURLConnection)url.openConnection();
	    	conn.setConnectTimeout(3000);
	    	conn.setReadTimeout(3000);
	    	conn.setDoInput(true);
	    	conn.setDoOutput(true);
	    	conn.setUseCaches(false);
	    	conn.setRequestMethod("POST");
	    	conn.setRequestProperty("User-Agent", "Fiddler");
	    	conn.setRequestProperty("Content-Type", "application/zip");
	    	conn.setRequestProperty("Charset", "UTF-8");
	    	conn.setRequestProperty("Content-Length", String.valueOf(fd_send.length()));
	    	conn.setRequestProperty("Connection", "Keep-Alive");
	    	conn.connect();
	    	OutputStream os = conn.getOutputStream();
	    	DataOutputStream out = new DataOutputStream(os);
	    	
            InputStream file_is = new FileInputStream(fd_send);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = file_is.read(buffer)) != -1) {
            	out.write(buffer, 0, len);

                if(mHttpSendListener != null){
                    mHttpSendListener.OnSize(len);
                }
            }
            file_is.close();
	    	
	    	out.flush();
	    	out.close();

	    	responseCode = conn.getResponseCode();
	    	if(responseCode == 200){
		    	InputStream php_is = conn.getInputStream();
		    	RetLine = dealResponseResult(php_is);  
	            php_is.close();

				if(mHttpSendListener != null){
					mHttpSendListener.OnState("succ",strFilePath);
				}

                Log.d(TAG,"Response Success,Code:"+responseCode);
	    	}else{
				if(mHttpSendListener != null){
					mHttpSendListener.OnState("fail",strFilePath);
				}

                Log.d(TAG,"Response Fail,Code:"+responseCode);
	    	}
            conn.disconnect();
    	}catch (Exception e){
    		e.printStackTrace();
			if(mHttpSendListener != null){
				mHttpSendListener.OnState("fail",strFilePath);
			}
    	}

		return RetLine;
	}
	
	private String dealResponseResult(InputStream inputStream) {
		String resultData;      //处理结果
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		
		try {
			while((len = inputStream.read(data)) != -1) {
				byteArrayOutputStream.write(data, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		resultData = new String(byteArrayOutputStream.toByteArray());
		
		return resultData;
	}    
}
