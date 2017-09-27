package com.android.EgLauncher;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.EgLauncher.file.FileManager;
import com.android.EgLauncher.file.FileNode;
import com.android.tools.File.UtilFileOper;
import com.android.tools.Misc.UtilSystem;

import java.io.File;
import java.util.ArrayList;

public class FilelistAdapter extends BaseAdapter {

	protected int m_Layout_APKListItem;
//	protected int m_TextView_AppName;
	protected int m_TextView_FileName;
	protected int m_Image_file_icon;
	protected int m_delete_file_button;
//	protected int m_ImgView_APPIcon;
//	protected int m_CheckBox_InstallState;
//	protected int m_CheckBox_SelState;
	private LayoutInflater mInflater;
	protected ArrayList<FileNode> m_apklist = null;
	protected ListView m_list = null;
    public int selectposition;
	public int playicon;
	private Context mContext;
	FilelistAdapter(Context context,  ArrayList<FileNode> apklist,int Layout_Id, int text_filename_id,
					int file_icon_id, int delete_file_id, ListView list, int position, int playpos) {
		mInflater = LayoutInflater.from(context);
		m_Layout_APKListItem = Layout_Id;
		m_Image_file_icon = file_icon_id;
		m_delete_file_button = delete_file_id;
		m_TextView_FileName = text_filename_id;
		m_apklist = apklist;
		m_list = list;
		selectposition = position;
		playicon = playpos;
		mContext = context;
	}

	public int getCount() {
		if (m_apklist != null)
			return m_apklist.size();
		else
			return 0;
	}

	public Object getItem(int position) {
		if (m_apklist != null)
			return m_apklist.get(position);
		else
			return null;
	}
	public String getItemString(int position) {
		String filePath = "";
		FileNode mSelectFile;
		try {
			mSelectFile = m_apklist.get(position);
			filePath = mSelectFile.getFilePath();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return filePath;
	}
	public long getItemId(int position) {
		return position;
	}

	public void ondeleate_select(String mFileName, final int pos) {
		final AlertDialog alertDialogEx;
		alertDialogEx = UtilSystem.getInstance().showAlertDialogEx(mContext, R.layout.dialog_msg,R.style.dialog_msg,R.id.title_msg, "是否删除文件"+mFileName+"?");
		if (alertDialogEx != null) {
			Button btn_cancel = (Button) alertDialogEx.getWindow().findViewById(R.id.btn_cancel);
			Button btn_confirm = (Button) alertDialogEx.getWindow().findViewById(R.id.btn_enter);
			btn_confirm.setText("删除");
			btn_cancel.setText("取消");
			btn_cancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					//UtilFileOper.getInstance().DeleteFile(m_apklist.get(pos).getFilePath());
					alertDialogEx.dismiss();
				}
			});
			btn_confirm.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
				//	UtilFileOper.getInstance().DeleteDir(absDir);
					//Log.e("TEST","xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx : "+getItemString(pos));
					m_apklist.get(pos).delete();
					//alertDialogEx.dismiss();
					m_apklist.remove(getItem(pos));
					// this.notify();
					notifyDataSetChanged();
					FileManager.getInstance().addWavRootMTP(mContext);
					alertDialogEx.dismiss();
				}
			});
		}
	}

	abstract class SelStateListener implements Button.OnClickListener {
		int PosInList = 0;

		SelStateListener(int pos) {
			PosInList = pos;
		}

		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (m_list.isItemChecked(PosInList) != isChecked)
				m_list.setItemChecked(PosInList, isChecked);
		}
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int file_pos = position;
		View layoutview = null;
		if (convertView == null) {
			layoutview = mInflater.inflate(m_Layout_APKListItem, parent, false);
		} else {
			layoutview = convertView;
		}
		TextView FileName = (TextView) layoutview.findViewById(m_TextView_FileName);
		ImageView mPlayImageView = (ImageView) layoutview.findViewById(m_Image_file_icon);
		Button mDeleteButton = (Button) layoutview.findViewById(m_delete_file_button);
		FileNode pinfo = (FileNode) getItem(position);
	//	TextView AppName = (TextView) layoutview.findViewById(m_TextView_AppName);
	//	CheckBox InstallState = (CheckBox) layoutview.findViewById(m_CheckBox_InstallState);
	//	ImageView Appicon = (ImageView) layoutview.findViewById(m_ImgView_APPIcon);
		//CheckBox SelState = (CheckBox) layoutview.findViewById(m_CheckBox_SelState);
	//	ImageView.setOnCheckedChangeListener(new SelStateListener(position));
		mDeleteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FileNode pinfo = (FileNode) getItem(file_pos);
				ondeleate_select(pinfo.getFileName(), file_pos);
				Log.i("TEST", "button onClick");
			}
		});

		Log.e("zhiwei","FileName getFilePath: " + pinfo.getFileName() + "position:"+position);
		FileName.setText(pinfo.getFileName());
		if (selectposition == position)
		{
			Log.e("zhiwei", "select position");
			//layoutview.setBackgroundColor(0xffffff);
			FileName.setTextColor(0xff0dab61);
		} else {
			FileName.setTextColor(0xff000000);
		}
		if (playicon == position) {
			mPlayImageView.setBackgroundResource(R.drawable.itemsel);
		} else {
			mPlayImageView.setBackgroundResource(R.drawable.rec_flie);
		}
//		AppName.setText(pinfo.getApplicationName());
	//	InstallState.setChecked(pinfo.checkInstalled());
//		Appicon.setImageDrawable(pinfo.getApkIcon());
//		SelState.setChecked(m_list.isItemChecked(position));

		return layoutview;
	}
    public void setSelectItem(int position){
		selectposition = position;
	}

	public void setPlayIconItem(int position) {
		playicon = position;
	}
	public boolean hasStableIds() {
		return true;
	}

}
