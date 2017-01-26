package lssc.check.sentencetest;

import android.app.Application;

public class LSSCSentenceTestApplication extends Application implements Constants{
	private String testee;
	private int[] recordTime = null;
	private int recordState = REC_STOP;
	private int tempRecordFileCnt = 0;
	private String questType = TEST_DIRECT;
	private int questSize = 2;
	private boolean is2Check = false;
	private boolean is3Check = false;
	private boolean is4Check = false;
	private int typeIdx = 0;
	
	public String getTestee() {
		return testee;
	}

	public void setTestee(String testee) {
		this.testee = testee;
	}
	
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

	public int getTempRecordFileCnt() {
		return tempRecordFileCnt;
	}

	public void setTempRecordFileCnt(int tempRecordFileCnt) {
		this.tempRecordFileCnt = tempRecordFileCnt;
	}

	public String getQuestType() {
		return questType;
	}

	public void setQuestType(String questType) {
		this.questType = questType;
	}

	public int getQuestSize() {
		return questSize;
	}

	public void setQuestSize(int questSize) {
		this.questSize = questSize;
	}

	public boolean getIs2Check() {
		return is2Check;
	}

	public void setIs2Check(boolean is2Check) {
		this.is2Check = is2Check;
	}

	public boolean getIs3Check() {
		return is3Check;
	}

	public void setIs3Check(boolean is3Check) {
		this.is3Check = is3Check;
	}

	public boolean getIs4Check() {
		return is4Check;
	}

	public void setIs4Check(boolean is4Check) {
		this.is4Check = is4Check;
	}

	public int getTypeIdx() {
		return typeIdx;
	}

	public void setTypeIdx(int typeIdx) {
		this.typeIdx = typeIdx;
	}
	
}
