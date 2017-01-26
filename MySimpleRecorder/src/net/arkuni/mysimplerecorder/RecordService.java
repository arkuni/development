package net.arkuni.mysimplerecorder;

import static net.arkuni.mysimplerecorder.WaveRecordUtil.displayMessage;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;


public class RecordService extends Service implements Constants{
	private Recorder recorder;
	private Thread recTh;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		recorder.stopRecording();
		Context context = RecordService.this;
		displayMessage(context, "STOP");
		if(recTh.isAlive() || recTh!=null) recTh.interrupt();
		recTh = null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		recorder = new Recorder(Environment.getExternalStorageDirectory()+RECORDING_PATH+TMP_PATH+"/", MySimpleRecorder.tempRecordFileCnt++);
		
		Log.d("RecordService", "TempRecordFileCnt : " + MySimpleRecorder.tempRecordFileCnt);
		 
		Log.d("Service", "≥Ï¿Ω Ω√¿€");
		recTh = new Thread(recorder);
		recTh.start();
	}
}
