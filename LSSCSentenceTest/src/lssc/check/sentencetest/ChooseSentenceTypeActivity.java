package lssc.check.sentencetest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

public class ChooseSentenceTypeActivity extends Activity implements Constants{
	private Button exampleTestBtn;
	private Button directTestBtn;
	private Button recordBtn;
	private Button recPauseBtn;
	private Button recStopBtn;
	private int RECORD_STATE = 0;
	private Handler handler = new Handler();
	private ProgressDialog progressDialog;
	private LSSCSentenceTestApplication app;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_sentence_type);
		app = (LSSCSentenceTestApplication)getApplication();
		exampleTestBtn = (Button)findViewById(R.id.button_exist_example);
		directTestBtn = (Button)findViewById(R.id.button_none_example);
		recordBtn = (Button)findViewById(R.id.button_record);
		recPauseBtn = (Button)findViewById(R.id.button_record_pause);
		recStopBtn = (Button)findViewById(R.id.button_record_stop);
		
		progressDialog = new ProgressDialog(ChooseSentenceTypeActivity.this);
		progressDialog.setTitle("");
		progressDialog.setIndeterminate(true);
		progressDialog.setCanceledOnTouchOutside(false);
		
		RECORD_STATE = app.getRecordState();
		
		exampleTestBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!nextStepCheck()) return;
				Intent intent = new Intent(ChooseSentenceTypeActivity.this, ChooseSentenceCountActivity.class);
				app.setQuestType(TEST_EXAMPLE);
				startActivity(intent);
			}
		});
		
		directTestBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!nextStepCheck()) return;
				Intent intent = new Intent(ChooseSentenceTypeActivity.this, ChooseSentenceCountActivity.class);
				app.setQuestType(TEST_DIRECT);
				startActivity(intent);
			}
		});
		
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
	
	private boolean nextStepCheck() {
		if (RECORD_STATE != RECORDING) {
			new AlertDialog.Builder(ChooseSentenceTypeActivity.this).setMessage( "녹음버튼을 먼저 눌러주세요." ).setPositiveButton("확인", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int whichButton){
					return;
				}
			}).show();
			return false;
		}
		return true;
	}
	
	@Override
    public void onBackPressed(){
		new AlertDialog.Builder(this).setMessage( "처음부터 다시 시작하시겠습니까?" ).setPositiveButton("확인", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
				WaveRecordUtil.removeFile(app.getTestee());
				Intent i = new Intent(ChooseSentenceTypeActivity.this, MainActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}
		}).setNegativeButton("취소", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
				return;
			}
		}).show();
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		//stopService(new Intent(RECORD_SERVICE));
		RECORD_STATE = app.getRecordState();
		setRecBtnUI();
	}
}
