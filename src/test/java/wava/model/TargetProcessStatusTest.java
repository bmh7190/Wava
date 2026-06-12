package wava.model;

public class TargetProcessStatusTest {
    public static void main(String[] args) {
        exposeDisplayLabels();
    }

    private static void exposeDisplayLabels() {
        assertEquals("Running", TargetProcessStatus.RUNNING.getLabel(), "running label");
        assertEquals("Ended", TargetProcessStatus.ENDED.getLabel(), "ended label");
        assertEquals("Unknown", TargetProcessStatus.UNKNOWN.getLabel(), "unknown label");
    }

    private static void assertEquals(String expected, String actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }
}
