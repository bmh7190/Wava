package wava.model.metric;

public class HeapMemorySnapshot {
    private final boolean available;
    private final double usedMb;

    private HeapMemorySnapshot(boolean available, double usedMb) {
        this.available = available;
        this.usedMb = usedMb;
    }

    public static HeapMemorySnapshot available(double usedMb) {
        return new HeapMemorySnapshot(true, usedMb);
    }

    public static HeapMemorySnapshot unavailable() {
        return new HeapMemorySnapshot(false, 0.0);
    }

    public boolean isAvailable() {
        return available;
    }

    public double getUsedMb() {
        return usedMb;
    }
}
