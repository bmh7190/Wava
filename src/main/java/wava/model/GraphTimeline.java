package wava.model;

public class GraphTimeline {
    private final long startMillis;
    private final long endMillis;

    public GraphTimeline(long startMillis, long endMillis) {
        if (endMillis < startMillis) {
            throw new IllegalArgumentException("Timeline end must be greater than or equal to start.");
        }
        this.startMillis = startMillis;
        this.endMillis = endMillis;
    }

    public int calculateX(long timestampMillis, int left, int right) {
        if (endMillis == startMillis) {
            return left;
        }

        double ratio = (double) (timestampMillis - startMillis) / (endMillis - startMillis);
        double clampedRatio = Math.max(0.0, Math.min(1.0, ratio));
        return left + (int) Math.round((right - left) * clampedRatio);
    }
}
