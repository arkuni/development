import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class FileStreamTest08 {
	public static void main(String[] args) {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			long startTime = System.currentTimeMillis();
			bis = new BufferedInputStream(new FileInputStream("D:\\\\download\\20120916_¼ÒÀÚ¼·P(¸¶¼®).mp3"));
			bos = new BufferedOutputStream(new FileOutputStream("D:\\\\test2.mp3"));
			int i;
			
			byte[] b = new byte[1024];
			while((i=bis.read(b))!= -1) {
				bos.write(b);
			}
			
			long endTime = System.currentTimeMillis();
			System.out.println("time : " + (endTime - startTime));

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
