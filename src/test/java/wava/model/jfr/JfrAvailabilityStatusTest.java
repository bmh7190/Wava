package wava.model.jfr;

public class JfrAvailabilityStatusTest {
    public static void main(String[] args) {
        createAvailableStatus();
        createUnavailableStatus();
    }

    private static void createAvailableStatus() {
        JfrAvailabilityStatus status = JfrAvailabilityStatus.available();

        assertTrue(status.isAvailable(), "available");
        assertEquals("Available", status.getStateLabel(), "state");
        assertTrue(status.formatLogMessage().contains("Available"), "message");
    }

    private static void createUnavailableStatus() {
        JfrAvailabilityStatus status = JfrAvailabilityStatus.unavailable("missing module");

        assertTrue(!status.isAvailable(), "unavailable");
        assertEquals("Unavailable", status.getStateLabel(), "state");
        assertEquals("missing module", status.getDetail(), "detail");
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label + " expected true");
        }
    }

    private static void assertEquals(String expected, String actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }
}
