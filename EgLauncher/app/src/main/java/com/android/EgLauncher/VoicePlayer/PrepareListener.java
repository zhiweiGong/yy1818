package com.android.EgLauncher.VoicePlayer;

import android.media.MediaPlayer;

public final class PrepareListener implements MediaPlayer.OnPreparedListener {
    private int mPosition;
    private MediaPlayer mMediaPlayer;

    public PrepareListener(MediaPlayer mediaPlayer,int position) {
        mPosition = position;
        mMediaPlayer = mediaPlayer;
    }

    public void onPrepared(MediaPlayer mp) {
        mMediaPlayer.start();
        if (mPosition > 0)
            mMediaPlayer.seekTo(mPosition);
    }
}
