package wava.model;

import java.nio.file.Path;

public class JitLogStatusTest {
    public static void main(String[] args) {
        createMissingStatus();
        createReadyStatus();
        createReadErrorStatus();
    }

    private static void createMissingStatus() {
        Path path = Path.of("logs", "missing.log");

        JitLogStatus status = JitLogStatus.missing(path);

        assertEquals("Missing", status.getStateLabel(), "state");
        assertTrue(!status.isReadable(), "readable");
        assertTrue(status.formatLogMessage().contains("Missing"), "message");
    }

    private static void createReadyStatus() {
        Path path = Path.of("logs", "jit.log");

        JitLogStatus status = JitLogStatus.ready(path, 120L);

        assertEquals("Ready", status.getStateLabel(), "state");
        assertTrue(status.isReadable(), "readable");
        assertTrue(status.getDetail().contains("120"), "detail");
    }

    private static void createReadErrorStatus() {
        Path path = Path.of("logs", "jit.log");

        JitLogStatus status = JitLogStatus.readError(path, "Input length = 1");

        assertEquals("Read error", status.getStateLabel(), "state");
        assertTrue(!status.isReadable(), "readable");
        assertTrue(status.getStatusKey().contains("Input length = 1"), "key");
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label + " expected true");
        }
    }

    private static void assertEquals(String expected, String actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }
}
