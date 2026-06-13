package wava.service.metric;

import wava.model.process.JavaProcessInfo;
import wava.model.metric.GcStatsSnapshot;
import wava.model.metric.HeapMemorySnapshot;
import wava.model.metric.MetricSample;

public class TargetProcessMetricCollectorTest {
    public static void main(String[] args) {
        useHeapReaderValue();
        markHeapUnavailableWhenReaderFails();
        includeGcDeltaValues();
    }

    private static void useHeapReaderValue() {
        TargetProcessMetricCollector collector = new TargetProcessMetricCollector(
                new ProcessCpuTracker(),
                new FixedHeapMemoryReader(256.5));
        JavaProcessInfo process = new JavaProcessInfo(-1L, "missing.Process");

        MetricSample sample = collector.collect(process, 0);

        assertEquals(256.5, sample.getHeapUsedMb(), "heap value");
        assertTrue(sample.isHeapAvailable(), "heap availability");
    }

    private static void markHeapUnavailableWhenReaderFails() {
        TargetProcessMetricCollector collector = new TargetProcessMetricCollector(
                new ProcessCpuTracker(),
                new FixedHeapMemoryReader(HeapMemorySnapshot.unavailable()));
        JavaProcessInfo process = new JavaProcessInfo(-1L, "missing.Process");

        MetricSample sample = collector.collect(process, 0);

        assertEquals(0.0, sample.getHeapUsedMb(), "heap value");
        assertTrue(!sample.isHeapAvailable(), "heap availability");
    }

    private static void includeGcDeltaValues() {
        TargetProcessMetricCollector collector = new TargetProcessMetricCollector(
                new ProcessCpuTracker(),
                new FixedHeapMemoryReader(256.5),
                new SequenceGcStatsReader(
                        GcStatsSnapshot.available(5L, 100L),
                        GcStatsSnapshot.available(8L, 160L)),
                new GcDeltaTracker());
        JavaProcessInfo process = new JavaProcessInfo(ProcessHandle.current().pid(), "current");

        MetricSample firstSample = collector.collect(process, 0);
        MetricSample secondSample = collector.collect(process, 1);

        assertTrue(firstSample.isGcAvailable(), "first gc availability");
        assertEquals(0L, firstSample.getGcCountDelta(), "first gc count");
        assertEquals(3L, secondSample.getGcCountDelta(), "second gc count");
        assertEquals(60L, secondSample.getGcTimeDeltaMillis(), "second gc time");
    }

    private static void assertEquals(double expected, double actual, String label) {
        if (Double.compare(expected, actual) != 0) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }

    private static class FixedHeapMemoryReader implements HeapMemoryReader {
        private final HeapMemorySnapshot snapshot;

        private FixedHeapMemoryReader(double heapUsedMb) {
            this(HeapMemorySnapshot.available(heapUsedMb));
        }

        private FixedHeapMemoryReader(HeapMemorySnapshot snapshot) {
            this.snapshot = snapshot;
        }

        @Override
        public HeapMemorySnapshot readHeapMemory(JavaProcessInfo targetProcess) {
            return snapshot;
        }
    }

    private static class SequenceGcStatsReader implements GcStatsReader {
        private final GcStatsSnapshot[] snapshots;
        private int index;

        private SequenceGcStatsReader(GcStatsSnapshot... snapshots) {
            this.snapshots = snapshots;
        }

        @Override
        public GcStatsSnapshot readGcStats(JavaProcessInfo targetProcess) {
            GcStatsSnapshot snapshot = snapshots[Math.min(index, snapshots.length - 1)];
            index++;
            return snapshot;
        }
    }

    private static void assertEquals(long expected, long actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label + " expected true");
        }
    }
}
