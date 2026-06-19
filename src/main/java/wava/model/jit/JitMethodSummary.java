package wava.model.jit;

import java.util.Locale;

public class JitMethodSummary {
    private static final long UNKNOWN_ELAPSED_MILLIS = -1L;

    private final String methodName;
    private final int eventCount;
    private final String latestLevel;
    private final long firstJvmElapsedMillis;
    private final long latestJvmElapsedMillis;
    private final boolean madeNotEntrantObserved;

    public JitMethodSummary(
            String methodName,
            int eventCount,
            String latestLevel,
            long firstJvmElapsedMillis,
            long latestJvmElapsedMillis,
            boolean madeNotEntrantObserved) {
        this.methodName = methodName;
        this.eventCount = eventCount;
        this.latestLevel = latestLevel;
        this.firstJvmElapsedMillis = firstJvmElapsedMillis;
        this.latestJvmElapsedMillis = latestJvmElapsedMillis;
        this.madeNotEntrantObserved = madeNotEntrantObserved;
    }

    public String getMethodName() {
        return methodName;
    }

    public int getEventCount() {
        return eventCount;
    }

    public String getLatestLevel() {
        return latestLevel;
    }

    public long getFirstJvmElapsedMillis() {
        return firstJvmElapsedMillis;
    }

    public long getLatestJvmElapsedMillis() {
        return latestJvmElapsedMillis;
    }

    public boolean isMadeNotEntrantObserved() {
        return madeNotEntrantObserved;
    }

    public boolean hasJvmElapsedTime() {
        return firstJvmElapsedMillis >= 0L && latestJvmElapsedMillis >= 0L;
    }

    public String getLatestLevelLabel() {
        if (latestLevel == null || latestLevel.isBlank()) {
            return "-";
        }
        return "L" + latestLevel;
    }

    public String formatElapsedRange() {
        if (!hasJvmElapsedTime()) {
            return "-";
        }
        String first = formatElapsed(firstJvmElapsedMillis);
        String latest = formatElapsed(latestJvmElapsedMillis);
        if (first.equals(latest)) {
            return first;
        }
        return first + " -> " + latest;
    }

    public static long unknownElapsedMillis() {
        return UNKNOWN_ELAPSED_MILLIS;
    }

    private String formatElapsed(long elapsedMillis) {
        long totalSeconds = elapsedMillis / 1000L;
        long hours = totalSeconds / 3600L;
        long minutes = (totalSeconds % 3600L) / 60L;
        long seconds = totalSeconds % 60L;
        long millis = elapsedMillis % 1000L;
        return String.format(Locale.US, "%02d:%02d:%02d.%03d", hours, minutes, seconds, millis);
    }
}
