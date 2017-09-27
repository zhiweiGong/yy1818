package com.android.tools.Media;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;

public class UtilPlayTrack {
	private static final String TAG = "UtilPlayTrack";

    private int mRes_ID;
    private int mRd_Size = 0;
    private int mDurning = 0;
    private byte[] mBuffer = new byte[1024*1024];
    
    private AudioTrack mAudioTrack;
    private InputStream mInputStream;
    private BufferedInputStream mBufferedInputStream;
    private DataInputStream mDataInputStream;

	private Context mContext;

	private static UtilPlayTrack mUtilPlayTrack = null;

	public static synchronized UtilPlayTrack getInstance(){
		if(mUtilPlayTrack == null){
			mUtilPlayTrack = new UtilPlayTrack();
		}
		return mUtilPlayTrack;
	}

	public UtilPlayTrack(){
		Log.d(TAG,"init...");
	}

    public void StartPlay(Context context, int res, int iDurning) {
		Log.d(TAG,"start track play!");

    	mContext = context;
		mRes_ID = res;
    	mDurning = iDurning;

		new Thread(){
			@Override
			public void run(){
				play();
			}
		}.start();
    }

	private void play() {
		mInputStream = mContext.getResources().openRawResource(mRes_ID);//R.raw.system_bootup
		mBufferedInputStream = new BufferedInputStream(mInputStream);

		int minSize = AudioTrack.getMinBufferSize(16000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);

		try {
			mDataInputStream = new DataInputStream(mBufferedInputStream);
			mRd_Size = mDataInputStream.read(mBuffer);

			mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
					16000,
					AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT,
					minSize*2,
					AudioTrack.MODE_STREAM);

			mAudioTrack.setStereoVolume(1.0f, 1.0f);
			mAudioTrack.play();
			mAudioTrack.write(mBuffer, 0, mRd_Size);

			Thread.sleep(mDurning);

			mAudioTrack.stop() ;
			mAudioTrack.release();
			Log.d(TAG,"Playback played rd_size:"+mRd_Size);
		} catch (Throwable t) {
			Log.e(TAG,"Playback Failed");
		}
    }
}

