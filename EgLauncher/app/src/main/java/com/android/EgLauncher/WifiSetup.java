package com.android.EgLauncher;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.EgLauncher.WiFiAdmin;
//import com.android.DriftSys.tools.ToolsUtil;

import java.util.List;

import static android.content.Context.WIFI_SERVICE;

public class WifiSetup  extends Dialog {
	private final String TAG = "WifiSetup";

	public WifiManual mWifiManual = null;

	private View mView;
	public WifiManager wifiManager;
	public List<ScanResult> list;
	public WiFiAdmin wiFiAdmin;
	public String wifiItemSSID;
        public String wifiItemPwd ;
	public ImageView mScan_ImageView;
	public Context mContextMain;

	private Thread Scan_Thread;
	public boolean bKill_Thread = false;

	private static final int []arr_wifi_signal={
			R.drawable.stat_sys_wifi_signal_0,
			R.drawable.stat_sys_wifi_signal_1,
			R.drawable.stat_sys_wifi_signal_2,
			R.drawable.stat_sys_wifi_signal_3,
			R.drawable.stat_sys_wifi_signal_4,
	};

	private static final int []arr_wifi_scan={
			R.drawable.stat_sys_wifi_scan_0,
			R.drawable.stat_sys_wifi_scan_1,
			R.drawable.stat_sys_wifi_scan_2,
			R.drawable.stat_sys_wifi_scan_3,
			R.drawable.stat_sys_wifi_scan_4,
	};
	private static final int MSG_SHOW_WIFI_LIST      = 1000;
	private static final int MSG_DRAW_WIFI_SCAN      = 1001;

	public Handler mScanHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_SHOW_WIFI_LIST:
					wiFiAdmin.GetScan();
					wiFiAdmin.getConfiguration();
					wifiManager = (WifiManager) mContextMain.getSystemService(WIFI_SERVICE);
					list = wifiManager.getScanResults();
					ListView listView = (ListView)mView.findViewById(R.id.wifi_setup_ListView);
					if (list != null) {
                                            sortByLevel(list);  
					    listView.setAdapter(new WifiListAdapter(mContextMain,list));
					    ItemClickListener_init(listView);
					}
					break;
				case MSG_DRAW_WIFI_SCAN:
					int id = ((Integer)msg.obj).intValue();
					mScan_ImageView.setBackgroundResource(id);
					break;
				default:
					break;
			}
			super.handleMessage(msg);
		}
	};

	public WifiSetup(Context context) {

		super(context, R.style.FullscreenTheme);
		Log.e(TAG,"xxxxxxxxxxpx");
		mContextMain = context;
		mView = View.inflate(mContextMain,R.layout.wifi_setup, null);
		super.setContentView(mView);

		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.height = 600;
		params.gravity = Gravity.CENTER;
		window.setAttributes(params);

		mScan_ImageView = (ImageView) mView.findViewById(R.id.wifi_setup_scan_ImageView);

		mView.findViewById(R.id.wifi_setup_Button_rescan).setOnKeyListener(new View.OnKeyListener(){
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_DOWN){
					if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
						ReScan();
					}
				}
				return false;
			}
		});
		mView.findViewById(R.id.wifi_setup_Button_manual).setOnKeyListener(new View.OnKeyListener(){
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_DOWN){
					if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
						bKill_Thread = true;
						mWifiManual = new WifiManual(mContextMain,WifiSetup.this,true);
						mWifiManual.show();
					}
				}
				return false;
			}
		});
		mView.findViewById(R.id.wifi_setup_Button_skip).setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_DOWN){
					if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
						bKill_Thread = true;
						dismiss();
					}
				}
				return false;
			}
		});

		Scan_Init();
	}

	public void Scan_Draw(){
		bKill_Thread = false;

		if(Scan_Thread != null) {
			if (Scan_Thread.isAlive() == true) {
				return;
			}
		}

		Scan_Thread =new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				int aaa = 0;
				while(aaa++ < 8) {
					int i = 0;
					while (i++ < (arr_wifi_scan.length-1)) {
						if(bKill_Thread == false){
						//	ToolsUtil.getInstance().sendMsg(mScanHandler,MSG_DRAW_WIFI_SCAN,(Integer)arr_wifi_scan[i]);
							try {
								Thread.sleep(300);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
				if(bKill_Thread == false){
			//		ToolsUtil.getInstance().sendMsg(mScanHandler,MSG_DRAW_WIFI_SCAN,(Object)arr_wifi_scan[0]);
			//		ToolsUtil.getInstance().sendMsg(mScanHandler,MSG_SHOW_WIFI_LIST,0);
				}
			}
		});
		Scan_Thread.start();
	}

	private void Scan_Init() {
		Log.e(TAG, "Scan init!");
		wiFiAdmin = new WiFiAdmin(mContextMain);
		Log.e(TAG,"wifiAdmin");
		Thread Start_Thread =new Thread(new Runnable()
		{
			@Override
			public void run() {
				Log.e(TAG,"sssss run openWifi");
				wiFiAdmin.openWifi();
				Scan_Draw();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ie) {
					Log.d(TAG, "Thread wait enableing InterruptedException!");
				}
				Log.e(TAG,"wifi Scan");
				//wiFiAdmin.startScan();
			//	ToolsUtil.getInstance().sendMsg(mScanHandler,MSG_SHOW_WIFI_LIST,100);
			}
		});
		Start_Thread.start();
	}

	public void ReScan() {
		wiFiAdmin.goScan();
		Scan_Draw();
	}
        private void sortByLevel(List<ScanResult> list) {  
            for(int i=0;i<list.size();i++)  
                for(int j=1+i;j<list.size();j++)  
                { 
                    if (list.get(j).SSID.isEmpty()) {
                        list.remove(j);
                        j--; 
                    } else if(list.get(i).level<list.get(j).level) {  
                        ScanResult temp = null;  
                        temp = list.get(i);
                        if (temp != null) {
                              list.set(i, list.get(j));  
                              list.set(j, temp);
                        } else
                            list.remove(i);
                    }
                }
        }  
	private void ItemClickListener_init(ListView listView){
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent,View view, int position, long id) {
				Log.d(TAG, "BSSID:" + list.get(position).BSSID);
				wifiItemSSID = list.get(position).SSID;
				String capabilities = list.get(position).capabilities;
                                if (!capabilities.isEmpty()) {
                                    if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                                        wifiItemPwd = "wpa";
                                    } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                                        wifiItemPwd = "wep";
                                    } else {
                                        wifiItemPwd = "no";
                                    }
                                } else
                                    wifiItemPwd = "no";
                                int wifiItemId = wiFiAdmin.IsConfiguration("\""+ wifiItemSSID + "\"");
//				if (wifiItemId != -1) {
//					if (wiFiAdmin.ConnectWifi(wifiItemId)) {
//						Scan_Draw();
//					}
//				} else {
					mWifiManual = new WifiManual(mContextMain,WifiSetup.this,false);
					mWifiManual.show();
//				}
			}
		});
	}

	public class WifiListAdapter extends BaseAdapter {

		LayoutInflater inflater;
		List<ScanResult> list;

		public WifiListAdapter(Context context, List<ScanResult> list) {
			// TODO Auto-generated constructor stub
			this.inflater = LayoutInflater.from(context);
			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = inflater.inflate(R.layout.item_wifi_list, null);
			ScanResult scanResult = list.get(position);
			String str_SSID = scanResult.SSID;
			TextView textView_ssid = (TextView) view.findViewById(R.id.SSID_TextView);
			textView_ssid.setText(str_SSID);
			ImageView imageview_connected = (ImageView) view.findViewById(R.id.Connect_ImageView);

			WifiManager wifiManager = (WifiManager)mContextMain.getSystemService(WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			if(wifiInfo.getSSID().equals("\""+str_SSID+"\"") && !str_SSID.isEmpty()){
                                Log.i("river", str_SSID + "connected");
				imageview_connected.setImageDrawable(mContextMain.getResources().getDrawable(R.drawable.wifi_connected));
			}else{
                            if (!str_SSID.isEmpty()) {
                                ScanResult scResult = scanResult;
                                String capabilities = scResult.capabilities;
                               // Log.i("river","capabilities=" + capabilities);
                                if (!capabilities.isEmpty()) {
                                    if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                                        Log.i("river", str_SSID + " wpa");
                                        imageview_connected.setImageDrawable(mContextMain.getResources().getDrawable(R.drawable.wifi_lock));
                                    } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                                        Log.i("river", str_SSID + " wep");
                                        imageview_connected.setImageDrawable(mContextMain.getResources().getDrawable(R.drawable.wifi_lock));
                                    } else {
                                        Log.i("river", str_SSID + " no");
                                        imageview_connected.setImageDrawable(mContextMain.getResources().getDrawable(R.drawable.wifi_unlock));
                                    }
                                }
                            } else {
                             //  list.remove(position);
                            }
			}

			ImageView imageView = (ImageView) view.findViewById(R.id.Signal_ImageView);
			//判断信号强度，显示对应的指示图标
			if (Math.abs(scanResult.level) > 100) {
				imageView.setImageDrawable(mContextMain.getResources().getDrawable(arr_wifi_signal[1]));
			} else if (Math.abs(scanResult.level) > 70) {
				imageView.setImageDrawable(mContextMain.getResources().getDrawable(arr_wifi_signal[2]));
			} else if (Math.abs(scanResult.level) > 40) {
				imageView.setImageDrawable(mContextMain.getResources().getDrawable(arr_wifi_signal[3]));
			} else {
				imageView.setImageDrawable(mContextMain.getResources().getDrawable(arr_wifi_signal[4]));
			}
			return view;
		}
	}

	@Override
	public void setContentView(int layoutResID) {

	}

	@Override
	public void setContentView(View view, ViewGroup.LayoutParams params) {

	}

	@Override
	public void setContentView(View view) {

	}
}
