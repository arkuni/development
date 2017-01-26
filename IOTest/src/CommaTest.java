import common.util.HMTrans;


public class CommaTest {
	public static void main(String[] args) {
		String no1 = "-3214521395032432.0321094129412";
		int no2 = 3214;
		float no3 = 332.3131f;
		double no4 = -332132.3134211d;
		long no5 = 87863134211l;
		
		System.out.println("no1 : "+ HMTrans.comma(no1));
		System.out.println("no2 : "+ HMTrans.comma(no2));
		System.out.println("no3 : "+ HMTrans.comma(no3));
		System.out.println("no4 : "+ HMTrans.comma(no4));
		System.out.println("no5 : "+ HMTrans.comma(no5));
	}
}
