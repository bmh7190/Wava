package wava.model.jit;

import java.util.Locale;

public class JitEvent {
    private static final long UNKNOWN_JVM_ELAPSED_MILLIS = -1L;

    private final long timestampMillis;
    private final long jvmElapsedMillis;
    private final int compileId;
    private final String level;
    private final String methodName;
    private final String rawLine;

    public JitEvent(long timestampMillis, int compileId, String level, String methodName, String rawLine) {
        this(timestampMillis, UNKNOWN_JVM_ELAPSED_MILLIS, compileId, level, methodName, rawLine);
    }

    public JitEvent(
            long timestampMillis,
            long jvmElapsedMillis,
            int compileId,
            String level,
            String methodName,
            String rawLine) {
        this.timestampMillis = timestampMillis;
        this.jvmElapsedMillis = jvmElapsedMillis;
        this.compileId = compileId;
        this.level = level;
        this.methodName = methodName;
        this.rawLine = rawLine;
    }

    public long getTimestampMillis() {
        return timestampMillis;
    }

    public long getJvmElapsedMillis() {
        return jvmElapsedMillis;
    }

    public boolean hasJvmElapsedMillis() {
        return jvmElapsedMillis >= 0L;
    }

    public int getCompileId() {
        return compileId;
    }

    public String getLevel() {
        return level;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getRawLine() {
        return rawLine;
    }

    public String formatLogMessage() {
        String displayName = methodName.replace("::", ".");
        String elapsedPrefix = formatJvmElapsedPrefix();
        if (level.isBlank()) {
            return elapsedPrefix + "#" + compileId + " " + displayName + " compiled";
        }
        return elapsedPrefix + "#" + compileId + " L" + level + " " + displayName + " compiled";
    }

    private String formatJvmElapsedPrefix() {
        if (!hasJvmElapsedMillis()) {
            return "";
        }
        long totalSeconds = jvmElapsedMillis / 1000L;
        long hours = totalSeconds / 3600L;
        long minutes = (totalSeconds % 3600L) / 60L;
        long seconds = totalSeconds % 60L;
        long millis = jvmElapsedMillis % 1000L;
        return String.format(Locale.US, "[%02d:%02d:%02d.%03d] ", hours, minutes, seconds, millis);
    }
}
