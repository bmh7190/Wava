package wava.model.metric;

public class GcStatsSnapshot {
    private final boolean available;
    private final long collectionCount;
    private final long collectionTimeMillis;

    private GcStatsSnapshot(boolean available, long collectionCount, long collectionTimeMillis) {
        this.available = available;
        this.collectionCount = collectionCount;
        this.collectionTimeMillis = collectionTimeMillis;
    }

    public static GcStatsSnapshot available(long collectionCount, long collectionTimeMillis) {
        return new GcStatsSnapshot(true, collectionCount, collectionTimeMillis);
    }

    public static GcStatsSnapshot unavailable() {
        return new GcStatsSnapshot(false, 0L, 0L);
    }

    public boolean isAvailable() {
        return available;
    }

    public long getCollectionCount() {
        return collectionCount;
    }

    public long getCollectionTimeMillis() {
        return collectionTimeMillis;
    }
}
