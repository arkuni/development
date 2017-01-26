package arkuni.test.gcmtestproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import common.util.HMTrans;

public class MainActivity extends Activity {
	public static boolean isMainRun = true;
	private Toast mToast;
	private Context curContext = null;
	private Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		curContext = MainActivity.this;
		
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		final String regId = HMTrans.trim(GCMRegistrar.getRegistrationId(this));
		
		if(regId.equals(""))
		      GCMRegistrar.register(this, GCMIntentService.PROJECT_ID);
		else
		      Log.d("==============", regId);
		
		button = (Button)findViewById(R.id.send_btn);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(curContext, SendGCMActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		isMainRun = false;
		finish();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isMainRun = true;
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		isMainRun = false;
	}

}
