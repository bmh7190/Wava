package wava.model.jit;

import java.nio.file.Path;

public class JitLogStatus {
    private final Path logPath;
    private final String stateLabel;
    private final String detail;
    private final boolean readable;

    private JitLogStatus(Path logPath, String stateLabel, String detail, boolean readable) {
        this.logPath = logPath;
        this.stateLabel = stateLabel;
        this.detail = detail;
        this.readable = readable;
    }

    public static JitLogStatus missing(Path logPath) {
        return new JitLogStatus(logPath, "Missing", "Create the log file or select a valid JIT log path.", false);
    }

    public static JitLogStatus unreadable(Path logPath) {
        return new JitLogStatus(logPath, "Unreadable", "The selected path cannot be read as a log file.", false);
    }

    public static JitLogStatus ready(Path logPath, long fileSizeBytes) {
        String detail = fileSizeBytes == 0L
                ? "File exists, waiting for JIT events."
                : "File ready, " + fileSizeBytes + " bytes.";
        return new JitLogStatus(logPath, "Ready", detail, true);
    }

    public static JitLogStatus readError(Path logPath, String message) {
        return new JitLogStatus(logPath, "Read error", message, false);
    }

    public Path getLogPath() {
        return logPath;
    }

    public String getStateLabel() {
        return stateLabel;
    }

    public String getDetail() {
        return detail;
    }

    public boolean isReadable() {
        return readable;
    }

    public String getStatusKey() {
        if ("Ready".equals(stateLabel)) {
            return logPath + "|" + stateLabel + "|" + readable;
        }
        return logPath + "|" + stateLabel + "|" + detail;
    }

    public String formatLogMessage() {
        return "JIT log status: " + stateLabel + " (" + logPath + "). " + detail;
    }
}
