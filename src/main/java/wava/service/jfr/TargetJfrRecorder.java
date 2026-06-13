package wava.service.jfr;

import java.io.IOException;
import java.nio.file.Path;
import wava.model.jfr.JfrRecordingStatus;
import wava.model.process.JavaProcessInfo;

public class TargetJfrRecorder {
    private final JfrRecordingClient recordingClient;

    public TargetJfrRecorder() {
        this(new TargetJfrRecordingClient());
    }

    TargetJfrRecorder(JfrRecordingClient recordingClient) {
        this.recordingClient = recordingClient;
    }

    public JfrRecordingStatus start(JavaProcessInfo targetProcess) {
        if (targetProcess == null) {
            return JfrRecordingStatus.failed("No target process selected.");
        }

        try {
            long recordingId = recordingClient.startRecording(targetProcess.getPid());
            return JfrRecordingStatus.recording(recordingId);
        } catch (IOException exception) {
            return JfrRecordingStatus.failed(exception.getMessage());
        }
    }

    public JfrRecordingStatus stop(JavaProcessInfo targetProcess, JfrRecordingStatus currentStatus) {
        if (currentStatus == null || !currentStatus.isRecording()) {
            return currentStatus == null ? JfrRecordingStatus.idle() : currentStatus;
        }
        if (targetProcess == null) {
            return JfrRecordingStatus.failed("No target process selected.");
        }

        try {
            Path outputPath = recordingClient.stopRecording(targetProcess.getPid(), currentStatus.getRecordingId());
            return JfrRecordingStatus.stopped(currentStatus.getRecordingId(), outputPath);
        } catch (IOException exception) {
            return JfrRecordingStatus.failed(exception.getMessage());
        }
    }
}
