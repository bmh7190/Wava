package wava.model;

public class MetricSampleTest {
    public static void main(String[] args) {
        storeMetricValues();
        storeHeapAvailability();
    }

    private static void storeMetricValues() {
        MetricSample sample = new MetricSample(1000L, 42.5, 128.25);

        assertEquals(1000L, sample.getTimestampMillis(), "timestamp");
        assertEquals(42.5, sample.getCpuUsagePercent(), "cpu");
        assertEquals(128.25, sample.getHeapUsedMb(), "heap");
        assertTrue(sample.isHeapAvailable(), "default heap availability");
    }

    private static void storeHeapAvailability() {
        MetricSample sample = new MetricSample(1000L, 42.5, 0.0, false);

        assertTrue(!sample.isHeapAvailable(), "heap availability");
    }

    private static void assertEquals(long expected, long actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(double expected, double actual, String label) {
        if (Double.compare(expected, actual) != 0) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label + " expected true");
        }
    }
}
