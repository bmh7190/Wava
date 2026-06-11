package wava.service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.time.Duration;
import java.util.Optional;
import wava.model.JavaProcessInfo;
import wava.model.MetricSample;
import wava.model.ProcessCpuSnapshot;

public class TargetProcessMetricCollector implements MetricCollector {
    private static final double BYTES_PER_MB = 1024.0 * 1024.0;

    private final ProcessCpuTracker cpuTracker;
    private final MemoryMXBean memoryBean;

    public TargetProcessMetricCollector() {
        cpuTracker = new ProcessCpuTracker();
        memoryBean = ManagementFactory.getMemoryMXBean();
    }

    @Override
    public MetricSample collect(JavaProcessInfo targetProcess, int sampleIndex) {
        long timestamp = System.currentTimeMillis();
        double cpuUsage = readTargetCpuUsage(targetProcess, timestamp);
        double heapUsedMb = readLocalHeapUsedMb();
        return new MetricSample(timestamp, cpuUsage, heapUsedMb);
    }

    private double readTargetCpuUsage(JavaProcessInfo targetProcess, long timestamp) {
        if (targetProcess == null) {
            return 0.0;
        }

        Optional<ProcessHandle> processHandle = ProcessHandle.of(targetProcess.getPid());
        if (processHandle.isEmpty()) {
            cpuTracker.remove(targetProcess.getPid());
            return 0.0;
        }

        Optional<Duration> cpuDuration = processHandle.get().info().totalCpuDuration();
        if (cpuDuration.isEmpty()) {
            return 0.0;
        }

        ProcessCpuSnapshot snapshot = new ProcessCpuSnapshot(
                targetProcess.getPid(),
                timestamp,
                cpuDuration.get().toMillis());
        return cpuTracker.calculateUsage(snapshot);
    }

    private double readLocalHeapUsedMb() {
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        return heapUsage.getUsed() / BYTES_PER_MB;
    }
}
