package wava.model.metric;

public class MetricSample {
    private final long timestampMillis;
    private final double cpuUsagePercent;
    private final double heapUsedMb;
    private final boolean heapAvailable;
    private final boolean gcAvailable;
    private final long gcCountDelta;
    private final long gcTimeDeltaMillis;

    public MetricSample(long timestampMillis, double cpuUsagePercent, double heapUsedMb) {
        this(timestampMillis, cpuUsagePercent, heapUsedMb, true);
    }

    public MetricSample(long timestampMillis, double cpuUsagePercent, double heapUsedMb, boolean heapAvailable) {
        this(timestampMillis, cpuUsagePercent, heapUsedMb, heapAvailable, false, 0L, 0L);
    }

    public MetricSample(
            long timestampMillis,
            double cpuUsagePercent,
            double heapUsedMb,
            boolean heapAvailable,
            boolean gcAvailable,
            long gcCountDelta,
            long gcTimeDeltaMillis) {
        this.timestampMillis = timestampMillis;
        this.cpuUsagePercent = cpuUsagePercent;
        this.heapUsedMb = heapUsedMb;
        this.heapAvailable = heapAvailable;
        this.gcAvailable = gcAvailable;
        this.gcCountDelta = gcCountDelta;
        this.gcTimeDeltaMillis = gcTimeDeltaMillis;
    }

    public long getTimestampMillis() {
        return timestampMillis;
    }

    public double getCpuUsagePercent() {
        return cpuUsagePercent;
    }

    public double getHeapUsedMb() {
        return heapUsedMb;
    }

    public boolean isHeapAvailable() {
        return heapAvailable;
    }

    public boolean isGcAvailable() {
        return gcAvailable;
    }

    public long getGcCountDelta() {
        return gcCountDelta;
    }

    public long getGcTimeDeltaMillis() {
        return gcTimeDeltaMillis;
    }

    public boolean hasGcActivity() {
        return gcAvailable && gcCountDelta > 0L;
    }
}
