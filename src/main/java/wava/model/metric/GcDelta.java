package wava.model.metric;

public class GcDelta {
    private final boolean available;
    private final long countDelta;
    private final long timeDeltaMillis;

    private GcDelta(boolean available, long countDelta, long timeDeltaMillis) {
        this.available = available;
        this.countDelta = countDelta;
        this.timeDeltaMillis = timeDeltaMillis;
    }

    public static GcDelta available(long countDelta, long timeDeltaMillis) {
        return new GcDelta(true, Math.max(0L, countDelta), Math.max(0L, timeDeltaMillis));
    }

    public static GcDelta unavailable() {
        return new GcDelta(false, 0L, 0L);
    }

    public boolean isAvailable() {
        return available;
    }

    public long getCountDelta() {
        return countDelta;
    }

    public long getTimeDeltaMillis() {
        return timeDeltaMillis;
    }

    public boolean hasActivity() {
        return available && countDelta > 0L;
    }
}
