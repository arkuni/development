package filedown;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;

import com.ncloud.filestorage.FSRestClient;
import com.ncloud.filestorage.model.FSFileDownload;
import com.ncloud.filestorage.model.FSResourceID;

public class FileDownCore extends Thread{
	private String localPath = "D:\\2013"; 
	private String accessKey = "itraVsMlWBlSbP7b1ok1"; // accessKey
	private String secretKey = "YtZrWi8Lz0l396TCIA4LAiOJNC9SEZEPD6HozqBs"; //seeretKey
	BufferedWriter fileList = null;
	
	public void downloadFile(String targetContainerAndFolderPath) throws Exception{
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		FSRestClient client;
		
		try {
			
			String temp = targetContainerAndFolderPath.substring(targetContainerAndFolderPath.lastIndexOf("."));
			
			String[] maskingName = targetContainerAndFolderPath.split(temp);
			
			FSRestClient.initialize();
			client = new FSRestClient("restapi.fs.ncloud.com", 80,  accessKey, secretKey);
			FSResourceID container = new FSResourceID("home2/nfs/edin/eds/upload/" + maskingName[0]);
			 
			byte[] readBuffer = new byte[1024];
			
			String fileOrDirectoryPath = localPath+"\\"+targetContainerAndFolderPath;
			bos = new BufferedOutputStream(new FileOutputStream(fileOrDirectoryPath));
			FSFileDownload fileDownload =client.download(container, null);  
			bis = new BufferedInputStream(fileDownload.getInputStream());

			int readSize = 0;
			while ((readSize = bis.read(readBuffer, 0, 1024)) != -1) {
				bos.write(readBuffer, 0, readSize);
			}
		} catch (Exception e) {
			System.out.println("error====" + targetContainerAndFolderPath);
			System.out.println("error====" + e);
		} finally {
			if (null != bis){
				bis.close();
			}
			if (null != bos){
				bos.close();
			}
		}
	}
	
	private void chekcDirAndCreate(String dirPath){
		String[] folder = dirPath.split("\\\\");
		String tempFolder = "";
		for(String dir : folder){
			tempFolder += dir+"\\";
			File directory = new File(tempFolder);
			if( false == directory.exists()){
				directory.mkdir();
			}
		}
	}
}
