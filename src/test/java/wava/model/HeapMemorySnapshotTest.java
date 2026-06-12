package wava.model;

public class HeapMemorySnapshotTest {
    public static void main(String[] args) {
        createAvailableSnapshot();
        createUnavailableSnapshot();
    }

    private static void createAvailableSnapshot() {
        HeapMemorySnapshot snapshot = HeapMemorySnapshot.available(128.5);

        assertTrue(snapshot.isAvailable(), "available");
        assertEquals(128.5, snapshot.getUsedMb(), "used mb");
    }

    private static void createUnavailableSnapshot() {
        HeapMemorySnapshot snapshot = HeapMemorySnapshot.unavailable();

        assertTrue(!snapshot.isAvailable(), "unavailable");
        assertEquals(0.0, snapshot.getUsedMb(), "used mb");
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label + " expected true");
        }
    }

    private static void assertEquals(double expected, double actual, String label) {
        if (Double.compare(expected, actual) != 0) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }
}
