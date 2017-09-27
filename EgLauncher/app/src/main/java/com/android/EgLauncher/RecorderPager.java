package com.android.EgLauncher;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.EgLauncher.file.FileManager;
import com.android.EgLauncher.util.Fm1388Util;
import com.android.EgLauncher.util.MiscUtil;
import com.android.EgLauncher.wav.AudioPlayWav;
import com.android.EgLauncher.wav.RecorderWav;
import com.android.tools.SysPorp.UtilShareDB;

import java.io.File;

import static android.content.Context.MODE_PRIVATE;

public class RecorderPager {
    private static String TAG = "RecorderPager";

    private MainActivity mMainctivity;

    private TextView mTextView_voice_to_text;
    private Button mButton_Times;
    private Button mButton_Start_Pause;
    private Button mButton_Stop;

    public static final int MSG_PLAY_TIME_TICK     = 1000;
    private String FileName = "";
    private int mState = mMainctivity.STATE_IDLE;
    private int mPlayFileLenInSec = 0;
    private boolean misInUiloopRender = false;
    private LinearLayout mDisplayLay = null;
    private SpectrumView mSpectrumView;
    private RecorderWav mRecorderWav = null;
    private AudioPlayWav mAudioPlayWav = null;
    //private TextView mStatusText = null;
    private long mTimeViewTime = 0;
    private String mTimerFormat = "%02d:%02d:%02d";
    private boolean mRecoderState = false;
    final Handler mLoopHandler = new Handler();
    Runnable mUpdateTimer = new Runnable() {
        public void run() {
            uiLoopRender(true);
        }
    };
    private void updateTimerRest() {
        String timeStr = String.format(mTimerFormat, 0, 0, 0);
        mButton_Times.setText(timeStr);
       // mStateLED.setVisibility(View.INVISIBLE);
       // mplaySeekBar.setProgress(0);
    }
    private void uiLoopRender(boolean loop) {
        misInUiloopRender = true;
        if (mRecorderWav != null && mState == mMainctivity.STATE_RECODE_STARTED){
            mTimeViewTime = mRecorderWav.getRecodTimeInSec();
            int[] hms = MiscUtil.sec2hms(mTimeViewTime);
            byte[] dates = mRecorderWav.getRecodCurrentDate();
            String timeStr = String
                    .format(mTimerFormat, hms[0], hms[1], hms[2]);
            mButton_Times.setText(timeStr);
            if (dates != null)
                mSpectrumView.updateVisualizer(dates);

            if (mTimeViewTime % 2 == 1) {
            //    mStateLED.setVisibility(View.INVISIBLE);
            } else {
            //    mStateLED.setVisibility(View.VISIBLE);
            }
            mTextView_voice_to_text.setText("STATUS: Recod: " + FileName);
        }
        if (mAudioPlayWav != null && mState == mMainctivity.STATE_PLAY_STARTED){
            mTimeViewTime = mAudioPlayWav.getPlayTimeInSec();
            int[] hms = MiscUtil.sec2hms(mTimeViewTime);

            String timeStr = String
                    .format(mTimerFormat, hms[0], hms[1], hms[2]);
            mButton_Times.setText(timeStr);
            mTextView_voice_to_text.setText("STATUS: Play: " + FileName);
           // mplaySeekBar.setMax(mPlayFileLenInSec);
           // mplaySeekBar.setProgress((int)mTimeViewTime);
        }

        if (mState == mMainctivity.STATE_IDLE){
            updateTimerRest();
            mTextView_voice_to_text.setText("STATUS: IDLE");
        }

        if (loop){
            mLoopHandler.postDelayed(mUpdateTimer, 128);//2048/16000=0.128s
        }
    }
    public Handler mRecHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MainActivity.UI_HANDLER_TEST:
                    Log.i(TAG, "..get a UI_HANDLER_TEST");
                    break;
                case MainActivity.SAVE_FILE_SUCCESS:
                    Toast.makeText(mMainctivity,
                            "save file success", Toast.LENGTH_LONG).show();
                    break;
                case MainActivity.FILE_REACH_SIZE:
                //    mRecord.callOnClick();
                    break;
                case MainActivity.STATE_RECODE_STARTED:
                    mState = mMainctivity.STATE_RECODE_STARTED;
                    FileName = msg.obj.toString();
                    uiLoopRender(false);
                    break;
                case MainActivity.STATE_RECODE_END:
                    mState = mMainctivity.STATE_IDLE;
                    if (mRecorderWav != null){
                        if (mRecorderWav.getState() == RecorderWav.IDLE_STATE){
                            mRecorderWav = null;
                        }
                    }
                    uiLoopRender(false);
                    break;
                case MainActivity.STATE_RECODE_ERR:
                    break;
                case MainActivity.STATE_PLAY_STARTED:
                    mState = mMainctivity.STATE_PLAY_STARTED;
                    mPlayFileLenInSec = msg.arg1;
                    FileName = msg.obj.toString();
                    uiLoopRender(false);
                    break;
                case MainActivity.STATE_PLAY_END:
                    mState = mMainctivity.STATE_IDLE;
                    if (mAudioPlayWav != null){
                        if (mAudioPlayWav.getState() == AudioPlayWav.PLAY_END){
                            mAudioPlayWav = null;
                        }
                    }
                    //mAudioPlayWav = null;
                    uiLoopRender(false);
                    break;
                case MSG_PLAY_TIME_TICK:
                    mButton_Times.setText("00:00:00");
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public RecorderPager(MainActivity mainActivity){
        mMainctivity = mainActivity;

        InitRecorder();
        Log.d(TAG,"init");
        if (!misInUiloopRender) {
            mLoopHandler.postDelayed(mUpdateTimer, 300);
        }
    }
    private void stopRecordForSafe(){
        if (mRecorderWav != null){
            mRecorderWav.stopRecording();
        }
        mRecorderWav = null;
    }

    private void stopPlayForSafe(){
        if(mAudioPlayWav != null){
            mAudioPlayWav.stop();
        }
        mAudioPlayWav = null;
        //addWavFileMTP
    }
    private String getPassword(){
        //String cityName = City.getCity().getCityName();
        // if(cityName==null ||cityName==""){
        Context ctx =mMainctivity;
        SharedPreferences sp =ctx.getSharedPreferences("PASSWORD", MODE_PRIVATE);

        //}
        String pwd = sp.getString("Password", "123456");
        Log.e("SetpwdDialogActivity", "get pwd = "+ pwd);
        return pwd;
    }


    public void InitRecorder() {
        mDisplayLay = (LinearLayout)mMainctivity.findViewById(R.id.spectrum);

        mSpectrumView = new SpectrumView(mMainctivity);
        mDisplayLay.addView(mSpectrumView);

        mTextView_voice_to_text = (TextView)mMainctivity.findViewById(R.id.voice_to_text);

        mButton_Times = (Button)mMainctivity.findViewById(R.id.rec_time);

        mButton_Start_Pause = (Button)mMainctivity.findViewById(R.id.rec_start_pause);
       /* mButton_Start_Pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        mButton_Start_Pause.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.e(TAG,"Button_Start_Pause DOWN");
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.e(TAG,"Button_Start_Pause UP");
                    ButtonStartRecord();
                }
                return false;
            }
        });
        mButton_Stop = (Button)mMainctivity.findViewById(R.id.rec_stop);
        mButton_Stop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.e(TAG,"mButton_Stop DOWN");
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.e(TAG,"mButton_Stop UP");
                    ButtonStopRecode();
                }
                return false;
            }
        });
    }
    public void ButtonStopRecode() {
        mRecoderState = false;
        stopRecordForSafe();
        stopPlayForSafe();
        mButton_Start_Pause.setText("录音");
        Fm1388Util.SetLed(false);
        mMainctivity.mRecordRunState = mMainctivity.NONE;
        //mButton_Start_Pause.setBackgroundColor(0xff00ffff);
        mButton_Start_Pause.setBackground(ContextCompat.getDrawable(mMainctivity,R.drawable.sw_button));
    }
    public void ButtonStartRecord() {
        mButton_Start_Pause.setBackground(ContextCompat.getDrawable(mMainctivity,R.drawable.sw_button2));
        if(mRecoderState) {
            mRecoderState = false;
            mButton_Start_Pause.setText("录音");
            Fm1388Util.SetLed(false);
            // mMainctivity.mRecordRunState = mMainctivity.NONE;
            //mButton_Start_Pause.setBackgroundColor(0xff00ffff);
            if (mRecorderWav != null) {
                mRecorderWav.pauseRecording();
                //break;
            } //else
            //  stopRecordForSafe();

        } else {
            mRecoderState = true;
            //  stopRecordForSafe();
            //   stopPlayForSafe();
            mButton_Start_Pause.setText("暂停");
            Fm1388Util.SetLed(true);
            mMainctivity.mRecordRunState = mMainctivity.RECORDING;
            //mButton_Start_Pause.setBackgroundColor(0xff000000);
            mButton_Start_Pause.setBackground(ContextCompat.getDrawable(mMainctivity,R.drawable.sw_button1));
            if (mRecorderWav != null) {
                mRecorderWav.pauseRecording();
                //break;
            }

            if (mRecorderWav == null){
                String password = getPassword();//UtilShareDB.getInstance().ReadSysKey("password", "123456");
                mRecorderWav = new RecorderWav(mMainctivity, mRecHandler, password);
                mRecorderWav.startRecording();
                //  mRecorderWav.mSpectrumView.onDraw();
            }

        }
    }
}
