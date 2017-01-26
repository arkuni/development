package filedown;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class FileDownThread extends Thread{
	private String filelist;
	public FileDownThread(String filelist) {
		this.filelist = filelist;
	}
	
	public void run() {
		FileDownCore fileTest1 = new FileDownCore();
		try {
			File file =  new File(filelist);
			BufferedReader in =  new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
			String s;
			while((s=in.readLine()) !=  null){
				fileTest1.downloadFile(s.trim());
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
