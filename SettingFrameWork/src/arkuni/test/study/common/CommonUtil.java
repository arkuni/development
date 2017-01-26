package arkuni.test.study.common;

import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.os.Build;
import android.util.Log;

public class CommonUtil {
	private static final String DEBUG_TAG = "CommonUtil";
	private static ThreadSafeClientConnManager httpConnManager;
	private static HttpParams httpInitParams;
	
	public static HttpClient createHttpClient() {
		if (httpInitParams != null && httpConnManager != null) {
			return new DefaultHttpClient(httpConnManager, httpInitParams);
		}
	    
		httpInitParams = new BasicHttpParams();
		httpInitParams.setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
		ConnManagerParams.setMaxTotalConnections(httpInitParams, 6);
		ConnManagerParams.setTimeout(httpInitParams, 5000);
		ConnPerRouteBean connsPerRout = new ConnPerRouteBean(2);
		
		HttpHost targetHost = new HttpHost(Constant.HOST_URL, Constant.HTTP_PORT_NO);
		connsPerRout.setMaxForRoute(new HttpRoute(targetHost), 3);
		
		ConnManagerParams.setMaxConnectionsPerRoute(httpInitParams, connsPerRout);
		HttpProtocolParams.setVersion(httpInitParams, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(httpInitParams, HTTP.UTF_8);
		HttpProtocolParams.setUserAgent(httpInitParams, Constant.APP_NAME+"/Android(" + Build.MODEL+";"+Build.BRAND + ")");
		
	    SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), Constant.HTTP_PORT_NO));
		registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), Constant.SSL_PORT_NO));
		
		httpConnManager = new ThreadSafeClientConnManager(httpInitParams, registry);
		return new DefaultHttpClient(httpConnManager, httpInitParams);
	}
	
	public static void releaseConnection(HttpClient httpClient) {
		httpClient.getConnectionManager().closeExpiredConnections();
	}
	
	public static void shutdownHttpClient(HttpClient httpClient) {
		if (httpConnManager != null) {
			httpConnManager.shutdown();
			Log.i(DEBUG_TAG, "connection shutdown");
		}
	}
}
