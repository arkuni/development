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
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import common.util.CommonUtil;
import common.util.HMTrans;
import common.util.XmlParserUtil;

public class CustomNiceActivity extends Activity {
	private enum DialogType {
		INPUT_NICE_CERT
		,SELECT_CERT_TYPE
		,INPUT_TEL_INFO
		,INPUT_CARD_INFO
		,INPUT_PUBLIC_CERT
		,INPUT_SMS_INFO
	};
	private final String NICE_RETURN_URL = "http://m.happymoney.co.kr/care_privaterequest_init.hm";
	private HttpContext localContext = new BasicHttpContext();
	private CookieManager cookieManager;
	private EditText checkSessionRst;
	private ProgressDialog progressDialog;
	private Dialog dialog = null;
	private Handler handler = new Handler();
	private String message = "";
	private String pageTxt = "";
	private String privateCookieInfo = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_nice);
		Button sessionCheckBtn = (Button)findViewById(R.id.check_session_btn);
		Button customNiceBtn = (Button)findViewById(R.id.nice_popup_btn);
		checkSessionRst = (EditText)findViewById(R.id.check_session_result);
		cookieManager = CookieManager.getInstance();
		sessionCheckBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				progressDialog = ProgressDialog.show(CustomNiceActivity.this,"", "로드중입니다.", true);
				SessionCheckThread checkThread = new SessionCheckThread();
				checkThread.start();
			}
		});
		
		customNiceBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				progressDialog = ProgressDialog.show(CustomNiceActivity.this,"", "로드중입니다.", true);
				CustomNiceThread customNice = new CustomNiceThread();
				customNice.start();
			}
		});
	}
	
	private class SessionCheckThread extends Thread {
		private HttpClient httpclient;
		private boolean isEndProcess = false;
		private boolean isStartProcess = false;
		public SessionCheckThread() {
			httpclient = CommonUtil.createHttpClient(getApplicationContext());
			isEndProcess = false;
			isStartProcess = false;
		}
		public void run() {
			int loopcnt = 0;

			while (!isEndProcess) {
				if (loopcnt > 3 ) break;
				try {
					checkSession(httpclient);
					isStartProcess = true;
				} catch (Exception e) {
					Log.d("CustomNiceActivity","connection losses but restart");
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
				new AlertDialog.Builder(CustomNiceActivity.this).setMessage( "네트워크 이용이 원할하지 않습니다. 잠시후 다시 시도해주세요." ).setPositiveButton("확인", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int whichButton){
						return;
					}
				}).show();
			}
		}
		
		private void checkSession(HttpClient httpclient) throws Exception {
			if (isEndProcess || isStartProcess) return;
			HttpResponse response = null;
			BufferedReader reader = null;
			HttpPost httppost = null;
			CookieStore cookieStore = null;
			String cookieStr = cookieManager.getCookie("m.happymoney.co.kr");
			
			if (!HMTrans.trim(cookieStr).equals("")) { 
				String[] keyValueSets = cookieStr.split(";");
				cookieStore = ((DefaultHttpClient)httpclient).getCookieStore();
				for(String cookie : keyValueSets)
				{
				    String[] keyValue = cookie.split("=");
				    String key = keyValue[0];
				    String value = "";
				    if(keyValue.length>1) value = keyValue[1];
				    cookieStore.addCookie(new BasicClientCookie(key, value));
				}
				
				localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
			}
			
			
			cookieStore = (CookieStore) localContext.getAttribute(ClientContext.COOKIE_STORE);
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
	}

	private class CustomNiceThread extends Thread {
		private HttpClient httpclient;
		private boolean isEndProcess = false;
		private boolean isStartProcess = false;
		public CustomNiceThread() {
			httpclient = CommonUtil.createHttpClient(getApplicationContext());
			isEndProcess = false;
			isStartProcess = false;
		}
		public void run() {
			int loopcnt = 0;

			while (!isEndProcess) {
				if (loopcnt > 3 ) break;
				try {
					personalIdCheck();
					isStartProcess = true;
				} catch (Exception e) {
					Log.d("CustomNiceActivity","connection losses but restart");
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
				new AlertDialog.Builder(CustomNiceActivity.this).setMessage( "네트워크 이용이 원할하지 않습니다. 잠시후 다시 시도해주세요." ).setPositiveButton("확인", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int whichButton){
						return;
					}
				}).show();
			}
		}
		
		private void personalIdCheck() throws Exception {
			if (isEndProcess || isStartProcess) return;
			HttpResponse response = null;
			BufferedReader reader = null;
			HttpPost httppost = null;
			CookieStore cookieStore = null;

			String cookieStr = cookieManager.getCookie("m.happymoney.co.kr");
			
			if (!HMTrans.trim(cookieStr).equals("")) { 
				String[] keyValueSets = cookieStr.split(";");
				cookieStore = ((DefaultHttpClient)httpclient).getCookieStore();
				for(String cookie : keyValueSets)
				{
				    String[] keyValue = cookie.split("=");
				    String key = keyValue[0];
				    String value = "";
				    if(keyValue.length>1) value = keyValue[1];
				    cookieStore.addCookie(new BasicClientCookie(key, value));
				}
				
				localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
			}
			
			
			cookieStore = (CookieStore) localContext.getAttribute(ClientContext.COOKIE_STORE);
			List<Cookie> cookies = cookieStore == null ? null : cookieStore.getCookies();
			JSONObject rslt = null;
			StringBuffer bf =null;

			String urlTotalInfo = "";
	    	String line = "";
	    	String rawData = "";
			
			
		    urlTotalInfo = "https://m.happymoney.co.kr/care_nicerequest.hm";
		    httppost = new HttpPost(urlTotalInfo);
		    
			ArrayList<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("rtnurl", NICE_RETURN_URL));
			nameValuePairs.add(new BasicNameValuePair("reserved1", "http://m.happymoney.co.kr/care_privateresponse_init.hm"));
			nameValuePairs.add(new BasicNameValuePair("reserved3", "http://m.happymoney.co.kr/app/care_result.hm"));
			UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
		    httppost.setEntity(entityRequest);


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

			bf = new StringBuffer();

			while ((line=reader.readLine())!= null) {
	   			bf.append(line);
	   		}
			
			rawData = bf.toString();
			rslt = new JSONObject(rawData);
			
			if(reader != null) reader.close();

			final JSONObject result = rslt;
			
			handler.post(new Runnable(){
				public void run(){
					String booltxt = "";
					//String page = "";
					try {
						booltxt = result.getString("RESULT");
						message = result.getString("MESSAGE");
						//page = result.getString("PAGE");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					isEndProcess = true;
					if (progressDialog.isShowing() || progressDialog != null ) progressDialog.dismiss(); 
					if (HMTrans.trim(booltxt).equals("TRUE")) {
						showDialog(DialogType.INPUT_NICE_CERT.ordinal());
					} else {
						Toast.makeText(CustomNiceActivity.this, "false" ,Toast.LENGTH_SHORT).show();
					}
					return;
				}
			});
		}
	}
	
	private class CertNiceThread implements Runnable {
		private HttpClient httpclient;
		private boolean isEndProcess = false;
		private boolean isStartProcess = false;
		private String userName = "";
		private String userIdno1 = "";
		private String userIdno2 = "";
		
		public CertNiceThread(String userName, String userIdno1, String userIdno2) {
			httpclient = CommonUtil.createHttpClient(getApplicationContext());
			isEndProcess = false;
			isStartProcess = false;
			this.userName = userName;
			this.userIdno1 = userIdno1;
			this.userIdno2 = userIdno2;
			
		}

		public void run() {
			int loopcnt = 0;

			while (!isEndProcess) {
				if (loopcnt > 3 ) break;
				try {
					certNice();
					isStartProcess = true;
				} catch (Exception e) {
					Log.d("CustomNiceActivity","connection losses but restart");
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
				new AlertDialog.Builder(CustomNiceActivity.this).setMessage( "네트워크 이용이 원할하지 않습니다. 잠시후 다시 시도해주세요." ).setPositiveButton("확인", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int whichButton){
						return;
					}
				}).show();
			}
		}
		
		private void certNice() throws Exception {
			if (isEndProcess || isStartProcess) return;
			HttpResponse response = null;
			BufferedReader reader = null;
			HttpPost httppost = null;
			
			CookieStore cookieStore = null;
			
			cookieStore = (CookieStore) localContext.getAttribute(ClientContext.COOKIE_STORE);
			List<Cookie> cookies = cookieStore == null ? null : cookieStore.getCookies();
			
			JSONObject rslt = null;
			StringBuffer bf =null;

			String urlTotalInfo = "";
	    	String line = "";
	    	String rawData = "";
			
	    	urlTotalInfo = "https://cert.namecheck.co.kr/NiceID/certnc_proc.asp";
	    	httppost = new HttpPost(urlTotalInfo);
	    	
			if (cookies != null && cookies.size() > 0) { 
				((DefaultHttpClient)httpclient).setCookieStore(cookieStore);
				CookieSpecBase cookieSpecBase = new BrowserCompatSpec();
				List<?> cookieHeader = cookieSpecBase.formatCookies(cookies);
				httppost.setHeader((Header) cookieHeader.get(0));
			}
			
			
			ArrayList<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("enc_data", message));
			nameValuePairs.add(new BasicNameValuePair("bg_type", "green"));
			nameValuePairs.add(new BasicNameValuePair("error_msg", ""));
			nameValuePairs.add(new BasicNameValuePair("client_img", "http://img.happymoney.co.kr/www/images/member/auth_log.gif"));
			nameValuePairs.add(new BasicNameValuePair("nation_gubun", "Kr"));
			nameValuePairs.add(new BasicNameValuePair("name", userName));
		    nameValuePairs.add(new BasicNameValuePair("juminid1", userIdno1));
		    nameValuePairs.add(new BasicNameValuePair("juminid2", userIdno2));
		    Log.d("CustomNiceActivity", "name : " + userName);
		    Log.d("CustomNiceActivity", "juminid1 : " + userIdno1);
		    Log.d("CustomNiceActivity", "juminid2 : " + userIdno2);
		    
		    UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(nameValuePairs, "EUC-KR");
		    httppost.setEntity(entityRequest);


		    httppost.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);


			response = httpclient.execute(httppost, localContext);
			HttpEntity entityResponse = response.getEntity();
			reader =  new BufferedReader(new InputStreamReader(entityResponse.getContent(), "EUC-KR"), 8);

			
			bf = new StringBuffer();

			while ((line=reader.readLine())!= null) {
	   			bf.append(line);
	   		}
			
			rawData = bf.toString();
			Log.d("CustomNiceActivity", "getStatusLine : "+response.getStatusLine());
			Log.d("CustomNiceActivity", rawData);
			if(reader != null) reader.close();
			

			try {
				String scriptTxt = rawData.substring(rawData.indexOf("<script"),rawData.indexOf("</script>"));
				Log.d("CustomNiceActivity", scriptTxt);
				
				String locationTxt = scriptTxt.substring(scriptTxt.indexOf("http"),scriptTxt.indexOf(".hm';"));
				locationTxt = locationTxt+".hm";
				
				if (!locationTxt.equals(NICE_RETURN_URL)) new Exception("데이터를 확인하세요.");
				
				String input = rawData.substring(rawData.indexOf("<input"),rawData.indexOf("</form>"));
				input = input.substring(input.indexOf("value="),input.indexOf(">"));
				input = input.substring(7,input.length()-1);
				
				
				urlTotalInfo = "http://m.happymoney.co.kr/care_privaterequest.hm";
		    	httppost = new HttpPost(urlTotalInfo);
		    	
				if (cookies != null && cookies.size() > 0) { 
					((DefaultHttpClient)httpclient).setCookieStore(cookieStore);
					CookieSpecBase cookieSpecBase = new BrowserCompatSpec();
					List<?> cookieHeader = cookieSpecBase.formatCookies(cookies);
					httppost.setHeader((Header) cookieHeader.get(0));
				}
				
				nameValuePairs = new ArrayList<BasicNameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("enc_data", input));
				entityRequest = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
			    httppost.setEntity(entityRequest);
				httppost.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);

				response = httpclient.execute(httppost, localContext);
				entityResponse = response.getEntity();
				
				reader =  new BufferedReader(new InputStreamReader(entityResponse.getContent(), "UTF-8"), 8);

				bf = new StringBuffer();

				while ((line=reader.readLine())!= null) {
		   			bf.append(line);
		   		}
				
				rawData = bf.toString();
				rslt = new JSONObject(rawData);
				Log.d("CustomNiceActivity", rawData);
				if(reader != null) reader.close();

				String booltxt = "";
				try {
					booltxt = rslt.getString("RESULT");
					message = rslt.getString("MESSAGE");
					pageTxt = rslt.getString("PAGE");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (!HMTrans.trim(booltxt).equals("TRUE")) 
					throw new Exception("실패했습니다.");
				
				Log.d("CustomNiceActivity","page :" +pageTxt);

				checkInit();
				
				handler.post(new Runnable(){
					public void run(){
						showDialog(DialogType.SELECT_CERT_TYPE.ordinal());
						return;
					}
				});
				
			}catch (Exception e) {
				e.printStackTrace();
				
				Toast.makeText(CustomNiceActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
			}finally {
				isEndProcess = true;
				if (progressDialog.isShowing() || progressDialog != null ) progressDialog.dismiss(); 
			}
		}
		
		private void checkInit() throws Exception {
			HttpResponse response = null;
			HttpPost httppost = null;
			
			CookieStore cookieStore = null;
			
			cookieStore = ((DefaultHttpClient)httpclient).getCookieStore();
			cookieStore.clear();

			httppost = new HttpPost(pageTxt);
			httppost.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
			httppost.setEntity(null);
			response = httpclient.execute(httppost);
			
			List<Cookie> cookies = cookieStore.getCookies();
			privateCookieInfo = "";
			if (cookies != null && cookies.size() > 0) {
				for(Cookie cookie : cookies)
				{
					String cookieString = cookie.getName() + "="+ cookie.getValue()+"; domain=.check.namecheck.co.kr";
					privateCookieInfo += cookieString+";";
				}
				privateCookieInfo = privateCookieInfo.substring(0,privateCookieInfo.length()-1);
				Log.d("CustomNiceActivity", "cookie info \n"+privateCookieInfo);
			} else {
				Log.d("CustomNiceActivity","cookie info null");
				throw new Exception("cookie is null");
			}
			
			Log.d("CustomNiceActivity", "getStatusLine : "+response.getStatusLine());
		}
	}

	
	
	private class PrivateCheckMethodThread implements Runnable {
		private HttpClient httpclient;
		private boolean isEndProcess = false;
		private boolean isStartProcess = false;
		private String priMethod = "";

		
		public PrivateCheckMethodThread(String priMethod) {
			httpclient = CommonUtil.createHttpClient(getApplicationContext());
			isEndProcess = false;
			isStartProcess = false;
			this.priMethod = priMethod;
		}

		public void run() {
			int loopcnt = 0;

			while (!isEndProcess) {
				if (loopcnt > 3 ) break;
				try {
					checkMethod();
					isStartProcess = true;
				} catch (Exception e) {
					Log.d("CustomNiceActivity","connection losses but restart");
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
			Log.d("CustomNiceActivity", "progressDialog is dismiss");
			if ( !isEndProcess ) {
				httpclient = null;
				new AlertDialog.Builder(CustomNiceActivity.this).setMessage( "네트워크 이용이 원할하지 않습니다. 잠시후 다시 시도해주세요." ).setPositiveButton("확인", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int whichButton){
						return;
					}
				}).show();
			}
		}
		
		private void checkMethod() throws Exception {
			if (isEndProcess || isStartProcess) return;
			HttpResponse response = null;
			HttpPost httppost = null;
			String urlTotalInfo = "";
	    	urlTotalInfo = "https://check.namecheck.co.kr/checkplus_new_si_model2/checkplus.cb?m="+priMethod;
	    	
	    	httppost = new HttpPost(urlTotalInfo);
			httppost.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
	    	
			CookieStore cookieStore = new BasicCookieStore();
			
			List<Cookie> cookies = null;
			String cookieStr = privateCookieInfo;
			
			if ( !HMTrans.trim(cookieStr).equals("") ) {
				((DefaultHttpClient)httpclient).getCookieStore().clear();
				cookies = new ArrayList<Cookie>();
				String[] keyValueSets = cookieStr.split(";");
				for(String cookie : keyValueSets)
				{
				    String[] keyValue = cookie.split("=");
				    String key = keyValue[0];
				    String value = "";
				    if(keyValue.length>1) value = keyValue[1];
				    BasicClientCookie cookieObj = new BasicClientCookie(key, value);
				    cookieObj.setPath("/");
				    cookieObj.setDomain(".check.namecheck.co.kr");
				    cookieObj.setSecure(true);
				    cookieStore.addCookie(cookieObj);
				    cookies.add(cookieObj);
				}
				((DefaultHttpClient)httpclient).setCookieStore(cookieStore);
				CookieSpecBase cookieSpecBase = new BrowserCompatSpec();
				List<?> cookieHeader = cookieSpecBase.formatCookies(cookies);
				httppost.setHeader((Header) cookieHeader.get(0));
			}
			Log.d("CustomNiceActivity", "cookie info \n"+privateCookieInfo);

			response = httpclient.execute(httppost);
			Log.d("CustomNiceActivity", "getStatusLine : "+response.getStatusLine());
			final String tmpPriMethod = priMethod;
			handler.post(new Runnable(){
				public void run(){
					Log.d("CustomNiceActivity", "tmpPriMethod : "+tmpPriMethod);
					if (tmpPriMethod.equals("auth_mobile01")) showDialog(DialogType.INPUT_TEL_INFO.ordinal());
					else if (tmpPriMethod.equals("auth_card01")) showDialog(DialogType.INPUT_CARD_INFO.ordinal());
					
					return;
				}
			});
			isEndProcess = true;
		}
	}
	
	private class SendSmsCert extends Thread {
		private HttpClient httpclient;
		private boolean isEndProcess = false;
		private boolean isStartProcess = false;
		private String telNo1 = "";
		private String telNo2 = "";
		private String telNo3 = "";
		private String telCo = "";
		public SendSmsCert(String telNo1, String telNo2, String telNo3, String telCo) {
			httpclient = CommonUtil.createHttpClient(getApplicationContext());
			isEndProcess = false;
			isStartProcess = false;
			this.telNo1 = telNo1;
			this.telNo2 = telNo2;
			this.telNo3 = telNo3;
			this.telCo = telCo;
		}
		public void run() {
			int loopcnt = 0;

			while (!isEndProcess) {
				if (loopcnt > 3 ) break;
				try {
					sendSmsCheck();
					isStartProcess = true;
				} catch (Exception e) {
					Log.d("CustomNiceActivity","connection losses but restart");
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
				new AlertDialog.Builder(CustomNiceActivity.this).setMessage( "네트워크 이용이 원할하지 않습니다. 잠시후 다시 시도해주세요." ).setPositiveButton("확인", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int whichButton){
						return;
					}
				}).show();
			}
		}
		
		private void sendSmsCheck() throws Exception {
			if (isEndProcess || isStartProcess) return;
			HttpResponse response = null;
			HttpPost httppost = null;
			String urlTotalInfo = "";
	    	//urlTotalInfo = "https://check.namecheck.co.kr/checkplus_new_si_model2/checkplus.cb?mobileno1="+telNo1+"&mobileno2="+telNo2+"&&mobileno3="+telNo3+"&m=auth_mobile01_proc&kisChk=on&mobileco="+telCo+"";
	    	urlTotalInfo = "https://check.namecheck.co.kr/checkplus_new_si_model2/checkplus.cb";
	    	
	    	httppost = new HttpPost(urlTotalInfo);
			httppost.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
	    	
			CookieStore cookieStore = new BasicCookieStore();
			
			List<Cookie> cookies = null;
			String cookieStr = privateCookieInfo;
			
			if ( !HMTrans.trim(cookieStr).equals("") ) {
				((DefaultHttpClient)httpclient).getCookieStore().clear();
				cookies = new ArrayList<Cookie>();
				String[] keyValueSets = cookieStr.split(";");
				for(String cookie : keyValueSets)
				{
				    String[] keyValue = cookie.split("=");
				    String key = keyValue[0];
				    String value = "";
				    if(keyValue.length>1) value = keyValue[1];
				    BasicClientCookie cookieObj = new BasicClientCookie(key, value);
				    cookieObj.setPath("/");
				    cookieObj.setDomain(".check.namecheck.co.kr");
				    cookieObj.setSecure(true);
				    cookieStore.addCookie(cookieObj);
				    cookies.add(cookieObj);
				}
				((DefaultHttpClient)httpclient).setCookieStore(cookieStore);
				CookieSpecBase cookieSpecBase = new BrowserCompatSpec();
				List<?> cookieHeader = cookieSpecBase.formatCookies(cookies);
				httppost.setHeader((Header) cookieHeader.get(0));
			}
			Log.d("CustomNiceActivity", "cookie info \n"+privateCookieInfo);
			
			ArrayList<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("mobileno1", telNo1));
			nameValuePairs.add(new BasicNameValuePair("mobileno2", telNo2));
			nameValuePairs.add(new BasicNameValuePair("mobileno3", telNo3));
			nameValuePairs.add(new BasicNameValuePair("mobileco", telCo));
			nameValuePairs.add(new BasicNameValuePair("m", "auth_mobile01_proc"));
			nameValuePairs.add(new BasicNameValuePair("kisChk", "on"));
		    Log.d("CustomNiceActivity", "mobileno1 : " + telNo1);
		    Log.d("CustomNiceActivity", "mobileno2 : " + telNo2);
		    Log.d("CustomNiceActivity", "mobileno3 : " + telNo3);
		    Log.d("CustomNiceActivity", "mobileco : " + telCo);
		    
		    UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(nameValuePairs, "EUC-KR");
		    httppost.setEntity(entityRequest);
		    
			BufferedReader reader = null;
			StringBuffer bf =null;
			String line = "";
	    	String rawData = "";
		    
		    response = httpclient.execute(httppost, localContext);
			HttpEntity entityResponse = response.getEntity();
			reader =  new BufferedReader(new InputStreamReader(entityResponse.getContent(), "EUC-KR"), 8);
			
			bf = new StringBuffer();

			while ((line=reader.readLine())!= null) {
	   			bf.append(line);
	   		}
			
			rawData = bf.toString();
			Log.d("CustomNiceActivity", HMTrans.trim(rawData).equals("") ? "null" : rawData);

			if(reader != null) reader.close();
			
			
			handler.post(new Runnable(){
				public void run(){
					showDialog(DialogType.INPUT_SMS_INFO.ordinal());
					return;
				}
			});
			isEndProcess = true;
		}
	}
	
	
	private class CheckSmsCert extends Thread {
		private HttpClient httpclient;
		private boolean isEndProcess = false;
		private boolean isStartProcess = false;
		private String smsinfo = "";
		private String serviceYn = "";
		public CheckSmsCert(String smsinfo, String serviceYn) {
			httpclient = CommonUtil.createHttpClient(getApplicationContext());
			isEndProcess = false;
			isStartProcess = false;
			this.smsinfo = smsinfo;
			this.serviceYn = serviceYn;
		}
		public void run() {
			int loopcnt = 0;

			while (!isEndProcess) {
				if (loopcnt > 3 ) break;
				try {
					sendSmsCheck();
					isStartProcess = true;
				} catch (Exception e) {
					Log.d("CustomNiceActivity","connection losses but restart");
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
				new AlertDialog.Builder(CustomNiceActivity.this).setMessage( "네트워크 이용이 원할하지 않습니다. 잠시후 다시 시도해주세요." ).setPositiveButton("확인", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int whichButton){
						return;
					}
				}).show();
			}
		}
		
		private void sendSmsCheck() throws Exception {
			if (isEndProcess || isStartProcess) return;
			HttpResponse response = null;
			HttpPost httppost = null;
			String urlTotalInfo = "";
	    	urlTotalInfo = "https://check.namecheck.co.kr/checkplus_new_si_model2/checkplus.cb";
	    	
	    	httppost = new HttpPost(urlTotalInfo);
			httppost.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
	    	
			CookieStore cookieStore = new BasicCookieStore();
			
			List<Cookie> cookies = null;
			String cookieStr = privateCookieInfo;
			
			if ( !HMTrans.trim(cookieStr).equals("") ) {
				((DefaultHttpClient)httpclient).getCookieStore().clear();
				cookies = new ArrayList<Cookie>();
				String[] keyValueSets = cookieStr.split(";");
				for(String cookie : keyValueSets)
				{
				    String[] keyValue = cookie.split("=");
				    String key = keyValue[0];
				    String value = "";
				    if(keyValue.length>1) value = keyValue[1];
				    BasicClientCookie cookieObj = new BasicClientCookie(key, value);
				    cookieObj.setPath("/");
				    cookieObj.setDomain(".check.namecheck.co.kr");
				    cookieObj.setSecure(true);
				    cookieStore.addCookie(cookieObj);
				    cookies.add(cookieObj);
				}
				((DefaultHttpClient)httpclient).setCookieStore(cookieStore);
				CookieSpecBase cookieSpecBase = new BrowserCompatSpec();
				List<?> cookieHeader = cookieSpecBase.formatCookies(cookies);
				httppost.setHeader((Header) cookieHeader.get(0));
			}
			
			ArrayList<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("authnumber", smsinfo));
			nameValuePairs.add(new BasicNameValuePair("chk_mng", serviceYn));
			nameValuePairs.add(new BasicNameValuePair("m", "auth_mobile02_proc"));
			Log.d("CustomNiceActivity", "authnumber : " + smsinfo);
		    Log.d("CustomNiceActivity", "chk_mng : " + serviceYn);
			UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(nameValuePairs, "EUC-KR");
		    httppost.setEntity(entityRequest);

			response = httpclient.execute(httppost);
			Log.d("CustomNiceActivity", "getStatusLine : "+response.getStatusLine());
			
			
			BufferedReader reader = null;
			StringBuffer bf =null;
			String line = "";
	    	String rawData = "";
		    
		    response = httpclient.execute(httppost, localContext);
			HttpEntity entityResponse = response.getEntity();
			reader =  new BufferedReader(new InputStreamReader(entityResponse.getContent(), "EUC-KR"), 8);
			
			bf = new StringBuffer();

			while ((line=reader.readLine())!= null) {
	   			bf.append(line);
	   		}
			
			rawData = bf.toString();
			Log.d("CustomNiceActivity", HMTrans.trim(rawData).equals("") ? "null" : rawData);

			if(reader != null) reader.close();
			
			
			
			handler.post(new Runnable(){
				public void run(){
					showDialog(DialogType.INPUT_SMS_INFO.ordinal());
					return;
				}
			});
			isEndProcess = true;
		}
	}
	
	
	
	@Override
	protected Dialog onCreateDialog(int seq) {
		dialog = new Dialog(CustomNiceActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		
		settingDialog(seq);
		return dialog;
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog tmp, Bundle args){
		removeDialog(id);
		super.onPrepareDialog(id, tmp, args);
	}
	
	private void settingDialog(int seq) {
		if (seq == DialogType.INPUT_NICE_CERT.ordinal()) {
			dialog.setContentView(R.layout.nice_dialog);
			Button nextBtn = (Button) dialog.findViewById(R.id.next_step_btn);
			Button closeBtn = (Button) dialog.findViewById(R.id.close_btn);
			final EditText userName = (EditText) dialog.findViewById(R.id.name);
			final EditText idno1 = (EditText) dialog.findViewById(R.id.idno1);
			final EditText idno2 = (EditText) dialog.findViewById(R.id.idno2);
			

			nextBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					progressDialog = ProgressDialog.show(CustomNiceActivity.this,"", "로드중입니다.", true);
					String sName = HMTrans.trim(userName.getText());
					String sIdno1 = HMTrans.trim(idno1.getText());
					String sIdno2 = HMTrans.trim(idno2.getText());
					Thread certNice = new Thread(new CertNiceThread(sName,sIdno1,sIdno2));
					certNice.start();
					dialog.dismiss();
				}
				
			});
			
			closeBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
		
		} else if (seq == DialogType.SELECT_CERT_TYPE.ordinal()) {

			dialog.setContentView(R.layout.sel_cert_dialog);
			Button telBtn = (Button) dialog.findViewById(R.id.mobileno_cert);
			Button creditBtn = (Button) dialog.findViewById(R.id.credit_cert);
			Button certBtn = (Button) dialog.findViewById(R.id.publiccert_cert);
			Button closeBtn = (Button) dialog.findViewById(R.id.close_btn);

			telBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					progressDialog = ProgressDialog.show(CustomNiceActivity.this,"", "로드중입니다.", true);
					Thread checkMethod = new Thread(new PrivateCheckMethodThread("auth_mobile01"));
					checkMethod.start();
					dialog.dismiss();
				}
			});
			creditBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					progressDialog = ProgressDialog.show(CustomNiceActivity.this,"", "로드중입니다.", true);
					Thread checkMethod = new Thread(new PrivateCheckMethodThread("auth_card01"));
					checkMethod.start();
					dialog.dismiss();
				}
			});
			certBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			closeBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
		} else if (seq == DialogType.INPUT_CARD_INFO.ordinal()) {
			
			Toast.makeText(CustomNiceActivity.this, "카드정보를 입력하세요.",Toast.LENGTH_SHORT).show();
		} else if (seq == DialogType.INPUT_TEL_INFO.ordinal()) {
			dialog.setContentView(R.layout.input_telno_dialog);
			final EditText telno1 = (EditText) dialog.findViewById(R.id.telno1);
			final EditText telno2 = (EditText) dialog.findViewById(R.id.telno2);
			final EditText telno3 = (EditText) dialog.findViewById(R.id.telno3);
			final Spinner telco = (Spinner) dialog.findViewById(R.id.telco);
			Button nextBtn = (Button) dialog.findViewById(R.id.next_step_btn);
			Button closeBtn = (Button) dialog.findViewById(R.id.close_btn);
			
			nextBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					progressDialog = ProgressDialog.show(CustomNiceActivity.this,"", "로드중입니다.", true);
					String sTelno1 = HMTrans.trim(telno1.getText());
					String sTelno2 = HMTrans.trim(telno2.getText());
					String sTelno3 = HMTrans.trim(telno3.getText());
					String sTelCo = HMTrans.trim(telco.getSelectedItem());
					Thread certNice = new Thread(new SendSmsCert(sTelno1,sTelno2,sTelno3,sTelCo));
					certNice.start();
					dialog.dismiss();
				}
				
			});
			
			closeBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
		} else if (seq == DialogType.INPUT_SMS_INFO.ordinal()) {
			dialog.setContentView(R.layout.input_sms_dialog);
			final EditText smsinfo = (EditText) dialog.findViewById(R.id.smsinfo);
			RadioButton yesBtn = (RadioButton) dialog.findViewById(R.id.check_yes);
			//RadioButton noBtn = (RadioButton) dialog.findViewById(R.id.check_no);
			Button nextBtn = (Button) dialog.findViewById(R.id.next_step_btn);
			Button closeBtn = (Button) dialog.findViewById(R.id.close_btn);
			final String btnValue = yesBtn.isChecked() ? "Y" : "N";
			nextBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					progressDialog = ProgressDialog.show(CustomNiceActivity.this,"", "로드중입니다.", true);
					String sSmsInfo = HMTrans.trim(smsinfo.getText());
					Thread certNice = new Thread(new CheckSmsCert(sSmsInfo, btnValue));
					certNice.start();
					dialog.dismiss();
				}
				
			});
			
			closeBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			
		}
	}
}
