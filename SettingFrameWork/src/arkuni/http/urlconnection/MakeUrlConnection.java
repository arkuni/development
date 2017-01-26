package arkuni.http.urlconnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import javax.net.ssl.HttpsURLConnection;

public class MakeUrlConnection {
	public static final int JSON = 1;
	public static final int XML = 2;
	public static final int PLAIN = 3;
	private URLConnection urlConnection = null;
	private ThreadPoolExecutor threadPool;
	private String commonUserAgent = "URLConnection";
	private String userAgent = "";
	private String cookieStr = "";
	private final String requestPostForm = "application/x-www-form-urlencoded";
	private boolean useCache = false;

	public MakeUrlConnection() {
		this("",  "", false);
	}

	public MakeUrlConnection(String cookieStr, String userAgent, boolean useCache) {
		this.cookieStr = cookieStr;
		this.userAgent = userAgent.equals("") ? commonUserAgent : userAgent;
		threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		
		//urlConnection.setRequestProperty("Accept", "application/json");
		//urlConnection.setRequestProperty("Accept", "application/xml");
		//urlConnection.setRequestProperty("Content-Type", "application/xml");
		//urlConnection.setRequestProperty("Content-Type", "application/json");
		//urlConnection.setRequestProperty("Content-Type", "text/html");
	}
	
	public URLConnection getUrlConnection(){
		return urlConnection;
	}
	public <T> T post(String urlTxt, String param, Class<T> type) {
		try {
			URL url = new URL(urlTxt);
			Future<T> result = threadPool.submit(new UrlConnectionThread<T>(url, param, true, type));
			return result.get();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public <T> T get(String urlTxt, Class<T> type) {
		return get(urlTxt, "", type);
	}
	
	
	public <T> T get(String urlTxt, String param, Class<T> type) {
		String urlStr = "";
		if (param != null && !param.equals("")) urlStr = urlTxt+"?"+param;
		else urlStr = urlTxt;
		try {
			URL url = new URL(urlStr);
			Future<T> result = threadPool.submit(new UrlConnectionThread<T>(url, false, type));
			return result.get();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
		

	public String encodedParamStr(Properties param) {
		if (param == null || param.isEmpty()) return "";
		StringBuilder sb = new StringBuilder(256);
		Enumeration<?> names = param.propertyNames();
		while(names.hasMoreElements()) {
			String name = (String)names.nextElement();
			String val = param.getProperty(name);
			sb.append(name);
			sb.append("=");
			try {
				sb.append(URLEncoder.encode(val, "UTF-8"));
			}catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				sb.append(val);
			}
			if(names.hasMoreElements()) sb.append("&");
			
		}
		return sb.toString();
	}
	
	public String getCookieStr() {
		cookieStr = "";
		if (urlConnection == null) return cookieStr;
		Map<String, List<String>> headers = urlConnection.getHeaderFields();
		if (headers == null) return cookieStr;
		List<String> cookies = headers.get("Set-Cookie");
		if (cookies == null || cookies.size() < 1) return cookieStr;
		for(String tmpCookie : cookies) {
			cookieStr += tmpCookie + ";";
		}
		cookieStr = cookieStr.substring(0, cookieStr.length()-1);
		
		return cookieStr;
	}

	public boolean checkConnect(HttpURLConnection conn) {
		
		boolean isServerSuccess = false;
		try {
			int responseCode = conn.getResponseCode();
			switch (responseCode) {
			case HttpURLConnection.HTTP_OK:
				isServerSuccess = true;
				break;
			case HttpURLConnection.HTTP_NOT_MODIFIED:
				isServerSuccess = true;
				break;
			default:
				break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isServerSuccess;
	}
	private class UrlConnectionThread<T> implements Callable<T> {
		private Class<T> type;
		private URL url;
		public UrlConnectionThread(URL url, boolean isPost, Class<T> type) {
			this(url, "", isPost, type);
		}
		public UrlConnectionThread(URL url, String paramStr, boolean isPost, Class<T> type) {
			this.type = type;
			this.url = url;
			try {
				urlConnection = url.openConnection();
				urlConnection.setConnectTimeout(5000);
				urlConnection.setRequestProperty("User-Agent", userAgent);
				urlConnection.setDoInput(true);
				if (cookieStr != null && !cookieStr.equals("") && cookieStr.indexOf("=") > -1) urlConnection.setRequestProperty("Cookie", cookieStr);
				if(!useCache) urlConnection.setRequestProperty("Cache-Control", "no-cache");
				if(isPost) {
					urlConnection.setDoOutput(true);
					urlConnection.setRequestProperty("Content-Type", requestPostForm);
					OutputStream out = urlConnection.getOutputStream();
					out.write(paramStr.getBytes("UTF-8"));
					out.flush();
					out.close();
				}
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		@Override
		public T call() {
			ParsingResult<T> resultObj = null;
			
			String totaldata = "";
			try {
				
				if (url.getProtocol().equalsIgnoreCase("https")) {
					totaldata = processHttpsUrlConnection();
				} else {
					totaldata = processHttpUrlConnection();
				}
				

				resultObj = new ParsingResult<T>(totaldata, type);
			} catch (IOException e) {
				e.printStackTrace();
			}

			return resultObj.getData();
		}
		private String processHttpsUrlConnection() throws IOException{
			HttpsURLConnection conn = (HttpsURLConnection)urlConnection;
			String line = "";
			boolean isConnectSuccess = checkConnect(conn);

			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()), 8);
			StringBuffer bf = new StringBuffer();
			while ((line=reader.readLine())!= null) {
				bf.append(line);
			}
			conn.disconnect();
			return bf.toString();
		}
		
		private String processHttpUrlConnection() throws IOException{
			HttpURLConnection conn = (HttpURLConnection)urlConnection;
			String line = "";
			boolean isConnectSuccess = checkConnect(conn);

			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()), 8);
			StringBuffer bf = new StringBuffer();
			while ((line=reader.readLine())!= null) {
				bf.append(line);
			}
			conn.disconnect();
			return bf.toString();
		}
	}
}
