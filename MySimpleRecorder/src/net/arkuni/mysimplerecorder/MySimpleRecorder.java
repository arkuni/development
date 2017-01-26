package net.arkuni.mysimplerecorder;

import android.app.Application;

public class MySimpleRecorder extends Application implements Constants{
	private int[] recordTime = null;
	private int recordState = REC_STOP;
	public static int tempRecordFileCnt = 0;
	
	public int[] getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(int[] recordTime) {
		this.recordTime = recordTime;
	}

	public int getRecordState() {
		return recordState;
	}

	public void setRecordState(int recordState) {
		this.recordState = recordState;
	}
}
