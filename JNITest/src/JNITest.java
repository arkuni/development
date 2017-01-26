public class JNITest {
	static {
		System.loadLibrary("libJNITest");
	}
	public native void fileCopy(String from, String to);
	
	public static void main(String[] args) {
		new JNITest().fileCopy("D:\\\\download\\tmp.blk","D:\\\\download\\tmp1.tmp");
	}
}
