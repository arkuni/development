package common.util;


public class HMException extends Exception {
	public enum ExceptionCD {
		EMPTY_SESSION("session is null"),
		CONNECTION_ERROR("connection error"),
		WRONG_VALUE("wrong value"),
		NULL_VALUE("value is null"),
		FAIL_VALUE("value is fail"),
		TOO_SHORT("data length is too short"),
		TOO_LONG("data length is too long"),
		USER_DEFINED("user defined exception"),
		ETC("etc error");
		private String des;
		private ExceptionCD(String des) {
			this.des = des;
		}
		private String getDes() {
			return des;
		}
	}
	private static final long serialVersionUID = 1L;
	
	public HMException() {
		super();
	}

	public HMException(String userMsg) {
		super (userMsg);
	}
	
	public HMException(ExceptionCD cd, String adminMsg, String userMsg) {
		super (cd.getDes()+ ":" +userMsg + "(" + adminMsg + ")");
	}
	
	public HMException(ExceptionCD cd, String userMsg) {
		super (cd.getDes()+ ":" +userMsg);
	}
	  
	public HMException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public HMException(Throwable throwable) {
		super(throwable);
	}
}
