package lssc.check.sentencetest;



import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;


public class RecordService extends Service implements Constants{
	private Recorder recorder;
	private Thread recTh;
	private static int tmpFileCnt = 0;

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
		if(recTh.isAlive() || recTh!=null) recTh.interrupt();
		recTh = null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		LSSCSentenceTestApplication app = (LSSCSentenceTestApplication)getApplication();
		String name = app.getTestee();
		/*
		int recordFileIdx = app.getRecordFileIdx();
		String tmp = recordFileIdx + ".wav";
		recorder = new Recorder(Environment.getExternalStorageDirectory()+"/lssc_test/"+name+"/", tmp);
		*/
		recorder = new Recorder(Environment.getExternalStorageDirectory()+"/lssc_test/"+name+"/", tmpFileCnt);
		
		Log.d("RecordService", "TempRecordFileCnt : " + tmpFileCnt++);
		app.setTempRecordFileCnt(tmpFileCnt);
		Log.d("Service", "≥Ï¿Ω Ω√¿€");
		recTh = new Thread(recorder);
		recTh.start();
	}
}
