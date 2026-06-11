package wava.service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import wava.model.JavaProcessInfo;
import wava.model.MetricSample;

public class SystemMetricCollector implements MetricCollector {
    private static final double BYTES_PER_MB = 1024.0 * 1024.0;

    private final MemoryMXBean memoryBean;

    public SystemMetricCollector() {
        memoryBean = ManagementFactory.getMemoryMXBean();
    }

    @Override
    public MetricSample collect(JavaProcessInfo targetProcess, int sampleIndex) {
        long timestamp = System.currentTimeMillis();
        double cpuUsage = calculatePlaceholderCpu(sampleIndex);
        double heapUsedMb = readHeapUsedMb();
        return new MetricSample(timestamp, cpuUsage, heapUsedMb);
    }

    private double calculatePlaceholderCpu(int sampleIndex) {
        double wave = Math.sin(sampleIndex / 4.0) * 18.0;
        double trend = Math.max(0.0, 35.0 - sampleIndex * 0.4);
        return clamp(20.0 + trend + wave, 0.0, 100.0);
    }

    private double readHeapUsedMb() {
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        return heapUsage.getUsed() / BYTES_PER_MB;
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
