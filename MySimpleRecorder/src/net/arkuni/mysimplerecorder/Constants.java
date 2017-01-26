package net.arkuni.mysimplerecorder;


public interface Constants {
	public static final String[] COUNTDOWN = {"3", "2", "1"};
	public static final String RECORD_SERVICE = "net.arkuni.RecordService";
	public static final String RECORDING_PATH = "/my_simple_recorder";
	public static final String TMP_PATH = "/tmp";
	public static final String TEST_DIRECT = "DIRECT";
	public static final String TEST_EXAMPLE = "EXAMPLE";
	public static final int RECORDING = 2;
	public static final int REC_PAUSE = 1;
	public static final int REC_STOP = 0;
	public static final int HISTORY_CLOSE = 0;
	public static final int HISTORY_OPEN = 1;
	
	public static final int MAXIMUM_RECORDING_TIME = 3600000;
	public static final int DEFAULT_SAMPLE_RATE = 22050;
	//8000 9600 11025 12000 15000 16000 22050 24000 32000 44100 48000 88200 96000 192000

	public static final String EXTRA_MESSAGE = "message";
	public static final String DISPLAY_MESSAGE_ACTION =
            "net.arkuni.mysimplerecorder.DISPLAY_MESSAGE";
}
