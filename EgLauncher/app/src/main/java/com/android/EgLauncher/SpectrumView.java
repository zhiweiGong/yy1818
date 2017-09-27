package com.android.EgLauncher;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;


public class SpectrumView extends View {

    private static String TAG = "SpectrumView";
    private byte[] mBytes;
    private float[] mPoints;
    private int[] temp;
    private int[] mShowTables;
    private int tc;
    private Rect mRect = new Rect();
    private Paint mForePaint = new Paint();
    public SpectrumView(Context context) {
        super(context);
        init();
    }
    /**
     * 初始化
     */
    private void init() {
        mBytes = null;
        mShowTables = new int[2048*24];
        temp = new int[2048*23];
        mForePaint.setStrokeWidth(1f);
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(Color.RED);
    }
    public void updateVisualizer(byte[] waveForm)
    {
    //    Log.e(TAG,"SpectrumView updateVisualizer");
        mBytes = waveForm;
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
 //       Log.e(TAG,"SpectrumView onDraw");

        // paintEvent(canvas);
       //     Log.e(TAG,"date:"+(int)(mBytes[i*2]) + "< ===== >"+ (int)(mBytes[i*2+1]));
      //  }
        //tc = (tc+342)%400;
        mRect.set(0, 0, getWidth(), getHeight());
        Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.background);
        canvas.drawBitmap(bmp,null,mRect,null);
        int h = getHeight() / 2;
        //   painter.translate(0, h);
        //   QPen vPen;
        //   vPen.setColor(Qt::red);
        //   vPen.setWidth(1);
        //   painter.setPen(vPen);
        //   painter.drawLine(0, 0, width(), 0);
        if (mBytes == null)
        {
            Log.e(TAG,"mByte == null");
            return;
        }
        if (mPoints == null)// || mPoints.length < mShowTables.length * 4)
        {
            mPoints = new float[mRect.width()*4 * 2];
        }
        System.arraycopy(mShowTables,0,temp,0,temp.length);
        for (int m = 0; m < mBytes.length/2; m++){
            mShowTables[m] = (int) mBytes[m*2] | (int) (mBytes[m*2+1] << 8);
            mBytes[m*2] = 0;
            mBytes[m*2+1] = 0;

        }
        System.arraycopy(temp,0,mShowTables,2048,temp.length);
        //   if (!(m_AudioList.size() > 0))
        //   {
        //       return;
        //   }
        //   vPen.setColor(Qt::green);
        //   painter.setPen(vPen);
        int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
        int vPointStep = 1;
        int vDataSetp  = 1;
        if (mShowTables.length >= mRect.width())
        {
            vPointStep = 1;
            vDataSetp  = mShowTables.length / mRect.width();
        }
        else
        {
            vPointStep = mRect.width() / mShowTables.length;
            vDataSetp  = 1;
        }
        int vDataIdx = 0;
  //      Log.e(TAG,"vDataSetp = " + vDataSetp);
  //      System.arraycopy(mPoints,0,temp,0,temp.length);
        for (int i = 0; i < mRect.width(); )
        {
            int vPlusVal = 0;
            int vNegaVal = 0;
            for(int j = 0; j < vDataSetp; j ++)
            {
                int vIdx = vDataIdx * vDataSetp + j;
                if (!(vIdx < mShowTables.length))
                {
                    break;
                }
                int vCurVal = ((mShowTables[vIdx] * h) / (65535 / 2) ) *3/4;
                if (vCurVal > 0)
                {
                    if (vCurVal > vPlusVal)
                    {
                        vPlusVal = vCurVal;
                    }
                }
                else if (vCurVal < 0)
                {
                    if (vCurVal < vNegaVal)
                    {
                        vNegaVal = vCurVal;
                    }
                }
            }
         //   Log.e(TAG,"vPlusVal: "+vPlusVal);
            if (vPlusVal >= 0)
            {
                x2 = i;
                y2 = - vPlusVal;
                //painter.drawLine(x1, y1, x2, y2);
                mPoints[i*4] = x1;
                mPoints[i*4 + 1] = y1 + h;
                mPoints[i*4 + 2] = x2;
                mPoints[i*4 + 3] = y2 + h;
                x1 = x2;
                y1 = y2;
            }
            if (vNegaVal < 0)
            {
                x2 = i;
                y2 = - vNegaVal;
                mPoints[vDataIdx*4] = x1;
                mPoints[vDataIdx*4 + 1] = y1 + h;
                mPoints[vDataIdx*4 + 2] = x2;
                mPoints[vDataIdx*4 + 3] = y2 + h;
                x1 = x2;
                y1 = y2;
            }
            i += vPointStep;
            vDataIdx ++;
        }
        //System.arraycopy(mPoints,0,temp,0,temp.length);

      //  System.arraycopy(temp,0,mPoints,temp.length,temp.length);
        canvas.drawLines(mPoints, mForePaint);
        //canvas.drawLines(mPoints, mForePaint);*/
    }
}












