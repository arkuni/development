package net.arkuni.mysimplerecorder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import common.util.HMTrans;

public class MainActivity extends ListActivity implements Constants{

	private Button recordBtn;
	private Button settingBtn;
	private Button historyBtn;
	private Button introBtn;
	private Button pauseBtn;
	private Button stopBtn;
	private Button saveNBtn;
	private Button saveYBtn;
	private Button historyBackBtn;
	private TextView recordingTime;
	private TextView waveInfo;
	private int RECORD_STATE = 0;
	private int HISTORY_STATE = 0;
	private MySimpleRecorder app;
	private View recordingBtnSet;
	private View historyBtnSet;
	private View saveBtnSet;
	private View stopBtnSet;
	private CounterThread checkTime;
	private ProgressBarThread playTime;
	private Context context;
	private ProgressBar progressbar;
	private ProgressBar progressbarMain;
	private TextView playProgressbarBG;
	private TextView playProgressbar;
	private final int XFLIP = 0;
	private final int YFLIP = 1;
	private final int ETC1 = 2;
	private final int ETC2 = 3;
	private List<HashMap<String, String>> recordFiles = null;
	private RecordFileAdapter adapter;
	private Handler handler = new Handler();
	private long beforeRecFile = -1;
	private MediaPlayer background;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = MainActivity.this;
		setContentView(R.layout.activity_main);
		RelativeLayout appMainLayout = (RelativeLayout)this.findViewById(R.id.app_bg);
		ViewGroup.LayoutParams params = appMainLayout.getLayoutParams();
		params.height = getResources().getDisplayMetrics().widthPixels;
		Typeface typeFace;
		String language = getResources().getConfiguration().locale.getLanguage();
		registerReceiver(mHandleMessageReceiver,
                new IntentFilter(DISPLAY_MESSAGE_ACTION));
		recordBtn = (Button)findViewById(R.id.record_btn);
		progressbar = (ProgressBar)findViewById(R.id.progressBar1);
		progressbarMain = (ProgressBar)findViewById(R.id.progressBar2);
		settingBtn = (Button)findViewById(R.id.setting_btn);
		historyBtn = (Button)findViewById(R.id.history_btn);
		introBtn = (Button)findViewById(R.id.intro_btn);
		pauseBtn = (Button)findViewById(R.id.pause_btn);
		stopBtn = (Button)findViewById(R.id.stop_btn);
		saveNBtn = (Button)findViewById(R.id.save_n_btn);
		saveYBtn = (Button)findViewById(R.id.save_y_btn);
		historyBackBtn = (Button)findViewById(R.id.history_back_btn);
		waveInfo = (TextView) findViewById(R.id.wav_info);
		
		playProgressbarBG = (TextView)findViewById(R.id.playProgressBG);
		playProgressbar = (TextView)findViewById(R.id.playProgress);
		recordingTime = (TextView)findViewById(R.id.recording_time);
		
		recordingBtnSet = findViewById(R.id.recording_btn_set);
		historyBtnSet = findViewById(R.id.history_btn_set);
		saveBtnSet = findViewById(R.id.save_btn_set);
		stopBtnSet = findViewById(R.id.stop_btn_set);

		Log.d("MainActivity", "language : " + language);
		if (language.equalsIgnoreCase("ko")||language.equalsIgnoreCase("korean")) {
			typeFace = Typeface.createFromAsset(getAssets(), "fonts/NanumGothicExtraBold.ttf");
		} else {
			typeFace = Typeface.createFromAsset(getAssets(), "fonts/ARIBLK.TTF");
		}
		Log.d("this name", this.getClass().getSimpleName());

		recordBtn.setTypeface(typeFace);
		settingBtn.setTypeface(typeFace);
		historyBtn.setTypeface(typeFace);
		introBtn.setTypeface(typeFace);
		pauseBtn.setTypeface(typeFace);
		stopBtn.setTypeface(typeFace);
		saveNBtn.setTypeface(typeFace);
		saveYBtn.setTypeface(typeFace);
		

		progressbar.setIndeterminate(true);
		progressbar.setKeepScreenOn(true);
		progressbar.setClickable(false);
		
		progressbarMain.setIndeterminate(true);
		progressbarMain.setKeepScreenOn(true);
		progressbarMain.setClickable(false);
		background = new MediaPlayer();
		
		recordBtn.setOnClickListener(
			new CustomBtnClickListener(context, recordBtn, recordingBtnSet, new CustomClickCallBack() {
				@Override
				public synchronized void callBack(Context context) {
					recordingTime.setText("");
					saveBtnSet.setVisibility(View.INVISIBLE);
					stopBtnSet.setVisibility(View.VISIBLE);
					//stopBtnSet.setVisibility(View.VISIBLE);
					new Thread() {
						public void run() {
							disableBtn();
							WaveRecordUtil.removeTmpFile();
							RECORD_STATE = RECORDING;
							Looper.prepare();
							checkTime = new CounterThread(recordingTime);
							checkTime.execute();
							Looper.loop();
						}
					}.start();
				}
			})
		);
		settingBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		historyBtn.setOnClickListener(
			new CustomBtnClickListener(context, historyBtn, historyBtnSet, historyBtn, ETC2, new CustomClickCallBack() {
				@Override
				public synchronized void callBack(Context context) {
					
					historyBtn.setText("");
				}
			}, new CustomClickCallBack() {
				@Override
				public synchronized void callBack(Context context) {
					if (HISTORY_STATE == HISTORY_CLOSE) {
						HISTORY_STATE = HISTORY_OPEN;
					} else {
						HISTORY_STATE = HISTORY_CLOSE;
					}
					
					try{
						recordFiles = getRecordFiles();
						Log.d("record file", recordFiles.size()+"");
						adapter = new RecordFileAdapter(MainActivity.this, recordFiles);
						ListView list = (ListView)findViewById(android.R.id.list);
						list.setAdapter(adapter);
						
						//Log.d("MainActivity", "playProgressbarBG.getWidth() : " + playProgressbarBG.getWidth() + ", playProgressbar.getWidth() : " + playProgressbar.getWidth());
					} catch (Exception e) {
						Log.e("MainActivity", e.getMessage(), e);
					}
				}
			})
		);
		introBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		pauseBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Log.d("MainActivity", "RECORD_STATE : " + RECORD_STATE);
				if ( RECORD_STATE == RECORDING) {
					disableBtn();
					checkTime.pause();
					pauseBtn.setText(R.string.resume_btn);
					RECORD_STATE = REC_PAUSE;
					
					
				} else if ( RECORD_STATE == REC_PAUSE ) {
					Log.d("MainActivity", "RECORD_STATE : PAUSE");
					RECORD_STATE = RECORDING;
					checkTime.resume();
					pauseBtn.setText(R.string.pause_btn);
				}
			}
		});
		
		
		stopBtn.setOnClickListener(
				
				new CustomBtnClickListener(context, stopBtnSet, saveBtnSet, stopBtn, ETC1, new CustomClickCallBack() {
					@Override
					public synchronized void callBack(Context context) {
						if ( RECORD_STATE != REC_STOP || (checkTime != null && (!checkTime.isCancelled()||!checkTime.getStatus().equals(Status.FINISHED))) ) {
							checkTime.cancel(true);
							
						}
						stopBtn.setText("");
						pauseBtn.setText(R.string.pause_btn);
						RECORD_STATE = REC_STOP;
					}
				}, null)
		);
		saveNBtn.setOnClickListener(
				
				new CustomBtnClickListener(context, recordingBtnSet, recordBtn, saveNBtn, false, new CustomClickCallBack() {
					
					@Override
					public synchronized void callBack(Context context) {
						if ( RECORD_STATE != REC_STOP || (checkTime != null && (!checkTime.isCancelled()||!checkTime.getStatus().equals(Status.FINISHED))) ) {
							checkTime.cancel(true);
							
						}
						recordingTime.setText("");
						stopBtn.setText(R.string.stop_btn);
						saveBtnSet.setVisibility(View.INVISIBLE);
						stopBtnSet.setVisibility(View.VISIBLE);
						WaveRecordUtil.removeTmpFile();
						MySimpleRecorder.tempRecordFileCnt = 0;
					}
				})
		);
		saveYBtn.setOnClickListener(

				new CustomBtnClickListener(context, recordingBtnSet, recordBtn, saveYBtn, false, new CustomClickCallBack() {

					@Override
					public synchronized void callBack(Context context) {
						if ( RECORD_STATE != REC_STOP || (checkTime != null && (!checkTime.isCancelled()||!checkTime.getStatus().equals(Status.FINISHED))) ) {
							checkTime.cancel(true);
							
						}
						recordingTime.setText("");
						stopBtn.setText(R.string.stop_btn);
						saveBtnSet.setVisibility(View.INVISIBLE);
						stopBtnSet.setVisibility(View.VISIBLE);
						
						Calendar oCalendar = Calendar.getInstance();
						String dateStr = oCalendar.get(Calendar.YEAR)+
								HMTrans.padLeftStr(2, (oCalendar.get(Calendar.MONTH) + 1),'0')+
								HMTrans.padLeftStr(2, oCalendar.get(Calendar.DAY_OF_MONTH),'0');
						
						progressbarMain.setVisibility(View.VISIBLE);
						WaveRecordUtil.combineWaveFile(dateStr);
						WaveRecordUtil.removeTmpFile();
						progressbarMain.setVisibility(View.INVISIBLE);
						MySimpleRecorder.tempRecordFileCnt = 0;
					}
				})
		);
		
		historyBackBtn.setOnClickListener(

				new CustomBtnClickListener(context, historyBtnSet, historyBtn, historyBackBtn, false, new CustomClickCallBack() {

					@Override
					public synchronized void callBack(Context context) {
						if (background.isPlaying()) {
							background.stop();
						}
						playProgressbarBG.setVisibility(View.INVISIBLE);
						playProgressbar.setVisibility(View.INVISIBLE);
						historyBtn.setText(R.string.his_btn);
						waveInfo.setText("");
						waveInfo.setVisibility(View.INVISIBLE);
						

					}
				})
		);
		

	}
	
	private class CustomBtnClickListener implements OnClickListener{
		private Context context;
		private View hideView;
		private View activeView;
		private Button clickBtn;
		private boolean isStarted = false;
		private Handler handler = new Handler();
		private CustomClickCallBack preExcute;
		private CustomClickCallBack postExcute;
		private final Animation hideAnim;
		private final Animation showAnim;

		public CustomBtnClickListener(Context context, View hideView, View activeView, CustomClickCallBack callback) {
			this(context, hideView, activeView, (Button) hideView, true, callback);
		}
		
		public CustomBtnClickListener(Context context, View hideView, View activeView, Button clickBtn, boolean isXFlip, CustomClickCallBack callback) {
			this.context = context;
			this.hideView = hideView;
			this.activeView = activeView;
			this.clickBtn = clickBtn;
			this.postExcute = callback;
			hideAnim = isXFlip ? AnimationUtils.loadAnimation(this.context, R.anim.flip_x_off) : AnimationUtils.loadAnimation(this.context, R.anim.flip_y_off);
			showAnim = isXFlip ? AnimationUtils.loadAnimation(this.context, R.anim.flip_x_on) : AnimationUtils.loadAnimation(this.context, R.anim.flip_y_on);
		}
		
		public CustomBtnClickListener(Context context, View hideView, View activeView, Button clickBtn, int aniType, CustomClickCallBack preExcute, CustomClickCallBack postExcute) {
			this.context = context;
			this.hideView = hideView;
			this.activeView = activeView;
			this.clickBtn = clickBtn;
			this.postExcute = postExcute;
			this.preExcute = preExcute;
			switch (aniType) {
			case XFLIP:
				hideAnim = AnimationUtils.loadAnimation(this.context, R.anim.flip_x_off);
				showAnim = AnimationUtils.loadAnimation(this.context, R.anim.flip_x_on);
				break;
			case YFLIP:
				hideAnim = AnimationUtils.loadAnimation(this.context, R.anim.flip_y_off);
				showAnim = AnimationUtils.loadAnimation(this.context, R.anim.flip_y_on);
				break;
			case ETC1:
				hideAnim = AnimationUtils.loadAnimation(this.context, R.anim.scale_x_increase);
				showAnim = AnimationUtils.loadAnimation(this.context, R.anim.fade_in);
				break;
			case ETC2:
				hideAnim = AnimationUtils.loadAnimation(this.context, R.anim.scale_y_increase);
				showAnim = AnimationUtils.loadAnimation(this.context, R.anim.fade_in);
				break;
			default:
				hideAnim = AnimationUtils.loadAnimation(this.context, R.anim.flip_x_off);
				showAnim = AnimationUtils.loadAnimation(this.context, R.anim.flip_x_on);
				break;
			}
		}

		
		protected void flipButton() {
			if (preExcute != null) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						try {
							preExcute.callBack(context);

						} catch (Exception e) {
						}

					}
				});
			}
			isStarted = true;
			handler.post(new Runnable() {
				@Override
				public void run() {
					try {
						hideView.startAnimation(hideAnim);
						
					} catch (Exception e) {
					}

				}
			});
			//handler.sendEmptyMessageDelayed(0, 300);
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					try {
						hideView.setAnimation(null);
						hideView.setVisibility(View.INVISIBLE);
						activeView.startAnimation(showAnim);
						
					} catch (Exception e) {
					}

				}
			},300);
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					try {
						activeView.setAnimation(null);
						activeView.setVisibility(View.VISIBLE);
						clickBtn.setClickable(true);
						isStarted = false;
						postExcute.callBack(context);
					} catch (Exception e) {
					}

				}
			},700);
		}

		@Override
		public void onClick(View v) {
			
			int getId = v.getId();
			if ( getId == R.id.history_btn) {
				Log.d("onclick", "RECORD_STATE : " + RECORD_STATE);
				if ( RECORD_STATE != REC_STOP || (checkTime != null && (!checkTime.isCancelled()||!checkTime.getStatus().equals(Status.FINISHED))) ) {
					return;
				}
				flipButton();
			} else {
				if (!isStarted) flipButton();
			}
			
		}
	}
	private class CounterThread extends AsyncTask<Void, Integer, String> {
		private String asyncResult = "";
		private TextView displayView; 
		private long finalInterval = 0;
		private long tmpInterval = 0;
		private boolean pause = false;
		private boolean resume = false;
		
		public CounterThread(TextView displayView) {
			this.displayView = displayView;
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				counter();

			} catch (Exception e) {
				e.printStackTrace();

			} finally {

			}
			return asyncResult;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		private void counter() {
			long clientRealtime = System.currentTimeMillis();
			
			boolean isStarted = false;
			while (true) {
				try {
					if (!pause) {

						if (!isStarted) {
							
							for(int i=0; i<COUNTDOWN.length; i++) {
								final int j = i;
								if ( this.isCancelled() ) break;
								MainActivity.this.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										//recordingTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
										displayView.setText(COUNTDOWN[j]);
									}
								});
								Thread.sleep(500);
								if ( this.isCancelled() ) break;
								MainActivity.this.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										displayView.setTextColor(0xFFDDDDDD);
									}
								});
								Thread.sleep(500);
								if ( this.isCancelled() ) break;
								MainActivity.this.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										displayView.setTextColor(0xFFA9A9A9);
									}
								});
							}
							if ( this.isCancelled() ) break;
							enableBtn();
							startService(new Intent(RECORD_SERVICE));
							isStarted = true;
							clientRealtime = System.currentTimeMillis();
						}

						
						
						
						
						finalInterval = System.currentTimeMillis()-clientRealtime;

						if (finalInterval > MAXIMUM_RECORDING_TIME - 50) {
							stopService(new Intent(RECORD_SERVICE));
							break;
						}
						MainActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								short sec = 0;
								short min = 0;
								short milsec = 0;
								
								sec = (short) (finalInterval/1000);
								min = (short) (sec/60);
								
								if (sec > 0) milsec = (short) (finalInterval - (sec*1000));
								else milsec = (short)finalInterval;
								if (min > 0) sec = (short) (sec - (min*60));
								
								displayView.setTextColor(0xFFFFFFFF);
								final String printTime = finalInterval < 50 ? "00:00:00" : HMTrans.padLeftStr(2, min, '0') + ":" + HMTrans.padLeftStr(2, sec, '0') + ":" + HMTrans.padLeftStr(3, milsec, '0');
								displayView.setText(printTime);
							}
						});
						Thread.sleep(40);
					}
					
					if ( this.isCancelled() ) {
						stopService(new Intent(RECORD_SERVICE));
						break;
					}
					if (resume) {
						clientRealtime = System.currentTimeMillis() - tmpInterval;
						resume = false;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					break;
				}
			}
			return;
		}
		
		protected void pause() {
			pause = true;
			resume = false;
			tmpInterval = finalInterval;
			stopService(new Intent(RECORD_SERVICE)); 
			
		}
		protected void resume() {
			resume = true;
			pause = false;
			startService(new Intent(RECORD_SERVICE));
			
		}
	}
	@Override
    protected void onDestroy() {
        unregisterReceiver(mHandleMessageReceiver);
        stopService(new Intent(RECORD_SERVICE)); 
        if (background != null) background.release();
        super.onDestroy();
    }
	
	private void enableBtn() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				pauseBtn.setClickable(true);
				stopBtn.setClickable(true);
				progressbar.setVisibility(View.GONE);
			}
		});

	}
	private void disableBtn() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				pauseBtn.setClickable(false);
				stopBtn.setClickable(false);
				progressbar.setVisibility(View.VISIBLE);
			}
		});

	}
	
	private final BroadcastReceiver mHandleMessageReceiver =
            new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
            if (newMessage.equals("STOP")) {
            	enableBtn();
            }
        }
    };
    
    private ArrayList<HashMap<String, String>> getRecordFiles() throws Exception{


		ArrayList<HashMap<String, String>> recordFiles = new ArrayList<HashMap<String, String>>();
		File rootPath = new File(Environment.getExternalStorageDirectory()+RECORDING_PATH+"/");
		if(!rootPath.isDirectory()) {
			return null;
		}
		ArrayList<String> dirList = new ArrayList<String>();
		File subDirs [] = rootPath.listFiles();
		Arrays.sort(subDirs, new ModifiedDate());
		for(File tmp : subDirs) {
			if (!tmp.isDirectory()) continue;
			if (!Pattern.matches("^[0-9]{8}$", tmp.getName())) continue;
			dirList.add(tmp.getName());
		}
		
		for(String dirName : dirList) {
			
			File subPath = new File(Environment.getExternalStorageDirectory()+RECORDING_PATH+"/"+dirName+"/");
			File tmpRecordFiles [] = subPath.listFiles();
			Arrays.sort(tmpRecordFiles, new ModifiedDate());
			
			for(File tmpFile : tmpRecordFiles) {
				if (!tmpFile.isFile()) continue;
				if (!Pattern.matches("^result[0-9]+\\.wav$", tmpFile.getName())) continue;
				HashMap<String, String> tmp = new HashMap<String, String>();
				tmp.put("dirnm", dirName);
				tmp.put("filenm", tmpFile.getName());
				recordFiles.add(tmp);
			}
		}
		return recordFiles;
	}
    
    
	private class RecordFileAdapter extends ArrayAdapter<HashMap<String, String>> {
		private Activity context;
    	private List<HashMap<String, String>> items; 
    	RecordFileAdapter(Activity context) {
			super(context, R.layout.file_list_element);
			this.context = context;
		}
		
    	RecordFileAdapter(Activity context, List<HashMap<String, String>> items) {
			super(context, R.layout.file_list_element, items);
			this.items = items;
			this.context = context;
		}
		
    	@Override
		public HashMap<String, String> getItem(int position) {
    		if (position >= recordFiles.size()) return null;
			return super.getItem(position);
		}
    	
    	public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = context.getLayoutInflater();
			final View row = inflater.inflate(R.layout.file_list_element, null);
			if (recordFiles == null || recordFiles.size() < 1) {
				return row;
			}
			final HashMap<String, String> rslt = getItem(position);
			if (rslt == null) return row;
			
			Log.d("MainActivity", "path : " + HMTrans.trim(rslt.get("dirnm")) + "/"+ HMTrans.trim(rslt.get("filenm")));
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					TextView dirName = (TextView) row.findViewById(R.id.directory_name);
					TextView fileName = (TextView) row.findViewById(R.id.filename);
					dirName.setText(HMTrans.trim(rslt.get("dirnm")));
					fileName.setText(HMTrans.trim(rslt.get("filenm")));
					
				}
			});

			return row;
		}
    }
	
	public void onListItemClick(ListView parents, View v, int position, long id) {
    	if (recordFiles == null || recordFiles.size() < 1) {
			return;
		}
    	HashMap<String, String> rslt = recordFiles.get(position);
    	String wavePath = Environment.getExternalStorageDirectory()+RECORDING_PATH+"/"+HMTrans.trim(rslt.get("dirnm"))+"/"+HMTrans.trim(rslt.get("filenm"));
    	File waveFile = new File(wavePath);
    	waveFile = setFileReadPermission(waveFile);
		float remainTime = 0;
		float filesize = waveFile.length();
		filesize = filesize - WaveRecordUtil.HEADER_SIZE;
		remainTime = filesize/(WaveRecordUtil.RECORDER_SAMPLERATE*(WaveRecordUtil.RECORDER_BPP/8));
		waveInfo.setText(HMTrans.trim((int)remainTime) + ", " + getString(R.string.playing));
		
		if (playTime == null || playTime.isCancelled() || playTime.getStatus().equals(Status.FINISHED)) {
			
		} else {
			playTime.cancel(true);
		}
		playTime = new ProgressBarThread((int)remainTime);
		if (background.isPlaying()) {
			background.stop();
		}
		if (beforeRecFile == -1 || beforeRecFile != id) {
			if (background != null) background.reset();
			
			try {
				background.setDataSource(wavePath);
				background.prepare();
				background.setLooping(false);
				background.setVolume((float)3.0, (float)3.0);
				playTime.execute();
				background.start();
				beforeRecFile = id;
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			beforeRecFile = -1;
		}
		background.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
			
			@Override
			public void onVideoSizeChanged(MediaPlayer mediaplayer, int i, int j) {
				// TODO Auto-generated method stub
				
			}
		});
		background.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer mediaplayer) {
				// TODO Auto-generated method stub
				
			}
		});
		background.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer mediaplayer, int i, int j) {
				// TODO Auto-generated method stub
				//mediaplayer.reset();
				playTime.cancel(true);
				return false;
			}
		});
		background.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mediaplayer) {
				// TODO Auto-generated method stub
				waveInfo.setText("");
				playTime.cancel(true);
			}
		});
    	playProgressbarBG.setVisibility(View.VISIBLE);
    	playProgressbar.setVisibility(View.VISIBLE);
    	
    	waveInfo.setVisibility(View.VISIBLE);
    }
    
    private class ModifiedDate implements Comparator<File> {

		@Override
		public int compare(File lhs, File rhs) {
			if (lhs.lastModified() > rhs.lastModified()) return -1;
			if (lhs.lastModified() == rhs.lastModified()) return 0;
			return 1;
		}
    }
    
    @Override
	public void onBackPressed(){
    	if (progressbarMain.getVisibility() == View.VISIBLE) progressbarMain.setVisibility(View.INVISIBLE);
    	WaveRecordUtil.removeTmpFile();
    	MySimpleRecorder.tempRecordFileCnt = 0;
    	if ( RECORD_STATE != REC_STOP || (checkTime != null && (!checkTime.isCancelled()||!checkTime.getStatus().equals(Status.FINISHED))) ) {
    		CustomBtnClickListener cbcl = new CustomBtnClickListener(context, recordingBtnSet, recordBtn, stopBtn, true, new CustomClickCallBack() {

    			@Override
    			public synchronized void callBack(Context context) {
    				checkTime.cancel(true);
    				pauseBtn.setText(R.string.pause_btn);
    				RECORD_STATE = REC_STOP;
    				recordingTime.setText("");
					saveBtnSet.setVisibility(View.INVISIBLE);
					stopBtnSet.setVisibility(View.VISIBLE);
    			}
    		});
    		cbcl.flipButton();

    	} else {
    		finish();
    	}
    }
    
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private File setFileReadPermission(File file) {
    	if (!file.canRead()) file.setReadable(true);
    	return file;
    }
    
    
	private class ProgressBarThread extends AsyncTask<Void, Integer, String> {
		private String asyncResult = "";
		private long finalInterval = 0;
		private int playSec = 0;
		private float animUnit = 0;
		private int bgWith = 0;
		
		public ProgressBarThread(int playSec) {
			this.playSec = playSec;
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				counter();

			} catch (Exception e) {
				e.printStackTrace();

			} finally {

			}
			return asyncResult;
		}

		@Override
		protected void onCancelled() {

			super.onCancelled();
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			bgWith = playProgressbarBG.getWidth();
			animUnit = (float)bgWith/(float)playSec;
			finalInterval = 0;
			MainActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					
					RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(1, 10);
					param.addRule(RelativeLayout.ALIGN_TOP, R.id.playProgressBG);
					param.addRule(RelativeLayout.ALIGN_LEFT, R.id.playProgressBG);
					playProgressbar.setLayoutParams(param);
					
				}
			});
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		private void counter() {
			long clientRealtime = System.currentTimeMillis();
			
			while (true) {
				try {
					finalInterval = System.currentTimeMillis()-clientRealtime;
					
					final int tmpDisplayWidth = (int)(animUnit*((float)finalInterval/(float)1000));
					
					MainActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							
							int displayWidth = tmpDisplayWidth;
							
							if (displayWidth > bgWith) {
								displayWidth = bgWith;
							}
							
							RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(displayWidth, 10);
							param.addRule(RelativeLayout.ALIGN_TOP, R.id.playProgressBG);
							param.addRule(RelativeLayout.ALIGN_LEFT, R.id.playProgressBG);
							playProgressbar.setLayoutParams(param);
						}
					});
					
					if (tmpDisplayWidth > bgWith) {
						Log.d("ProgressBarThread", "end!");
						break;
					}
					Thread.sleep(40);
					if (this.isCancelled()) {
						Log.d("ProgressBarThread", "break");
						break;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					break;
				}
			}
			return;
		}
	}
}
