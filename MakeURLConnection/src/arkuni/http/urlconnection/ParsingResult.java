package arkuni.http.urlconnection;

import java.io.StringReader;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class ParsingResult<T>{
	private String txt;
	private Class<T> type;
	public ParsingResult(String txt, Class<T> type) {
		this.txt = txt;
		this.type = type;
	}
	
	public T getData() {
		if (type.equals(JSONObject.class)) return type.cast(parsingJSON());
		//if (type.equals(XmlPullParser.class)) return type.cast(parsingXML());
		if (type.equals(String.class)) return type.cast(txt);
		return null;
	}
	
	private Object parsingJSON() {
		JSONObject rslt = null;
		rslt = JSONObject.fromObject(JSONSerializer.toJSON(txt));
		return rslt;
	}
/*
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
*/
}
