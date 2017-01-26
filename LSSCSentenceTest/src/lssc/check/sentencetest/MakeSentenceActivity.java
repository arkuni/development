package lssc.check.sentencetest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import common.util.HMTrans;

public class MakeSentenceActivity extends Activity implements Constants{
	private Button nextBtn;
	private Button recordBtn;
	private Button recPauseBtn;
	private Button recStopBtn;
	private TextView remainTimeTxt;
	private TextView exampleSentence;
	private TextView makeSentence;
	private String questType = TEST_EXAMPLE;
	private int size = 0;
	private String[] examples = null;
	private String[] quests = null;
	private int[] recordTime = null;
	private int exampleIdx = 0;
	private int questIdx = 0;

	private String tagName = "MakeSentenceActivity";
	private Builder alert = null;
	private int recordSum = 0;
	private Handler handler = new Handler();
	private ProgressDialog progressDialog;
	private AsyncCheckTime checkTime;
	private boolean isEnd = false;
	private boolean isMaxCntType = false;
	private LSSCSentenceTestApplication app;
	
	private int SKIP_COL_CNT = 6; //상위 제목을 위한 배열공간 스킵
	private int TYPE_CNT = 6; // 문제 종류 갯수
	private int TYPE_IDX = 0; // 문제 종류 순서
	
	private int RECORD_STATE = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int trueCnt = 0;
		app = (LSSCSentenceTestApplication)getApplication();
		questType = app.getQuestType();
		size = app.getQuestSize();
		trueCnt = (app.getIs2Check()?1:0)+(app.getIs3Check()?1:0)+(app.getIs4Check()?1:0);
		TYPE_CNT = trueCnt*2;
		SKIP_COL_CNT = trueCnt*2;
		RECORD_STATE = app.getRecordState();
		TYPE_IDX = app.getTypeIdx();
		
		app.setTypeIdx(TYPE_IDX+1);
		if (app.getRecordTime() != null && app.getRecordTime().length > 0) recordTime = app.getRecordTime();
		else {
			recordTime = new int[trueCnt*24];
			for (int i=0; i<trueCnt*2; i++) {
				recordTime[i] = i+1;
			}
		}

		alert = new AlertDialog.Builder(this);
		progressDialog = new ProgressDialog(MakeSentenceActivity.this);
		progressDialog.setTitle("");
		progressDialog.setIndeterminate(true);
		progressDialog.setCanceledOnTouchOutside(false);

		
		if (!questType.equals(TEST_DIRECT)) {
			showExampleSetting();
		} else {
			makeSentenceSetting();
		}

		testStart();
	}

	
	private void setRecBtnUI() {
		if(RECORD_STATE==RECORDING) {
			recordBtn.setVisibility(View.GONE);
			recPauseBtn.setVisibility(View.VISIBLE);
			recStopBtn.setVisibility(View.VISIBLE);
			recPauseBtn.setText(R.string.button_record_pause);
		} else if(RECORD_STATE == REC_PAUSE) {
			recordBtn.setVisibility(View.GONE);
			recPauseBtn.setVisibility(View.VISIBLE);
			recStopBtn.setVisibility(View.VISIBLE);
			recPauseBtn.setText(R.string.button_record_restart);
		} else if(RECORD_STATE == REC_STOP) {
			recordBtn.setVisibility(View.VISIBLE);
			recPauseBtn.setVisibility(View.GONE);
			recStopBtn.setVisibility(View.GONE);
			recPauseBtn.setText(R.string.button_record_pause);
		}
	}
	
	@Override
    public void onBackPressed(){
		new AlertDialog.Builder(this).setMessage( "녹음을 종료하고 처음부터 다시 시작하시겠습니까?" ).setPositiveButton("확인", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
				WaveRecordUtil.removeFile(app.getTestee());
				Intent i = new Intent(MakeSentenceActivity.this, MainActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}
		}).setNegativeButton("취소", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
				return;
			}
		}).show();
    }
	
	private void showExampleSetting() {
		switch (size) {
		case 2:
			examples = getResources().getStringArray(R.array.example_exist_2sentences);
			quests = getResources().getStringArray(R.array.test_exist_2sentences);
			if (!app.getIs3Check() && !app.getIs4Check()) isMaxCntType = true;
			break;
		case 3:
			examples = getResources().getStringArray(R.array.example_exist_3sentences);
			quests = getResources().getStringArray(R.array.test_exist_3sentences);
			if (!app.getIs4Check()) isMaxCntType = true;
			break;
		case 4:
			examples = getResources().getStringArray(R.array.example_exist_4sentences);
			quests = getResources().getStringArray(R.array.test_exist_4sentences);
			isMaxCntType = true;
			break;
		default:
			break;
		}
	}
	
	private void makeSentenceSetting() {
		switch (size) {
		case 2:
			quests = getResources().getStringArray(R.array.test_none_2sentences);
			if (!app.getIs3Check() && !app.getIs4Check()) isMaxCntType = true;
			break;
		case 3:
			quests = getResources().getStringArray(R.array.test_none_3sentences);
			if (!app.getIs4Check()) isMaxCntType = true;
			break;
		case 4:
			quests = getResources().getStringArray(R.array.test_none_4sentences);
			isMaxCntType = true;
			break;

		default:
			break;
		}
	}
	
	private void commonButtonSetting() {

		recordBtn = (Button)findViewById(R.id.button_record);
		recPauseBtn = (Button)findViewById(R.id.button_record_pause);
		recStopBtn = (Button)findViewById(R.id.button_record_stop);
		
		recordBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RECORD_STATE = RECORDING;
				startService(new Intent(RECORD_SERVICE));
				recordBtn.setVisibility(View.GONE);
				recPauseBtn.setVisibility(View.VISIBLE);
				recStopBtn.setVisibility(View.VISIBLE);
				app.setRecordState(RECORDING);
			}
		});
		
		recPauseBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(RECORD_STATE == RECORDING) {
					RECORD_STATE = REC_PAUSE;
					recPauseBtn.setText(R.string.button_record_restart);
					progressDialog.setMessage("잠시만 기다려주세요.");
					progressDialog.show();
					
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							stopService(new Intent(RECORD_SERVICE));
							progressDialog.dismiss();
						}
					}, 1500);
					app.setRecordState(REC_PAUSE);
				} else if(RECORD_STATE == REC_PAUSE) {
					RECORD_STATE = RECORDING;
					recPauseBtn.setText(R.string.button_record_pause);
					startService(new Intent(RECORD_SERVICE));
					app.setRecordState(RECORDING);
				}
				
			}
		});
		
		recStopBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				stopService(new Intent(RECORD_SERVICE));
				RECORD_STATE = REC_STOP;
				recPauseBtn.setText(R.string.button_record_pause);
				recordBtn.setVisibility(View.VISIBLE);
				recPauseBtn.setVisibility(View.GONE);
				recStopBtn.setVisibility(View.GONE);
				WaveRecordUtil.removeFile(app.getTestee());
				app.setRecordState(REC_STOP);
			}
		});
		
		setRecBtnUI();
	}
	
	private void testStart() {
		if (questIdx > 9) {
			isEnd = true;
			int sumIdx = (TYPE_CNT*questIdx)+TYPE_IDX+SKIP_COL_CNT;
			Log.i(tagName, "sum : " +recordSum);
			
			recordTime[sumIdx] = (int)(Math.round((float)recordSum/10.0));
			if (TYPE_CNT-1 == TYPE_IDX) {
				alert.setMessage( "테스트가 끝났습니다.\n 수고하셨습니다." )
					 .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							Intent i = new Intent(MakeSentenceActivity.this, FinalStatusActivity.class);
							i.putExtra("recordTime", recordTime);
							startActivity(i);
							return;
						}
					 }).show();
				return;
			} else if (isMaxCntType) {
				app.setRecordTime(recordTime);
				alert.setMessage( "해당 테스트가 끝났습니다.\n 다음 단계를 선택해주세요.")
					 .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							Intent i = new Intent(MakeSentenceActivity.this, ChooseSentenceTypeActivity.class);
							i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(i);
							finish();
							return;
						}
					 }).show();
				return;
			} else {
				app.setRecordTime(recordTime);
				alert.setMessage( "해당 테스트가 끝났습니다.\n 다음 단계를 선택해주세요." )
					 .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							Intent i = new Intent(MakeSentenceActivity.this, ChooseSentenceCountActivity.class);
							i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(i);
							finish();
							return;
						}
					 }).show();
				return;
			}
		} else {
			if (!questType.equals(TEST_DIRECT)) showExample();
			else showQuest();
			commonButtonSetting();
		}
	}
	
	private void showExample() {
		if (exampleIdx > 9) return;
		setContentView(R.layout.show_example);
		exampleSentence = (TextView)findViewById(R.id.example_sentence);
		remainTimeTxt = (TextView)findViewById(R.id.remain_time);
		exampleSentence.setText(examples[exampleIdx++].replaceAll("\\|", "\n"));
		nextBtn = (Button)findViewById(R.id.button_next);
		checkTime = new AsyncCheckTime();
		checkTime.execute();
		nextBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				checkTime.getStatus();
				if ( checkTime != null && (!checkTime.isCancelled()||!Status.FINISHED.equals(Status.FINISHED)) ) checkTime.cancel(true);
				showQuest();
				commonButtonSetting();
			}
		});
	}
	
	private void showQuest() {
		if (questIdx > 9) return;
		
		setContentView(R.layout.make_sentense);
		makeSentence = (TextView)findViewById(R.id.make_sentence);
		
		final long curtime = System.currentTimeMillis();
		final int recordIdx = (TYPE_CNT*questIdx)+TYPE_IDX+SKIP_COL_CNT;
		Log.i(tagName, "recordIdx : " +recordIdx);
		makeSentence.setText(quests[questIdx++].replaceAll("\\|", "\n"));
		nextBtn = (Button)findViewById(R.id.button_next);
		nextBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isEnd) { 
					int record = (int)(System.currentTimeMillis()-curtime);
					recordSum += record;
					Log.i(tagName, "item : " +record + ", sum : " +recordSum);
					recordTime[recordIdx] = record;
				}
				testStart();
			}
		});
	}
	
	public class AsyncCheckTime extends AsyncTask<Void, Integer, String> {
		private String asyncResult = "";

		@Override
		protected String doInBackground(Void... params) {
			try {
					unLockTimer();

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

		private void unLockTimer() {
			long clientRealtime = System.currentTimeMillis();
			//double defaultTime = 5000;
			//double remainTime = defaultTime;
			int nextinterval = 0;
			int sec = 5;
			while (sec > 0) {
				double interval = System.currentTimeMillis()-clientRealtime;
				//remainTime = defaultTime - interval;
				nextinterval = (int)(1000*(5-sec)-interval);
				
				String tmpPrintTime = "";
				tmpPrintTime = HMTrans.trim(sec--) + "초";
				
				final String printTime = tmpPrintTime;
				handler.post(new Runnable() {
					@Override
					public void run() {
						remainTimeTxt.setText(HMTrans.trim(printTime));
					}
				});
				try {
					if ( !this.isCancelled() ) Thread.sleep(1000+nextinterval);
				} catch (InterruptedException e) {
					break;
				}
			}
			if (!this.isCancelled()) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						showQuest();
						commonButtonSetting();
					}
				});
			}
			return;
		}
	}
}
