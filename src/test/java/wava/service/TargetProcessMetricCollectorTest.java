package wava.service;

import wava.model.JavaProcessInfo;
import wava.model.MetricSample;

public class TargetProcessMetricCollectorTest {
    public static void main(String[] args) {
        useHeapReaderValue();
    }

    private static void useHeapReaderValue() {
        TargetProcessMetricCollector collector = new TargetProcessMetricCollector(
                new ProcessCpuTracker(),
                new FixedHeapMemoryReader(256.5));
        JavaProcessInfo process = new JavaProcessInfo(-1L, "missing.Process");

        MetricSample sample = collector.collect(process, 0);

        assertEquals(256.5, sample.getHeapUsedMb(), "heap value");
    }

    private static void assertEquals(double expected, double actual, String label) {
        if (Double.compare(expected, actual) != 0) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }

    private static class FixedHeapMemoryReader implements HeapMemoryReader {
        private final double heapUsedMb;

        private FixedHeapMemoryReader(double heapUsedMb) {
            this.heapUsedMb = heapUsedMb;
        }

        @Override
        public double readHeapUsedMb(JavaProcessInfo targetProcess) {
            return heapUsedMb;
        }
    }
}
