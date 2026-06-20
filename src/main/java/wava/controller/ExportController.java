package wava.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import wava.model.jit.JitEvent;
import wava.model.metric.MetricSample;
import wava.service.export.CsvExportResult;
import wava.service.export.CsvExportService;
import wava.service.jit.JitEventFilter;

public class ExportController {
    private static final Path DEFAULT_MONITORING_EXPORT_PATH = Path.of("exports", "wava-monitoring.csv");
    private static final Path DEFAULT_JIT_EVENTS_EXPORT_PATH = Path.of("exports", "wava-jit-events.csv");

    private final CsvExportService csvExportService;

    public ExportController() {
        csvExportService = new CsvExportService();
    }

    public CsvExportResult export(
            List<MetricSample> samples,
            List<JitEvent> jitEvents,
            JitEventFilter jitEventFilter) throws IOException {
        return csvExportService.export(
                samples,
                jitEvents,
                jitEventFilter,
                DEFAULT_MONITORING_EXPORT_PATH,
                DEFAULT_JIT_EVENTS_EXPORT_PATH);
    }
}
