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
        String elapsedText = formatJvmElapsedText();
        if (level.isBlank()) {
            return "#" + compileId + " " + displayName + " compiled" + elapsedText;
        }
        return "#" + compileId + " L" + level + " " + displayName + " compiled" + elapsedText;
    }

    private String formatJvmElapsedText() {
        if (!hasJvmElapsedMillis()) {
            return "";
        }
        return " (JVM +" + String.format(Locale.US, "%.3f", jvmElapsedMillis / 1000.0) + "s)";
    }
}
