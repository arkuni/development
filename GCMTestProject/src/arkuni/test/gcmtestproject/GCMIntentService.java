package arkuni.test.gcmtestproject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Iterator;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {
	private static final String tag = "GCMIntentService";
	public static final String PROJECT_ID = "542556573882";
	public static final String INTENT_ACTION = "intent.action.arkuni.test.service";
	
	public GCMIntentService(){ this(PROJECT_ID); }
	
	public GCMIntentService(String project_id) { super(project_id); }

	

	@Override
    protected void onError(Context context, String errorId) {
        Log.d(tag, "onError. errorId : "+errorId);
    }

	@Override
    protected void onMessage(Context context, Intent intent) {
        Bundle b = intent.getExtras();
        String value = "";
        String key = "";
        Iterator<String> iterator = b.keySet().iterator();
        while(iterator.hasNext()) {
            key = iterator.next();
            try {
            	value = URLDecoder.decode(b.get(key).toString(), "UTF-8");
    		} catch (UnsupportedEncodingException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
            Log.d(tag, value);
        }

        showMessage(context, intent);
    }

	@Override
    protected void onRegistered(Context context, String regId) {
        Log.d(tag, "onRegistered. regId : "+regId);
        /*서버에 전송*/
    }

	@Override
    protected void onUnregistered(Context context, String regId) {
        Log.d(tag, "onUnregistered. regId : "+regId);
    }
	
	private void showMessage(Context context, Intent intent){
		String title = intent.getStringExtra("title");
		String msg = intent.getStringExtra("msg");
		String ticker = intent.getStringExtra("ticker");

		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Activity.NOTIFICATION_SERVICE);

		Intent tmp = new Intent(context, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("msg", msg);
		// 해당 어플을 실행하는 이벤트를 하고싶을 때 아래 주석을 풀어주세요
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, tmp , PendingIntent.FLAG_UPDATE_CURRENT);
		
		//PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);

		Notification notification = new Notification();
		notification.icon = R.drawable.ic_launcher;
		notification.tickerText = ticker;
		notification.when = System.currentTimeMillis();
		notification.vibrate = new long[] { 500, 100, 500, 100 };
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(context, title, msg, pendingIntent);

		notificationManager.notify(0, notification);
		Intent tmp2 = new Intent(context, PopUpActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("msg", msg);
		startActivity(tmp2);
		}
}
