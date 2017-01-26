import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class FileStreamTest07 {
	public static void main(String[] args) {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			long startTime = System.currentTimeMillis();
			System.out.println("###### start");
			bis = new BufferedInputStream(new FileInputStream("D:\\\\MyData\\download\\20140601_흔들리지않는믿음_고린도전서15장_진두원목사.mp3"));
			bos = new BufferedOutputStream(new FileOutputStream("D:\\\\test.mp3"));
			int i;
			byte[] b = new byte[1024];
			while((i=bis.read(b))!= -1) {
				bos.write(b,0,i);
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
