package com.android.EgLauncher.VoicePlayer;

import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.android.EgLauncher.MainActivity;
import com.android.EgLauncher.R;

public class PlayBarListener implements OnSeekBarChangeListener {
    private final String TAG = "PlayBarListener";

    public PlayBar mPlayBar = null;

    public MainActivity mMainActivity;
    public MediaPlay mMediaPlay;

    public PlayBarListener(MediaPlay mediaPlay){
        mMediaPlay = mediaPlay;
    }

    public void InitPlayBar(MainActivity mainActivity) {
        mMainActivity = mainActivity;
        mPlayBar = (PlayBar) mMainActivity.findViewById(R.id.Play_Slider);
        mPlayBar.setOnSeekBarChangeListener(this);
    }

    public void setVisibility(int flag) {
        mPlayBar.setVisibility(flag);
    }

    public void setMax(int max) {
        mPlayBar.setMax(max);
    }

    public void setProgress(int progress) {
        mPlayBar.setProgress(progress);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int id = seekBar.getId();

        int bar = mPlayBar.getProgress();

        if(id == R.id.Play_Slider){
            mMediaPlay.Seek(bar);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }
}
