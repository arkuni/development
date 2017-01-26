package arkuni.study.httpurlconnectiontest;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import arkuni.http.urlconnection.MakeUrlConnection;

public class MainActivity extends Activity {
	private TextView tv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv = (TextView)findViewById(R.id.app_name_str);
		MakeUrlConnection urlConnection = new MakeUrlConnection();
		//JSONObject result = urlConnection.get("https://tfinder.happymoney.co.kr/app/store_position_count.hm", JSONObject.class);
		String result = urlConnection.get("https://tfinder.happymoney.co.kr", String.class);
/*
		try {
			System.out.println(result.get("message"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		System.out.println(result);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
