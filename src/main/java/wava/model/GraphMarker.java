package wava.model;

public class GraphMarker {
    private final long timestampMillis;
    private final String label;

    public GraphMarker(long timestampMillis, String label) {
        this.timestampMillis = timestampMillis;
        this.label = label;
    }

    public long getTimestampMillis() {
        return timestampMillis;
    }

    public String getLabel() {
        return label;
    }
}
