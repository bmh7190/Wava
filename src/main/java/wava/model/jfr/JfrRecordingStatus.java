package wava.model.jfr;

import java.nio.file.Path;

public class JfrRecordingStatus {
    private final boolean recording;
    private final long recordingId;
    private final String stateLabel;
    private final String detail;
    private final Path outputPath;

    private JfrRecordingStatus(
            boolean recording,
            long recordingId,
            String stateLabel,
            String detail,
            Path outputPath) {
        this.recording = recording;
        this.recordingId = recordingId;
        this.stateLabel = stateLabel;
        this.detail = detail;
        this.outputPath = outputPath;
    }

    public static JfrRecordingStatus idle() {
        return new JfrRecordingStatus(false, -1L, "Idle", "JFR recording has not started.", null);
    }

    public static JfrRecordingStatus recording(long recordingId) {
        return new JfrRecordingStatus(
                true,
                recordingId,
                "Recording",
                "Recording id " + recordingId + " is active.",
                null);
    }

    public static JfrRecordingStatus stopped(long recordingId, Path outputPath) {
        return new JfrRecordingStatus(
                false,
                recordingId,
                "Stopped",
                "Recording id " + recordingId + " was saved to " + outputPath + ".",
                outputPath);
    }

    public static JfrRecordingStatus failed(String detail) {
        return new JfrRecordingStatus(false, -1L, "Failed", detail, null);
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

    public Path getOutputPath() {
        return outputPath;
    }

    public boolean hasOutputPath() {
        return outputPath != null;
    }

    public String formatLogMessage() {
        return "JFR recording status: " + stateLabel + ". " + detail;
    }
}
