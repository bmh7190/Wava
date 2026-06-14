package wava.model.graph;

public class GraphTimeWindow {
    private final long startTimestampMillis;
    private final long endTimestampMillis;

    public GraphTimeWindow(long startTimestampMillis, long endTimestampMillis) {
        this.startTimestampMillis = startTimestampMillis;
        this.endTimestampMillis = Math.max(startTimestampMillis, endTimestampMillis);
    }

    public long getStartTimestampMillis() {
        return startTimestampMillis;
    }

    public long getEndTimestampMillis() {
        return endTimestampMillis;
    }

    public boolean contains(long timestampMillis) {
        return timestampMillis >= startTimestampMillis && timestampMillis <= endTimestampMillis;
    }
}
