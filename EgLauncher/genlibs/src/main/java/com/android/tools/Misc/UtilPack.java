package com.android.tools.Misc;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UtilPack {
    private static final String TAG = "UtilPack";
    public static final String mEnd_tag = "end\r\n";
    public static final int MAX_TAG_LEN = 256;

    private String mDeviceCid = "";
    private static UtilPack mUtilPack = null;

    public static synchronized UtilPack getInstance(){
        if(mUtilPack == null){
            mUtilPack = new UtilPack();
        }
        return mUtilPack;
    }

    public UtilPack(){
        mDeviceCid = UtilSystem.getInstance().getSystemCid();
    }

    public String md5sum(byte[] buffer) {
        String ret = "000";
        MessageDigest md5;
        char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9','a', 'b', 'c', 'd', 'e', 'f' };

        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(buffer,0,buffer.length);
            byte[] b = md5.digest();
            StringBuilder sb = new StringBuilder(b.length * 2);

            for(int i = 0; i < b.length; i++) {
                sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
                sb.append(HEX_DIGITS[b[i] & 0x0f]);
            }
            ret = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        //Log.d(TAG,"MD5:"+ret);

        return ret;
    }

    public byte[] MakePackage(String tag,String value,int index,int total,byte[] buffer) {
        byte[] buf_send = null;
        JSONObject ObjJson = new JSONObject();

        if(buffer == null){
            Log.d(TAG, "MakePackage buffer null.");
            return null;
        }

        if(buffer.length == 0){
            Log.d(TAG, "MakePackage buffer length = 0.");
            return null;
        }

        String md5 = md5sum(buffer);
        try {
            ObjJson.put("tag", tag);
            ObjJson.put("val", value);
            ObjJson.put("len", buffer.length);
            ObjJson.put("idx", index);
            ObjJson.put("tal", total);
            ObjJson.put("md5", md5);
            ObjJson.put("cid", mDeviceCid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        byte[] json_buffer = ObjJson.toString().getBytes();

        if (json_buffer != null) {
            Log.d(TAG, "send json package:" + ObjJson.toString());
            buf_send = new byte[MAX_TAG_LEN + buffer.length + mEnd_tag.length()];//{"GetPic":"374277"}
            System.arraycopy(json_buffer, 0, buf_send, 0, json_buffer.length);
            System.arraycopy(buffer, 0, buf_send, MAX_TAG_LEN, buffer.length);
            System.arraycopy(mEnd_tag.getBytes(), 0, buf_send, MAX_TAG_LEN + buffer.length, mEnd_tag.length());
        } else {
            Log.d(TAG, "make json error!");
        }

        return buf_send;
    }
}
