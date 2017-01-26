import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class FileStreamTest04 {
	public static void main(String[] args) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			long startTime = System.currentTimeMillis();
			System.out.println("###### start");
			fis = new FileInputStream("D:\\\\MyData\\download\\20140601_��鸮���ʴ¹���_��������15��_���ο����.mp3");
			fos = new FileOutputStream("D:\\\\test.mp3");
			int i;
			while((i=fis.read())!= -1) {
				fos.write(i);
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
