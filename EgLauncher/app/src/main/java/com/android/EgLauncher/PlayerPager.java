package com.android.EgLauncher;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.android.EgLauncher.VoicePlayer.FileAdapter;
import com.android.EgLauncher.VoicePlayer.MediaPlay;
import com.android.EgLauncher.VoicePlayer.PlayBarListener;
import com.android.tools.Misc.UtilHandler;
import com.android.tools.Misc.UtilSystem;
import com.android.tools.SysPorp.UtilShareDB;
import com.android.EgLauncher.file.FileManager;
import com.android.EgLauncher.util.Fm1388Util;
import com.android.EgLauncher.util.MiscUtil;
import com.android.EgLauncher.wav.AudioPlayWav;
import com.android.EgLauncher.wav.RecorderWav;
import java.io.File;

import static android.content.Context.MODE_PRIVATE;

public class PlayerPager {
	private final String TAG = "SettingPager";

	private MediaPlay mMediaPlay;
	private PlayBarListener mPlayBarListener;
	private FileAdapter mFileAdapter;
	private FileManager mFileManager;
	private FilelistAdapter mFilelistAdapter;
	private ListView mListView;
	private MainActivity mMainActivity;
	private String mCurrFilePath = "";
	private String FileName = "";
	private Button mPrevButton;
	private Button mPlayButton;
	//private Button mPauseButton;
	private Button mStopButton;
	private Button mNextButton;
	private TextView mPlayedTime;
	private TextView mTotleTime;
	private AudioPlayWav mAudioPlayWav = null;
	private SeekBar mplaySeekBar = null;
	private int mState = mMainActivity.STATE_IDLE;
	private int mPlayFileLenInSec = 0;
	private long mTimeViewTime = 0;
	private String mTimerFormat = "%02d:%02d:%02d";
	private boolean misInUiloopRender = false;
	private String mSelectFilePath = "";
	private String moldSelectFilePath="";
	public static final int MSG_PLAY_TIME_TICK     = 1000;
	public int mSelectPosition;
	public int mPlayPosition;
	final Handler mLoopHandler = new Handler();
	Runnable mUpdateTimer = new Runnable() {
		public void run() {
			uiLoopRender(true);
		}
	};
	private void uiLoopRender(boolean loop) {
		misInUiloopRender = true;
		if (mAudioPlayWav != null && mState == mMainActivity.STATE_PLAY_STARTED){
			mTimeViewTime = mAudioPlayWav.getPlayTimeInSec();
			int[] hms = MiscUtil.sec2hms(mTimeViewTime);

			String timeStr = String
					.format(mTimerFormat, hms[0], hms[1], hms[2]);
			//mButton_Times.setText(timeStr);

			mPlayedTime.setText(timeStr);
			Log.e(TAG,"play time"+timeStr);
			//mTextView_voice_to_text.setText("STATUS: Play: " + FileName);
			mplaySeekBar.setMax(mPlayFileLenInSec);
			mplaySeekBar.setProgress((int)mTimeViewTime);
		}

		if (mState == mMainActivity.STATE_IDLE){
			//updateTimerRest();
			//mTextView_voice_to_text.setText("STATUS: IDLE");
		}

		if (loop){
			mLoopHandler.postDelayed(mUpdateTimer, 400);
		}
	}
	public Handler mPlaysHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MainActivity.UI_HANDLER_TEST:
					Log.i(TAG, "..get a UI_HANDLER_TEST");
					break;
				case MainActivity.STATE_PLAY_STARTED:
					mState = mMainActivity.STATE_PLAY_STARTED;
					mPlayFileLenInSec = msg.arg1;
					int[] toteltim = MiscUtil.sec2hms(mPlayFileLenInSec);
					String toteltime = String.format(mTimerFormat,toteltim[0],toteltim[1],toteltim[2]);
					mTotleTime.setText(toteltime);
					FileName = msg.obj.toString();
					uiLoopRender(false);
					break;
				case MainActivity.STATE_PLAY_END:
					mState = mMainActivity.STATE_IDLE;
					if (mAudioPlayWav != null){
						if (mAudioPlayWav.getState() == AudioPlayWav.PLAY_END){
							mAudioPlayWav = null;
							int[] toteltim1 = MiscUtil.sec2hms(mPlayFileLenInSec);
							String toteltime1 = String.format(mTimerFormat,toteltim1[0],toteltim1[1],toteltim1[2]);
							mPlayedTime.setText(toteltime1);
							mplaySeekBar.setProgress(mPlayFileLenInSec);
							mPlayButton.setText("播放");
							mMainActivity.mRecordRunState = mMainActivity.NONE;
						}
					}
					//mAudioPlayWav = null;
					uiLoopRender(false);
					break;
				case MSG_PLAY_TIME_TICK:
				//	mButton_Times.setText("00:00:00");
					mPlayedTime.setText("00:00:00");
					mTotleTime.setText("00:00:00");
					Log.e(TAG,"PLAY_TIME_TICK : set time tick");
					break;
				default:
					break;
			}
			super.handleMessage(msg);
		}
	};
	public Handler mPlayHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_PLAY_TIME_TICK:
					if(mMediaPlay.mIsPlay == true) {
						int iPos = mMediaPlay.GetPlayPosition();
						mPlayBarListener.setProgress(iPos);
						UtilHandler.getInstance().sendMsg(mPlayHandler, MSG_PLAY_TIME_TICK, 10);
					}
					break;
				default:
					break;
			}
			super.handleMessage(msg);
		}
	};

	public PlayerPager(MainActivity mainActivity) {
		mMainActivity = mainActivity;
		mSelectPosition = 0;
		mPlayPosition = 0;
		Log.d(TAG,"init");
		if (!misInUiloopRender) {
			mLoopHandler.postDelayed(mUpdateTimer, 500);
		}
	}
	private void stopPlayForSafe(){
		if(mAudioPlayWav != null){
			mAudioPlayWav.stop();
		}
		mAudioPlayWav = null;
	}
	private String getPassword(){
		//String cityName = City.getCity().getCityName();
		// if(cityName==null ||cityName==""){
		Context ctx =mMainActivity;
		SharedPreferences sp =ctx.getSharedPreferences("PASSWORD", MODE_PRIVATE);

		//}
		String pwd = sp.getString("Password", "123456");
		Log.e("SetpwdDialogActivity", "get pwd = "+ pwd);
		return pwd;
	}

	public void ondeleate_select(String mFileName) {
		final AlertDialog alertDialogEx;
		alertDialogEx = UtilSystem.getInstance().showAlertDialogEx(mMainActivity, R.layout.dialog_msg,R.style.dialog_msg,R.id.title_msg, "是否删除文件"+mFileName+"?");
		if (alertDialogEx != null) {
			Button btn_cancel = (Button) alertDialogEx.getWindow().findViewById(R.id.btn_cancel);
			Button btn_confirm = (Button) alertDialogEx.getWindow().findViewById(R.id.btn_enter);
			btn_confirm.setText("删除");
			btn_cancel.setText("取消");
			btn_cancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					alertDialogEx.dismiss();
				}
			});
			btn_confirm.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					//	UtilFileOper.getInstance().DeleteDir(absDir);
					alertDialogEx.dismiss();
				}
			});
		}
	}

    public void InitPlayList() {
		Log.e(TAG,"InitPlayList");
		mListView =(ListView) mMainActivity.findViewById(R.id.play_listview);
		Log.e(TAG,"mListView");
		mFileManager = FileManager.getInstance();
	//	mFileManager.initFileList(mFileManager.mFile);
		Log.e(TAG,"mFileManager");
	//	mFileAdapter = new FileAdapter(mMainActivity,mPlayHandler);
		mFileManager.syncFile();
		mFilelistAdapter = new FilelistAdapter(mMainActivity, mFileManager.getFileList(), R.layout.file_list, R.id.path_text,
				R.id.file_icon,R.id.delete_file, mListView, mSelectPosition, mPlayPosition);
		Log.e(TAG,"mFilelistAdapter");
	//	mFileAdapter.scanFiles(UtilShareDB.PATH_MAIN+"record/");
//		if (mFileAdapter.getCount() >0 ) {
//			mCurrFilePath = mFileAdapter.getItemString(0);
//			mSelectFilePath = FileManager.getInstance().getNextFile().getFilePath();
//			Log.e(TAG,mFileAdapter.mListFile.toString());
//		}

		//getNewestFile

		mListView.setAdapter(mFilelistAdapter);
		Log.e(TAG,"mListView.setAdapter");
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView adapterView, View view,int arg2, long arg3) {
				Log.e(TAG,"mListView setOnItemClickListener" + arg2);
				mSelectPosition = arg2;
				mFilelistAdapter.setSelectItem(mSelectPosition);
				mPlayPosition = mSelectPosition;
				mFilelistAdapter.setPlayIconItem(mPlayPosition);
				mFilelistAdapter.notifyDataSetInvalidated();//notifyDataSetChanged();
				//mSelectFilePath = mFilelistAdapter.getItemString(arg2);
			}
		});
		mplaySeekBar = (SeekBar)mMainActivity.findViewById(R.id.Play_Slider);
//		mFileAdapter.setSelectItem(0);
//		mFileAdapter.notifyDataSetInvalidated();
		mPlayedTime =(TextView)mMainActivity.findViewById(R.id.play_current);
		mTotleTime =(TextView)mMainActivity.findViewById(R.id.play_total);
		mMediaPlay = new MediaPlay();
		mPlayBarListener = new PlayBarListener(mMediaPlay);
		mPlayBarListener.InitPlayBar(mMainActivity);
		mplaySeekBar.setClickable(false);
		mplaySeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			}
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				if (mAudioPlayWav != null && arg2){
					if (mAudioPlayWav.getState() == AudioPlayWav.PLAY_PAUSE_STATE ||
							mAudioPlayWav.getState() == AudioPlayWav.PLAY_STARTED){
						mAudioPlayWav.seekTo(arg1);
					}
				}
			}
		});
		////////////////////////////////////////
		mPrevButton = (Button) mMainActivity.findViewById(R.id.play_prev);
		mPrevButton.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Log.e(TAG,"mPrevButton DOWN");
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Log.e(TAG,"mPrevButton UP");
					if (mFilelistAdapter.getCount() >0 && mSelectPosition > 0) {
						stopPlayForSafe();
						String password = getPassword();//UtilShareDB.getInstance().ReadSysKey("password", "123456");
						mSelectPosition--;
						mFilelistAdapter.setSelectItem(mSelectPosition);
						mPlayPosition = mSelectPosition;
						mFilelistAdapter.setPlayIconItem(mPlayPosition);
						mFilelistAdapter.notifyDataSetInvalidated();
						//mListView.smoothScrollToPositionFromTop(mSelectPosition, -1);

						mSelectFilePath = FileManager.getInstance().getNumFile(mSelectPosition).getFilePath();

						mAudioPlayWav = new AudioPlayWav(mMainActivity, mPlaysHandler,
								password, new File(mSelectFilePath));
						moldSelectFilePath = mSelectFilePath;
						mPlayButton.setText("暂停");
						mMainActivity.mRecordRunState = mMainActivity.PLAYING;
						//	}
					}
				}
				return false;
			}
		});
        mPlayButton = (Button) mMainActivity.findViewById(R.id.play_play);
        mPlayButton.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Log.e(TAG,"mPlayButton DOWN");
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Log.e(TAG,"mPlayButton UP");
					if (mFilelistAdapter.getCount() >0 ) {
						mSelectFilePath = FileManager.getInstance().getNumFile(mSelectPosition).getFilePath();
						if (moldSelectFilePath != mSelectFilePath) {
							moldSelectFilePath = mSelectFilePath;
							stopPlayForSafe();
						}
						if (mAudioPlayWav == null) {
							Log.e(TAG, "play 2");
							String password = getPassword();//UtilShareDB.getInstance().ReadSysKey("password", "123456");
							mPlayButton.setText("暂停");
							mMainActivity.mRecordRunState = mMainActivity.PLAYING;
							mAudioPlayWav = new AudioPlayWav(mMainActivity, mPlaysHandler,
									password, new File(mSelectFilePath));
						} else { // in play status alread. play plau.
							Log.e(TAG, "play 1");
							if (mAudioPlayWav.getState() == AudioPlayWav.PLAY_STARTED) {
								mPlayButton.setText("播放");
								mMainActivity.mRecordRunState = mMainActivity.NONE;
								mAudioPlayWav.pause();
							} else if (mAudioPlayWav.getState() == AudioPlayWav.PLAY_PAUSE_STATE) {
								mAudioPlayWav.resume();
								mPlayButton.setText("暂停");
								mMainActivity.mRecordRunState = mMainActivity.PLAYING;
							}
						}
					}
				}
				return false;
			}
		});

        mStopButton = (Button) mMainActivity.findViewById(R.id.play_stop);
        mStopButton.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Log.e(TAG,"mStopButton DOWN");
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Log.e(TAG,"mStopButton UP");
					if (mFilelistAdapter.getCount() >0 ) {
						mPlayButton.setText("播放");
						mMainActivity.mRecordRunState = mMainActivity.NONE;
						stopPlayForSafe();
					}
				}
				return false;
			}
		});
        mNextButton = (Button) mMainActivity.findViewById(R.id.play_next);
        mNextButton.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Log.e(TAG,"mNextButton DOWN");
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Log.e(TAG,"mNextButton UP");
					if (mFilelistAdapter.getCount() >0 && mSelectPosition < (mFilelistAdapter.getCount()-1)) {
						stopPlayForSafe();
						String password = getPassword();//UtilShareDB.getInstance().ReadSysKey("password", "123456");
						//mSelectFilePath = FileManager.getInstance().getNextFile().getFilePath();
						mSelectPosition++;
						mFilelistAdapter.setSelectItem(mSelectPosition);
						mPlayPosition = mSelectPosition;
						mFilelistAdapter.setPlayIconItem(mPlayPosition);
						mFilelistAdapter.notifyDataSetInvalidated();

					//	if (mSelectPosition % 6 == 0 ) {
					//		mListView.smoothScrollToPositionFromTop(mSelectPosition ,6);
					//	}

						mSelectFilePath = FileManager.getInstance().getNumFile(mSelectPosition).getFilePath();
						mAudioPlayWav = new AudioPlayWav(mMainActivity, mPlaysHandler,
								password, new File(mSelectFilePath));
						moldSelectFilePath = mSelectFilePath;
						mPlayButton.setText("暂停");
						//addWavFileMTP
					}
				}
				return false;
			}
		});
	}
}