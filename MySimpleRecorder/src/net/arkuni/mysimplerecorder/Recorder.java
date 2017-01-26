package net.arkuni.mysimplerecorder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class Recorder implements Runnable, Constants{
	private final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;  
	private final int RECORDER_CHANNELS = AudioFormat.CHANNEL_CONFIGURATION_MONO;  //안드로이드 녹음시 채널 상수값
	private final int RECORDER_SAMPLERATE = DEFAULT_SAMPLE_RATE;
	private final int BUFFER_SIZE;
	private final String TEMP_FILE_NAME = "record_temp.bak";

	private AudioRecord mAudioRecord;
	private boolean mIsRecording;
	private String path;
	private BufferedOutputStream mBOStream;
	private int tmpFileCnt = 0;


	public Recorder(String path, int tmpFileCnt) {
		super();
		this.tmpFileCnt = tmpFileCnt;
		this.path = path;
		BUFFER_SIZE = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
		mIsRecording = false;
	}

	@Override
	public void run() {
		mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, RECORDER_SAMPLERATE, RECORDER_CHANNELS,
				RECORDER_AUDIO_ENCODING, BUFFER_SIZE);
		mAudioRecord.startRecording();
		mIsRecording = true;
		//writeAudioDataToFile();

		writeAudioDataToTmpFile();
	}

	private void writeAudioDataToTmpFile() {
		byte[] data = new byte[BUFFER_SIZE];
		if (mAudioRecord == null) return;
		File recPath = new File(path);
		if(!recPath.isDirectory()) {
			recPath.mkdirs();
		}
		
		File tempFile = new File(path+tmpFileCnt+TEMP_FILE_NAME);
		if (tempFile.exists()) tempFile.delete();
		mBOStream = null;
		try {
			mBOStream = new BufferedOutputStream(new FileOutputStream(tempFile));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		int read = 0;
		
		if (null != mBOStream) {
			try {
				while (mIsRecording) {
					if (mAudioRecord == null) break;
					read = mAudioRecord.read(data, 0, BUFFER_SIZE);
					if (AudioRecord.ERROR_INVALID_OPERATION != read) {
						mBOStream.write(data);
					}
				}

				mBOStream.flush();
				mBOStream.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NullPointerException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		Log.d("Recorder", "file size : " + tempFile.length());
	}

	public void stopRecording() {
		if (null != mAudioRecord) {
			mIsRecording = false;
			mAudioRecord.stop();
			mAudioRecord.release();
			mAudioRecord = null;
			Log.d("Recorder", "recording end");
		}
	}
}