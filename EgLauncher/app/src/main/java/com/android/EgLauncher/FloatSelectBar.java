package com.android.EgLauncher;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class FloatSelectBar {
    private static String TAG = "FloatSelectBar";

    private MainActivity mMainActivity;
    private WindowManager.LayoutParams mLayoutParams;
    private View mLayoutView;

    private Button mButton_Record;
    private Button mButton_Player;
    private Button mButton_Setting;

    public FloatSelectBar(MainActivity mainActivity){
        mMainActivity = mainActivity;

        createFloatWindow(mMainActivity);
        Log.d(TAG,"init");
    }

    private void createFloatWindow(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.gravity = Gravity.CENTER | Gravity.BOTTOM;
        mLayoutParams.x = 0;
        mLayoutParams.y = 0;
        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        initStateBarView(context);

        windowManager.addView(mLayoutView, mLayoutParams);
        mButton_Player.setBackgroundResource(R.drawable.sw_button);
        mButton_Setting.setBackgroundResource(R.drawable.sw_button);
        mButton_Record.setBackgroundResource(R.drawable.sw_button2);
    }

    public void initStateBarView(Context context) {
        mLayoutView = View.inflate(context, R.layout.select_bar, null);

        mButton_Record = (Button)mLayoutView.findViewById(R.id.rec_pager);
        mButton_Record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMainActivity.mRecordRunState != mMainActivity.RECORDING && mMainActivity.mRecordRunState != mMainActivity.PLAYING) {
                    mMainActivity.setContentView(R.layout.page_recorder);
                    mMainActivity.mRecorderPager.InitRecorder();
                    mButton_Player.setBackgroundResource(R.drawable.sw_button);
                    mButton_Setting.setBackgroundResource(R.drawable.sw_button);
                    mButton_Record.setBackgroundResource(R.drawable.sw_button2);
                    mMainActivity.mSelectMenu = 1;
                }
            }
        });

        mButton_Player = (Button)mLayoutView.findViewById(R.id.play_pager);
        mButton_Player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMainActivity.mRecordRunState != mMainActivity.RECORDING && mMainActivity.mRecordRunState != mMainActivity.PLAYING) {
                    mMainActivity.setContentView(R.layout.page_play);
                    mMainActivity.mPlayerPager.InitPlayList();
                    mButton_Player.setBackgroundResource(R.drawable.sw_button2);
                    mButton_Setting.setBackgroundResource(R.drawable.sw_button);
                    mButton_Record.setBackgroundResource(R.drawable.sw_button);
                    mMainActivity.mSelectMenu = 0;
                }
            }
        });

        mButton_Setting = (Button)mLayoutView.findViewById(R.id.set_pager);
        mButton_Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMainActivity.mRecordRunState != mMainActivity.RECORDING && mMainActivity.mRecordRunState != mMainActivity.PLAYING) {
                    mMainActivity.setContentView(R.layout.page_setting);
                    mMainActivity.mSettingPager.InitSetting();
                    mButton_Player.setBackgroundResource(R.drawable.sw_button);
                    mButton_Setting.setBackgroundResource(R.drawable.sw_button2);
                    mButton_Record.setBackgroundResource(R.drawable.sw_button);
                    mMainActivity.mSelectMenu = 2;
                }
            }
        });
    }

    public void ShowFloatWindow() {
        mLayoutView.setVisibility(View.VISIBLE);
    }

    public void HideFloatWindow() {
        mLayoutView.setVisibility(View.INVISIBLE);
    }
}
