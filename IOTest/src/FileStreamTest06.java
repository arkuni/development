import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class FileStreamTest06 {
	public static void main(String[] args) {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			long startTime = System.currentTimeMillis();
			System.out.println("###### start");
			bis = new BufferedInputStream(new FileInputStream("D:\\\\MyData\\download\\20140601_��鸮���ʴ¹���_��������15��_���ο����.mp3"));
			bos = new BufferedOutputStream(new FileOutputStream("D:\\\\test.mp3"));
			int i;
			while((i=bis.read())!= -1) {
				bos.write(i);
			}
			long endTime = System.currentTimeMillis();
			System.out.println("time : " + (endTime - startTime));
			System.out.println("###### end");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bis !=null) {
				try{
					bis.close();
				} catch (IOException e) {}
			}
			if (bos !=null) {
				try{
					bos.close();
				} catch (IOException e) {}
			}
		}
	}
}
