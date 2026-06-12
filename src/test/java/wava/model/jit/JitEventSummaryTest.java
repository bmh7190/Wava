package wava.model.jit;

import java.util.List;

public class JitEventSummaryTest {
    public static void main(String[] args) {
        storeSummaryValues();
        copyTopMethodList();
    }

    private static void storeSummaryValues() {
        JitEventSummary summary = new JitEventSummary(
                5,
                2,
                "sample.Target.run",
                List.of(new JitMethodCount("sample.Target.run", 2)));

        assertEquals(5, summary.getTotalEventCount(), "total count");
        assertEquals(2, summary.getMatchedEventCount(), "matched count");
        assertEquals("sample.Target.run", summary.getLatestMethodName(), "latest method");
        assertTrue(summary.hasMatchedEvents(), "matched events");
    }

    private static void copyTopMethodList() {
        JitEventSummary summary = new JitEventSummary(0, 0, "", List.of());

        assertTrue(!summary.hasMatchedEvents(), "no matched events");
        assertEquals(0, summary.getTopMethods().size(), "top method size");
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label + " expected true");
        }
    }

    private static void assertEquals(int expected, int actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(String expected, String actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }
}
