package wava.model.warmup;

public class WarmupSummary {
    private final boolean available;
    private final int sampleCount;
    private final double earlyAverageCpu;
    private final double lateAverageCpu;
    private final double earlyAverageHeap;
    private final double lateAverageHeap;

    private WarmupSummary(
            boolean available,
            int sampleCount,
            double earlyAverageCpu,
            double lateAverageCpu,
            double earlyAverageHeap,
            double lateAverageHeap) {
        this.available = available;
        this.sampleCount = sampleCount;
        this.earlyAverageCpu = earlyAverageCpu;
        this.lateAverageCpu = lateAverageCpu;
        this.earlyAverageHeap = earlyAverageHeap;
        this.lateAverageHeap = lateAverageHeap;
    }

    public static WarmupSummary unavailable(int sampleCount) {
        return new WarmupSummary(false, sampleCount, 0.0, 0.0, 0.0, 0.0);
    }

    public static WarmupSummary available(
            int sampleCount,
            double earlyAverageCpu,
            double lateAverageCpu,
            double earlyAverageHeap,
            double lateAverageHeap) {
        return new WarmupSummary(
                true,
                sampleCount,
                earlyAverageCpu,
                lateAverageCpu,
                earlyAverageHeap,
                lateAverageHeap);
    }

    public boolean isAvailable() {
        return available;
    }

    public int getSampleCount() {
        return sampleCount;
    }

    public double getEarlyAverageCpu() {
        return earlyAverageCpu;
    }

    public double getLateAverageCpu() {
        return lateAverageCpu;
    }

    public double getCpuChange() {
        return lateAverageCpu - earlyAverageCpu;
    }

    public double getEarlyAverageHeap() {
        return earlyAverageHeap;
    }

    public double getLateAverageHeap() {
        return lateAverageHeap;
    }

    public double getHeapChange() {
        return lateAverageHeap - earlyAverageHeap;
    }
}
