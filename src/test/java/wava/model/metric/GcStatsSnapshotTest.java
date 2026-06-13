package wava.model.metric;

public class GcStatsSnapshotTest {
    public static void main(String[] args) {
        createAvailableSnapshot();
        createUnavailableSnapshot();
    }

    private static void createAvailableSnapshot() {
        GcStatsSnapshot snapshot = GcStatsSnapshot.available(3L, 120L);

        assertTrue(snapshot.isAvailable(), "available");
        assertEquals(3L, snapshot.getCollectionCount(), "count");
        assertEquals(120L, snapshot.getCollectionTimeMillis(), "time");
    }

    private static void createUnavailableSnapshot() {
        GcStatsSnapshot snapshot = GcStatsSnapshot.unavailable();

        assertTrue(!snapshot.isAvailable(), "unavailable");
        assertEquals(0L, snapshot.getCollectionCount(), "count");
        assertEquals(0L, snapshot.getCollectionTimeMillis(), "time");
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
