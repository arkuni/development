package arkuni.http.urlconnection;

import org.json.JSONException;
import org.json.JSONObject;

public class UrlConnectionTest {
	public static void main(String[] args) {
		MakeUrlConnection urlConnection = new MakeUrlConnection();
		JSONObject result = urlConnection.get("http://tfinder.happymoney.co.kr/app/store_position_count.hm", JSONObject.class);
		try {
			System.out.println(result.get("message"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
