package wava.service.metric;

import wava.model.process.JavaProcessInfo;
import wava.model.metric.HeapMemorySnapshot;
import wava.model.metric.MetricSample;

public class TargetProcessMetricCollectorTest {
    public static void main(String[] args) {
        useHeapReaderValue();
        markHeapUnavailableWhenReaderFails();
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

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label + " expected true");
        }
    }
}
