package wava.service.jit;

import wava.model.jit.JitEvent;

public class JitEventFilterTest {
    public static void main(String[] args) {
        emptyFilterMatchesAll();
        matchMethodNameIgnoringCase();
        matchRawLine();
        rejectNonMatchingEvent();
    }

    private static void emptyFilterMatchesAll() {
        JitEventFilter filter = new JitEventFilter("");

        assertTrue(filter.matches(event("sample.Target::work", "raw")), "empty filter");
    }

    private static void matchMethodNameIgnoringCase() {
        JitEventFilter filter = new JitEventFilter("target");

        assertTrue(filter.matches(event("sample.Target::work", "raw")), "method match");
    }

    private static void matchRawLine() {
        JitEventFilter filter = new JitEventFilter("made not entrant");

        assertTrue(filter.matches(event("sample.Target::work", "made not entrant")), "raw match");
    }

    private static void rejectNonMatchingEvent() {
        JitEventFilter filter = new JitEventFilter("missing");

        assertTrue(!filter.matches(event("sample.Target::work", "raw")), "missing filter");
    }

    private static JitEvent event(String methodName, String rawLine) {
        return new JitEvent(1000L, 1, "3", methodName, rawLine);
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label + " expected true");
        }
    }
}
