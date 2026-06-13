package wava.service.metric;

import wava.model.metric.GcStatsSnapshot;

public class TargetJvmGcReaderTest {
    public static void main(String[] args) {
        returnUnavailableForNullTarget();
    }

    private static void returnUnavailableForNullTarget() {
        TargetJvmGcReader reader = new TargetJvmGcReader();

        GcStatsSnapshot snapshot = reader.readGcStats(null);

        assertTrue(!snapshot.isAvailable(), "availability");
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label + " expected true");
        }
    }
}
