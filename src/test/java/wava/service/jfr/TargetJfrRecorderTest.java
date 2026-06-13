package wava.service.jfr;

import java.io.IOException;
import java.nio.file.Path;
import wava.model.jfr.JfrRecordingStatus;
import wava.model.process.JavaProcessInfo;

public class TargetJfrRecorderTest {
    public static void main(String[] args) {
        startRecordingWithTargetProcess();
        stopActiveRecording();
        keepIdleStatusWhenStoppingInactiveRecording();
        returnFailedStatusWhenStartFails();
        returnFailedStatusWhenStopFails();
    }

    private static void startRecordingWithTargetProcess() {
        FakeRecordingClient client = new FakeRecordingClient(12L);
        TargetJfrRecorder recorder = new TargetJfrRecorder(client);

        JfrRecordingStatus status = recorder.start(process());

        assertTrue(status.isRecording(), "recording");
        assertEquals(12L, status.getRecordingId(), "recording id");
        assertEquals(1234L, client.startedPid, "started pid");
    }

    private static void stopActiveRecording() {
        FakeRecordingClient client = new FakeRecordingClient(12L);
        TargetJfrRecorder recorder = new TargetJfrRecorder(client);

        JfrRecordingStatus status = recorder.stop(process(), JfrRecordingStatus.recording(12L));

        assertTrue(!status.isRecording(), "recording");
        assertEquals("Stopped", status.getStateLabel(), "state");
        assertEquals(1234L, client.stoppedPid, "stopped pid");
        assertEquals(12L, client.stoppedRecordingId, "stopped id");
        assertTrue(status.hasOutputPath(), "output path");
        assertEquals(client.outputPath.toString(), status.getOutputPath().toString(), "output path");
    }

    private static void keepIdleStatusWhenStoppingInactiveRecording() {
        TargetJfrRecorder recorder = new TargetJfrRecorder(new FakeRecordingClient(12L));

        JfrRecordingStatus status = recorder.stop(process(), JfrRecordingStatus.idle());

        assertEquals("Idle", status.getStateLabel(), "state");
    }

    private static void returnFailedStatusWhenStartFails() {
        FakeRecordingClient client = new FakeRecordingClient(12L);
        client.failStart = true;
        TargetJfrRecorder recorder = new TargetJfrRecorder(client);

        JfrRecordingStatus status = recorder.start(process());

        assertEquals("Failed", status.getStateLabel(), "state");
    }

    private static void returnFailedStatusWhenStopFails() {
        FakeRecordingClient client = new FakeRecordingClient(12L);
        client.failStop = true;
        TargetJfrRecorder recorder = new TargetJfrRecorder(client);

        JfrRecordingStatus status = recorder.stop(process(), JfrRecordingStatus.recording(12L));

        assertEquals("Failed", status.getStateLabel(), "state");
    }

    private static JavaProcessInfo process() {
        return new JavaProcessInfo(1234L, "sample.Target");
    }

    private static class FakeRecordingClient implements JfrRecordingClient {
        private final long recordingId;
        private final Path outputPath;
        private long startedPid;
        private long stoppedPid;
        private long stoppedRecordingId;
        private boolean failStart;
        private boolean failStop;

        private FakeRecordingClient(long recordingId) {
            this.recordingId = recordingId;
            outputPath = Path.of("exports", "jfr", "sample.jfr");
        }

        @Override
        public long startRecording(long pid) throws IOException {
            if (failStart) {
                throw new IOException("start failed");
            }
            startedPid = pid;
            return recordingId;
        }

        @Override
        public Path stopRecording(long pid, long recordingId) throws IOException {
            if (failStop) {
                throw new IOException("stop failed");
            }
            stoppedPid = pid;
            stoppedRecordingId = recordingId;
            return outputPath;
        }
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label + " expected true");
        }
    }

    private static void assertEquals(long expected, long actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(String expected, String actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }
}
