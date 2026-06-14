package wava.model.graph;

public enum GraphDisplayRange {
    LAST_60_SECONDS("60s", 60_000L),
    LAST_180_SECONDS("180s", 180_000L);

    private final String label;
    private final long durationMillis;

    GraphDisplayRange(String label, long durationMillis) {
        this.label = label;
        this.durationMillis = durationMillis;
    }

    public String getLabel() {
        return label;
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    @Override
    public String toString() {
        return label;
    }
}
