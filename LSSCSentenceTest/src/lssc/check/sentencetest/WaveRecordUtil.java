package lssc.check.sentencetest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Environment;
import android.util.Log;

import common.util.HMTrans;

public class WaveRecordUtil implements Constants{
	private static final int WAVE_CHANNEL_MONO = 1;  //wav 파일 헤더 생성시 채널 상수값
	private static final int HEADER_SIZE = 0x2c;
	private static final int RECORDER_BPP = 16;
	private static final int RECORDER_SAMPLERATE = DEFAULT_SAMPLE_RATE;
	public static void combineWaveFile(String resultName, String path) {
		final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;  
		final int RECORDER_CHANNELS = AudioFormat.CHANNEL_CONFIGURATION_MONO;  //안드로이드 녹음시 채널 상수값
		
		BufferedOutputStream mBOStream;
		BufferedInputStream mBIStream;
		int BUFFER_SIZE;
		
		BUFFER_SIZE = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
		byte[] buffer = new byte[BUFFER_SIZE];
		int mAudioLen = 0;
		if(HMTrans.trim(path).equals("")) return;
		
		File recPath = new File(Environment.getExternalStorageDirectory()+"/lssc_test/"+path+"/");
		if(!recPath.isDirectory()) {
			return;
		}
		path = Environment.getExternalStorageDirectory()+"/lssc_test/"+path+"/";
		
		File tmpFiles [] = recPath.listFiles();
		if (tmpFiles == null || tmpFiles.length < 1) {
			return;
		}
		File mergeTempFile = new File(path+"merge.bak");
		
		try {
			int read = 0;
			mBOStream = new BufferedOutputStream(new FileOutputStream(mergeTempFile));
			int existFileCnt = 0;
			for(int i=0; i<tmpFiles.length; i++) {
				File tmpFile = tmpFiles[i];
				if (tmpFile ==null || !tmpFile.isFile()) continue;
				String tmpFileNm = tmpFile.getName();
				Log.d("WaveRecordUtil", "file name : " + tmpFileNm);
				if (Pattern.matches("^result[0-9]+\\.wav$", tmpFileNm)) {
					Log.d("WaveRecordUtil", "exist file cnt : " + existFileCnt);
					existFileCnt++;
					continue;
				}
				if (!Pattern.matches("^[0-9]+record_temp\\.bak$", tmpFileNm)) continue;

				mAudioLen += (int)tmpFile.length();
				
				Log.d("WaveRecordUtil", "file size : " + tmpFile.length());
				Log.d("WaveRecordUtil", "sum  size : " + mAudioLen);
				
				
				mBIStream = new BufferedInputStream(new FileInputStream(tmpFile));
				while ((read = mBIStream.read(buffer)) != -1) {
					mBOStream.write(buffer);
				}
				
				mBIStream.close();
				
				tmpFile.delete();

			}
			mBOStream.flush();
			mBOStream.close();
			
			
			File finalResultRecordFile = new File(path+"result"+existFileCnt+".wav");
			
			Log.d("WaveRecordUtil", "result file name : " + "result"+existFileCnt+".wav");
			mBOStream = new BufferedOutputStream(new FileOutputStream(finalResultRecordFile));
			mBOStream.write(getFileHeader(mAudioLen));
			mBIStream = new BufferedInputStream(new FileInputStream(mergeTempFile));
			while ((read = mBIStream.read(buffer)) != -1) {
				mBOStream.write(buffer);
			}
			mBIStream.close();
			mBOStream.flush();
			mBOStream.close();
			
			mergeTempFile.delete();
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} 
		
		//FLAC_FileEncoder flacEncoder = new FLAC_FileEncoder();
		//File outputFile = new File(path+"result.flac");
		//flacEncoder.encode(finalResultRecordFile, outputFile);
		//finalResultRecordFile.delete();
	}

	private static byte[] getFileHeader(int mAudioLen) {
		

		byte[] header = new byte[HEADER_SIZE];
		int totalDataLen = mAudioLen + 40;
		long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * WAVE_CHANNEL_MONO/8;
		header[0] = 'R';  // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f';  // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = (byte)1;  // format = 1 (PCM방식)
		header[21] = 0;
		header[22] =  WAVE_CHANNEL_MONO;
		header[23] = 0;
		header[24] = (byte) (RECORDER_SAMPLERATE & 0xff);
		header[25] = (byte) ((RECORDER_SAMPLERATE >> 8) & 0xff);
		header[26] = (byte) ((RECORDER_SAMPLERATE >> 16) & 0xff);
		header[27] = (byte) ((RECORDER_SAMPLERATE >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) RECORDER_BPP * WAVE_CHANNEL_MONO/8;  // block align
		header[33] = 0;
		header[34] = RECORDER_BPP;  // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte)(mAudioLen & 0xff);
		header[41] = (byte)((mAudioLen >> 8) & 0xff);
		header[42] = (byte)((mAudioLen >> 16) & 0xff);
		header[43] = (byte)((mAudioLen >> 24) & 0xff);
		return header;
	}
	
	public static void removeFile(String path) {
		if(HMTrans.trim(path).equals("")) return;
		path = Environment.getExternalStorageDirectory()+"/lssc_test/"+path+"/";
		
		File recPath = new File(path);
		if(!recPath.isDirectory()) {
			return;
		}
		
		File tmpFiles [] = recPath.listFiles();
		if (tmpFiles == null || tmpFiles.length < 1) {
			return;
		}
		
		for(int i=0; i<tmpFiles.length; i++) {
			File tmpFile = tmpFiles[i];
			if (tmpFile ==null || !tmpFile.isFile()) return;
			String tmpFileNm = tmpFile.getName();
			if (!Pattern.matches("^[0-9]+record_temp\\.bak$", tmpFileNm)) return;
			
			tmpFile.delete();

		}		
	}
}
