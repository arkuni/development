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

/**
 * HttpURLConnection 클래스를 이용하여 연결을 위해 만든 클래스
 * @author arkuni
 * 
 */
public class MakeUrlConnection {
	private URLConnection urlConnection = null;
	private ThreadPoolExecutor threadPool;
	private String commonUserAgent = "URLConnection";
	private String userAgent = "";
	private String cookieStr = "";
	private final String requestPostForm = "application/x-www-form-urlencoded";
	private boolean useCache = false;

	/**
	 * Default Construct : empty user-agent, empty cookie, not use cache
	 */
	public MakeUrlConnection() {
		this("",  "", false);
	}

	/**
	 * user-agent, cookie, cache setting 
	 * @param cookieStr
	 * @param userAgent
	 * @param useCache
	 */
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
	
	/**
	 * 생성된 URLConnection을 리턴한다.
	 * @return URLConnection
	 */
	public URLConnection getUrlConnection(){
		return urlConnection;
	}
	/**
	 * URLConnection 객체를 생성하고 post방식으로 통신한 후 사용자가 지정한 클래스 타입으로 리턴받는다.
	 * @param urlTxt URL String
	 * @param param Parameter String
	 * @param type return type (JSONObject, XmlPullParser, String)
	 * @return Object casted by Class<T>
	 */
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
	
	/**
	 * URLConnection 객체를 생성하고 get 방식으로 통신한 후 사용자가 지정한 클래스 타입으로 리턴받는다.
	 * @param urlTxt URL String
	 * @param type return type (JSONObject, XmlPullParser, String)
	 * @return Object casted by Class<T>
	 */
	public <T> T get(String urlTxt, Class<T> type) {
		return get(urlTxt, "", type);
	}
	
	
	/**
	 * URLConnection 객체를 생성하고 get 방식으로 통신한 후 사용자가 지정한 클래스 타입으로 리턴받는다.
	 * @param urlTxt URL String
	 * @param param Parameter String
	 * @param type return type (JSONObject, XmlPullParser, String)
	 * @return return type 
	 */
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
		

	/**
	 * Properties의 파라미터를 String으로 변환한다.
	 * @param param
	 * @return String
	 */
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
	
	/**
	 * URLConnection의 쿠키스트링을 리턴한다.
	 * @return String
	 */
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

	/**
	 * HTTP연결이 잘되었는지 확인한다. 200, 304 이외에는 false.
	 * @param conn
	 * @return boolean
	 */
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
	
	/**
	 * URLConnection를 생성하기 위한 클래스
	 * @author arkuni
	 * @param <T>
	 */
	private class UrlConnectionThread<T> implements Callable<T> {
		private Class<T> type;
		private URL url;
		
		/**
		 * @param url
		 * @param isPost
		 * @param type
		 * 
		 */
		public UrlConnectionThread(URL url, boolean isPost, Class<T> type) {
			this(url, "", isPost, type);
		}
		/**
		 * URL정보, Parameter String, method 방식, return 타입을 받아서 URLConnection을 생성한다.
		 * @param url
		 * @param paramStr
		 * @param isPost
		 * @param type
		 */
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
		
		/* (non-Javadoc)
		 * @see java.util.concurrent.Callable#call()
		 */
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
		
		/**
		 * HTTP통신을 처리하고 응답값 String을 리턴한다.
		 * @return String
		 * @throws IOException
		 */
		private String processHttpsUrlConnection() throws IOException{
			HttpsURLConnection conn = (HttpsURLConnection)urlConnection;
			String line = "";
			boolean isConnectSuccess = checkConnect(conn);
			if(!isConnectSuccess) throw new IOException("fail connect");
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()), 8);
			StringBuffer bf = new StringBuffer();
			while ((line=reader.readLine())!= null) {
				bf.append(line);
			}
			conn.disconnect();
			return bf.toString();
		}
		
		/**
		 * HTTPS통신을 처리하고 응답값 String을 리턴한다.
		 * @return String
		 * @throws IOException
		 */
		private String processHttpUrlConnection() throws IOException{
			HttpURLConnection conn = (HttpURLConnection)urlConnection;
			String line = "";
			boolean isConnectSuccess = checkConnect(conn);
			if(!isConnectSuccess) throw new IOException("fail connect");
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
