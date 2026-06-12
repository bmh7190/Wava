package wava.service.metric;

public final class MemoryUnit {
    private static final double BYTES_PER_MB = 1024.0 * 1024.0;

    private MemoryUnit() {
    }

    public static double bytesToMb(long bytes) {
        return bytes / BYTES_PER_MB;
    }
}
