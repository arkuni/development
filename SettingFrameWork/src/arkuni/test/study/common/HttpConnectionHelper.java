package arkuni.test.study.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

public class HttpConnectionHelper {
	private static final String DEBUG_TAG = "HttpConnectionHelper";
	
	public static HttpResponse httpExcute(String url, ArrayList<BasicNameValuePair> nameValuePairs) {
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		HttpResponse httpResponse = null;
		try {
			httpClient = CommonUtil.createHttpClient();
			httpPost = new HttpPost(url);
			UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
			httpPost.setEntity(entityRequest);
			httpResponse = httpClient.execute(httpPost);
		} catch(Exception e) {
			e.printStackTrace();
			Log.e(DEBUG_TAG, "excute error");
		} finally {
			if ( httpPost != null) httpPost.abort();
			if ( httpClient != null) CommonUtil.releaseConnection(httpClient);
		}
		return httpResponse;
	}
	
	public static ArrayList<HashMap<String, String>> parsingXmlToArrayList(HttpEntity entityResponse, String startTag) {
		ArrayList<HashMap<String, String>> tmpList = null;
		InputStream reader = null;
		BufferedReader bufferReader = null;
		try {
			reader = entityResponse.getContent();
			
			bufferReader =  new BufferedReader(new InputStreamReader(entityResponse.getContent(), "UTF-8"), 8);
			
			
			String line = "";
			StringBuffer bf = new StringBuffer();
			while ((line=bufferReader.readLine())!= null) {
	   			bf.append(line);
	   		}
			
			String totaldata = HMTrans.trim(bf);
			totaldata = totaldata.replaceAll("&", "&amp;");
			tmpList = new ArrayList<HashMap<String,String>>();
	       	XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
	       	XmlPullParser parser = parserCreator.newPullParser();
	       	parser.setInput(new StringReader(totaldata));
	   		int parserEvent = parser.getEventType();
	   		int beforeType = 0;
	   		String beforeData = "";
	   		HashMap<String, String> tmp = null;
	   		while (parserEvent != XmlPullParser.END_DOCUMENT ){
	   			String tag = "";
	   			String tagnm = "";
	
	   			tag = parser.getText();
	   			tagnm = parser.getName();
	   			if (tag == null) tag = "";
	   			else tag = tag.trim();
	   			if (tagnm == null) tagnm = "";
	   			else tagnm = tagnm.trim();
	   			
	   			
	   			switch(beforeType){
	    		case 1:
	    			break;
	    		case 2:
	    			switch(parserEvent){
		    		case XmlPullParser.START_TAG:
		    			break;
		    		case XmlPullParser.TEXT:
		    			break;
		    		case XmlPullParser.END_TAG:
		    			if (!beforeData.equals("")) {
			    			if (tmp != null) tmp.put(tagnm, beforeData);
		    			}
		    			break;
		       		}
	    			break;
	    		case 3:
	    			break;
	    		case 0:
	    			break;
	       		}
	   			
	   			switch(parserEvent){
	    		case XmlPullParser.START_TAG:
	    			beforeType = 1;
	    			beforeData = tagnm;
	    			if (tagnm.equals(startTag)) tmp = new HashMap<String, String>();
	    			break;
	    		case XmlPullParser.TEXT:
	    			beforeType = 2;
	    			beforeData = tag;
	    			break;
	    		case XmlPullParser.END_TAG:
	    			beforeType = 3;
	    			beforeData = tagnm;
	    			if (tagnm.equals(startTag)) tmpList.add(tmp);
	    			break;
	       		}
	   			
	    		parserEvent = parser.next();
	    	}
	   		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(reader != null) reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return tmpList;
	}
}
