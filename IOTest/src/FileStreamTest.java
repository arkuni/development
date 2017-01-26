import java.io.FileOutputStream;
import java.io.IOException;



public class FileStreamTest {
	public static void main(String[] args) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("D:\\\\a.dat");
			fos.write(72);
			fos.write(101);
			fos.write(108);
			fos.write(111);
			System.out.println("created.");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				}catch (IOException e){	}
			}
		}
	}
}
