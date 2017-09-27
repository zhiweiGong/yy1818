package com.android.EgLauncher.VoicePlayer;

import android.media.MediaPlayer;
import android.view.SurfaceView;

public class MediaPlay {
    private final String TAG = "MediaPlay";

    public boolean mIsPlay = false;
    public int mPausePos = 0;

    private MediaPlayer mMediaPlayer;

    public MediaPlay() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
            @Override
            public void onCompletion(MediaPlayer mp) {
                mIsPlay = false;
                //mp.release();
            }
        });
    }

    public void Seek(int position){
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.seekTo(position);
        }
    }

    public int GetPlayPosition(){
        int ret=0;

        if (mMediaPlayer.isPlaying()) {
            ret = mMediaPlayer.getCurrentPosition();
        }

        return ret;
    }

    public int Play(String filePath,int position) {
        int retDuration = 0;

        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepare();
            PrepareListener prepareListener = new PrepareListener(mMediaPlayer,position);
            mMediaPlayer.setOnPreparedListener(prepareListener);
            retDuration =mMediaPlayer.getDuration();
            mIsPlay = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retDuration;
    }

    public void Pause(){
        if (mMediaPlayer.isPlaying()) {
            mPausePos = GetPlayPosition();
            mMediaPlayer.pause();
            mIsPlay = false;
        }
    }

    public void Stop(){
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mIsPlay = false;
        }
    }

    public void Prev(){

    }

    public void Next(){

    }
}
