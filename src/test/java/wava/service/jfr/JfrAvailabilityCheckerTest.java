package wava.service.jfr;

import wava.model.jfr.JfrAvailabilityStatus;

public class JfrAvailabilityCheckerTest {
    public static void main(String[] args) {
        returnAvailableWhenProbeSucceeds();
        returnUnavailableWhenProbeReturnsFalse();
        returnUnavailableWhenProbeFails();
        inspectCurrentRuntime();
    }

    private static void returnAvailableWhenProbeSucceeds() {
        JfrAvailabilityChecker checker = new JfrAvailabilityChecker(() -> true);

        JfrAvailabilityStatus status = checker.check();

        assertTrue(status.isAvailable(), "available");
    }

    private static void returnUnavailableWhenProbeReturnsFalse() {
        JfrAvailabilityChecker checker = new JfrAvailabilityChecker(() -> false);

        JfrAvailabilityStatus status = checker.check();

        assertTrue(!status.isAvailable(), "unavailable");
        assertEquals("Unavailable", status.getStateLabel(), "state");
    }

    private static void returnUnavailableWhenProbeFails() {
        JfrAvailabilityChecker checker = new JfrAvailabilityChecker(() -> {
            throw new IllegalStateException("missing");
        });

        JfrAvailabilityStatus status = checker.check();

        assertTrue(!status.isAvailable(), "unavailable");
        assertEquals("missing", status.getDetail(), "detail");
    }

    private static void inspectCurrentRuntime() {
        JfrAvailabilityChecker checker = new JfrAvailabilityChecker();

        JfrAvailabilityStatus status = checker.check();

        assertTrue(!status.getStateLabel().isBlank(), "state label");
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
