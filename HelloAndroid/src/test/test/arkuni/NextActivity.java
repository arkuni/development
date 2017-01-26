package test.test.arkuni;

import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.TextView;

import common.util.HMTrans;

public class NextActivity extends Activity {
	private HttpContext localContext = new BasicHttpContext();
	private CookieManager cookieManager;
	private TextView cookieTxt1;
	private TextView cookieTxt2;
	private TextView cookieTxt3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.next);
		Button cookieCheckBtn = (Button)findViewById(R.id.cookie_check_btn);
		Button goHappymoneyBtn = (Button)findViewById(R.id.go_site);
		cookieTxt1 = (TextView)findViewById(R.id.cookie_str1);
		cookieTxt2 = (TextView)findViewById(R.id.cookie_str2);
		cookieTxt3 = (TextView)findViewById(R.id.cookie_str3);
		
		Intent intent = getIntent();
		cookieTxt1.setText("original : " + HMTrans.trim(intent.getStringExtra("cookieStr")));
		cookieCheckBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showCookieStr();
			}
		});
		goHappymoneyBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(NextActivity.this, WebViewActivity.class);
				startActivity(intent);
			}
		});
	}
	
	private void showCookieStr() {
		CookieStore cookieStore = (CookieStore) localContext.getAttribute(ClientContext.COOKIE_STORE);
		List<Cookie> cookies = cookieStore == null ? null : cookieStore.getCookies();
		String cookieRawStr = "";
		if (cookies != null && cookies.size() > 0) {
			for (int i = 0; i < cookies.size(); i++) {
				String cookieString = cookies.get(i).getName() + "="+ cookies.get(i).getValue()+"; domain="+cookies.get(i).getDomain();
				cookieRawStr += cookieString+";";
			}
			cookieRawStr = cookieRawStr.substring(0,cookieRawStr.length()-1);
		}
		cookieTxt2.setText("cookie store : " + cookieRawStr);
		
		CookieSyncManager.createInstance(NextActivity.this);
		CookieSyncManager.getInstance().sync();
		cookieManager = CookieManager.getInstance();
		
		String cookieStr = cookieManager.getCookie("m.happymoney.co.kr");
		cookieTxt3.setText("cookie manager : " + cookieStr);
	}

}
