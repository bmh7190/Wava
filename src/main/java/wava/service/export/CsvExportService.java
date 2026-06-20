package wava.service.export;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import wava.model.jit.JitEvent;
import wava.model.metric.MetricSample;
import wava.service.jit.JitEventFilter;

public class CsvExportService {
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            .withZone(ZoneId.systemDefault());
    private static final String MONITORING_HEADER = String.join(",",
            "timestampMillis",
            "timestampText",
            "elapsedSeconds",
            "cpuUsagePercent",
            "heapUsedMb",
            "heapAvailable",
            "gcAvailable",
            "gcCountDelta",
            "gcTimeDeltaMillis",
            "jitEventCount");
    private static final String JIT_EVENTS_HEADER = String.join(",",
            "timestampMillis",
            "timestampText",
            "elapsedSeconds",
            "jvmElapsedMillis",
            "jvmElapsedText",
            "compileId",
            "compileLevel",
            "methodName",
            "displayMethodName",
            "matchedFilter",
            "rawLine");

    public CsvExportResult export(
            List<MetricSample> samples,
            List<JitEvent> jitEvents,
            JitEventFilter filter,
            Path monitoringPath,
            Path jitEventsPath) throws IOException {
        createParentDirectory(monitoringPath);
        createParentDirectory(jitEventsPath);

        long firstTimestampMillis = firstTimestampMillis(samples, jitEvents);
        writeMonitoringCsv(samples, jitEvents, filter, monitoringPath, firstTimestampMillis);
        writeJitEventsCsv(jitEvents, filter, jitEventsPath, firstTimestampMillis);
        return new CsvExportResult(
                monitoringPath.toAbsolutePath().normalize(),
                jitEventsPath.toAbsolutePath().normalize());
    }

    private void writeMonitoringCsv(
            List<MetricSample> samples,
            List<JitEvent> jitEvents,
            JitEventFilter filter,
            Path outputPath,
            long firstTimestampMillis) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            writer.write(MONITORING_HEADER);
            writer.newLine();
            for (int index = 0; index < samples.size(); index++) {
                MetricSample sample = samples.get(index);
                writer.write(formatMonitoringRow(
                        sample,
                        firstTimestampMillis,
                        countJitEvents(samples, jitEvents, filter, index)));
                writer.newLine();
            }
        }
    }

    private void writeJitEventsCsv(
            List<JitEvent> jitEvents,
            JitEventFilter filter,
            Path outputPath,
            long firstTimestampMillis) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            writer.write(JIT_EVENTS_HEADER);
            writer.newLine();
            for (JitEvent event : jitEvents) {
                writer.write(formatJitEventRow(event, firstTimestampMillis, filter.matches(event)));
                writer.newLine();
            }
        }
    }

    private String formatMonitoringRow(MetricSample sample, long firstTimestampMillis, int jitEventCount) {
        return String.join(",",
                csv(sample.getTimestampMillis()),
                csv(formatTimestamp(sample.getTimestampMillis())),
                csv(formatElapsedSeconds(sample.getTimestampMillis(), firstTimestampMillis)),
                csv(sample.getCpuUsagePercent()),
                csv(sample.getHeapUsedMb()),
                csv(sample.isHeapAvailable()),
                csv(sample.isGcAvailable()),
                csv(sample.getGcCountDelta()),
                csv(sample.getGcTimeDeltaMillis()),
                csv(jitEventCount));
    }

    private String formatJitEventRow(JitEvent event, long firstTimestampMillis, boolean matchedFilter) {
        return String.join(",",
                csv(event.getTimestampMillis()),
                csv(formatTimestamp(event.getTimestampMillis())),
                csv(formatElapsedSeconds(event.getTimestampMillis(), firstTimestampMillis)),
                csv(event.hasJvmElapsedMillis() ? event.getJvmElapsedMillis() : ""),
                csv(event.hasJvmElapsedMillis() ? formatElapsedMillis(event.getJvmElapsedMillis()) : ""),
                csv(event.getCompileId()),
                csv(event.getLevel().isBlank() ? "" : "L" + event.getLevel()),
                csv(event.getMethodName()),
                csv(event.getMethodName().replace("::", ".")),
                csv(matchedFilter),
                csv(event.getRawLine()));
    }

    private int countJitEvents(
            List<MetricSample> samples,
            List<JitEvent> jitEvents,
            JitEventFilter filter,
            int sampleIndex) {
        long startMillis = samples.get(sampleIndex).getTimestampMillis();
        long endMillis = getExclusiveEndMillis(samples, sampleIndex);
        int count = 0;
        for (JitEvent event : jitEvents) {
            if (event.getTimestampMillis() >= startMillis
                    && event.getTimestampMillis() < endMillis
                    && filter.matches(event)) {
                count++;
            }
        }
        return count;
    }

    private long getExclusiveEndMillis(List<MetricSample> samples, int sampleIndex) {
        if (sampleIndex + 1 >= samples.size()) {
            return Long.MAX_VALUE;
        }
        return samples.get(sampleIndex + 1).getTimestampMillis();
    }

    private long firstTimestampMillis(List<MetricSample> samples, List<JitEvent> jitEvents) {
        if (!samples.isEmpty()) {
            return samples.get(0).getTimestampMillis();
        }
        if (!jitEvents.isEmpty()) {
            return jitEvents.get(0).getTimestampMillis();
        }
        return 0L;
    }

    private void createParentDirectory(Path outputPath) throws IOException {
        Path parent = outputPath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }

    private String formatTimestamp(long timestampMillis) {
        return TIMESTAMP_FORMATTER.format(Instant.ofEpochMilli(timestampMillis));
    }

    private String formatElapsedSeconds(long timestampMillis, long firstTimestampMillis) {
        if (firstTimestampMillis <= 0L) {
            return "";
        }
        return String.format(Locale.US, "%.3f", (timestampMillis - firstTimestampMillis) / 1000.0);
    }

    private String formatElapsedMillis(long elapsedMillis) {
        long totalSeconds = elapsedMillis / 1000L;
        long hours = totalSeconds / 3600L;
        long minutes = (totalSeconds % 3600L) / 60L;
        long seconds = totalSeconds % 60L;
        long millis = elapsedMillis % 1000L;
        return String.format(Locale.US, "%02d:%02d:%02d.%03d", hours, minutes, seconds, millis);
    }

    private String csv(long value) {
        return String.valueOf(value);
    }

    private String csv(int value) {
        return String.valueOf(value);
    }

    private String csv(double value) {
        return String.format(Locale.US, "%.4f", value);
    }

    private String csv(boolean value) {
        return String.valueOf(value);
    }

    private String csv(Object value) {
        if (value == null) {
            return "";
        }
        String text = String.valueOf(value);
        if (text.contains(",") || text.contains("\"") || text.contains("\r") || text.contains("\n")) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }
}
