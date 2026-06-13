package wava.model.metric;

public class GcDeltaTest {
    public static void main(String[] args) {
        createAvailableDelta();
        clampNegativeValues();
        createUnavailableDelta();
    }

    private static void createAvailableDelta() {
        GcDelta delta = GcDelta.available(2L, 30L);

        assertTrue(delta.isAvailable(), "available");
        assertTrue(delta.hasActivity(), "activity");
        assertEquals(2L, delta.getCountDelta(), "count");
        assertEquals(30L, delta.getTimeDeltaMillis(), "time");
    }

    private static void clampNegativeValues() {
        GcDelta delta = GcDelta.available(-1L, -10L);

        assertEquals(0L, delta.getCountDelta(), "count");
        assertEquals(0L, delta.getTimeDeltaMillis(), "time");
    }

    private static void createUnavailableDelta() {
        GcDelta delta = GcDelta.unavailable();

        assertTrue(!delta.isAvailable(), "unavailable");
        assertTrue(!delta.hasActivity(), "activity");
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label + " expected true");
        }
    }

    private static void assertEquals(long expected, long actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }
}
