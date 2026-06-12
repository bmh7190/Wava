package wava.model.metric;

public class ProcessCpuSnapshotTest {
    public static void main(String[] args) {
        storeSnapshotValues();
    }

    private static void storeSnapshotValues() {
        ProcessCpuSnapshot snapshot = new ProcessCpuSnapshot(100L, 200L, 300L);

        assertEquals(100L, snapshot.getPid(), "pid");
        assertEquals(200L, snapshot.getTimestampMillis(), "timestamp");
        assertEquals(300L, snapshot.getCpuTimeMillis(), "cpu time");
    }

    private static void assertEquals(long expected, long actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }
}
