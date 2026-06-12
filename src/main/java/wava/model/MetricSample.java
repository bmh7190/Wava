package wava.model;

public class MetricSample {
    private final long timestampMillis;
    private final double cpuUsagePercent;
    private final double heapUsedMb;
    private final boolean heapAvailable;

    public MetricSample(long timestampMillis, double cpuUsagePercent, double heapUsedMb) {
        this(timestampMillis, cpuUsagePercent, heapUsedMb, true);
    }

    public MetricSample(long timestampMillis, double cpuUsagePercent, double heapUsedMb, boolean heapAvailable) {
        this.timestampMillis = timestampMillis;
        this.cpuUsagePercent = cpuUsagePercent;
        this.heapUsedMb = heapUsedMb;
        this.heapAvailable = heapAvailable;
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
}
