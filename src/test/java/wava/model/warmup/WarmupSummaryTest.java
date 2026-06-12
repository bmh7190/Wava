package wava.model.warmup;

public class WarmupSummaryTest {
    public static void main(String[] args) {
        calculateChanges();
        markUnavailable();
    }

    private static void calculateChanges() {
        WarmupSummary summary = WarmupSummary.available(10, 50.0, 25.0, 100.0, 125.0);

        assertTrue(summary.isAvailable(), "available");
        assertEquals(10, summary.getSampleCount(), "sample count");
        assertEquals(-25.0, summary.getCpuChange(), "cpu change");
        assertEquals(25.0, summary.getHeapChange(), "heap change");
    }

    private static void markUnavailable() {
        WarmupSummary summary = WarmupSummary.unavailable(3);

        assertTrue(!summary.isAvailable(), "unavailable");
        assertEquals(3, summary.getSampleCount(), "unavailable sample count");
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

    private static void assertEquals(double expected, double actual, String label) {
        if (Double.compare(expected, actual) != 0) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }
}
