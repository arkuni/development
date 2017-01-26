package arkuni.http.urlconnection;

import java.io.StringReader;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class ParsingResult<T>{
	private String txt;
	private Class<T> type;
	public ParsingResult(String txt, Class<T> type) {
		this.txt = txt;
		this.type = type;
	}
	
	public T getData() {
		if (type.equals(JSONObject.class)) return type.cast(parsingJSON());
		if (type.equals(XmlPullParser.class)) return type.cast(parsingXML());
		if (type.equals(String.class)) return type.cast(txt);
		return null;
	}
	
	private Object parsingJSON() {
		JSONObject rslt = null;
		try {
			rslt = new JSONObject(txt);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return rslt;
	}
	
	private Object parsingXML() {
		StringReader txtReader = new StringReader(txt);
		XmlPullParser parser = null;
		try {
			XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
			parser = parserCreator.newPullParser();
			parser.setInput(txtReader);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return parser;
	}
}
