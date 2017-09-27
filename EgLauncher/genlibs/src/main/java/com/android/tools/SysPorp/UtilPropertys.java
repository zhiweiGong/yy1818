package com.android.tools.SysPorp;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**  
* @author  
* @version  
*/   
public class UtilPropertys {
	private final String TAG = "UtilProperty";

	private static Properties props = new Properties();
    private String PROP_NAME = "DynProperty.txt";
    private String PATH_MAIN = "/mnt/sdcard/trunk/";

    private static UtilPropertys mUtilPropertys = null;

	public static synchronized UtilPropertys getInstance(){
		if(mUtilPropertys == null){
			mUtilPropertys = new UtilPropertys();
		}
		return mUtilPropertys;
	}

    public UtilPropertys(){
        Log.d(TAG,"init...");

	 	File pfile = new File(PATH_MAIN+PROP_NAME);
 	    if(pfile.exists() == false){
 	    	Log.e(TAG,"profile is not exist");
 	    	
 	    	try {
				pfile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
 	    }
    	
        try {   
            props.load(new FileInputStream(PATH_MAIN+PROP_NAME));
        } catch (FileNotFoundException e) {   
            e.printStackTrace();
        } catch (IOException e) {          
        	e.printStackTrace();
        }   
    }
      
    /**  
    * 读取属性文件中相应键的值  
    * @param key 主键  
    * @return String  
    */   
    public String getKeyValue(String key) {   
        return props.getProperty(key);   
    }   
  
    public String getKeyValue(String key,String strDef) {
    	String strProp = props.getProperty(key);
    	
    	//Log.d(TAG,"key:"+key + ",strProp:"+ strProp+",strDef:"+strDef);   
    	
    	if(strProp == null){
    		Log.e(TAG,"warning , get key value is null,make as a default~~~!!!");
    		strProp = strDef;
    	}
    	
        return strProp;   
    }   
    /**  
    * 根据主键key读取主键的值value  
    * @param filePath 属性文件路径  
    * @param key 键名  
    */   
    public String readValue(String filePath, String key) {   
        Properties props = new Properties();   
        try {   
            InputStream in = new BufferedInputStream(new FileInputStream(filePath));   
            props.load(in);   
            String value = props.getProperty(key);   
            Log.d(TAG,key +"键的值是："+ value);   
            return value;   
        } catch (Exception e) {   
            e.printStackTrace();   
            return null;   
        }   
    }   
      
    /**  
    * 更新（或插入）一对properties信息(主键及其键值)  
    * 如果该主键已经存在，更新该主键的值；  
    * 如果该主键不存在，则插件一对键值。  
    * @param keyname 键名  
    * @param keyvalue 键值  
    */   
    public void writeProperties(String keyname,String keyvalue) {   
    	String RdValue = props.getProperty(keyname);
    	
    	if(RdValue != null){
    		if(RdValue.equals(keyvalue) == true){
    			return;
    		}
    	}
    	
        try {   
            // 调用 Hashtable 的方法 put，使用 getProperty 方法提供并行性。   
            // 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。   
            OutputStream fos = new FileOutputStream(PATH_MAIN+PROP_NAME);
            props.setProperty(keyname, keyvalue);   
            // 以适合使用 load 方法加载到 Properties 表中的格式，   
            // 将此 Properties 表中的属性列表（键和元素对）写入输出流   
            props.store(fos, "Update '" + keyname + "' value");   
            Log.d(TAG,"keyname:"+keyname + ",keyvalue:"+ keyvalue);   
        } catch (IOException e) {   
            Log.e(TAG,"属性文件更新错误");   
        }   
    }   
  
    /**  
    * 更新properties文件的键值对  
    * 如果该主键已经存在，更新该主键的值；  
    * 如果该主键不存在，则插件一对键值。  
    * @param keyname 键名  
    * @param keyvalue 键值  
    */   
    public void updateProperties(String keyname,String keyvalue) {   
    	String RdValue = props.getProperty(keyname);
    	
    	if(RdValue != null){
    		if(RdValue.equals(keyvalue) == true){
    			return;
    		}
    	}
    	
        try {   
            props.load(new FileInputStream(PATH_MAIN+PROP_NAME));
            // 调用 Hashtable 的方法 put，使用 getProperty 方法提供并行性。   
            // 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。   
            OutputStream fos = new FileOutputStream(PATH_MAIN+PROP_NAME);
            props.setProperty(keyname, keyvalue);   
            // 以适合使用 load 方法加载到 Properties 表中的格式，   
            // 将此 Properties 表中的属性列表（键和元素对）写入输出流   
            props.store(fos, "Update '" + keyname + "' value");   
        } catch (IOException e) {   
        	Log.e(TAG,"属性文件更新错误");   
        }   
    }
}