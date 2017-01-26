package lssc.check.sentencetest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import common.util.HMTrans;

public class MainActivity extends Activity implements Constants{

	private TextView nameTxt;
	private Button nextBtn;
	private CheckBox sentence2Check;
	private CheckBox sentence3Check;
	private CheckBox sentence4Check;
	private LSSCSentenceTestApplication app;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		nameTxt = (TextView)findViewById(R.id.name);
		nextBtn = (Button)findViewById(R.id.button_next);
		app = (LSSCSentenceTestApplication)getApplication();
		sentence2Check = (CheckBox)findViewById(R.id.sentence2check);
		sentence3Check = (CheckBox)findViewById(R.id.sentence3check);
		sentence4Check = (CheckBox)findViewById(R.id.sentence4check);
		
		nextBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = HMTrans.trim(nameTxt.getText());
				if (name.equals("")) {
					new AlertDialog.Builder(MainActivity.this).setMessage( "이름을 입력해주세요." ).setPositiveButton("확인", new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int whichButton){
							return;
						}
					}).show();
					return;
				}
				app.setIs2Check(sentence2Check.isChecked());
				app.setIs3Check(sentence3Check.isChecked());
				app.setIs4Check(sentence4Check.isChecked());
				
				app.setTestee(name);
				Intent intent = new Intent(MainActivity.this, ChooseSentenceTypeActivity.class);
				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		stopService(new Intent(RECORD_SERVICE));
		app = (LSSCSentenceTestApplication)getApplication();
		app.setTestee(null);
		app.setRecordTime(null);
		app.setRecordState(REC_STOP);
		app.setTempRecordFileCnt(0);
		app.setQuestType(TEST_DIRECT);
		app.setQuestSize(2);
		app.setIs2Check(false);
		app.setIs3Check(false);
		app.setIs4Check(false);
		app.setTypeIdx(0);
	}
}
