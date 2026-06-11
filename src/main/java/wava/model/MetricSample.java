package wava.model;

public class MetricSample {
    private final long timestampMillis;
    private final double cpuUsagePercent;
    private final double heapUsedMb;

    public MetricSample(long timestampMillis, double cpuUsagePercent, double heapUsedMb) {
        this.timestampMillis = timestampMillis;
        this.cpuUsagePercent = cpuUsagePercent;
        this.heapUsedMb = heapUsedMb;
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
}
