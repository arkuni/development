package filedown;

import java.io.BufferedWriter;
import java.io.IOException;

public class FileDown extends Thread{
	BufferedWriter fileList = null;
	
	public static void main(String[] args) throws IOException{
		FileDownThread fd1 = new FileDownThread("D:\\2013\\file-list\\2013_file_b1.txt");
		FileDownThread fd2 = new FileDownThread("D:\\2013\\file-list\\2013_file_b2.txt");
		FileDownThread fd3 = new FileDownThread("D:\\2013\\file-list\\2013_file_b3.txt");
		FileDownThread fd4 = new FileDownThread("D:\\2013\\file-list\\2013_file_b4.txt");
		FileDownThread fd5 = new FileDownThread("D:\\2013\\file-list\\2013_file_b5.txt");
		fd1.start();
		fd2.start();
		fd3.start();
		fd4.start();
		fd5.start();
	}

}
