package wava.model;

public class MetricSampleTest {
    public static void main(String[] args) {
        storeMetricValues();
    }

    private static void storeMetricValues() {
        MetricSample sample = new MetricSample(1000L, 42.5, 128.25);

        assertEquals(1000L, sample.getTimestampMillis(), "timestamp");
        assertEquals(42.5, sample.getCpuUsagePercent(), "cpu");
        assertEquals(128.25, sample.getHeapUsedMb(), "heap");
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
}
