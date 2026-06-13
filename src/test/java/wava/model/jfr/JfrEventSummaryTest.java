package wava.model.jfr;

import java.nio.file.Path;

public class JfrEventSummaryTest {
    public static void main(String[] args) {
        createEmptySummary();
        createUnavailableSummary();
        createAvailableSummary();
    }

    private static void createEmptySummary() {
        JfrEventSummary summary = JfrEventSummary.empty();

        assertTrue(!summary.isAvailable(), "available");
        assertEquals("No JFR recording has been analyzed.", summary.getDetail(), "detail");
    }

    private static void createUnavailableSummary() {
        Path sourcePath = Path.of("exports", "jfr", "missing.jfr");
        JfrEventSummary summary = JfrEventSummary.unavailable(sourcePath, "missing");

        assertTrue(!summary.isAvailable(), "available");
        assertEquals(sourcePath.toString(), summary.getSourcePath().toString(), "source path");
        assertEquals("missing", summary.getDetail(), "detail");
    }

    private static void createAvailableSummary() {
        Path sourcePath = Path.of("exports", "jfr", "sample.jfr");
        JfrEventSummary summary = JfrEventSummary.available(sourcePath, 10, 3, 2);

        assertTrue(summary.isAvailable(), "available");
        assertEquals(10, summary.getTotalEventCount(), "total events");
        assertEquals(3, summary.getCompilationEventCount(), "compilation events");
        assertEquals(2, summary.getGcEventCount(), "gc events");
        assertTrue(summary.formatLogMessage().contains("10 total"), "log message");
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label + " expected true");
        }
    }

    private static void assertEquals(int expected, int actual, String label) {
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
