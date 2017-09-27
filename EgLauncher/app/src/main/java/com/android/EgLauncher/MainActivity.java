package com.android.EgLauncher;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;

import com.android.EgLauncher.util.Fm1388Util;
import com.android.tools.SysPorp.UtilShareDB;
import com.android.EgLauncher.file.FileManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.KeyEvent;

public class MainActivity extends Activity {
    private static String TAG = "MainActivity";
    public static final int UI_HANDLER_TEST = -1;
    public static final int STATE_IDLE = 0;
    public static final int UI_HANDLER_UPDATE_UP = 0X01;
    public static final int SAVE_FILE_SUCCESS = 0x02;
    public static final int FILE_REACH_SIZE = 0X3;
    public static final int UI_HANDLER_UPDATE_TIMERVIEW = 0X04;
    public static final int UI_HANDLER_UPDATE_TIME_RESET = 0X05;
    public static final int STATE_RECODE_STARTED = 0X06;
    public static final int STATE_RECODE_END = 0x07;
    public static final int STATE_PLAY_STARTED = 0x08;
    public static final int STATE_PLAY_END = 0X09;
    public static final int STATE_RECODE_ERR = 0x10;

    public static final int RECORDING = 0xc0;
    public static final int PLAYING = 0xc1;
    public static final int NONE = 0xc3;

    private FloatStateBar mFloatStateBar;
    private FloatSelectBar mFloatSelectBar;
    private  AudioManager audioManager;
    public PlayerPager mPlayerPager;
    public RecorderPager mRecorderPager;
    public SettingPager mSettingPager;
    private WakeLock mWakeLock;
    public int mRecordRunState;
    public int mSelectMenu;
    private int mVolumeData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG,"Main FileManager init");
        UtilShareDB.getInstance().InitSystem(this);
        //FileManager.getInstance();
        FileManager.getInstance().addWavRootMTP(this);
        setContentView(R.layout.page_recorder);
        mRecordRunState = NONE;
        mSelectMenu = 1;

        mPlayerPager = new PlayerPager(this);
        mRecorderPager = new RecorderPager(this);
        mSettingPager = new SettingPager(this);
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE) ;
        CreateStatusBar();
        CreateSelectBar();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SoundReco" +
                "rder");
        Fm1388Util.changeModeMP();
    }
    /*
    *

        <enum name="KEYCODE_F7" value="137" />
        <enum name="KEYCODE_F8" value="138" />



    *
    *
    * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("zxw", "..on key down " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_F8) {
            // recodebutton
            Log.e("zxw", "get f8");
            //Fm1388Util.SetLed(true);
            if (mSelectMenu == 1)
                mRecorderPager.ButtonStartRecord();
            else if (mSelectMenu == 0) {
                mVolumeData = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                if (mVolumeData < 15) {
                    mVolumeData++;
                    mSettingPager.VolumeSet(mVolumeData);
                }
            }

        } else if (keyCode == KeyEvent.KEYCODE_F7) {
            // playpause
            Log.e("zxw", "get f7");
            //Fm1388Util.SetLed(false);
            if (mSelectMenu == 1)
                mRecorderPager.ButtonStopRecode();
            else if (mSelectMenu == 0) {
                mVolumeData = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                if (mVolumeData > 0) {
                    mVolumeData--;
                    mSettingPager.VolumeSet(mVolumeData);
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    private void CreateStatusBar(){
        mFloatStateBar = new FloatStateBar(this);
        mFloatStateBar.ShowFloatWindow();
    }

    private void CreateSelectBar(){
        mFloatSelectBar = new FloatSelectBar(this);
        mFloatSelectBar.ShowFloatWindow();
    }
}
