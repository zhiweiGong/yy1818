package com.android.EgLauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.format.DateFormat;

import com.android.tools.Misc.UtilHandler;
import com.android.tools.NetWork.UtilWifiAdmin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FloatStateBar {
    private static String TAG = "FloatStateBar";

    private Context mContext;
    private WindowManager.LayoutParams mLayoutParams;
    private View mLayoutView;

    private ImageView mImageView_Wifi;
    private TextView  mTextView_Times;
    private ImageView mImageView_Volume;
    private TextView  mTextView_Volume;
    private ImageView mImageView_Signal;
    private ImageView mImageView_Battery;
    private  AudioManager audioManager;
    private static final int MSG_ICON_STATE_UPDATE = 1000;
    private Handler mSBarHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ICON_STATE_UPDATE:
                    updateWifiState();
                    UtilHandler.getInstance().sendMsg(mSBarHandler,MSG_ICON_STATE_UPDATE,1000);
                    break;
                default:
                    break;
            }

            super.handleMessage(msg);
        }
    };

    public FloatStateBar(Context context){
        mContext = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE) ;
        createFloatWindow(context);

        UtilHandler.getInstance().sendMsg(mSBarHandler,MSG_ICON_STATE_UPDATE);

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(ubiReceiver, filter);
        Log.d(TAG,"init");
    }

    private BroadcastReceiver ubiReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")){
                int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                Log.e(TAG,"volume is changed" + currVolume);
                mTextView_Volume.setText(String.valueOf(currVolume));
            }
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)){
                Log.e(TAG,"ACTION_BATTERY_CHANGED");
            //    StringBuilder sb = new StringBuilder();
                int rawlevel = intent.getIntExtra("level", -1);
                int scale = intent.getIntExtra("scale", -1);
                int status = intent.getIntExtra("status", -1);
                int health = intent.getIntExtra("health", -1);
                int level = -1; // percentage, or -1 for unknown
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }
                Log.e(TAG,"The phone");
                if (BatteryManager.BATTERY_HEALTH_OVERHEAT == health) {
                    Log.e(TAG,"s battery feels very hot!");
                } else {
                    switch (status) {
                        case BatteryManager.BATTERY_STATUS_UNKNOWN:
                            Log.e(TAG,"no battery.");
                            break;
                        case BatteryManager.BATTERY_STATUS_CHARGING:
                            Log.e(TAG,"s battery");

                            if (level > 80 && level <= 100) {
                                mImageView_Battery.setImageResource(R.drawable.stat_sys_battery_charge_100);
                                Log.e(TAG, " needs charging right away.");
                            }
                            else if (level > 60 && level <= 80)
                                mImageView_Battery.setImageResource(R.drawable.stat_sys_battery_charge_80);
                            else if (level > 40 && level <= 60)
                                mImageView_Battery.setImageResource(R.drawable.stat_sys_battery_charge_60);
                            else if (level > 20 && level <= 40)
                                mImageView_Battery.setImageResource(R.drawable.stat_sys_battery_charge_40);
                            else if (level > 10 && level <= 20)
                                mImageView_Battery.setImageResource(R.drawable.stat_sys_battery_charge_20);
                            else if (level > 0 && level <= 10)
                                mImageView_Battery.setImageResource(R.drawable.stat_sys_battery_charge_20);
                            else if (level == 0 )
                                mImageView_Battery.setImageResource(R.drawable.stat_sys_battery_charge_0);
                            break;
                        case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                            if (level > 80 && level <= 100) {
                                mImageView_Battery.setImageResource(R.drawable.stat_sys_battery_100);
                                Log.e(TAG, " needs charging right away.");
                            }
                            else if (level > 60 && level <= 80)
                                mImageView_Battery.setImageResource(R.drawable.stat_sys_battery_80);
                            else if (level > 40 && level <= 60)
                                mImageView_Battery.setImageResource(R.drawable.stat_sys_battery_60);
                            else if (level > 20 && level <= 40)
                                mImageView_Battery.setImageResource(R.drawable.stat_sys_battery_40);
                            else if (level > 10 && level <= 20)
                                mImageView_Battery.setImageResource(R.drawable.stat_sys_battery_20);
                            else if (level > 0 && level <= 10)
                                mImageView_Battery.setImageResource(R.drawable.stat_sys_battery_10);
                            else if (level == 0 )
                                mImageView_Battery.setImageResource(R.drawable.stat_sys_battery_0);
                            break;
                        case BatteryManager.BATTERY_STATUS_FULL:
                            Log.e(TAG," is fully charged.");
                            break;
                        default:
                            Log.e(TAG,"s battery is indescribable!");
                            break;
                    }
                }
                //b.append(' ');
                //batterLevel.setText(sb.toString());
            }
        }
    };

    private void createFloatWindow(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.gravity = Gravity.CENTER | Gravity.TOP;
        mLayoutParams.x = 0;
        mLayoutParams.y = 0;
        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        initStateBarView(context);

        windowManager.addView(mLayoutView, mLayoutParams);
    }
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
   //                 long sysTime = System.currentTimeMillis();
   //                 CharSequence sysTimeStr = DateFormat.format("hh:mm", sysTime);

                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                    String fileName = formatter.format(date);

                    mTextView_Times.setText(fileName); //更新时间
                    break;
                default:
                    break;
            }
        }
    };
    class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 1;  //消息(一个整型值)
                    mHandler.sendMessage(msg);// 每隔1秒发送一个msg给mHandler
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    public static String  readSysfs(String path) {

        if (!new File(path).exists()) {
            Log.e(TAG, "File not found: " + path);
            return null;
        }

        String str = null;
        StringBuilder value = new StringBuilder();

     //   if(DEBUG)
            Log.i(TAG, "readSysfs path:" + path);

        try {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            try {
                while ((str = br.readLine()) != null) {
                    if(str != null)
                        value.append(str);
                };
                fr.close();
                br.close();
                if(value != null)
                    return value.toString();
                else
                    return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void initStateBarView(Context context) {
        mLayoutView = View.inflate(context, R.layout.status_bar, null);
        mImageView_Wifi = (ImageView)mLayoutView.findViewById(R.id.wifi_state);
        mImageView_Battery = (ImageView)mLayoutView.findViewById(R.id.battery_state);
        mTextView_Volume = (TextView)mLayoutView.findViewById(R.id.volume_text_state);
        mTextView_Times = (TextView)mLayoutView.findViewById(R.id.time_state);
        int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mTextView_Volume.setText(String.valueOf(currVolume));
        int level = Integer.parseInt(readSysfs("/sys/class/power_supply/battery/capacity"));
        if (level > 80 && level <= 100) {
            mImageView_Battery.setImageResource(R.drawable.stat_sys_battery_100);
            Log.e(TAG, " needs charging right away.");
        }
        else if (level > 60 && level <= 80)
            mImageView_Battery.setImageResource(R.drawable.stat_sys_battery_80);
        else if (level > 40 && level <= 60)
            mImageView_Battery.setImageResource(R.drawable.stat_sys_battery_60);
        else if (level > 20 && level <= 40)
            mImageView_Battery.setImageResource(R.drawable.stat_sys_battery_40);
        else if (level > 10 && level <= 20)
            mImageView_Battery.setImageResource(R.drawable.stat_sys_battery_20);
        else if (level > 0 && level <= 10)
            mImageView_Battery.setImageResource(R.drawable.stat_sys_battery_10);
        else if (level == 0 )
            mImageView_Battery.setImageResource(R.drawable.stat_sys_battery_0);

        new TimeThread().start(); //启动新的线程

    }

    public void ShowFloatWindow() {
        mLayoutView.setVisibility(View.VISIBLE);
    }

    public void HideFloatWindow() {
        mLayoutView.setVisibility(View.INVISIBLE);
    }


    public void updateWifiState(){
        if (UtilWifiAdmin.getInstance(mContext).WifiConnectCheck() == true) {
            int level = UtilWifiAdmin.getInstance(mContext).getRssi();
            mImageView_Wifi.setVisibility(View.VISIBLE);

            if (level <= 0 && level >= -50) {
                mImageView_Wifi.setImageResource(R.drawable.stat_sys_wifi_signal_4);
            } else if (level < -50 && level >= -70) {
                mImageView_Wifi.setImageResource(R.drawable.stat_sys_wifi_signal_3);
            } else if (level < -70 && level >= -80) {
                mImageView_Wifi.setImageResource(R.drawable.stat_sys_wifi_signal_2);
            } else if (level < -80 && level >= -100) {
                mImageView_Wifi.setImageResource(R.drawable.stat_sys_wifi_signal_1);
            } else {
                mImageView_Wifi.setImageResource(R.drawable.stat_sys_wifi_signal_0);
            }
        }else{
            mImageView_Wifi.setVisibility(View.GONE);
        }
    }

    public void updateTimesState(){

    }

    public void updateBatteryState(){

    }
}
