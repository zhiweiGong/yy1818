package com.android.EgLauncher.VoicePlayer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class PlayBar extends SeekBar {
    private static final String TAG = "PlayBar";

    private Paint mPaint;

    public PlayBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(16);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        int pos = (this.getProgress()+500)/1000;
        int pos_hh = (pos/1200)%60;
        int pos_mm = (pos/60)%60;
        int pos_ss = pos%60;

        int max = this.getMax()/1000;
        int max_hh = (max/1200)%60;
        int max_mm = (max/60)%60;
        int max_ss = max%60;

     //   canvas.drawText(String.format("%02d", pos_hh)+":"+String.format("%02d", pos_mm)+":"+String.format("%02d", pos_ss), 40, 20, mPaint);
     //   canvas.drawText(String.format("%02d", max_hh)+":"+String.format("%02d", max_mm)+":"+String.format("%02d", max_ss), this.getWidth()-30, 20, mPaint);
        super.onDraw(canvas);
    }

}

