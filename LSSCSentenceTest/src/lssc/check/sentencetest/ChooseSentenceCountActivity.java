package lssc.check.sentencetest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

public class ChooseSentenceCountActivity extends Activity implements Constants{
	private Button sentences2Btn;
	private Button sentences3Btn;
	private Button sentences4Btn;
	private Button recordBtn;
	private Button recPauseBtn;
	private Button recStopBtn;
	private Handler handler = new Handler();
	private ProgressDialog progressDialog;
	private int RECORD_STATE = 0;
	private LSSCSentenceTestApplication app;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_sentences);
		app = (LSSCSentenceTestApplication)getApplication();
		sentences2Btn = (Button)findViewById(R.id.button_2sentences);
		sentences3Btn = (Button)findViewById(R.id.button_3sentences);
		sentences4Btn = (Button)findViewById(R.id.button_4sentences);
		recordBtn = (Button)findViewById(R.id.button_record);
		recPauseBtn = (Button)findViewById(R.id.button_record_pause);
		recStopBtn = (Button)findViewById(R.id.button_record_stop);

		progressDialog = new ProgressDialog(ChooseSentenceCountActivity.this);
		progressDialog.setTitle("");
		progressDialog.setIndeterminate(true);
		progressDialog.setCanceledOnTouchOutside(false);
		
		RECORD_STATE = app.getRecordState();
		
		sentences2Btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ChooseSentenceCountActivity.this, MakeSentenceActivity.class);
				app.setQuestSize(2);
				startActivity(intent);
			}
		});
		
		sentences3Btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ChooseSentenceCountActivity.this, MakeSentenceActivity.class);
				app.setQuestSize(3);
				startActivity(intent);
			}
		});
		
		sentences4Btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ChooseSentenceCountActivity.this, MakeSentenceActivity.class);
				app.setQuestSize(4);
				startActivity(intent);
			}
		});
		if ( !app.getIs2Check() ) sentences2Btn.setVisibility(View.GONE);
		if ( !app.getIs3Check() ) sentences3Btn.setVisibility(View.GONE);
		if ( !app.getIs4Check() ) sentences4Btn.setVisibility(View.GONE);
		
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
}
