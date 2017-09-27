package com.android.tools.Media;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class UtilAudioRec{
    private static final String TAG = "UtilAudioRec";
	
	private MediaRecorder mRecorder = null;
	private int mTimerWait = 0;

    private static UtilAudioRec mUtilAudioRec = null;

    public static synchronized UtilAudioRec getInstance(){
        if(mUtilAudioRec == null){
            mUtilAudioRec = new UtilAudioRec();
        }
        return mUtilAudioRec;
    }

    public UtilAudioRec(){
        Log.d(TAG,"init...");
    }

    public void StartRec(String save_path,int rec_len)
    {
    	File iRecAudioFile = new File(save_path);
    	    
    	mTimerWait = rec_len;
    	
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(iRecAudioFile.getAbsolutePath());

        new Thread(){
            @Override
            public void run(){
                recoder();
            }
        }.start();
    }

    public void StopRec()
    {
        mTimerWait = 0;
        Log.d(TAG,"Stop_Recorder");
    }

    private void recoder() {
        try {
            mRecorder.prepare();
            mRecorder.start();
            Log.d(TAG,"audio rec. started");
        } catch(IOException exception) {
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
            Log.d(TAG,"audio rec init fail.");
        } catch (RuntimeException exception) {
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        }

        while(mTimerWait-- > 0){
            try{
                Thread.sleep(1000);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        if (mRecorder == null){
            Log.d(TAG,"mRecorder is null");
            return;
        }

        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }
} 

