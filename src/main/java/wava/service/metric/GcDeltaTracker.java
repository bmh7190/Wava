package wava.service.metric;

import java.util.HashMap;
import java.util.Map;
import wava.model.metric.GcDelta;
import wava.model.metric.GcStatsSnapshot;

public class GcDeltaTracker {
    private final Map<Long, GcStatsSnapshot> previousSnapshots;

    public GcDeltaTracker() {
        previousSnapshots = new HashMap<>();
    }

    public synchronized GcDelta calculateDelta(long pid, GcStatsSnapshot currentSnapshot) {
        if (!currentSnapshot.isAvailable()) {
            previousSnapshots.remove(pid);
            return GcDelta.unavailable();
        }

        GcStatsSnapshot previousSnapshot = previousSnapshots.put(pid, currentSnapshot);
        if (previousSnapshot == null || !previousSnapshot.isAvailable()) {
            return GcDelta.available(0L, 0L);
        }

        return GcDelta.available(
                currentSnapshot.getCollectionCount() - previousSnapshot.getCollectionCount(),
                currentSnapshot.getCollectionTimeMillis() - previousSnapshot.getCollectionTimeMillis());
    }

    public synchronized void remove(long pid) {
        previousSnapshots.remove(pid);
    }
}
