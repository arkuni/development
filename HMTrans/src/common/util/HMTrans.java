package common.util;

public class HMTrans {
	public static String trim(String txt) {
		if (txt == null) return "";
		return txt.trim();
	}
	public static String trim(String txt, String alter) {
		String rst = "";
		if (txt == null) return "";
		rst = txt.trim();
		if (rst.equals("")) return alter;
		return rst;
	}
	public static String trim(Object obj) {
		if (obj == null) return "";
		return trim(obj.toString());
	}
	public static int toI(String txt) {
		if (trim(txt).equals("")) return 0;
		return toICore(txt, 0);
	}
	public static int toI(String txt, int num) {
		if (trim(txt).equals("")) return num;
		return toICore(txt, num);
	}
	public static int toICore(String txt, int num) {
		int rst = 0;
		try {
			rst = Integer.parseInt(txt);
		} catch (Exception e) {
			return num;
		}
		return rst;
	}
	public static int toI(float noF) {
		return (int)noF;
	}
	public static int toI(double noD) {
		return (int)noD;
	}
	public static int toI(long noL) {
		return (int)noL;
	}
	public static int toI(Object obj) {
		int rst = 0;
		return rst;
	}
	public static float toF(String txt) {
		if (trim(txt).equals("")) return 0f;
		return toFCore(txt, 0f);
	}
	public static float toF(String txt, float num) {
		if (trim(txt).equals("")) return num;
		return toFCore(txt, num);
	}
	public static float toFCore(String txt, float num) {
		float rst = 0f;
		try {
			rst = Float.parseFloat(txt);
		} catch (Exception e) {
			return num;
		}
		return rst;
	}
	public static float toF(int noI) {
		return (float)noI;
	}
	public static float toF(double noD) {
		return (float)noD;
	}
	public static float toF(long noL) {
		float rst = 0f;
		return rst;
	}
	public static float toF(Object obj) {
		float rst = 0f;
		return rst;
	}
	
	public static double toD(String txt) {
		if (trim(txt).equals("")) return 0d;
		return toDCore(txt, 0d);
	}
	public static double toD(String txt, double num) {
		if (trim(txt).equals("")) return num;
		return toDCore(txt, num);
	}
	public static double toDCore(String txt, double num) {
		double rst = 0d;
		try {
			rst = Double.parseDouble(txt);
		} catch (Exception e) {
			return num;
		}
		return rst;
	}
	public static double toD(int noI) {
		return (double)noI;
	}
	public static double toD(float noD) {
		return (double)noD;
	}
	public static double toD(long noL) {
		double rst = (double)noL;
		return rst;
	}
	public static double toD(Object obj) {
		double rst = Double.parseDouble(trim(obj));
		return rst;
	}
	public static boolean toBCore(String txt, boolean bool) {
		boolean rst = false;
		try {
			if ( trim(txt).equals("1") ||  trim(txt).toLowerCase().equals("true") ) {
				rst = true;
			} else if ( trim(txt).equals("0") ||  trim(txt).toLowerCase().equals("false") ){
				rst = false;
			} else {
				return bool;
			}
		} catch (Exception e) {
			return bool;
		}
		return rst;
	}
	public static boolean toB(String txt) {
		if (trim(txt).equals("")) return false;
		return toBCore(txt, false);
	}
	public static boolean toB(String txt, boolean bool) {
		if (trim(txt).equals("")) return bool;
		return toBCore(txt, bool);
	}
	public static boolean toB(int noI) {
		return noI > 0 ? true : false;
	}
	public static boolean toB(float noD) {
		return noD > 0 ? true : false;
	}
	public static boolean toB(long noL) {
		return noL > 0 ? true : false;
	}
	public static boolean toB(Object obj) {
		return toBCore(trim(obj),false);
	}
	
}
