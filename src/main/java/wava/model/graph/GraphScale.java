package wava.model.graph;

import java.util.List;

public class GraphScale {
    private static final double DEFAULT_AUTO_MAX = 1.0;
    private static final double AUTO_HEADROOM_RATIO = 1.2;

    private final double min;
    private final double max;

    public GraphScale(double min, double max) {
        if (max <= min) {
            throw new IllegalArgumentException("Scale max must be greater than min.");
        }
        this.min = min;
        this.max = max;
    }

    public static GraphScale fixed(double min, double max) {
        return new GraphScale(min, max);
    }

    public static GraphScale auto(List<Double> values) {
        if (values.isEmpty()) {
            return new GraphScale(0.0, DEFAULT_AUTO_MAX);
        }
        double maxValue = values.stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(DEFAULT_AUTO_MAX);
        double scaleMax = Math.max(DEFAULT_AUTO_MAX, maxValue * AUTO_HEADROOM_RATIO);
        return new GraphScale(0.0, scaleMax);
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getRange() {
        return max - min;
    }

    public double getTickValue(int tickIndex, int tickCount) {
        if (tickCount <= 1) {
            return min;
        }
        return min + getRange() * tickIndex / (tickCount - 1);
    }

    public double normalize(double value) {
        double ratio = (value - min) / getRange();
        return Math.max(0.0, Math.min(1.0, ratio));
    }
}
