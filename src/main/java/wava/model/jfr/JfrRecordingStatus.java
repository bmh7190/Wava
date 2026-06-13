package wava.model.jfr;

public class JfrRecordingStatus {
    private final boolean recording;
    private final long recordingId;
    private final String stateLabel;
    private final String detail;

    private JfrRecordingStatus(boolean recording, long recordingId, String stateLabel, String detail) {
        this.recording = recording;
        this.recordingId = recordingId;
        this.stateLabel = stateLabel;
        this.detail = detail;
    }

    public static JfrRecordingStatus idle() {
        return new JfrRecordingStatus(false, -1L, "Idle", "JFR recording has not started.");
    }

    public static JfrRecordingStatus recording(long recordingId) {
        return new JfrRecordingStatus(true, recordingId, "Recording", "Recording id " + recordingId + " is active.");
    }

    public static JfrRecordingStatus stopped(long recordingId) {
        return new JfrRecordingStatus(false, recordingId, "Stopped", "Recording id " + recordingId + " was stopped.");
    }

    public static JfrRecordingStatus failed(String detail) {
        return new JfrRecordingStatus(false, -1L, "Failed", detail);
    }

    public boolean isRecording() {
        return recording;
    }

    public long getRecordingId() {
        return recordingId;
    }

    public String getStateLabel() {
        return stateLabel;
    }

    public String getDetail() {
        return detail;
    }

    public String formatLogMessage() {
        return "JFR recording status: " + stateLabel + ". " + detail;
    }
}
