package com.android.EgLauncher;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;


public class SettimeDialogActivity extends Activity{
    private Button mEnter;
    private Button mCancel;

    private Button mHourAdd;
    private Button mHourSub;

    private Button mMinAdd;
    private Button mMinSub;

    private TextView mShowhou;
    private TextView mShowmin;
    private WindowManager.LayoutParams mLayoutParams;
    private View mLayoutView;
    private int hour;
    private int min;
    private int apm;

    public static void setTime(int hour, int minute) {

        //requestPermission();

        Calendar c = Calendar.getInstance();

        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        long when = c.getTimeInMillis();

        if (when / 1000 < Integer.MAX_VALUE) {
            SystemClock.setCurrentTimeMillis(when);
        }

        long now = Calendar.getInstance().getTimeInMillis();
        //Log.d(TAG, "set tm="+when + ", now tm="+now);
    }
    private void createFloatWindow(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.gravity = Gravity.CENTER;
        mLayoutParams.x = 0;
        mLayoutParams.y = 0;
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        initStateBarView(context);
        // mLayoutView = View.inflate(context, R.layout.dateset_dialog, null);
        windowManager.addView(mLayoutView, mLayoutParams);
    }
    public void initStateBarView(Context context) {
        mLayoutView = View.inflate(context, R.layout.timeset_dialog, null);
        mEnter = (Button) mLayoutView.findViewById(R.id.timeset_enter);
        mCancel = (Button) mLayoutView.findViewById(R.id.timeset_cancel);

        mHourAdd = (Button) mLayoutView.findViewById(R.id.tv_hou_add);
        mHourSub = (Button) mLayoutView.findViewById(R.id.tv_hou_sub);

        mMinAdd = (Button) mLayoutView.findViewById(R.id.tv_min_add);
        mMinSub = (Button) mLayoutView.findViewById(R.id.tv_min_sub);

        mShowhou = (TextView) mLayoutView.findViewById(R.id.show_hou);
        mShowmin = (TextView) mLayoutView.findViewById(R.id.show_min);

        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        // apm = c.get(Calendar.AM);
        min = c.get(Calendar.MINUTE);

        mShowhou.setText(String.valueOf(hour));
        mShowmin.setText(String.valueOf(min));

        mHourAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this(Dialog).dismiss();
                //  SetdateDialogActivity.this.dismissKeyboardShortcutsHelper();
                //SetdateDialogActivity.this.finish();
                if (hour < 23)
                    hour = hour+1;
                mShowhou.setText(String.valueOf(hour));
            }
        });

        mHourSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this(Dialog).dismiss();
                //  SetdateDialogActivity.this.dismissKeyboardShortcutsHelper();
                //SetdateDialogActivity.this.finish();
                if (hour > 0)
                    hour = hour - 1;
                mShowhou.setText(String.valueOf(hour));
            }
        });

        mMinAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this(Dialog).dismiss();
                //  SetdateDialogActivity.this.dismissKeyboardShortcutsHelper();
                //SetdateDialogActivity.this.finish();
                if (min < 59)
                    min = min+1;
                mShowmin.setText(String.valueOf(min));
            }
        });

        mMinSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this(Dialog).dismiss();
                //  SetdateDialogActivity.this.dismissKeyboardShortcutsHelper();
                //SetdateDialogActivity.this.finish();
                if (min > 0)
                    min = min - 1;
                mShowmin.setText(String.valueOf(min));
            }
        });


        // mCancel.OnClickListener()
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this(Dialog).dismiss();
                SettimeDialogActivity.this.finish();
            }
        });
        mEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(hour, min);
                mShowhou.setText(String.valueOf(hour));
                mShowmin.setText(String.valueOf(min));
                SettimeDialogActivity.this.finish();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("SetdateDialogActivity", "start timeset_dialog");
        //setContentView(R.layout.timeset_dialog);
        createFloatWindow(this);
    }
}
