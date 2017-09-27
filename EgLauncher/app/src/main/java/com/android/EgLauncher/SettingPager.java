package com.android.EgLauncher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.tools.File.UtilFileOper;
import com.android.tools.Misc.UtilSystem;
import com.android.tools.SysPorp.UtilShareDB;

import java.util.Calendar;


public class SettingPager {
    private static String TAG = "SettingPager";

    private int mVolumeData;

    private TextView showDate = null;
    private Button pickDate = null;
    private TextView showTime = null;
    private Button pickTime = null;

    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;

    private Button mBtn_encryption_setting;
    private Button mBtn_volume_add;
    private Button mBtn_volume_sub;
    private Button mBtn_wifi_setting;
    private Button mBtn_format_setting;
    private TextView mTxt_volume_data;
    private CheckBox mCheckBox_voice_to_text;
    private CheckBox mCheckBox_voice_change_rec;

    private MainActivity mMainActivity;

    public SettingPager(MainActivity mainActivity){
        mMainActivity = mainActivity;
        Log.d(TAG,"init");
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        mMainActivity.registerReceiver(ubiReceiver, filter);
    }
    private BroadcastReceiver ubiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_DATE_CHANGED)){

                Log.e(TAG,"date is changed");


            }

            if (intent.getAction().equals(Intent.ACTION_TIME_CHANGED)){

                Log.e(TAG,"time is changed");
                setTimeOfDay();
                setDateTime();
            }
        }
    };
    private void showWifiSetting(Context mContext) {
        WifiSetup mWifiSetup = new WifiSetup(mContext);
        Log.e(TAG, "showWifiSetting mWifiSetup ");
        mWifiSetup.show();
    }

    /**
     * 更新日期显示
     */
    private void updateDateDisplay(){
        showDate.setText(new StringBuilder().append(mYear).append("-")
                .append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1)).append("-")
                .append((mDay < 10) ? "0" + mDay : mDay));
    }

    public void InitSetting() {
  //      mTxt_volume_data = (TextView) mMainActivity.findViewById(R.id.volume_data);
        showDate = (TextView) mMainActivity.findViewById(R.id.showdate);

        showTime = (TextView) mMainActivity.findViewById(R.id.showtime);
        pickDate = (Button) mMainActivity.findViewById(R.id.pickdate);
        pickDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.e(TAG,"start dialod");
              //  mMainActivity.startActivity(new Intent(mMainActivity, SetdateDialogActivity.class));
                Intent intent=new Intent();
                //intent.putExtra("testIntent", "123");
                intent.setClass(mMainActivity, SetdateDialogActivity.class); //设置跳转的Activity
                mMainActivity.startActivity(intent);
            }
        });
        pickTime = (Button)mMainActivity.findViewById(R.id.picktime);
        pickTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mMainActivity.startActivity(new Intent(mMainActivity, SettimeDialogActivity.class));
            }
        });

        mCheckBox_voice_to_text = (CheckBox) mMainActivity.findViewById(R.id.voice_to_text);
        mCheckBox_voice_to_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mCheckBox_voice_change_rec = (CheckBox) mMainActivity.findViewById(R.id.voice_change_rec);
        mCheckBox_voice_change_rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

     //   mBtn_date_setting = (Button) mMainActivity.findViewById(R.id.date_setting);
     //   mBtn_date_setting.setOnClickListener(new View.OnClickListener(){
     //       public void onClick(View v) {

     //       }
     //   });
        mBtn_encryption_setting = (Button) mMainActivity.findViewById(R.id.encryption_setting);
        mBtn_encryption_setting.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                mMainActivity.startActivity(new Intent(mMainActivity, SetpwdDialogActivity.class));
            }
        });
        mBtn_wifi_setting = (Button) mMainActivity.findViewById(R.id.wifi_setting);
        mBtn_wifi_setting.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
         //       showWifiSetting(mMainActivity);
              /*  if(android.os.Build.VERSION.SDK_INT > 10) {
                    mMainActivity.startActivity(new Intent( android.provider.Settings.ACTION_SETTINGS));
                } else {
                    mMainActivity.startActivity(new Intent( android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                }*/
                mMainActivity.startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
            }
        });
        mBtn_format_setting = (Button) mMainActivity.findViewById(R.id.format_setting);
        mBtn_format_setting.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                AskFormat(UtilShareDB.PATH_MAIN);
            }
        });
       /* mBtn_volume_add = (Button) mMainActivity.findViewById(R.id.volume_add);
        mBtn_volume_add.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if (mVolumeData < 15) {
                    mVolumeData++;
                    VolumeSet(mVolumeData);
                }
            }
        });
        mBtn_volume_sub = (Button) mMainActivity.findViewById(R.id.volume_sub);
        mBtn_volume_sub.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if (mVolumeData > 0) {
                    mVolumeData--;
                    VolumeSet(mVolumeData);
                }
            }
        });*/
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        setDateTime();
        setTimeOfDay();
    }
    /**
     * 日期控件的事件
     */
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;

            updateDateDisplay();
        }
    };
    /**
     * 设置日期
     */
    private void setDateTime(){
        final Calendar c = Calendar.getInstance();

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        updateDateDisplay();
    }
    /**
     * 设置时间
     */
    private void setTimeOfDay(){
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        updateTimeDisplay();
    }

    /**
     * 更新时间显示
     */
    private void updateTimeDisplay(){
        showTime.setText(new StringBuilder().append(mHour).append(":")
                .append((mMinute < 10) ? "0" + mMinute : mMinute));
    }

    /**
     * 时间控件事件
     */
    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mHour = hourOfDay;
            mMinute = minute;

            updateTimeDisplay();
        }
    };

  /*//  @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(mMainActivity, mDateSetListener, mYear, mMonth, mDay);
            case TIME_DIALOG_ID:
                return new TimePickerDialog(mMainActivity, mTimeSetListener, mHour, mMinute, true);
        }

        return null;
    }*/
    //@Override



    /**
     * 处理日期和时间控件的Handler
     */


    private void AskFormat(final String absDir) {
        final AlertDialog alertDialogEx;
        alertDialogEx = UtilSystem.getInstance().showAlertDialogEx(mMainActivity, R.layout.dialog_msg,R.style.dialog_msg,R.id.title, "Format The Device?");
        if (alertDialogEx != null) {
            Button btn_cancel = (Button) alertDialogEx.getWindow().findViewById(R.id.btn_cancel);
            Button btn_confirm = (Button) alertDialogEx.getWindow().findViewById(R.id.btn_enter);
            btn_confirm.setText("Enter");
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialogEx.dismiss();
                }
            });
            btn_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UtilFileOper.getInstance().DeleteDir(absDir);
                    alertDialogEx.dismiss();
                }
            });
        }
    }

    public void VolumeSet(int data) {
        AudioManager audioManager = (AudioManager) ((Activity)mMainActivity).getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, data, 0);
   //     mTxt_volume_data.setText(String.valueOf(String.format("%02d", data)));
    }

    //@Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mYear = year;
        mMonth = month;
        mDay = dayOfMonth;
        updateDateDisplay();
    }

    //@Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mHour = hourOfDay;
        mMinute = minute;
        updateTimeDisplay();
    }


}
