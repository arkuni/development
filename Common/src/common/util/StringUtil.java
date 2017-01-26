package common.util;

import java.io.UnsupportedEncodingException;

public class StringUtil {
	public static String cutKorStr(String szText, String szKey, int nLength, boolean isAdddot) {
		String rsltStr = szText;
	    int oF = 0, oL = 0, rF = 0, rL = 0; 
	    int nLengthPrev = 0;
	    
	    //Pattern p = Pattern.compile("<(/?)([^<>]*)?>", Pattern.CASE_INSENSITIVE);  // 태그제거 패턴
	    //if(isNotag) {rsltStr = p.matcher(rsltStr).replaceAll("");}  // 태그 제거
	    
	    rsltStr = rsltStr.replaceAll("&amp;", "&");
	    rsltStr = rsltStr.replaceAll("(!/|\r|\n|&nbsp;)", "");  // 공백제거
	 
	    try {
	      byte[] bytes = rsltStr.getBytes("UTF-8");     // 바이트로 보관
	      if(szKey != null && !szKey.equals("")) {
	        nLengthPrev = (rsltStr.indexOf(szKey) == -1)? 0: rsltStr.indexOf(szKey);  // 일단 위치찾고
	        nLengthPrev = rsltStr.substring(0, nLengthPrev).getBytes("MS949").length;  // 위치까지길이를 byte로 다시 구한다
	        //nLengthPrev = (nLengthPrev-nPrev >= 0)? nLengthPrev-nPrev:0;    // 좀 앞부분부터 가져오도록한다.
	      }
	    
	      // x부터 y길이만큼 잘라낸다. 한글안깨지게.
	      int j = 0;
	      if(nLengthPrev > 0) while(j < bytes.length) {
	        if((bytes[j] & 0x80) != 0) {
	          oF+=2; rF+=3; if(oF+2 > nLengthPrev) {break;} j+=3;
	        } else {if(oF+1 > nLengthPrev) {break;} ++oF; ++rF; ++j;}
	      }
	      
	      j = rF;
	      while(j < bytes.length) {
	        if((bytes[j] & 0x80) != 0) {
	          if(oL+2 > nLength) {break;} oL+=2; rL+=3; j+=3;
	        } else {if(oL+1 > nLength) {break;} ++oL; ++rL; ++j;}
	      }
	      rsltStr = new String(bytes, rF, rL, "UTF-8");  // charset 옵션
	      if(isAdddot && rF+rL+3 <= bytes.length) {rsltStr+="...";}  // ...을 붙일지말지 옵션 
	    } catch(UnsupportedEncodingException e){ e.printStackTrace(); }   
	    
		return rsltStr;
	}
}
