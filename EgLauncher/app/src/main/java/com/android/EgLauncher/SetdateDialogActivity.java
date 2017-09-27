package com.android.EgLauncher;

import android.app.Activity;
import android.app.Dialog;
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

import com.android.tools.File.UtilFileOper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class SetdateDialogActivity extends Activity{
    private Button mEnter;
    private Button mCancel;

    private Button mYearAdd;
    private Button mYearSub;

    private Button mMonAdd;
    private Button mMonSub;

    private Button mDayAdd;
    private Button mDaySub;

    private TextView mShowYear;
    private TextView mShowMou;
    private TextView mShowDay;
    private WindowManager.LayoutParams mLayoutParams;
    private View mLayoutView;
    private int year;
    private int mou;
    private int day;
    public static void setDate(int year, int month, int day){

        //requestPermission();

        Calendar c = Calendar.getInstance();

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
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
        mLayoutView = View.inflate(context, R.layout.dateset_dialog, null);

        mEnter = (Button) mLayoutView.findViewById(R.id.dateset_enter);
        mCancel = (Button) mLayoutView.findViewById(R.id.dateset_cancel);

        mYearAdd = (Button) mLayoutView.findViewById(R.id.tv_year_add);
        mYearSub = (Button) mLayoutView.findViewById(R.id.tv_year_sub);

        mMonAdd = (Button) mLayoutView.findViewById(R.id.tv_mou_add);
        mMonSub = (Button) mLayoutView.findViewById(R.id.tv_mou_sub);

        mDayAdd = (Button) mLayoutView.findViewById(R.id.tv_day_add);
        mDaySub = (Button) mLayoutView.findViewById(R.id.tv_day_sub);

        mShowYear = (TextView) mLayoutView.findViewById(R.id.show_year);
        mShowMou = (TextView) mLayoutView.findViewById(R.id.show_mou);
        mShowDay = (TextView) mLayoutView.findViewById(R.id.show_day);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        mou = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        //mHour = c.get(Calendar.HOUR_OF_DAY);
        //mMinute = c.get(Calendar.MINUTE);

        mShowYear.setText(String.valueOf(year));
        mShowMou.setText(String.valueOf(mou+1));
        mShowDay.setText(String.valueOf(day));



        mYearAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this(Dialog).dismiss();
                //  SetdateDialogActivity.this.dismissKeyboardShortcutsHelper();
                //SetdateDialogActivity.this.finish();
                if (year < 2050)
                    year = year+1;
                mShowYear.setText(String.valueOf(year));
            }
        });

        mYearSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this(Dialog).dismiss();
                //  SetdateDialogActivity.this.dismissKeyboardShortcutsHelper();
                //SetdateDialogActivity.this.finish();
                if (year > 1970)
                    year = year - 1;
                mShowYear.setText(String.valueOf(year));
            }
        });

        mMonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this(Dialog).dismiss();
                //  SetdateDialogActivity.this.dismissKeyboardShortcutsHelper();
                //SetdateDialogActivity.this.finish();
                if (mou < 11)
                    mou = mou + 1;
                mShowMou.setText(String.valueOf(mou+1));

            }
        });

        mMonSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this(Dialog).dismiss();
                //  SetdateDialogActivity.this.dismissKeyboardShortcutsHelper();
                //SetdateDialogActivity.this.finish();
                if (mou > 0 )
                    mou = mou - 1;
                mShowMou.setText(String.valueOf(mou+1));

            }
        });

        mDayAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this(Dialog).dismiss();
                //  SetdateDialogActivity.this.dismissKeyboardShortcutsHelper();
                //SetdateDialogActivity.this.finish();
                if (day < 31)
                    day = day + 1;
                mShowDay.setText(String.valueOf(day));
            }
        });

        mDaySub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this(Dialog).dismiss();
                //  SetdateDialogActivity.this.dismissKeyboardShortcutsHelper();
                //SetdateDialogActivity.this.finish();
                if (day > 1)
                    day = day - 1;
                mShowDay.setText(String.valueOf(day));
            }
        });

        // mCancel.OnClickListener()
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this(Dialog).dismiss();
                //  SetdateDialogActivity.this.dismissKeyboardShortcutsHelper();
                SetdateDialogActivity.this.finish();
            }
        });
        mEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate(year, mou, day);
                mShowYear.setText(String.valueOf(year));
                mShowMou.setText(String.valueOf(mou+1));
                mShowDay.setText(String.valueOf(day));
                SetdateDialogActivity.this.finish();
            }
        });

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("SetdateDialogActivity", "start dateset_dialog");
        //setContentView(R.layout.dateset_dialog);
        createFloatWindow(this);

    }

}
