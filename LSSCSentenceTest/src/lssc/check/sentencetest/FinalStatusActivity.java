package lssc.check.sentencetest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import common.util.HMTrans;

public class FinalStatusActivity extends Activity implements Constants{
	private Button homeBtn;
	private Button captureBtn;
	private GridView statusGrid;
	private View rootView;
	private int[] recordTime = null;
	private LSSCSentenceTestApplication app;
	private ProgressBar progressbar;
	private Handler handler = new Handler();
	private int DIVIDE_NO = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.final_status);
		app = (LSSCSentenceTestApplication)getApplication();
		homeBtn = (Button)findViewById(R.id.button_home);
		captureBtn = (Button)findViewById(R.id.button_capture);
		statusGrid = (GridView)findViewById(R.id.statistics);
		rootView = findViewById(R.id.final_statistics);
		statusGrid.setAdapter(new DataAdapter(this));
		progressbar = (ProgressBar)findViewById(R.id.progressBar1);
		progressbar.bringToFront();
		progressbar.setIndeterminate(true);
		progressbar.setKeepScreenOn(true);
		progressbar.setClickable(false);
		
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(progressbar!=null && progressbar.getVisibility()==View.VISIBLE) progressbar.setVisibility(View.GONE);
				stopService(new Intent(RECORD_SERVICE));
				WaveRecordUtil.combineWaveFile("result.wav", app.getTestee());	
				
			}
		}, 4000);
		
		Intent i = getIntent();
		recordTime = i.getIntArrayExtra("recordTime");
		DIVIDE_NO = app.getTypeIdx();
		statusGrid.setNumColumns(DIVIDE_NO);
		homeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FinalStatusActivity.this, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}
		});
		captureBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				screenCapture(rootView);
				Toast.makeText(FinalStatusActivity.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	
	@Override
    public void onBackPressed(){
		Intent i = new Intent(FinalStatusActivity.this, MainActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
    }
	
	private void screenCapture(View view) {
		view.setDrawingCacheEnabled(true);
		Bitmap screenShot = view.getDrawingCache();
		
		Calendar oCalendar = Calendar.getInstance();
		try{
			LSSCSentenceTestApplication app = (LSSCSentenceTestApplication)getApplication();
			String name = app.getTestee();
			File path = new File(Environment.getExternalStorageDirectory()+"/lssc_test/"+name);
			if(!path.isDirectory()) {
				path.mkdirs();
			}
			String tmp = Environment.getExternalStorageDirectory()+"/lssc_test/"+name+"/";
			tmp+= oCalendar.get(Calendar.YEAR)+
					HMTrans.padLeftStr(2, (oCalendar.get(Calendar.MONTH) + 1),'0')+
					HMTrans.padLeftStr(2, oCalendar.get(Calendar.DAY_OF_MONTH),'0')+
					HMTrans.padLeftStr(2, oCalendar.get(Calendar.HOUR_OF_DAY),'0')+
					HMTrans.padLeftStr(2, oCalendar.get(Calendar.MINUTE),'0')+
					HMTrans.padLeftStr(2, oCalendar.get(Calendar.SECOND),'0')+
					"_result.jpg";
			FileOutputStream out = new FileOutputStream(tmp);
			screenShot.compress(Bitmap.CompressFormat.JPEG, 100, out);
			
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			view.setDrawingCacheEnabled(false);
		}
		
	}
	public class DataAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Context curContext;
		public DataAdapter(Context context) {
			curContext = context;
			mInflater = LayoutInflater.from(curContext);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return recordTime==null ? 0 : recordTime.length;
		}
		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v;
			if (convertView == null) {
				v = mInflater.inflate(R.layout.statistics, null);
			} else {
				v = convertView;
			}
			
			TextView textView = (TextView)v.findViewById(R.id.statistics_item);
			String data = "";
			if (position < DIVIDE_NO){
				textView.setBackgroundColor(Color.rgb(213, 213, 213));
				data = recordTime[position] + "회";  
			} else if (position > (DIVIDE_NO*11)-1){
				textView.setBackgroundColor(Color.rgb(255, 218, 170));
				data = "평균\n" + HMTrans.trim(String.valueOf((float)(recordTime[position])/1000.0f), "0");  
			} else {
				data = HMTrans.trim(String.valueOf((float)(recordTime[position])/1000.0f), "0");
			}
			textView.setText(data);
			return v;
		}
	}
}
