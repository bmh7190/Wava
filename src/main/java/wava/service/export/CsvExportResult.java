package wava.service.export;

import java.nio.file.Path;

public class CsvExportResult {
    private final Path monitoringPath;
    private final Path jitEventsPath;

    public CsvExportResult(Path monitoringPath, Path jitEventsPath) {
        this.monitoringPath = monitoringPath;
        this.jitEventsPath = jitEventsPath;
    }

    public Path getMonitoringPath() {
        return monitoringPath;
    }

    public Path getJitEventsPath() {
        return jitEventsPath;
    }
}
