package wava.service.metric;

public class MemoryUnitTest {
    public static void main(String[] args) {
        convertBytesToMegabytes();
        keepFractionalMegabytes();
    }

    private static void convertBytesToMegabytes() {
        assertEquals(1.0, MemoryUnit.bytesToMb(1024L * 1024L), "one megabyte");
    }

    private static void keepFractionalMegabytes() {
        assertEquals(1.5, MemoryUnit.bytesToMb(1024L * 1024L + 512L * 1024L), "fractional megabyte");
    }

    private static void assertEquals(double expected, double actual, String label) {
        if (Double.compare(expected, actual) != 0) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }
}
