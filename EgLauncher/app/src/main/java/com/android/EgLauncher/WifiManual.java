package com.android.EgLauncher;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;
import com.android.EgLauncher.WiFiAdmin;
//import com.android.DriftSys.tools.ToolsUtil;

public class WifiManual extends Dialog{
	private final String TAG = "WifiManual";
    public static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
	private WifiSetup mWifiSetup;
	private Context mContextMain ;
	private EditText mEditTextPASSWD;
	private EditText mEditTextSSID;
        private TextView mTextPASSWD;
	private String strPassword;
	private boolean bShowPassWD = true;
	private View mView;
    private int wifistat=0;
	//private TextView mStatus_TextView;

	private static final int MSG_DRAW_WIFI_STATUS_FAIL      = 1000;
	public Handler mScanHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_DRAW_WIFI_STATUS_FAIL:
				//	if(mStatus_TextView.getText().equals("Connecting...")) {
				//		mStatus_TextView.setText("Connecting fail!");
				//	}
					break;
				default:
					break;
			}
			super.handleMessage(msg);
		}
	};

	public WifiManual(Context context,WifiSetup wifiSetup, boolean bEdit) {
		super(context, R.style.FullscreenTheme);
		mContextMain = context;
		mWifiSetup = wifiSetup;
		Log.e("zhiwei","xxxxxxxxxxxxx wifiSetup.wifiItemSSID:"+wifiSetup.wifiItemSSID);
		if (getNetworkState(mContextMain) && getConnectedSsid(mContextMain).equals("\""+mWifiSetup.wifiItemSSID+"\"")) {
			wifistat = 1;
			mView = View.inflate(mContextMain,R.layout.wifi_connected, null);
			super.setContentView(mView);
			Window window = getWindow();
			WindowManager.LayoutParams params = window.getAttributes();
			params.height = 600;
			params.gravity = Gravity.CENTER;
			window.setAttributes(params);

			mView.findViewById(R.id.wifi_manual_Button_determine).setOnKeyListener(new View.OnKeyListener(){
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if(event.getAction() == KeyEvent.ACTION_DOWN){
						if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
							mWifiSetup.ReScan();
							dismiss();
							//
						}
					}
					return false;
				}
			});
			mView.findViewById(R.id.wifi_manual_Button_forget).setOnKeyListener(new View.OnKeyListener(){
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if(event.getAction() == KeyEvent.ACTION_DOWN){
						if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
							Log.e("zhiwei","xxxxxxxxxxxxx start  wifi forget"+mWifiSetup.wifiItemSSID);
							if (mWifiSetup.wiFiAdmin.forgetConfig("\""+mWifiSetup.wifiItemSSID+"\"")){
							//	dismiss();
							    mWifiSetup.ReScan();
							    Log.e("zhiwei","xxxxxxxxxxxxx wifi forget");
							}
						}
					}
					return false;
				}
			});			
		} else {
			wifistat = 0;
			mView = View.inflate(mContextMain,R.layout.wifi_manual, null);
			super.setContentView(mView);

			Window window = getWindow();
			WindowManager.LayoutParams params = window.getAttributes();
			params.height = 600;
			params.gravity = Gravity.CENTER;
			window.setAttributes(params);

			
			//mStatus_TextView = (TextView) mView.findViewById(R.id.wifi_manual_Status_TextView);
			//mStatus_TextView.setText("Not Connected");
					bShowPassWD = true;
			mEditTextPASSWD = (EditText) mView.findViewById(R.id.wifi_manual_PassWD_EditText);
			mEditTextPASSWD.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
			mTextPASSWD = (TextView) mView.findViewById(R.id.wifi_manual_PassWD_Text);
			if (mWifiSetup.wifiItemPwd != null && mWifiSetup.wifiItemPwd.equals("no")) {
				mEditTextPASSWD.setVisibility(View.INVISIBLE);
				mTextPASSWD.setVisibility(View.INVISIBLE);
			}
			mEditTextSSID = (EditText) mView.findViewById(R.id.wifi_manual_SSID_EditText);
			if(wifiSetup.wifiItemSSID != null) {
				mEditTextSSID.setText(wifiSetup.wifiItemSSID);
			}
			if(bEdit == true) {
				mEditTextSSID.setEnabled(true);
				//mEditTextSSID.requestFocus();
				mEditTextSSID.setText("");
			}else{
				mEditTextSSID.setFocusable(false);
				mEditTextSSID.setEnabled(false);
				mEditTextPASSWD.setText("");
				//mEditTextPASSWD.requestFocus();
			}

			mView.findViewById(R.id.wifi_manual_Button_back).setOnKeyListener(new View.OnKeyListener(){
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if(event.getAction() == KeyEvent.ACTION_DOWN){
						if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
							dismiss();
							mWifiSetup.ReScan();
						}
					}
					return false;
				}
			});
			mView.findViewById(R.id.wifi_manual_Button_show).setOnKeyListener(new View.OnKeyListener(){
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if(event.getAction() == KeyEvent.ACTION_DOWN){
						if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
							if(bShowPassWD == true) {
								mEditTextPASSWD.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
								bShowPassWD = false;
							}else{
								mEditTextPASSWD.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
								bShowPassWD = true;
							}
						}
					}
					return false;
				}
			});
			mView.findViewById(R.id.wifi_manual_Button_connect).setOnKeyListener(new View.OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if(event.getAction() == KeyEvent.ACTION_DOWN){
						if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
							if (mWifiSetup.wifiItemSSID != null) {
								if (!mWifiSetup.wifiItemPwd.equals("no")) {
									strPassword = mEditTextPASSWD.getText().toString();
									if (strPassword != null) {
										mWifiSetup.wiFiAdmin.disconnectWifi();
										if (mWifiSetup.wifiItemPwd.equals("wpa")) {
											mWifiSetup.wiFiAdmin.connect(mWifiSetup.wifiItemSSID, strPassword, WiFiAdmin.WifiCipherType.WIFICIPHER_WPA);
										} else if (mWifiSetup.wifiItemPwd.equals("wep")) {
											mWifiSetup.wiFiAdmin.connect(mWifiSetup.wifiItemSSID, strPassword, WiFiAdmin.WifiCipherType.WIFICIPHER_WEP);
										}
										//mStatus_TextView.setText("Connecting...");
							//			ToolsUtil.getInstance().sendMsg(mScanHandler,MSG_DRAW_WIFI_STATUS_FAIL,10000);
									}
							    } else {
									mWifiSetup.wiFiAdmin.disconnectWifi();
									mWifiSetup.wiFiAdmin.connect(mWifiSetup.wifiItemSSID, null, WiFiAdmin.WifiCipherType.WIFICIPHER_NOPASS);
						//			ToolsUtil.getInstance().sendMsg(mScanHandler,MSG_DRAW_WIFI_STATUS_FAIL,10000);
								}
								mWifiSetup.ReScan();
							}
					    }
					}
					return false;
				}
			});
        }
		InfoReceiver mInfoReceiver = new InfoReceiver();
		IntentFilter mInfoReceiverFilter = new IntentFilter(CONNECTIVITY_CHANGE_ACTION);
		mContextMain.registerReceiver(mInfoReceiver, mInfoReceiverFilter);
	}
    public NetworkInfo getActiveNetwork(Context context){
        if (context == null)
            return null;
        ConnectivityManager mConnMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mConnMgr == null)
            return null;
		//NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo info = mConnMgr.getActiveNetworkInfo(); // 获取活动网络连接信息
        return info;
    }
	public boolean getNetworkState(Context context){
		NetworkInfo info = getActiveNetwork(context);
		if (info != null)
		    return info.getState().equals(NetworkInfo.State.CONNECTED);
		else
			return false;
	}
	public String getConnectedSsid(Context context) {
		String mSSID =null;
		WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		if (wifiInfo != null)
			mSSID = wifiInfo.getSSID();
		Log.e("zhiwei","xxxxxxxxxxxxx getSSID:"+mSSID);
		return mSSID;
	}
	@Override
	public void setContentView(int layoutResID) {

	}

	@Override
	public void setContentView(View view, LayoutParams params) {

	}

	@Override
	public void setContentView(View view) {

	}

	private class InfoReceiver extends BroadcastReceiver {
		public  void onReceive(Context context, Intent intent){
			if(intent.getAction().equals(CONNECTIVITY_CHANGE_ACTION)){
				Log.e("zhiwei","xxxxxxxxxxxxx CONNECTIVITY_CHANGE_ACTION");
				NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if(info.getState().equals(NetworkInfo.State.DISCONNECTED)){
					Log.e("zhiwei","xxxxxxxxxxxxx wifi disconnected");
					if (wifistat ==1 ) {
					    mWifiSetup.Scan_Draw();
						dismiss();
					}
		//			if(mStatus_TextView != null){
		//				mStatus_TextView.setText("Not Connected");
		//			}
				}else if(info.getState().equals(NetworkInfo.State.CONNECTED)){
					Log.e("zhiwei","xxxxxxxxxxxxx wifi connected");
					WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
					WifiInfo wifiInfo = wifiManager.getConnectionInfo();
	        //				if(mStatus_TextView != null){
					if(wifiInfo.getSSID().equals("\""+mWifiSetup.wifiItemSSID+"\"") == true) {
			//			mStatus_TextView.setText("Connected");
			            if (wifistat == 0) {
						    mWifiSetup.Scan_Draw();
							dismiss();
						}
						mView = View.inflate(mContextMain,R.layout.wifi_connected, null);

						mView.findViewById(R.id.wifi_manual_Button_determine).setOnKeyListener(new View.OnKeyListener(){
							@Override
							public boolean onKey(View v, int keyCode, KeyEvent event) {
								if(event.getAction() == KeyEvent.ACTION_DOWN){
									if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
										mWifiSetup.ReScan();
										dismiss();
										//mWifiSetup.ReScan();
									}
								}
								return false;
							}
						});
						mView.findViewById(R.id.wifi_manual_Button_forget).setOnKeyListener(new View.OnKeyListener(){
							@Override
							public boolean onKey(View v, int keyCode, KeyEvent event) {
								if(event.getAction() == KeyEvent.ACTION_DOWN){
									if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
										if (mWifiSetup.wiFiAdmin.forgetConfig("\""+mWifiSetup.wifiItemSSID+"\"")){
											mWifiSetup.ReScan();
											dismiss();
										}
									}
								}
								return false;
							}
						});		
					}
			//		}
			        
				}else if(info.getState().equals(NetworkInfo.State.CONNECTING)){
					Log.e("zhiwei","xxxxxxxxxxxxx wifi connecting");
		//			if(mStatus_TextView != null){
				//	if(mStatus_TextView != null){
				//		mStatus_TextView.setText("Connecting...");
				//	}
				}
			}
		}
	}

}
