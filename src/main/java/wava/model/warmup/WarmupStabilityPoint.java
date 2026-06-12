package wava.model.warmup;

public class WarmupStabilityPoint {
    private final boolean available;
    private final int sampleCount;
    private final int sampleIndex;
    private final long timestampMillis;
    private final double cpuRangePercent;
    private final int jitEventCount;

    private WarmupStabilityPoint(
            boolean available,
            int sampleCount,
            int sampleIndex,
            long timestampMillis,
            double cpuRangePercent,
            int jitEventCount) {
        this.available = available;
        this.sampleCount = sampleCount;
        this.sampleIndex = sampleIndex;
        this.timestampMillis = timestampMillis;
        this.cpuRangePercent = cpuRangePercent;
        this.jitEventCount = jitEventCount;
    }

    public static WarmupStabilityPoint unavailable(int sampleCount) {
        return new WarmupStabilityPoint(false, sampleCount, -1, 0L, 0.0, 0);
    }

    public static WarmupStabilityPoint available(
            int sampleCount,
            int sampleIndex,
            long timestampMillis,
            double cpuRangePercent,
            int jitEventCount) {
        return new WarmupStabilityPoint(
                true,
                sampleCount,
                sampleIndex,
                timestampMillis,
                cpuRangePercent,
                jitEventCount);
    }

    public boolean isAvailable() {
        return available;
    }

    public int getSampleCount() {
        return sampleCount;
    }

    public int getSampleIndex() {
        return sampleIndex;
    }

    public long getTimestampMillis() {
        return timestampMillis;
    }

    public double getCpuRangePercent() {
        return cpuRangePercent;
    }

    public int getJitEventCount() {
        return jitEventCount;
    }
}
