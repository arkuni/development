import java.io.FileInputStream;
import java.io.IOException;


public class FileStreamTest03 {
	public static void main(String[] args) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream("D:\\\\a2.dat");
			int i = 0;
			byte[] b = new byte[1024];
			while ((i=fis.read(b)) != -1) {
				for ( int j=0; j<i; j++){
					System.out.println((char)b[j]);
				}
			}
		}catch (IOException e) {
			e.printStackTrace();

		}finally {
			if (fis !=null) {
				try {
					fis.close();
				}catch (IOException e) {
				}
			}
		}
	}
}
