package wava.service.metric;

import java.util.HashMap;
import java.util.Map;
import wava.model.metric.ProcessCpuSnapshot;

public class ProcessCpuTracker {
    private static final double PERCENT_SCALE = 100.0;

    private final Map<Long, ProcessCpuSnapshot> previousSnapshots;
    private final int processorCount;

    public ProcessCpuTracker() {
        this(Runtime.getRuntime().availableProcessors());
    }

    public ProcessCpuTracker(int processorCount) {
        this.processorCount = Math.max(1, processorCount);
        previousSnapshots = new HashMap<>();
    }

    public synchronized double calculateUsage(ProcessCpuSnapshot currentSnapshot) {
        ProcessCpuSnapshot previousSnapshot = previousSnapshots.put(
                currentSnapshot.getPid(),
                currentSnapshot);
        if (previousSnapshot == null) {
            return 0.0;
        }

        long elapsedMillis = currentSnapshot.getTimestampMillis() - previousSnapshot.getTimestampMillis();
        long cpuDeltaMillis = currentSnapshot.getCpuTimeMillis() - previousSnapshot.getCpuTimeMillis();
        if (elapsedMillis <= 0 || cpuDeltaMillis < 0) {
            return 0.0;
        }

        double singleCoreUsage = (double) cpuDeltaMillis / elapsedMillis * PERCENT_SCALE;
        double processUsage = singleCoreUsage / processorCount;
        return clamp(processUsage, 0.0, PERCENT_SCALE);
    }

    public synchronized void remove(long pid) {
        previousSnapshots.remove(pid);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
