package com.android.LongSocket;

import android.util.Log;

import com.android.tools.Misc.UtilPack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;

public class DealRespose {
    private static final String TAG = "DealRespose";

    private LongSocketClient mLongSocketClient;
	private DealResposeListener mDealResposeListener;

    public DealRespose(LongSocketClient longSocketClient){
    	mLongSocketClient = longSocketClient;
    }

    public void SetDealResposeListener(DealResposeListener listener) {
        mDealResposeListener = listener;
    }

    public void ReadRespose() {
        new Thread(){
            @Override
            public void run(){
            	while(true){
            		if(mLongSocketClient.mSocket != null){
		            	String strReadInJson;

		        		try {
                            DataInputStream input = new DataInputStream( mLongSocketClient.mSocket.getInputStream());
                            while(true) {
                                byte[] tag_data = new byte[UtilPack.MAX_TAG_LEN];
                                int iTagLen = input.read(tag_data, 0, tag_data.length);
                                if (iTagLen == tag_data.length) {
                                    String tag = new String(tag_data);
                                    tag = tag.trim();
                                    ReadPackage(tag, input);
                                } else {
                                    if(iTagLen > 0){
                                        Log.e(TAG, "read tag len error");
                                    }
                                    break;
                                }
                            }
		        		} catch (IOException e) {
		        			e.printStackTrace();
		        		}
		                
		        		mLongSocketClient.SocketConnect();
            		}
	        		
	                try{
	                    Thread.sleep(2000);
	                }catch(InterruptedException e){
	                    e.printStackTrace();
	                }
            	}
            }
        }.start();
	}

	private boolean ReadPackage(String tag_value,DataInputStream input){
		boolean ret = false;

		try {
			JSONObject dataJson = new JSONObject(tag_value);
			String strTag = dataJson.getString("tag");
			String strVal = dataJson.getString("val");
			int      iLen = dataJson.getInt("len");
			int      iIdx = dataJson.getInt("idx");
			int    iTotal = dataJson.getInt("tal");
			String strMd5 = dataJson.getString("md5");
			String strCid = dataJson.getString("cid");

			byte[] buf_data = new byte[iLen];
			int iBufLen = input.read(buf_data,0,buf_data.length);
			if(iBufLen == iLen){
				String strBufMd5 = UtilPack.getInstance().md5sum(buf_data);
				if(strBufMd5.equals(strMd5) == true){
					if(ReadEndTag(input) == true){
						if(mDealResposeListener != null) {
							if (strVal.equals("data")) {
                                mDealResposeListener.OnSocketData(strVal, iIdx, iTotal, buf_data);
							} else if (strVal.equals("file")) {
                                mDealResposeListener.OnSocketFile(strVal, iIdx, iTotal, buf_data);
							} else if (strVal.equals("oper")) {
                                mDealResposeListener.OnSocketOper(strVal, iIdx, iTotal, buf_data);
                            } else if(strVal.equals("lcmd")){
								mDealResposeListener.OnSocketCmd(strTag, new String(buf_data).trim());
							} else {
                                mDealResposeListener.OnSocketCmd(strTag, strVal);
							}
						}
					}
				}else{
					Log.e(TAG,"compare md5 error");
				}
			}else{
				Log.e(TAG,"read buffer length error");
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		return ret;
	}

	private boolean ReadEndTag(DataInputStream input){
		boolean ret = false;

		try {
			byte[] end_data = new byte[UtilPack.mEnd_tag.length()];
			int iEndLen = input.read(end_data,0,end_data.length);
			String end = new String(end_data);
			if(iEndLen == UtilPack.mEnd_tag.length()){
				if(end.equals(UtilPack.mEnd_tag)){
					ret = true;
				}else{
					Log.e(TAG,"read end str not the same");
				}
			}else{
				Log.e(TAG,"read end len error");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ret;
	}
}
