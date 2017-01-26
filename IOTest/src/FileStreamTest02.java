import java.io.FileOutputStream;
import java.io.IOException;


public class FileStreamTest02 {
public static void main(String[] args) {
	FileOutputStream fos = null;
	try {
		fos = new FileOutputStream("D:\\\\a2.dat");
		byte[] b = new byte[] {72,101,108,108,111};
		fos.write(b);
		System.out.println("created.");
		
	}catch (IOException e) {
		e.printStackTrace();
	}finally {
		if (fos != null) {
			try{
				fos.close();
			}catch (IOException e) {
				// TODO: handle exception
			}
		}
	}
}
}
