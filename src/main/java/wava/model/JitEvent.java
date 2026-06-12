package wava.model;

public class JitEvent {
    private final long timestampMillis;
    private final int compileId;
    private final String level;
    private final String methodName;
    private final String rawLine;

    public JitEvent(long timestampMillis, int compileId, String level, String methodName, String rawLine) {
        this.timestampMillis = timestampMillis;
        this.compileId = compileId;
        this.level = level;
        this.methodName = methodName;
        this.rawLine = rawLine;
    }

    public long getTimestampMillis() {
        return timestampMillis;
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
        if (level.isBlank()) {
            return "#" + compileId + " " + displayName + " compiled";
        }
        return "#" + compileId + " L" + level + " " + displayName + " compiled";
    }
}
