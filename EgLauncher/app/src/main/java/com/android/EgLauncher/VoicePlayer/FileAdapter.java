package com.android.EgLauncher.VoicePlayer;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.EgLauncher.R;
import com.android.tools.File.UtilFileOper;
import com.android.tools.Misc.UtilSystem;
import com.android.tools.SysPorp.UtilShareDB;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class FileAdapter extends BaseAdapter {
    private final String TAG = "FileAdapter";

    private int  mSelectItem = -1;

    public List<File> mListFile =new LinkedList<File>();
    public  String  mCurrPath;

    private Context mContext;
    private Handler mPlayHandler;
    private File    mSelectFile;

    private Bitmap  mBmp_Sel;

    public FileAdapter(Context context,Handler handler){
        mContext = context;
        mPlayHandler = handler;
        mBmp_Sel = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.itemsel);
    }

    public int getCount() {
        return mListFile.size();
    }

   // public Object getItem(int position) {
//        return position;
 //   }

    public File getItem(int position) {
        // TODO Auto-generated method stub
        return mListFile.get(position);
    }
    public long getItemId(int position) {
        return position;
    }

    public String getItemString(int position) {
        String filePath = "";

        try {
            mSelectFile = mListFile.get(position);
            filePath = mSelectFile.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return filePath;
    }

    public void AskDeleteFile(final String absPath, final int position) {
        final AlertDialog alertDialogEx;
        alertDialogEx = UtilSystem.getInstance().showAlertDialogEx(mContext, R.layout.dialog_msg,R.style.dialog_msg,R.id.title, "Delete This File?");
        if (alertDialogEx != null) {
            Button btn_cancel = (Button) alertDialogEx.getWindow().findViewById(R.id.btn_cancel);
            Button btn_confirm = (Button) alertDialogEx.getWindow().findViewById(R.id.btn_enter);
            TextView btn_message = (TextView) alertDialogEx.getWindow().findViewById(R.id.title_msg);
            btn_message.setText("Delete This File?");
            btn_confirm.setText("Delete");
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialogEx.dismiss();
                }
            });
            btn_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UtilFileOper.getInstance().DeleteFile(absPath);
                    alertDialogEx.dismiss();
                    mListFile.remove(getItem(position));
                   // this.notify();
                    notifyDataSetChanged();
                }
            });
        }
    }

    public View getView(int position, View arg1, ViewGroup arg2) {
        final int file_pos = position;

        View v = View.inflate(mContext, R.layout.file_list, null);
        ImageView file_icon = (ImageView) v.findViewById(R.id.file_icon);
        TextView path_text = (TextView) v.findViewById(R.id.path_text);
        Button delete_file = (Button) v.findViewById(R.id.delete_file);
        path_text.setText(getItemString(file_pos).substring((UtilShareDB.PATH_MAIN+"record/").length()));
        delete_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String absPath = getItemString(file_pos);
                AskDeleteFile(absPath,file_pos);

            }
        });

        try {
            mSelectFile = mListFile.get(position);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (position == mSelectItem) {
            path_text.setTextColor(Color.rgb(70,185,149));
            file_icon.setImageBitmap(mBmp_Sel);
        }else{
            file_icon.setVisibility(View.INVISIBLE);
        }

        path_text.setBackgroundColor(Color.TRANSPARENT);

        return v;
    }

    public  void setSelectItem(int selectItem) {
        this.mSelectItem = selectItem;

        if (mSelectFile == null) {
            return;
        }

        if ((mListFile.size()>0) && (selectItem<mListFile.size())) {
            mSelectFile = mListFile.get(selectItem);
        }
    }

    public void scanFiles(String path) {
        mListFile.clear();
        File dir=new File(path);
        File[] subFiles=dir.listFiles();

        if(subFiles!=null){
            for(File f:subFiles){
                if(f.getName().endsWith("wav")){
                    mListFile.add(f);
                }
            }
        }
        this.notifyDataSetChanged();
        mCurrPath = path;
    }
}