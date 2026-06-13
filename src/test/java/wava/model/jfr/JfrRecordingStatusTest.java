package wava.model.jfr;

import java.nio.file.Path;

public class JfrRecordingStatusTest {
    public static void main(String[] args) {
        createIdleStatus();
        createRecordingStatus();
        createStoppedStatus();
        createFailedStatus();
    }

    private static void createIdleStatus() {
        JfrRecordingStatus status = JfrRecordingStatus.idle();

        assertTrue(!status.isRecording(), "recording");
        assertEquals("Idle", status.getStateLabel(), "state");
    }

    private static void createRecordingStatus() {
        JfrRecordingStatus status = JfrRecordingStatus.recording(7L);

        assertTrue(status.isRecording(), "recording");
        assertEquals(7L, status.getRecordingId(), "id");
        assertEquals("Recording", status.getStateLabel(), "state");
    }

    private static void createStoppedStatus() {
        Path outputPath = Path.of("exports", "jfr", "sample.jfr");
        JfrRecordingStatus status = JfrRecordingStatus.stopped(7L, outputPath);

        assertTrue(!status.isRecording(), "recording");
        assertEquals(7L, status.getRecordingId(), "id");
        assertEquals("Stopped", status.getStateLabel(), "state");
        assertTrue(status.hasOutputPath(), "output path");
        assertEquals(outputPath.toString(), status.getOutputPath().toString(), "output path");
    }

    private static void createFailedStatus() {
        JfrRecordingStatus status = JfrRecordingStatus.failed("attach failed");

        assertTrue(!status.isRecording(), "recording");
        assertEquals("Failed", status.getStateLabel(), "state");
        assertEquals("attach failed", status.getDetail(), "detail");
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
