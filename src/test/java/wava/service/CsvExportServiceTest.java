package wava.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import wava.model.JitEvent;
import wava.model.MetricSample;

public class CsvExportServiceTest {
    public static void main(String[] args) throws IOException {
        exportSamplesWithJitEventCounts();
        createParentDirectory();
    }

    private static void exportSamplesWithJitEventCounts() throws IOException {
        CsvExportService service = new CsvExportService();
        Path outputPath = Files.createTempFile("wava-export-test", ".csv");

        service.export(samples(), events(), new JitEventFilter("Target"), outputPath);

        List<String> lines = Files.readAllLines(outputPath);
        assertEquals("timestampMillis,cpuUsagePercent,heapUsedMb,jitEventCount", lines.get(0), "header");
        assertEquals("1000,12.3456,100.0000,1", lines.get(1), "first row");
        assertEquals("2000,5.0000,110.5000,1", lines.get(2), "second row");
        assertEquals("3000,2.0000,120.0000,1", lines.get(3), "third row");

        Files.deleteIfExists(outputPath);
    }

    private static void createParentDirectory() throws IOException {
        CsvExportService service = new CsvExportService();
        Path directory = Files.createTempDirectory("wava-export-parent");
        Path outputPath = directory.resolve("nested").resolve("monitoring.csv");

        service.export(samples(), List.of(), new JitEventFilter(""), outputPath);

        assertTrue(Files.exists(outputPath), "output file");
        Files.deleteIfExists(outputPath);
        Files.deleteIfExists(outputPath.getParent());
        Files.deleteIfExists(directory);
    }

    private static List<MetricSample> samples() {
        return List.of(
                new MetricSample(1000L, 12.3456, 100.0),
                new MetricSample(2000L, 5.0, 110.5),
                new MetricSample(3000L, 2.0, 120.0));
    }

    private static List<JitEvent> events() {
        return List.of(
                event(1000L, "sample.Target::run"),
                event(1500L, "sample.Other::work"),
                event(2500L, "sample.Target::compute"),
                event(3500L, "sample.Target::late"));
    }

    private static JitEvent event(long timestampMillis, String methodName) {
        return new JitEvent(timestampMillis, 1, "3", methodName, methodName);
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
