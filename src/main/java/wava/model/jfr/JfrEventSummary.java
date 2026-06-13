package wava.model.jfr;

import java.nio.file.Path;

public class JfrEventSummary {
    private final boolean available;
    private final Path sourcePath;
    private final int totalEventCount;
    private final int compilationEventCount;
    private final int gcEventCount;
    private final String detail;

    private JfrEventSummary(
            boolean available,
            Path sourcePath,
            int totalEventCount,
            int compilationEventCount,
            int gcEventCount,
            String detail) {
        this.available = available;
        this.sourcePath = sourcePath;
        this.totalEventCount = totalEventCount;
        this.compilationEventCount = compilationEventCount;
        this.gcEventCount = gcEventCount;
        this.detail = detail;
    }

    public static JfrEventSummary empty() {
        return new JfrEventSummary(false, null, 0, 0, 0, "No JFR recording has been analyzed.");
    }

    public static JfrEventSummary unavailable(Path sourcePath, String detail) {
        return new JfrEventSummary(false, sourcePath, 0, 0, 0, detail);
    }

    public static JfrEventSummary available(
            Path sourcePath,
            int totalEventCount,
            int compilationEventCount,
            int gcEventCount) {
        return new JfrEventSummary(
                true,
                sourcePath,
                totalEventCount,
                compilationEventCount,
                gcEventCount,
                "JFR recording was analyzed.");
    }

    public boolean isAvailable() {
        return available;
    }

    public Path getSourcePath() {
        return sourcePath;
    }

    public int getTotalEventCount() {
        return totalEventCount;
    }

    public int getCompilationEventCount() {
        return compilationEventCount;
    }

    public int getGcEventCount() {
        return gcEventCount;
    }

    public String getDetail() {
        return detail;
    }

    public String formatLogMessage() {
        if (!available) {
            return "JFR event summary unavailable. " + detail;
        }
        return "JFR event summary: " + totalEventCount
                + " total, " + compilationEventCount
                + " compilation, " + gcEventCount
                + " GC events.";
    }
}
