package wava.model.graph;

public class GraphDisplayRangeTest {
    public static void main(String[] args) {
        exposeRangeLabels();
        exposeRangeDurations();
    }

    private static void exposeRangeLabels() {
        assertEquals("60s", GraphDisplayRange.LAST_60_SECONDS.getLabel(), "60s label");
        assertEquals("180s", GraphDisplayRange.LAST_180_SECONDS.toString(), "180s text");
    }

    private static void exposeRangeDurations() {
        assertEquals(60_000L, GraphDisplayRange.LAST_60_SECONDS.getDurationMillis(), "60s duration");
        assertEquals(180_000L, GraphDisplayRange.LAST_180_SECONDS.getDurationMillis(), "180s duration");
    }

    private static void assertEquals(long expected, long actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(String expected, String actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }
}
