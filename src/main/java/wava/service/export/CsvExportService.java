package wava.service.export;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import wava.model.jit.JitEvent;
import wava.model.metric.MetricSample;
import wava.service.jit.JitEventFilter;

public class CsvExportService {
    private static final String HEADER = "timestampMillis,cpuUsagePercent,heapUsedMb,heapAvailable,"
            + "gcAvailable,gcCountDelta,gcTimeDeltaMillis,jitEventCount";

    public Path export(
            List<MetricSample> samples,
            List<JitEvent> jitEvents,
            JitEventFilter filter,
            Path outputPath) throws IOException {
        Path parent = outputPath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            writer.write(HEADER);
            writer.newLine();
            for (int index = 0; index < samples.size(); index++) {
                MetricSample sample = samples.get(index);
                writer.write(formatRow(sample, countJitEvents(samples, jitEvents, filter, index)));
                writer.newLine();
            }
        }
        return outputPath;
    }

    private String formatRow(MetricSample sample, int jitEventCount) {
        return String.format(
                Locale.US,
                "%d,%.4f,%.4f,%s,%s,%d,%d,%d",
                sample.getTimestampMillis(),
                sample.getCpuUsagePercent(),
                sample.getHeapUsedMb(),
                sample.isHeapAvailable(),
                sample.isGcAvailable(),
                sample.getGcCountDelta(),
                sample.getGcTimeDeltaMillis(),
                jitEventCount);
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
}
