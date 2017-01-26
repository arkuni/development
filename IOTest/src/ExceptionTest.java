import common.util.HMException;


public class ExceptionTest {
	public static void main(String[] args) {
		try {
			int no2 = 3214;
			float no3 = 332.3131f;
			double no4 = -332132.3134211d;
			long no5 = 87863134211l;
			throw new HMException(HMException.ExceptionCD.USER_DEFINED, "test");
		} catch (HMException e) {
			
		}
		return;
	}
}
