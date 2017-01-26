package common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@CustomAnnotation()
public class CommonUtil {
	public static String GET = "GET";
	public static String POST = "POST";
	public static String typeXML = "application/xml";
	public static String typeJSON = "application/json";
	public static String typeHTML = "text/html";
	public static String requestPostForm = "application/x-www-form-urlencoded";
	public static String commonUserAgent = "CUSTOM_USER_AGENT";
	@CustomAnnotation("test")
	public static URLConnection creatUrlConnection(String urlStr) throws IOException{
		return creatUrlConnection(urlStr, false, false, null, commonUserAgent);
	}

	
	public static URLConnection creatUrlConnection(String urlStr, Properties param) throws IOException{
		return creatUrlConnection(urlStr, false, true, param, commonUserAgent);
	}

	
	public static URLConnection creatUrlConnection(String urlStr, String userAgent) throws IOException{
		return creatUrlConnection(urlStr, false, false, null, userAgent);
	}

	
	public static URLConnection creatUrlConnection(String urlStr, Properties param, String userAgent) throws IOException{
		return creatUrlConnection(urlStr, false, true, param, userAgent);
	}

	
	public static URLConnection creatUrlConnection(String urlStr, boolean useCache, boolean isPost, Properties param, String userAgent) throws IOException{
		URLConnection urlConnection = null;
		urlConnection = createUrlConnection(urlStr, useCache, isPost, param, userAgent);
		getCookieStr(urlConnection);
		return urlConnection;
	}
	
	private static URLConnection createUrlConnection(String urlStr, boolean useCache, boolean isPost, Properties param, String userAgent) throws IOException{
		URLConnection urlConnection = null;
		URL url = null;
		String paramStr = encodedParamStr(param);
		if (isPost) {
			url = new URL(urlStr);
		} else {
			url = new URL(urlStr+"?"+paramStr);
		}
		urlConnection = url.openConnection();
		urlConnection.setConnectTimeout(5000);
		
		
		// cache usage
		if(!useCache) urlConnection.setRequestProperty("Cache-Control", "no-cache");
		urlConnection.setUseCaches(useCache);
		// user-agent setting
		if(userAgent == null || userAgent.trim().equals("")) 
			urlConnection.setRequestProperty("User-Agent", commonUserAgent);
		else urlConnection.setRequestProperty("User-Agent", userAgent);
		
		// get value from server
		urlConnection.setDoInput(true);
		
		// post method setting
		if (isPost) {
			urlConnection.setDoOutput(true);
			urlConnection.setRequestProperty("Content-Type", requestPostForm);
			OutputStream out = urlConnection.getOutputStream();
			out.write(paramStr.getBytes("UTF-8"));
			out.flush();
			out.close();
		}
		
		return urlConnection;
	}

	private static String encodedParamStr(Properties param) {
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
	
	public static String getCookieStr(URLConnection conn) {
		String cookieStr = "";
		if (conn == null) return cookieStr;
		Map<String, List<String>> headers = conn.getHeaderFields();
		if (headers == null) return cookieStr;
		List<String> cookies = headers.get("Set-Cookie");
		if (cookies == null || cookies.size() < 1) return cookieStr;
		for(String tmpCookie : cookies) {
			cookieStr += tmpCookie + ";";
		}
		cookieStr = cookieStr.substring(0, cookieStr.length()-1);
		
		return cookieStr;
	}
	
	public static void setCookieStr(List<String> cookies) {
		return;
	}
	
	public static boolean checkConnect(HttpURLConnection conn) {
		
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
			e.printStackTrace();
		}
		return isServerSuccess;
	}
	public static void closeInputStream(InputStream in) {
		try {
			if (in != null)
			in.close();
		} catch (IOException e) {}
	}
}
