package wava.service.metric;

import java.time.Duration;
import java.util.Optional;
import wava.model.metric.GcDelta;
import wava.model.metric.GcStatsSnapshot;
import wava.model.metric.HeapMemorySnapshot;
import wava.model.process.JavaProcessInfo;
import wava.model.metric.MetricSample;
import wava.model.metric.ProcessCpuSnapshot;

public class TargetProcessMetricCollector implements MetricCollector {
    private final ProcessCpuTracker cpuTracker;
    private final HeapMemoryReader heapMemoryReader;
    private final GcStatsReader gcStatsReader;
    private final GcDeltaTracker gcDeltaTracker;

    public TargetProcessMetricCollector() {
        this(new ProcessCpuTracker(), new TargetJvmMemoryReader(), new TargetJvmGcReader(), new GcDeltaTracker());
    }

    public TargetProcessMetricCollector(ProcessCpuTracker cpuTracker, HeapMemoryReader heapMemoryReader) {
        this(cpuTracker, heapMemoryReader, process -> GcStatsSnapshot.unavailable(), new GcDeltaTracker());
    }

    public TargetProcessMetricCollector(
            ProcessCpuTracker cpuTracker,
            HeapMemoryReader heapMemoryReader,
            GcStatsReader gcStatsReader,
            GcDeltaTracker gcDeltaTracker) {
        this.cpuTracker = cpuTracker;
        this.heapMemoryReader = heapMemoryReader;
        this.gcStatsReader = gcStatsReader;
        this.gcDeltaTracker = gcDeltaTracker;
    }

    @Override
    public MetricSample collect(JavaProcessInfo targetProcess, int sampleIndex) {
        long timestamp = System.currentTimeMillis();
        double cpuUsage = readTargetCpuUsage(targetProcess, timestamp);
        HeapMemorySnapshot heapMemory = heapMemoryReader.readHeapMemory(targetProcess);
        GcDelta gcDelta = readTargetGcDelta(targetProcess);
        return new MetricSample(
                timestamp,
                cpuUsage,
                heapMemory.getUsedMb(),
                heapMemory.isAvailable(),
                gcDelta.isAvailable(),
                gcDelta.getCountDelta(),
                gcDelta.getTimeDeltaMillis());
    }

    private GcDelta readTargetGcDelta(JavaProcessInfo targetProcess) {
        if (targetProcess == null) {
            return GcDelta.unavailable();
        }
        GcStatsSnapshot snapshot = gcStatsReader.readGcStats(targetProcess);
        return gcDeltaTracker.calculateDelta(targetProcess.getPid(), snapshot);
    }

    private double readTargetCpuUsage(JavaProcessInfo targetProcess, long timestamp) {
        if (targetProcess == null) {
            return 0.0;
        }

        Optional<ProcessHandle> processHandle = ProcessHandle.of(targetProcess.getPid());
        if (processHandle.isEmpty()) {
            cpuTracker.remove(targetProcess.getPid());
            gcDeltaTracker.remove(targetProcess.getPid());
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
