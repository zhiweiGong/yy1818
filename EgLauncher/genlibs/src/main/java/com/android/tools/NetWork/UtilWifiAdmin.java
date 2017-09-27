package com.android.tools.NetWork;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

public class UtilWifiAdmin {
    private static final String TAG = "UtilWifiAdmin";

    private WifiManager mWifiManager;
    private List<WifiConfiguration> mWifiConfigList;
    private Context mContext;
    private WifiInfo mWifiInfo;

    private static UtilWifiAdmin mUtilWifiAdmin = null;
    public static synchronized UtilWifiAdmin getInstance(Context context){
        if(mUtilWifiAdmin == null){
            mUtilWifiAdmin = new UtilWifiAdmin(context);
        }
        return mUtilWifiAdmin;
    }

    public UtilWifiAdmin(Context context) {
        Log.d(TAG,"init");
        mContext = context;
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    public int checkState() {
        return mWifiManager.getWifiState();
    }

    public void startScan() {
        mWifiManager.startScan();
    }

    public WifiManager getWifiManager() {
        return mWifiManager;
    }

    public WifiInfo getConnectionInfo() {
       return mWifiManager.getConnectionInfo();
    }

    public int getRssi() {
        return mWifiManager.getConnectionInfo().getRssi();
    }

    public boolean ConnectWifi(int wifiId) {
        for (int i = 0; i < mWifiConfigList.size(); i++) {
            WifiConfiguration wifi = mWifiConfigList.get(i);
            if (wifi.networkId == wifiId) {
                while (!(mWifiManager.enableNetwork(wifiId, true))) {
                    Log.i(TAG,String.valueOf(mWifiConfigList.get(wifiId).status));
                }
                return true;
            }
        }
        return false;
    }

    public void disConnectionWifi() {
        if(mWifiInfo == null){
            return;
        }

        int netId = mWifiInfo.getNetworkId();
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
        mWifiManager.removeNetwork(netId);
        mWifiManager.saveConfiguration();
    }

    public String GetWiFiIpAddress() {
        String strIP = "127.0.0.0";

        if (mWifiManager.isWifiEnabled()){
            WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            if(ipAddress != 0){
                strIP = intToIp(ipAddress);
            }
        }

        return strIP;
    }

    public boolean WifiConnectCheck() {
        if (mWifiManager.isWifiEnabled()){
            WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            if(ipAddress != 0){
                return true;
            }
        }
        return false;
    }

    public String intToIp(int i) {
        return ( i & 0xFF) + "." +  ((i >> 8 ) & 0xFF) + "." +  ((i >> 16 ) & 0xFF)  + "." +   ((i >> 24 ) & 0xFF );
    }

    public int IsConfiguration(String SSID) {
        mWifiConfigList = mWifiManager.getConfiguredNetworks();

        if (mWifiConfigList == null) {
            return -1;
        }

        Log.i(TAG, String.valueOf(mWifiConfigList.size()));
        for (int i = 0; i < mWifiConfigList.size(); i++) {
            Log.i(mWifiConfigList.get(i).SSID,
                    String.valueOf(mWifiConfigList.get(i).networkId));
            if (mWifiConfigList.get(i).SSID.equals(SSID)) {
                return mWifiConfigList.get(i).networkId;
            }
        }

        return -1;
    }

    public enum WifiCipherType {
        WIFICIPHER_WEP,
        WIFICIPHER_WPA,
        WIFICIPHER_NOPASS
    }

    public void connect(String ssid, String password) {
        Thread thread = new Thread(new ConnectRunnable(ssid, password, WifiCipherType.WIFICIPHER_WPA));
        thread.start();
    }

    public void connect(String ssid, String password, WifiCipherType type) {
        Thread thread = new Thread(new ConnectRunnable(ssid, password, type));
        thread.start();
    }

    class ConnectRunnable implements Runnable {
        private String mSsid;
        private String mPassword;
        private WifiCipherType mType;

        public ConnectRunnable(String ssid, String password, WifiCipherType type) {
            mSsid = ssid;
            mPassword = password;
            mType = type;
        }

        @Override
        public void run() {
            if (!mWifiManager.isWifiEnabled()) {
                mWifiManager.setWifiEnabled(true);
                try{
                    Thread.sleep(1500);
                } catch(Exception e) {
                    Log.e(TAG,"wait error");
                }
            }

            WifiConfiguration wifiConfig = createWifiInfo(mSsid, mPassword, mType);

            if (wifiConfig == null) {
                Log.d(TAG, "wifiConfig is null!");
                return;
            }

            int netID = mWifiManager.addNetwork(wifiConfig);

            boolean enabled = mWifiManager.enableNetwork(netID, true);
            Log.d(TAG, "enableNetwork status enable=" + enabled);

            boolean connected = mWifiManager.reconnect();
            Log.d(TAG, "enableNetwork connected=" + connected);
        }
    }

    private WifiConfiguration createWifiInfo(String SSID, String Password, WifiCipherType Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        // nopass
        if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }

        // wep
        if (Type == WifiCipherType.WIFICIPHER_WEP) {
            if (!TextUtils.isEmpty(Password)) {
                if (isHexWepKey(Password)) {
                    config.wepKeys[0] = Password;
                } else {
                    config.wepKeys[0] = "\"" + Password + "\"";
                }
            }
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }

        // wpa
        if (Type == WifiCipherType.WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);

            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }

        return config;
    }

    private static boolean isHexWepKey(String wepKey){
        final int len = wepKey.length();
        // WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
        if (len != 10 && len != 26 && len != 58) {
            return false;
        }
        return isHex(wepKey);
    }

    private static boolean isHex(String key){
        for (int i = key.length() - 1; i >= 0; i--) {
            final char c = key.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f')) {
                return false;
            }
        }
        return true;
    }
}