package wava.service.export;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import wava.model.jit.JitEvent;
import wava.model.metric.MetricSample;
import wava.service.jit.JitEventFilter;

public class CsvExportServiceTest {
    public static void main(String[] args) throws IOException {
        exportMonitoringAndJitEventsToSeparateFiles();
        escapeRawJitLines();
        createParentDirectories();
    }

    private static void exportMonitoringAndJitEventsToSeparateFiles() throws IOException {
        CsvExportService service = new CsvExportService();
        Path directory = Files.createTempDirectory("wava-export-test");
        Path monitoringPath = directory.resolve("monitoring.csv");
        Path jitEventsPath = directory.resolve("jit-events.csv");

        CsvExportResult result = service.export(
                samples(),
                events(),
                new JitEventFilter("Target"),
                monitoringPath,
                jitEventsPath);

        List<String> monitoringLines = Files.readAllLines(result.getMonitoringPath());
        List<String> jitLines = Files.readAllLines(result.getJitEventsPath());

        assertEquals(expectedMonitoringHeader(), monitoringLines.get(0), "monitoring header");
        assertTrue(monitoringLines.get(1).startsWith("1000,"), "first metric timestamp");
        assertTrue(monitoringLines.get(1).contains(",0.000,12.3456,100.0000,true,false,0,0,1"), "first metric values");
        assertTrue(monitoringLines.get(2).contains(",1.000,5.0000,110.5000,false,true,1,15,1"), "second metric values");
        assertTrue(monitoringLines.get(3).contains(",2.000,2.0000,120.0000,true,true,0,0,0"), "third metric values");
        assertEquals(4, monitoringLines.size(), "monitoring line count");

        assertEquals(expectedJitEventsHeader(), jitLines.get(0), "jit header");
        assertTrue(jitLines.get(1).startsWith("1000,"), "first jit timestamp");
        assertTrue(jitLines.get(1).contains(",123,00:00:00.123,1,L3,sample.Target::run,sample.Target.run,true,"), "first jit values");
        assertTrue(jitLines.get(2).contains(",456,00:00:00.456,2,L4,sample.Other::work,sample.Other.work,false,"), "unmatched jit values");
        assertEquals(4, jitLines.size(), "jit line count");

        deleteIfExists(jitEventsPath);
        deleteIfExists(monitoringPath);
        deleteIfExists(directory);
    }

    private static void escapeRawJitLines() throws IOException {
        CsvExportService service = new CsvExportService();
        Path directory = Files.createTempDirectory("wava-export-escape-test");
        Path monitoringPath = directory.resolve("monitoring.csv");
        Path jitEventsPath = directory.resolve("jit-events.csv");
        List<JitEvent> events = List.of(new JitEvent(
                1000L,
                123L,
                1,
                "3",
                "sample.Target::run",
                "123 1 3 sample.Target::run compiled, \"quoted\""));

        service.export(samples().subList(0, 1), events, new JitEventFilter("Target"), monitoringPath, jitEventsPath);

        List<String> lines = Files.readAllLines(jitEventsPath);
        assertTrue(lines.get(1).endsWith("\"123 1 3 sample.Target::run compiled, \"\"quoted\"\"\""), "escaped raw line");

        deleteIfExists(jitEventsPath);
        deleteIfExists(monitoringPath);
        deleteIfExists(directory);
    }

    private static void createParentDirectories() throws IOException {
        CsvExportService service = new CsvExportService();
        Path directory = Files.createTempDirectory("wava-export-parent");
        Path monitoringPath = directory.resolve("nested").resolve("monitoring.csv");
        Path jitEventsPath = directory.resolve("nested").resolve("jit-events.csv");

        service.export(samples(), List.of(), new JitEventFilter(""), monitoringPath, jitEventsPath);

        assertTrue(Files.exists(monitoringPath), "monitoring file");
        assertTrue(Files.exists(jitEventsPath), "jit events file");
        deleteIfExists(jitEventsPath);
        deleteIfExists(monitoringPath);
        deleteIfExists(monitoringPath.getParent());
        deleteIfExists(directory);
    }

    private static String expectedMonitoringHeader() {
        return "timestampMillis,timestampText,elapsedSeconds,cpuUsagePercent,heapUsedMb,heapAvailable,"
                + "gcAvailable,gcCountDelta,gcTimeDeltaMillis,jitEventCount";
    }

    private static String expectedJitEventsHeader() {
        return "timestampMillis,timestampText,elapsedSeconds,jvmElapsedMillis,jvmElapsedText,compileId,"
                + "compileLevel,methodName,displayMethodName,matchedFilter,rawLine";
    }

    private static List<MetricSample> samples() {
        return List.of(
                new MetricSample(1000L, 12.3456, 100.0),
                new MetricSample(2000L, 5.0, 110.5, false, true, 1L, 15L),
                new MetricSample(3000L, 2.0, 120.0, true, true, 0L, 0L));
    }

    private static List<JitEvent> events() {
        return List.of(
                event(1000L, 123L, 1, "3", "sample.Target::run"),
                event(1500L, 456L, 2, "4", "sample.Other::work"),
                event(2500L, 789L, 3, "4", "sample.Target::compute"));
    }

    private static JitEvent event(
            long timestampMillis,
            long jvmElapsedMillis,
            int compileId,
            String level,
            String methodName) {
        return new JitEvent(timestampMillis, jvmElapsedMillis, compileId, level, methodName, methodName);
    }

    private static void deleteIfExists(Path path) throws IOException {
        Files.deleteIfExists(path);
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
