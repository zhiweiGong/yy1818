package com.android.tools.NetWork;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

public class UtilWifiAp {
    private static final String TAG = "UtilWifiAp";

    public static final int WIFI_AP_STATE_DISABLING = 10;
    public static final int WIFI_AP_STATE_DISABLED = 11;
    public static final int WIFI_AP_STATE_ENABLING = 12;
    public static final int WIFI_AP_STATE_ENABLED = 13;
    public static final int WIFI_AP_STATE_FAILED = 14;

    private static UtilWifiAp mUtilWifiAp = null;
    public static synchronized UtilWifiAp getInstance(){
        if(mUtilWifiAp == null){
            mUtilWifiAp = new UtilWifiAp();
        }
        return mUtilWifiAp;
    }

    public UtilWifiAp(){
        Log.d(TAG,"init");
    }

    public void setWifiApEnabled(Context context,String ssid,String passwd){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if(wifiManager.getConnectionInfo() != null){
            wifiManager.setWifiEnabled(false);
            try {
                Thread.sleep(1500);
            } catch (Exception e) {
                Log.e(TAG,"open ap error");
            }
        }

        try {
            WifiConfiguration netConfig = new WifiConfiguration();
            netConfig.SSID = ssid;//"android_0007";
            netConfig.preSharedKey = passwd;
            netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            netConfig.status = WifiConfiguration.Status.ENABLED;

            Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifiManager, netConfig, true);
        } catch (Exception e) {
            Log.e(TAG,"Cannot set WiFi AP state" + e);
        }
    }

    public boolean closeWifiAp(Context context) {
        boolean ret = false;

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (isWifiApEnabled(context)) {
            try {
                Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");
                method.setAccessible(true);
                WifiConfiguration config = (WifiConfiguration) method.invoke(wifiManager);
                Method method2 = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                ret = (Boolean) method2.invoke(wifiManager, config, false);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public boolean isWifiApEnabled(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        try {
            Method method = wifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifiManager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getWifiApState(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApState");
            int i = (Integer) method.invoke(wifiManager);
            Log.i(TAG,"wifi state:  " + i);
            return i;
        } catch (Exception e) {
            Log.e(TAG,"Cannot get WiFi AP state" + e);
            return WIFI_AP_STATE_FAILED;
        }
    }

    public int getApType(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        int nret = 0;
        try {

            Method localMethod = wifiManager.getClass().getDeclaredMethod("getWifiApConfiguration");

            if (localMethod == null) nret = 0;
                Object localObject1 = localMethod.invoke(wifiManager);
            if (localObject1 == null) nret = 0;
                WifiConfiguration localWifiConfiguration = (WifiConfiguration) localObject1;

            if (localWifiConfiguration.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
                nret = WifiConfiguration.KeyMgmt.WPA_PSK;
            } else if (localWifiConfiguration.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP)) {
                nret = WifiConfiguration.KeyMgmt.WPA_EAP;
            } else if (localWifiConfiguration.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
                nret = WifiConfiguration.KeyMgmt.IEEE8021X;
            } else if (localWifiConfiguration.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.NONE)) {
                nret = WifiConfiguration.KeyMgmt.NONE;
            } else if (localWifiConfiguration.allowedKeyManagement.get(4)) {
                nret = 4;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return nret;
    }

    public String getApSSID(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        try {
            Method localMethod = wifiManager.getClass().getDeclaredMethod("getWifiApConfiguration");
            if (localMethod == null) return null;
            Object localObject1 = localMethod.invoke(wifiManager);
            if (localObject1 == null) return null;
            WifiConfiguration localWifiConfiguration = (WifiConfiguration) localObject1;
            if (localWifiConfiguration.SSID != null) return localWifiConfiguration.SSID;
            Field localField1 = WifiConfiguration.class.getDeclaredField("mWifiApProfile");
            if (localField1 == null) return null;
            localField1.setAccessible(true);
            Object localObject2 = localField1.get(localWifiConfiguration);
            localField1.setAccessible(false);
            if (localObject2 == null) return null;
            Field localField2 = localObject2.getClass().getDeclaredField("SSID");
            localField2.setAccessible(true);
            Object localObject3 = localField2.get(localObject2);
            if (localObject3 == null) return null;
            localField2.setAccessible(false);
            String str = (String) localObject3;
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
