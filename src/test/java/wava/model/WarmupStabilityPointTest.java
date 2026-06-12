package wava.model;

public class WarmupStabilityPointTest {
    public static void main(String[] args) {
        createUnavailablePoint();
        createAvailablePoint();
    }

    private static void createUnavailablePoint() {
        WarmupStabilityPoint point = WarmupStabilityPoint.unavailable(4);

        assertTrue(!point.isAvailable(), "available");
        assertEquals(4, point.getSampleCount(), "sample count");
        assertEquals(-1, point.getSampleIndex(), "sample index");
    }

    private static void createAvailablePoint() {
        WarmupStabilityPoint point = WarmupStabilityPoint.available(12, 5, 1000L, 1.2, 0);

        assertTrue(point.isAvailable(), "available");
        assertEquals(12, point.getSampleCount(), "sample count");
        assertEquals(5, point.getSampleIndex(), "sample index");
        assertEquals(1000L, point.getTimestampMillis(), "timestamp");
        assertEquals(1.2, point.getCpuRangePercent(), "cpu range");
        assertEquals(0, point.getJitEventCount(), "jit count");
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label + " expected true");
        }
    }

    private static void assertEquals(int expected, int actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
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
