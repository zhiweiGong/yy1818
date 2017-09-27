package com.android.TcpSocket;

import android.util.Log;

import com.android.tools.Misc.UtilPack;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TcpSocketClient {
	private static final String TAG = "TcpSocketClient";

	private final int SEND_BUFFER_LEN = 1024*100;
	private Socket mPkgSocket;

	private String REMOTE_IP = "127.0.0.0";
	private String REMOTE_PORT = "0000";

	public boolean mTransRunning = false;

	public TcpSocketClient() {
		Log.d(TAG,"init");
	}

	public void StartTcpTrans(String ip,String port)
	{
		if((ip != null)&&(port != null)){
			REMOTE_IP = ip;
			REMOTE_PORT = port;
			int pt = Integer.parseInt(port);
			Log.e(TAG, "Connect start:"+REMOTE_IP+","+REMOTE_PORT);

			try {
				mPkgSocket = new Socket();
				mPkgSocket.connect(new InetSocketAddress(ip, pt), 5000);
				mPkgSocket.setKeepAlive(true);

				SendCmd("ConnectType", "TcpSocketClient");
				Log.e(TAG, "Connect Success!");
			} catch (Exception e) {
				mPkgSocket = null;
				Log.e(TAG, "Connect Error");
			}

			mTransRunning = true;
		}
	}

	public void StopTcpTrans(){
		Log.d(TAG,"StopTcpTrans.");
		mTransRunning = false;

		if(mPkgSocket != null) {
			try {
				mPkgSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mPkgSocket = null;
		}
	}

    private void ReStartTcpTrans(){
		if(mTransRunning == true) {
			if ((REMOTE_IP.equals("127.0.0.0") == false) && (REMOTE_PORT.equals("0000") == false)) {
				StartTcpTrans(REMOTE_IP, REMOTE_PORT);
				Log.d(TAG,"restart tcp trans connect");
			}else{
				Log.e(TAG,"REMOTE_IP or REMOTE_PORT is false");
			}
		}else{
			Log.e(TAG,"restart mTransRunning false");
		}
	}

	public void SendCmd(String tag,String value)
	{
		byte[] send_buffer = UtilPack.getInstance().MakePackage(tag,value,0,1," ".getBytes());
		if(send_buffer != null){
			writeBytes(send_buffer,0,send_buffer.length);
		}
	}

	public void SendData(String value,int index,int total,byte[] data) {
		if(data.length == 0){
			Log.e(TAG,"TransData is == 0");
			return;
		}
		byte[] send_buffer = UtilPack.getInstance().MakePackage("data",value,0,total,data);
		if(send_buffer != null){
			writeBytes(send_buffer,0,send_buffer.length);
		}else{
			Log.e(TAG,"make trans buffer null");
		}
	}

	public void SendFile(String value,String send_file) {
		byte[] buf_send = null;
		File fd_send = new File(send_file);

		if(fd_send.exists() == true){
			try {
				InputStream file_is = new FileInputStream(fd_send);
				int total_len = (int)fd_send.length();
				byte[] file_buffer = new byte[SEND_BUFFER_LEN];
				int readIndex = 0;
				while(true){
					int iRdLen = file_is.read(file_buffer);
					if(iRdLen > 0){
						buf_send = UtilPack.getInstance().MakePackage("file",value,readIndex,total_len/SEND_BUFFER_LEN,file_buffer);
						writeBytes(buf_send,0,buf_send.length);
						readIndex++;
					}else{
						break;
					}
				}
				file_is.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private synchronized boolean writeBytes(byte[] buffer,int offset,int count)
	{
		boolean bRet = false;

		if (mPkgSocket != null && mPkgSocket.isConnected()) {
			if (!mPkgSocket.isOutputShutdown()) {
				try {
					DataOutputStream dataOutputStream = new DataOutputStream(mPkgSocket.getOutputStream());
					dataOutputStream.write(buffer, offset, count);
					dataOutputStream.flush();
					Log.d(TAG,"w:"+count);
					bRet = true;
				} catch (IOException e) {
					Log.e(TAG,"tcp send fail");
				}
			}else{
				Log.e(TAG,"output is shutdown");
			}
		}else{
			Log.e(TAG,"socket is disconnect!!!");
		}

		if(bRet == false){
			ReStartTcpTrans();
		}

		return bRet;
	}
}