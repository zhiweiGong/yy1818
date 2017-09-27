package com.android.UdpSocket;

import android.os.NetworkOnMainThreadException;
import android.util.Log;

import com.android.tools.Misc.UtilPack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UdpSocketClient {
	private static final String TAG = "UdpSocketClient";

    private final int SEND_BUFFER_LEN = 1024*60;//max udp package 64K
	private DatagramSocket mDatagramSocket;
	private InetAddress mInetAddress;
	private String REMOTE_IP = "127.0.0.0";
	private String REMOTE_PORT = "0000";

	public boolean mTransRunning = false;

	public UdpSocketClient() {
		Log.d(TAG,"init");
	}

	public boolean StartUdpSocketClient(String UdpIP,String UdpPort) {
		boolean ret = false;

		Log.d(TAG,"InitTransUDPSocket:"+UdpIP+","+UdpPort);
		REMOTE_IP = UdpIP;
		REMOTE_PORT = UdpPort;

		try {
			mDatagramSocket = new DatagramSocket();
			mInetAddress = InetAddress.getByName(REMOTE_IP);
			mDatagramSocket.connect(mInetAddress, Integer.valueOf(UdpPort));

			ret = true;
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (NetworkOnMainThreadException e){
			e.printStackTrace();
		}

		mTransRunning = true;

		return ret;
	}

	public void StopUdpSocketClient() {
		Log.d(TAG,"StopUdpTrans.");

		mTransRunning = false;

		if(mDatagramSocket != null){
			mDatagramSocket.disconnect();
			mDatagramSocket.close();
			mDatagramSocket = null;
		}
	}

	private void ReStartUdpTrans(){
		if(mTransRunning == false) {
			if ((REMOTE_IP.equals("127.0.0.0") == false) && (REMOTE_PORT.equals("0000") == false)) {
				StartUdpSocketClient(REMOTE_IP, REMOTE_PORT);
				Log.d(TAG,"restart udp trans connect");
			}
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
		byte[] send_buffer = UtilPack.getInstance().MakePackage("data",value,index,total,data);
		if(send_buffer != null){
			writeBytes(send_buffer,0,send_buffer.length);
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

		if(mTransRunning == true){
			if((mDatagramSocket != null)&&(mInetAddress != null)) {
				try {
					Log.d(TAG,"w:"+count);
					int iport = Integer.parseInt(REMOTE_PORT);
					DatagramPacket packet = new DatagramPacket(buffer, count, mInetAddress, iport);
					if (count < SEND_BUFFER_LEN) { //防止UDP包过大,报异常.
						mDatagramSocket.send(packet);
					}
					bRet = true;
					//Log.d(TAG, "TransData:" + mDatagramSocket + "," + System.currentTimeMillis());
				} catch (IOException e) {
					Log.e(TAG, "transdata error!" + e.toString());
				}
			}
		}

		if(bRet == false){
			ReStartUdpTrans();
		}
		return bRet;
	}
}