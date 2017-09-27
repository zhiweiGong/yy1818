package com.android.LongSocket;

import android.content.Context;
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

public class LongSocketClient {
    private static final String TAG = "LongSocketClient";

    private final int SEND_BUFFER_LEN = 1024*1024*10;

	private String HOST_IP   = "120.76.42.245";
	private int HOST_PORT = 0;
    private String mConnectType;

	private KeepAlive mKeepAlive;

    public DealRespose mDealRespose;

	public Context mContext;
    public Socket mSocket;

	public LongSocketClient(Context context, String HostIp, int Port, String type) {
		Log.d(TAG,"init...");

		mContext = context;
        mConnectType = type;

        HOST_IP = HostIp;
        HOST_PORT = Port;

    	mKeepAlive = new KeepAlive();
    	mDealRespose = new DealRespose(this);
		mDealRespose.ReadRespose();
	}
	    
	public synchronized void SocketConnect()
	{
		Log.e(TAG, "Connect start... :" + HOST_IP + ":" + HOST_PORT);
		// DisConnect() 不必定义成synchronized 否则在同一个synchronized调用另一个
        //synchronized,将造成死锁.ls
		DisConnect();

		try {
			mSocket = new Socket();
			mSocket.connect(new InetSocketAddress(HOST_IP, HOST_PORT), 5000);
			mSocket.setKeepAlive(true);
			
			SendCmd("ConnectType",mConnectType);
            Log.e(TAG, "Connect Success! " + HOST_IP + ":" + HOST_PORT);
		} catch (Exception e) {
            DisConnect();
			mSocket = null;
			Log.e(TAG, "Connect Error");
			e.printStackTrace();
		}
	}

    public void DisConnect(){
        if(mSocket != null) {
        	try {
	            mSocket.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
            mSocket = null;
        }
    }

    public void Running(){
        new Thread(){
            @Override
            public void run(){
                boolean reConnect;

                do{
                    try {
                        if(mSocket==null){
                            Log.e(TAG, "mSocket is null " + HOST_IP + ":" + HOST_PORT);
                            reConnect = true;
                        }else{
                            Log.e(TAG, "Listen************************:"+mSocket.isConnected() + " " + HOST_IP + ":" + HOST_PORT);

                            if(mSocket.isConnected() == true){
                                mKeepAlive.checkIsAlive(LongSocketClient.this);
                                reConnect = false;
                            }else{
                                reConnect = true;
                            }
                        }
                    }catch(Exception e){
                        Log.e(TAG, "Listen error:" + e.toString());
                        e.printStackTrace();
                        reConnect = true;
                    }

                    if(reConnect == true){
                        SocketConnect();
                    }

                    try{
                        Thread.sleep(5000);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                } while(true);
            }
        }.start();
    }

    public boolean SendCmd(String tag,String value)
    {
        boolean ret = false;

        byte[] send_buffer = UtilPack.getInstance().MakePackage(tag,value,0,1," ".getBytes());

        if(send_buffer != null){
            ret = writeBytes(send_buffer,0,send_buffer.length);
        }

        if(ret == true){
            Log.d(TAG,"send cmd succ");
        }else {
            Log.d(TAG,"send cmd fail");
        }

        return false;
    }

    public boolean SendData(String value,int total,byte[] data) {
        boolean ret = false;

        byte[] send_buffer = UtilPack.getInstance().MakePackage("data",value,0,total,data);

        if(send_buffer != null){
            ret = writeBytes(send_buffer,0,send_buffer.length);
        }

        if(ret == true){
            Log.d(TAG,"send data succ");
        }else {
            Log.d(TAG,"send data fail");
        }

        return false;
    }

    public void SendFile(String value,String send_file) {
        byte[] buf_send = null;
        File fd_send = new File(send_file);

        if(fd_send.exists() == true){
            try {
                InputStream file_is = new FileInputStream(fd_send);
                int total_len = (int)fd_send.length();
                byte[] file_buffer = new byte[total_len];
                file_is.read(file_buffer);
                buf_send = UtilPack.getInstance().MakePackage("file",value,0,1,file_buffer);
                writeBytes(buf_send,0,buf_send.length);
                file_is.close();
                Log.d(TAG,"send file");
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

        if (mSocket != null && mSocket.isConnected()) {
            if (!mSocket.isOutputShutdown()) {
                try {
                    DataOutputStream dataOutputStream = new DataOutputStream(mSocket.getOutputStream());
                    dataOutputStream.write(buffer, offset, count);
                    dataOutputStream.flush();
                    bRet = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if(bRet == false){
            SocketConnect();
        }

        return bRet;
    }
}

