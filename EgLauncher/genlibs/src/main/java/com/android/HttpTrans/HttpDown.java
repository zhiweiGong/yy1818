package com.android.HttpTrans;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpDown {
	private static final String TAG = "HttpDown";

    private int BUFFER_SIZE = 1024*255;

	private HttpDownListener mHttpDownListener;
	public void SetHttpDownListener(HttpDownListener listener) {
		mHttpDownListener = listener;
	}

	public HttpDown(){
		Log.d(TAG,"init...");
	}

    public void DownLoadFile(String strHttpUrl, String strSavePath)
	{
		final String mHttpUrl =strHttpUrl;
		final String mSavePath = strSavePath;

		new Thread(){
			@Override
			public void run(){
				startDownLoad(mHttpUrl,mSavePath);
			}
		}.start();
    }

	public int getFileSize(String strUrl)
	{
		int iFileLen = -1;
		int responsecode = 0;
		String sheader;

		try{
			URL url = new URL(strUrl);
			HttpURLConnection httpconnection = (HttpURLConnection)url.openConnection();
			httpconnection.setRequestProperty("user-agent","netfox");
			responsecode=httpconnection.getResponseCode();
			if(responsecode>=400)
			{
				Log.e(TAG,"get response code error:"+responsecode);
				return -2; //-2 represent access is error
			}

			for(int i=1;;i++)
			{
				//datainputstream in = new datainputstream(httpconnection.getinputstream ());
				sheader=httpconnection.getHeaderFieldKey(i);
				if(sheader!=null)
				{
					Log.d(TAG,"Get Header sheader:"+sheader);
					if(sheader.equals("Content-Length"))
					{
						String strConLen = null;
						strConLen = httpconnection.getHeaderField(sheader);
						Log.d(TAG,"Get Header sheader:"+strConLen);
						try{
							iFileLen = Integer.parseInt(strConLen);
						}catch(NumberFormatException e){
							e.printStackTrace ();
						}
						break;
					}
				}else{
					break;
				}
			}
		}catch(IOException e){
			e.printStackTrace ();
		}catch(Exception e){
			e.printStackTrace ();
		}

		Log.d(TAG,"get iFileLen:"+iFileLen);

		return iFileLen;
	}

	public boolean startDownLoad(String strHttpUrl, String strSavePath)
    {
		int     mPkgSize   = 0;
    	int    iReadSize   = 0;
		OutputStream output=null;
		
		try {	 
			URL url=new URL(strHttpUrl);
			Log.d(TAG,"Download URL:"+url);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			InputStream input = conn.getInputStream();
			
			File file=new File(strSavePath);
			Log.d(TAG,"Download Path:"+strSavePath);
			
			if(file.exists()){	  
				Log.d(TAG,"exits delete it");
				file.delete();
			}
			
			file.createNewFile();
			output=new FileOutputStream(file);
			
			byte[] buffer=new byte[BUFFER_SIZE];
			while((iReadSize = input.read(buffer)) != -1){
				output.write(buffer, 0, iReadSize);
				mPkgSize = mPkgSize + iReadSize;
				//Log.d(TAG,"PKG_S:"+mPkgSize);
                if(mHttpDownListener != null){
                    mHttpDownListener.OnSize(mPkgSize);
                }
			}
            if(mHttpDownListener != null){
                mHttpDownListener.OnState("succ",strSavePath);
            }
			output.flush();
			output.close();
			return true;
		}catch(MalformedURLException e){
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}

        if(mHttpDownListener != null){
            mHttpDownListener.OnState("fail",strHttpUrl);
        }
		return false;
    }
}
