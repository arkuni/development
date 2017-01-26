import java.io.File;


public class IOTest {
	public static void main(String[] args) {
		File f = new File("D:\\download");
		File[] fs = f.listFiles();
		for (int i=0; i<fs.length; i++) {
			long size = fs[i].length() / 1024;
			System.out.println(fs[i].getName() +": " + size + "KB");
		}
	}
}
