package wava.model;

public class GraphMarkerTest {
    public static void main(String[] args) {
        storeMarkerValues();
    }

    private static void storeMarkerValues() {
        GraphMarker marker = new GraphMarker(1000L, "JIT");

        assertEquals(1000L, marker.getTimestampMillis(), "timestamp");
        assertEquals("JIT", marker.getLabel(), "label");
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
