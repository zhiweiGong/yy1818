package com.android.tools.Media;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

import java.io.IOException;

public class UtilPlayMusic {
	private final String TAG = "UtilPlayMusic";
	private static UtilPlayMusic mUtilPlayMusic = null;
	private MediaPlayer mPlayer;
	
	public static synchronized UtilPlayMusic getInstance(){
		if(mUtilPlayMusic == null){
			mUtilPlayMusic = new UtilPlayMusic();
		}
		return mUtilPlayMusic;
	}

    public UtilPlayMusic(){
		Log.d(TAG,"init...");
    }

	
	public String StartPlay(String path) {
		if(mPlayer != null) {
			Stop();
		}
		 
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(path);
			mPlayer.setLooping(false);		
			mPlayer.prepare();
			mPlayer.start();
			mPlayer.setOnCompletionListener(mOnCompletionListener);
			mPlayer.start();
			Log.i(TAG, "mediaplayer is start:"+path);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public void Stop() {
		if(mPlayer != null) {
			try {
				if(mPlayer.isPlaying()) {
					mPlayer.stop();
				}
				mPlayer.release();
				mPlayer = null;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}
	
	private OnCompletionListener mOnCompletionListener = new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer arg0) {
			// TODO Auto-generated method stub
			if(mPlayer != null) {
				Stop();
			}
		}
	};
}
