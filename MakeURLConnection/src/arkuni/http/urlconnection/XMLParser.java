package arkuni.http.urlconnection;

import java.util.HashMap;

public class XMLParser {
	private HashMap<String, Object> map = new HashMap<String, Object>();
	private String [] tagNms = null; 
	private String c = null;
	private int idx = 0;
	private boolean enddoc = false;

	public XMLParser(String txt) {
		parseStr(txt);
	}
	
	public void parseStr(String txt) {
		final int MAX_SEARCH = 100;
		try {
			char check = 0;
			if (txt == null || txt.length() < 1 ) {
				throw syntaxError("xml string null.");
			}
			c = txt.trim();

			check = c.charAt(0);
			if (check != '<') {
				throw syntaxError("invalid xml format");
			}
			check = txt.charAt(txt.length()-1);
			if (check != '>') {
				throw syntaxError("invalid xml format");
			}
			if ((check = next()) == 0) {
				throw syntaxError("invalid xml format");
			}
			if (check == '?') {
				if (next() != 'x' || next() != 'm' || next() != 'l') {
					throw syntaxError("invalid xml format");
				}
				check = skipBlank();
				while (check != '?'&& idx < MAX_SEARCH) {
					if ((check = next()) == 0) break;
				}
				if (enddoc) {
					throw syntaxError("invalid xml format");
				}
				if ((check = next()) == 0) {
					throw syntaxError("invalid xml format");
				}
				if (check != '>') {
					throw syntaxError("invalid xml format " + check);
				}
				if ((check = next()) == 0) {
					throw syntaxError("invalid xml format");
				}
				c = c.substring(idx,c.length());
				idx = 0;
			} else {
				idx = 0;
			}
			
			parsingContents();
			
		} catch (XMLParseException e) {
			e.printStackTrace();
		} finally {
			
		}
	}
	
	private HashMap<String, Object> parsingContents() {
		char ch = skipBlank();
		final int TAG = 1;
		final int DATA = 2;
		final int ATTRIBUTE = 3;
		final int NUM_VALUE = 4;
		final int STR_VALUE = 5;
		final int DEFAULT = -1;
		
		int readtype = DEFAULT;
		int readidx = DEFAULT;

		boolean simpletag = false;
		
		boolean closetag = true;
		
		String openedTagNm = "";
		try {
			if (ch != '<') {
				throw syntaxError("invalid xml format");
			}

			while (ch != 0) {
				switch(ch) {
				case '\n' :
				case '\r' :
				case ' ' :
					if (readtype != STR_VALUE && readtype != DEFAULT) break;
					if ((ch = next()) == 0) break;
					continue;
				case '<' :
					if (readtype != STR_VALUE) {
						if (!closetag) {
							throw syntaxError("invalid xml format");
						}
						if (readidx != DEFAULT) break;
					}
					if ((ch = next()) == 0) break;
					if (ch == '/') {
						if (openedTagNm == null || openedTagNm.length() < 1) {
							throw syntaxError("invalid xml format");
						}
						if (!openedTagNm.equalsIgnoreCase(c.substring(idx+1,idx+openedTagNm.length()+1))) {
							throw syntaxError("not closed tag");
						}
						
						if ((ch = next()) == 0) break;
						readtype = 99;
						readidx = idx;
					} else {
						
						readtype = TAG;
						readidx = idx;
					}
					closetag = false;
					continue;
				case '>' :
					if (readtype != STR_VALUE && c.charAt(idx-1) != '/') {
						if (closetag) {
							throw syntaxError("invalid xml format");
						}
						if (readidx != DEFAULT) break;
					}
					if ((ch = next()) == 0) break;
					readtype = DATA;
					readidx = idx;
					closetag = true;
					continue;
				case '/' :
					if (c.charAt(idx+1) == '>') {
						openedTagNm = pop();
						if ((ch = next()) == 0) break;
						simpletag = true;
						closetag = true;
						continue;
					}
				case '=' :
					if (readidx != DEFAULT) break;
					if ((ch = next()) == 0) break;
					ch = skipBlank();
					if (ch != '"') {
						if ((int)ch < 48 || (int)ch > 57) {
							throw syntaxError("invalid xml format");
						}
						readtype = NUM_VALUE;
					} else {
						if ((ch = next()) == 0) break;
						readtype = STR_VALUE;
					}
					readidx = idx;
					continue;
				case '"' :
					if (readtype != STR_VALUE && readtype != DATA) {
						throw syntaxError("invalid xml format");
					}
					if ((ch = next()) == 0) break;
					break;
					
				default :
					if (readtype == DEFAULT) {
						readtype = ATTRIBUTE;
						readidx = idx;
					}
					
					
					if ((ch = next()) == 0) break;
					continue;
					
				}
				
					
				if (enddoc) break;
				
				if (readtype != DEFAULT) {
					String txt;
					int endidx = idx;
					if (readtype == STR_VALUE) endidx = idx-1;
					
					txt = c.substring(readidx, endidx);
					
					if (readtype == TAG) {
						openedTagNm = txt;
						push(openedTagNm);
					}
					System.out.println(txt);
					readidx = DEFAULT;
					readtype = DEFAULT;
				}
			}
			
		} catch (XMLParseException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	private char skipBlank() {
		char ch = c.charAt(idx);
		while (ch == '\n' || ch == '\r' || ch == ' ' || (int) ch == 0 || ch == 0) {
			ch = next();
		}
		
		return ch;
	}
	
	private boolean more() {
		return ++idx < c.length()-1;
	}
	
	private char next() {
		if (more()) {
			return c.charAt(idx);
		}
		return 0;
	}
	
	private XMLParseException syntaxError(String msg) {
		return new XMLParseException(msg + toString());
	}
	
	public String toString() {
		return " at character " + this.idx + " of " + this.c;
	}
	
	private String [] resizeArr(int n) {
		String [] tmp = null;
		int len = n;
		if (tagNms != null && tagNms.length > 0) {
			len = tagNms.length + n;
			tmp = new String[len];
			for (int i=0; i<tagNms.length && i<len; i++) {
				tmp[i] = tagNms[i];
			}
		} else {
			tmp = new String[len];
		}
		return tmp;
	}
	
	private void push(String tag) {
		tagNms = resizeArr(1);
		tagNms[tagNms.length-1] = tag;
	}
	
	private String pop() {
		tagNms = resizeArr(-1);
		return tagNms[tagNms.length-1];
	}
}
