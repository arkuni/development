package test.test.arkuni;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.cookie.CookieSpecBase;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import arkuni.http.urlconnection.MakeUrlConnection;

import common.util.CommonUtil;
import common.util.CryptUtil;
import common.util.HMTrans;
import common.util.XmlParserUtil;

public class HelloAndroidActivity extends Activity {
    /** Called when the activity is first created. */
	private boolean isEndProcess = false;
	private boolean isStartProcess = false;

	private HttpContext localContext = new BasicHttpContext();
	private CookieManager cookieManager;
	
	
	private ProgressDialog progressDialog;
	private EditText checkSessionRst;
	private EditText captchaInfo;
	private EditText idText;
	private EditText pwText;
	private ImageView captchaImg;
	private static String cookieRawStr = "";
	private MakeUrlConnection urlConnection = null;
	private final String CRYPTO_SEED_PASSWORD = "HAPPYMONEY!23#";
	
	private Handler handler = new Handler();
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button sessionCheckBtn = (Button)findViewById(R.id.check_session_btn);
        Button captchaImgBtn = (Button)findViewById(R.id.get_captcha_img_btn);
        Button loginBtn = (Button)findViewById(R.id.login_btn);
        Button nextBtn = (Button)findViewById(R.id.show_cookie_btn);
        Button customNice = (Button)findViewById(R.id.nice_custom_btn);
        CookieSyncManager.createInstance(this);
        idText = (EditText)findViewById(R.id.user_id);
        pwText = (EditText)findViewById(R.id.user_pw);
        checkSessionRst = (EditText)findViewById(R.id.check_session_result);
        captchaInfo = (EditText)findViewById(R.id.captcha_info);
        captchaImg = (ImageView)findViewById(R.id.captchaImg);
        urlConnection = new MakeUrlConnection();
		SharedPreferences pref = getSharedPreferences("happymoney_info", MODE_PRIVATE);
		String idTxt = pref.getString("userid", "");
		String pwCryptTxt = pref.getString("userpw", "");
		String pwTxt = "";
		
        try {
        	pwTxt = CryptUtil.aesDecrypt(CRYPTO_SEED_PASSWORD, pwCryptTxt);
        	idText.setText(idTxt);
        	pwText.setText(pwTxt);
        } catch (Exception e) {

        }

        nextBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HelloAndroidActivity.this, NextActivity.class);
				intent.putExtra("cookieStr", cookieRawStr);
				startActivity(intent);
			}
		});
        customNice.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HelloAndroidActivity.this, CustomNiceActivity.class);
				startActivity(intent);
			}
		});
        sessionCheckBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				progressDialog = ProgressDialog.show(HelloAndroidActivity.this,"", "로드중입니다.", true);
				String t = urlConnection.post("https://m.happymoney.co.kr/app/check_session.hm","",String.class);
				Log.d("test",t);
				//sessionCheck();
			}
		});
        captchaImgBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				progressDialog = ProgressDialog.show(HelloAndroidActivity.this,"", "로드중입니다.", true);
				getCaptchaImgProcess();
			}
		});
        loginBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (HMTrans.trim(captchaInfo.getText()).equals("")) {
					if (progressDialog != null ) progressDialog.dismiss(); 
					new AlertDialog.Builder(HelloAndroidActivity.this).setMessage( "캡챠정보를 입력하세요." ).setPositiveButton("확인", new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int whichButton){
							return;
						}
					}).show();
					return;
				}
				if (HMTrans.trim(idText.getText()).equals("")) {
					if (progressDialog != null ) progressDialog.dismiss(); 
					new AlertDialog.Builder(HelloAndroidActivity.this).setMessage( "ID를 입력하세요." ).setPositiveButton("확인", new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int whichButton){
							return;
						}
					}).show();
					return;
				}
				if (HMTrans.trim(pwText.getText()).equals("")) {
					if (progressDialog != null ) progressDialog.dismiss(); 
					new AlertDialog.Builder(HelloAndroidActivity.this).setMessage( "PASSWORD를 입력하세요." ).setPositiveButton("확인", new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int whichButton){
							return;
						}
					}).show();
					return;
				}

				progressDialog = ProgressDialog.show(HelloAndroidActivity.this,"", "로드중입니다.", true);
				loginProcess();
			}
		});
    }
    
	private void sessionCheck() {
		
		Thread t1 = new Thread() { public void run() {
			Looper.prepare();
			HttpClient httpclient = CommonUtil.createHttpClient(getApplicationContext());

			int loopcnt = 0;
			isEndProcess = false;
			isStartProcess = false;
			while (!isEndProcess) {
				if (loopcnt > 3 ) break;
				try {
					checkSession(httpclient);
					isStartProcess = true;
				} catch (Exception e) {
					Log.d("MainMenuActivity","connection losses but restart");
					e.printStackTrace();
					isEndProcess = false;
					isStartProcess = false;
					loopcnt++;
				} finally {
					if ( isEndProcess ) {
						httpclient.getConnectionManager().shutdown();
						httpclient = null;
					}
				}
			}
			
			if (progressDialog.isShowing() || progressDialog != null ) progressDialog.dismiss(); 
			if ( !isEndProcess ) {
				httpclient = null;
				new AlertDialog.Builder(HelloAndroidActivity.this).setMessage( "네트워크 이용이 원할하지 않습니다. 잠시후 다시 시도해주세요." ).setPositiveButton("확인", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int whichButton){
						return;
					}
				}).show();
			}
			
			
			Looper.loop();
		}};
		t1.start();
	}
	
	private void loginProcess() {
		
		Thread t1 = new Thread() { public void run() {
			Looper.prepare();
			HttpClient httpclient = CommonUtil.createHttpClient(getApplicationContext());

			int loopcnt = 0;
			isEndProcess = false;
			isStartProcess = false;
			while (!isEndProcess) {
				if (loopcnt > 3 ) break;
				try {
					login(httpclient);
					isStartProcess = true;
				} catch (Exception e) {
					Log.d("MainMenuActivity","connection losses but restart");
					e.printStackTrace();
					isEndProcess = false;
					isStartProcess = false;
					loopcnt++;
				} finally {
					if ( isEndProcess ) {
						httpclient.getConnectionManager().shutdown();
						httpclient = null;
					}
				}
			}
			if (progressDialog.isShowing() || progressDialog != null ) progressDialog.dismiss(); 
			
			if ( !isEndProcess ) {
				httpclient = null;
				new AlertDialog.Builder(HelloAndroidActivity.this).setMessage( "네트워크 이용이 원할하지 않습니다. 잠시후 다시 시도해주세요." ).setPositiveButton("확인", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int whichButton){
						return;
					}
				}).show();
			}
			
			
			Looper.loop();
		}};
		t1.start();
	}
	
	
	private void getCaptchaImgProcess() {
		Thread t1 = new Thread() { public void run() {
			Looper.prepare();
			HttpClient httpclient = CommonUtil.createHttpClient(getApplicationContext());
			isEndProcess = false;
			isStartProcess = false;
			int loopcnt = 0;
			while (!isEndProcess) {
				if (loopcnt > 3 ) break;
				
				try {
					getCaptchaImg(httpclient);
					isStartProcess = true;
				} catch (Exception e) {
					Log.d("MainMenuActivity","connection losses but restart");
					e.printStackTrace();
					isEndProcess = false;
					isStartProcess = false;
					loopcnt++;
				} finally {
					if ( isEndProcess ) {
						httpclient.getConnectionManager().shutdown();
						httpclient = null;
					}
				}
			}
			if (progressDialog.isShowing() || progressDialog != null ) progressDialog.dismiss(); 
			
			if ( !isEndProcess ) {
				httpclient = null;
				new AlertDialog.Builder(HelloAndroidActivity.this).setMessage( "네트워크 이용이 원할하지 않습니다. 잠시후 다시 시도해주세요." ).setPositiveButton("확인", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int whichButton){
						return;
					}
				}).show();
			}
			
			Looper.loop();
			}
		};
		t1.start();
	}
	
	private void getCaptchaImg(HttpClient httpclient) throws Exception {
		if (isEndProcess || isStartProcess) return;
		HttpResponse response = null;
		HttpPost httppost = null;
		HttpEntity entityResponse = null;
		CookieStore cookieStore = ((DefaultHttpClient)httpclient).getCookieStore();
		httppost = new HttpPost("https://m.happymoney.co.kr/DrawCaptCha.aspx");
		httppost.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
		httppost.setEntity(null);
		response = httpclient.execute(httppost);
		
		List<Cookie> cookies = cookieStore.getCookies();
		cookieRawStr = "";
		if (cookies != null && cookies.size() > 0) {
			for(Cookie cookie : cookies)
			{
				String cookieString = cookie.getName() + "="+ cookie.getValue()+"; domain=m.happymoney.co.kr";
				cookieRawStr += cookieString+";";
			}
			cookieRawStr = cookieRawStr.substring(0,cookieRawStr.length()-1);
			Log.d("HelloAndroidActivity", "cookie info \n"+cookieRawStr);
		} else {
			Log.d("HelloAndroidActivity","cookie info null");
			throw new Exception("cookie is null");
		}
		
		entityResponse = response.getEntity();
		final Bitmap bmresult = BitmapFactory.decodeStream(entityResponse.getContent());


		
		

		entityResponse.consumeContent();
		
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				Bitmap bm = bmresult;
				captchaImg.setImageBitmap(bm);	
				isEndProcess = true;
				if (progressDialog.isShowing() || progressDialog != null ) progressDialog.dismiss(); 
				return;
				
			}
		});
		


	}
	
	private void checkSession(HttpClient httpclient) throws Exception {
		if (isEndProcess || isStartProcess) return;
		HttpResponse response = null;
		BufferedReader reader = null;
		HttpPost httppost = null;
		
		CookieStore cookieStore = (CookieStore) localContext.getAttribute(ClientContext.COOKIE_STORE);
		List<Cookie> cookies = cookieStore == null ? null : cookieStore.getCookies();
		String boolTxt = "";
		String urlTotalInfo = "";
		
	    urlTotalInfo = "https://m.happymoney.co.kr/app/check_session.hm";
	    httppost = new HttpPost(urlTotalInfo);
	    httppost.setEntity(null);
	    httppost.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);

	    
		if (cookies != null && cookies.size() > 0) { 
			((DefaultHttpClient)httpclient).setCookieStore(cookieStore);
			CookieSpecBase cookieSpecBase = new BrowserCompatSpec();
			List<?> cookieHeader = cookieSpecBase.formatCookies(cookies);
			httppost.setHeader((Header) cookieHeader.get(0));
		}
		
		response = httpclient.execute(httppost, localContext);
		HttpEntity entityResponse = response.getEntity();
		reader =  new BufferedReader(new InputStreamReader(entityResponse.getContent(), "UTF-8"), 8);
		
		boolTxt = XmlParserUtil.parsingPullXml(reader, "bool");
		if(reader != null) reader.close();
		
		
		final String result = boolTxt;
		handler.post(new Runnable(){
			public void run(){
				String booltxt = result;
				if (HMTrans.trim(booltxt).equals("\""+"TRUE"+"\"")) {
					checkSessionRst.setText("로그인 되어있습니다.");
				} else {
					checkSessionRst.setText("세션이 없습니다.");
				}
				isEndProcess = true;
				if (progressDialog.isShowing() || progressDialog != null ) progressDialog.dismiss(); 
				return;
			}
		});
		
		
	}
	
	
	private void login(HttpClient httpclient) throws Exception {
		if (isEndProcess || isStartProcess) return;
		String urlTotalInfo = "https://m.happymoney.co.kr/LoginProcess.hm";
		
		String captchaInfoTxt = ""; 
		String userIdTxt = ""; 
		String userPwTxt = ""; 
		captchaInfoTxt = HMTrans.trim(captchaInfo.getText());
		userIdTxt = HMTrans.trim(idText.getText());
		userPwTxt = HMTrans.trim(pwText.getText());
		HttpPost httppost = new HttpPost(urlTotalInfo);
		httppost.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
		
		cookieManager = CookieManager.getInstance();

		ArrayList<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("hm_p_UserId", userIdTxt));
	    nameValuePairs.add(new BasicNameValuePair("hm_p_Password", userPwTxt));
	    nameValuePairs.add(new BasicNameValuePair("hm_p_Captcha", captchaInfoTxt));
	    UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
	    httppost.setEntity(entityRequest);
	    
		CookieStore cookieStore = new BasicCookieStore();
		
		List<Cookie> cookies = null;
		String cookieStr = cookieRawStr;
		
		if ( !HMTrans.trim(cookieStr).equals("") ) {
			((DefaultHttpClient)httpclient).getCookieStore().clear();
			cookies = new ArrayList<Cookie>();
			String[] keyValueSets = cookieStr.split(";");
			cookieManager.setAcceptCookie(true);
			for(String cookie : keyValueSets)
			{
			    String[] keyValue = cookie.split("=");
			    String key = keyValue[0];
			    String value = "";
			    if(keyValue.length>1) value = keyValue[1];
			    Cookie cookieObj = new BasicClientCookie(key, value);
			    cookieStore.addCookie(cookieObj);
			    cookies.add(cookieObj);
			    cookieManager.setCookie("m.happymoney.co.kr", cookie);
			}
			((DefaultHttpClient)httpclient).setCookieStore(cookieStore);
			CookieSpecBase cookieSpecBase = new BrowserCompatSpec();
			List<?> cookieHeader = cookieSpecBase.formatCookies(cookies);
			httppost.setHeader((Header) cookieHeader.get(0));
			localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
			CookieSyncManager.getInstance().sync();
			
			
		}
		
		Log.d("cookie Str", cookieStr);
		
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
	    String responseRst = "";
	    responseRst = httpclient.execute(httppost, responseHandler, localContext);
		String result = HMTrans.trim(responseRst);
		
		final String loginMessage = result;
		
		handler.post(new Runnable(){
			public void run(){
				String data = loginMessage;
				boolean isLive = false;
				String loginMsg = "";
				JSONObject loginResult;

				try {
					loginResult = new JSONObject(data);
					isLive = HMTrans.toB(loginResult.getString("RESULT"));
					loginMsg = HMTrans.trim(loginResult.getString("MESSAGE"));
					if (isLive) {
						String pwCryptTxt = "";
						try {
							pwCryptTxt = CryptUtil.aesEncrypt(CRYPTO_SEED_PASSWORD, HMTrans.trim(pwText.getText()));
							SharedPreferences pref = getSharedPreferences("happymoney_info", MODE_PRIVATE);
							SharedPreferences.Editor editor = pref.edit();
							editor.putString("userid", HMTrans.trim(idText.getText()));
							editor.putString("userpw", pwCryptTxt);
							editor.commit();
						} catch (Exception e) {
						}

						String cookieStr = cookieRawStr;
						CookieSyncManager.createInstance(HelloAndroidActivity.this);
						CookieSyncManager.getInstance().startSync();
						cookieManager = CookieManager.getInstance();
						if ( !HMTrans.trim(cookieStr).equals("") ) {
							String[] keyValueSets = cookieStr.split(";");
							for(String cookie : keyValueSets)
							{
								cookieManager.setCookie("m.happymoney.co.kr", cookie);
							}
							CookieSyncManager.getInstance().sync();
						}

						checkSessionRst.setText("로그인 되었습니다.");
					} else {
						checkSessionRst.setText("로그인실패했습니다. 사유:" + loginMsg);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} finally {
					isEndProcess = true;
				}
			}
		});
	}
	
}