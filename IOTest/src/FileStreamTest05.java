import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class FileStreamTest05 {
	public static void main(String[] args) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			long startTime = System.currentTimeMillis();
			System.out.println("###### start");
			fis = new FileInputStream("D:\\\\MyData\\download\\20140601_흔들리지않는믿음_고린도전서15장_진두원목사.mp3");
			fos = new FileOutputStream("D:\\\\test.mp3");
			int i;
			byte[] b = new byte[1024];
			while((i=fis.read(b))!= -1) {
				fos.write(b);
			}
			long endTime = System.currentTimeMillis();
			System.out.println("time : " + (endTime - startTime));
			System.out.println("###### end");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis !=null) {
				try{
					fis.close();
				} catch (IOException e) {}
			}
			if (fos !=null) {
				try{
					fos.close();
				} catch (IOException e) {}
			}
		}
	}
}
