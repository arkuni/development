package arkuni.test.gcmtestproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import common.util.HMTrans;

public class SendGCMActivity extends Activity {

	private Button sendBtn;
	private EditText pushBody;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sender_main);
		String name = "¹ÚÀ±±â";
		String gcmServerKey = "AIzaSyAeXisp27FVL5DmDEe9vkXlN3GmYTkeFlw";
		final String deviceIds [] = { 
				"APA91bGxyKPgO_9usz7XkPcYTawdFT5e5JV_gu9qP7zNFuoVrHCCrcd57GmUdNZQLKmyRpEfpeRNrfrI1rL8aOjFsCzQ6cqu8XXVpDLPDK4St5BCclZxM48X9hNWxV6Cr4tcX9ZRKSJ16vOnbubJIDUQSp1647USUQ",
				"APA91bGqpkLpc2NEcoyBQtmfqqKsbmujSWmnKOkhQH69e6_jzoSimU95E5W5rNC-0WtKt4119zAY1mZJtUsiGv3Fxax0Ybt23ZCpGuy7ib00zwzpkdaB1IVmLBEamFCKCfbhFkmoD1auEbaUiNsIG4NEaO4FcdyljA",
				"APA91bGBII1FtCSxZ9DSjgX2waHHSnnjg7SiHkiL8GS1V8r62vo7XUPizgIf5gBNrrdkrqHE6OJAo7RaHl3Y3586BENWHuhNtu_7t5hMuKEbpDpDBPnSbEUZXs6KFIkHij7Pt_O4yRUEYkOL8LI4uXQKkVkmbzgoLg"
				};
		final Sender sender = new Sender(gcmServerKey);
		
		sendBtn = (Button)findViewById(R.id.button1);
		pushBody = (EditText)findViewById(R.id.editText1);
		sendBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String resultStr = "";
				try {
					
					String contents = HMTrans.trim(pushBody.getText());
					if (contents.equals("")) return;
					Message.Builder message = new Message.Builder();
					message.delayWhileIdle(false);
					message.timeToLive(1800);
					message.addData("msg", contents).build();
					for (int i=0; i<deviceIds.length; i++) {
						Result result = sender.send(message.build(), deviceIds[i], 5);
						if (result.getMessageId() != null) {
							resultStr = "send ok!";
						} else {
							String error = result.getErrorCodeName();
							resultStr = error;
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				Log.d("SendGCMActivity", resultStr);
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
		finish();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}

}
