package wava.service;

import java.time.Duration;
import java.util.Optional;
import wava.model.HeapMemorySnapshot;
import wava.model.JavaProcessInfo;
import wava.model.MetricSample;
import wava.model.ProcessCpuSnapshot;

public class TargetProcessMetricCollector implements MetricCollector {
    private final ProcessCpuTracker cpuTracker;
    private final HeapMemoryReader heapMemoryReader;

    public TargetProcessMetricCollector() {
        this(new ProcessCpuTracker(), new TargetJvmMemoryReader());
    }

    public TargetProcessMetricCollector(ProcessCpuTracker cpuTracker, HeapMemoryReader heapMemoryReader) {
        this.cpuTracker = cpuTracker;
        this.heapMemoryReader = heapMemoryReader;
    }

    @Override
    public MetricSample collect(JavaProcessInfo targetProcess, int sampleIndex) {
        long timestamp = System.currentTimeMillis();
        double cpuUsage = readTargetCpuUsage(targetProcess, timestamp);
        HeapMemorySnapshot heapMemory = heapMemoryReader.readHeapMemory(targetProcess);
        return new MetricSample(timestamp, cpuUsage, heapMemory.getUsedMb(), heapMemory.isAvailable());
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
}
