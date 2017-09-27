package com.android.tools.File;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class UtilAssetFiles {
	private final String TAG = "UtilAssetFiles";
	private static UtilAssetFiles mUtilAssetFiles = null;

	public static synchronized UtilAssetFiles getInstance(){
		if(mUtilAssetFiles == null){
			mUtilAssetFiles = new UtilAssetFiles();
		}
		return mUtilAssetFiles;
	}

    public UtilAssetFiles(){
        Log.d(TAG,"init...");
    }

    public String[] ReadAssetFileLine(Context context,String path)
    {
        String strRet[] = null;
        String[] content = new String[1024];
        int index = 0;
        String strLine;

        try {
            InputStream inputstream = context.getAssets().open(path);
            if (inputstream != null) {
                InputStreamReader inputreader = new InputStreamReader(inputstream);
                BufferedReader buffreader = new BufferedReader(inputreader);

                while (( strLine = buffreader.readLine()) != null) {
                    if(index < 1024){
                        content[index]=strLine;
                        index++;
                    }else{
                        break;
                    }
                }
                buffreader.close();
                inputreader.close();
                inputstream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (index > 0) {
                strRet = new String[index];

                for (int i = 0; i < index; i++) {
                    strRet[i] = content[i];
                }
            }
        }

        return strRet;
    }

    public String ReadAssetRawFile(Context context,String path){
        String srtRawFile = "";
        String strReadLine = "";

        try{
            InputStream inputstream = context.getAssets().open(path);
            if (inputstream != null){
                InputStreamReader inputreader = new InputStreamReader(inputstream);
                BufferedReader buffreader = new BufferedReader(inputreader);

                while (( strReadLine = buffreader.readLine()) != null) {
                    //Log.i(TAG, "line:"+line);
                    srtRawFile = srtRawFile+strReadLine+"\r\n";//dangerous not change it!!!
                }
                buffreader.close();
                inputreader.close();
                inputstream.close();
            }else{
                //Log.e(TAG,"file not exists or emputy");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Log.i(TAG, "ReadFile path:"+path+",readlen:"+srtFile);
        return srtRawFile;
    }

    public void CopyAssetFile(Context context,String strInFileName,String strOutFileName)
    {
        InputStream inputstream;
        OutputStream outputstream;

		try {
            outputstream = new FileOutputStream(strOutFileName);
            inputstream = context.getAssets().open(strInFileName);
	        byte[] buffer = new byte[1024];
	        int length = inputstream.read(buffer);
	        while(length > 0)
	        {
                outputstream.write(buffer, 0, length);
	            length = inputstream.read(buffer);
	        }

            outputstream.flush();
            inputstream.close();
            outputstream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public void CopyAssetDir(Context context, String assetDir, String toDir) {
        String[] files;

        try {
            files = context.getAssets().list(assetDir);
        } catch (IOException e) {
            return;
        }

        File targetDir = new File(toDir);
        if (targetDir.exists() == false) {
        	targetDir.mkdirs();
        }

        for (int i = 0; i < files.length; i++) {
            try {
                String fileName = files[i];
                // we make sure file name not contains '.' to be a folder.
                if (fileName.contains(".") == false) {
                    if (0 == assetDir.length()) {
                    	CopyAssetDir(context, fileName, toDir + fileName + "/");
                    } else {
                    	CopyAssetDir(context, assetDir + "/" + fileName, toDir+"/"+ fileName + "/");
                    }
                    continue;
                }
                File outFile = new File(targetDir, fileName);
                if (outFile.exists() == true){
                    //outFile.delete();
                    continue;
                }
                InputStream inputstream = null;
                if (0 != assetDir.length()){
                    inputstream = context.getAssets().open(assetDir + "/" + fileName);
                } else {
                    inputstream = context.getAssets().open(fileName);
                }
                OutputStream outputstream = new FileOutputStream(outFile);

                byte[] buf = new byte[1024];
                int len;
                while ((len = inputstream.read(buf)) > 0) {
                    outputstream.write(buf, 0, len);
                }

                inputstream.close();
                outputstream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
