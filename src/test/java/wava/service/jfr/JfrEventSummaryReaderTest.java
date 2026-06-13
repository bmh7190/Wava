package wava.service.jfr;

import java.nio.file.Path;
import wava.model.jfr.JfrEventSummary;

public class JfrEventSummaryReaderTest {
    public static void main(String[] args) {
        classifyCompilationEvents();
        classifyGcEvents();
        returnUnavailableSummaryForMissingFile();
    }

    private static void classifyCompilationEvents() {
        JfrEventSummaryReader reader = new JfrEventSummaryReader();

        assertTrue(reader.isCompilationEvent("jdk.Compilation"), "compilation");
        assertTrue(reader.isCompilationEvent("jdk.CompilerStatistics"), "compiler statistics");
        assertTrue(reader.isCompilationEvent("jdk.CodeCacheStatistics"), "code cache");
        assertTrue(!reader.isCompilationEvent("jdk.GarbageCollection"), "gc is not compilation");
    }

    private static void classifyGcEvents() {
        JfrEventSummaryReader reader = new JfrEventSummaryReader();

        assertTrue(reader.isGcEvent("jdk.GarbageCollection"), "garbage collection");
        assertTrue(reader.isGcEvent("jdk.GCHeapSummary"), "gc heap summary");
        assertTrue(reader.isGcEvent("jdk.GCPhasePause"), "gc phase");
        assertTrue(!reader.isGcEvent("jdk.Compilation"), "compilation is not gc");
    }

    private static void returnUnavailableSummaryForMissingFile() {
        JfrEventSummaryReader reader = new JfrEventSummaryReader();
        JfrEventSummary summary = reader.read(Path.of("exports", "jfr", "missing-test-file.jfr"));

        assertTrue(!summary.isAvailable(), "available");
        assertEquals("JFR recording file does not exist.", summary.getDetail(), "detail");
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
