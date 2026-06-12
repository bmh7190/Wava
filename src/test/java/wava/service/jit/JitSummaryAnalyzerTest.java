package wava.service.jit;

import java.util.List;
import wava.model.jit.JitEvent;
import wava.model.jit.JitEventSummary;
import wava.model.jit.JitMethodCount;

public class JitSummaryAnalyzerTest {
    public static void main(String[] args) {
        countFilteredEventsAndTopMethods();
        limitTopMethods();
        returnEmptySummaryWhenNothingMatches();
    }

    private static void countFilteredEventsAndTopMethods() {
        JitSummaryAnalyzer analyzer = new JitSummaryAnalyzer();

        JitEventSummary summary = analyzer.analyze(events(), new JitEventFilter("Target"), 3);

        assertEquals(5, summary.getTotalEventCount(), "total count");
        assertEquals(4, summary.getMatchedEventCount(), "matched count");
        assertEquals("sample.Target.compute", summary.getLatestMethodName(), "latest method");
        assertEquals(2, summary.getTopMethods().size(), "top method size");
        assertMethod(summary.getTopMethods().get(0), "sample.Target.run", 3, "first method");
        assertMethod(summary.getTopMethods().get(1), "sample.Target.compute", 1, "second method");
    }

    private static void limitTopMethods() {
        JitSummaryAnalyzer analyzer = new JitSummaryAnalyzer();

        JitEventSummary summary = analyzer.analyze(events(), new JitEventFilter(""), 1);

        assertEquals(1, summary.getTopMethods().size(), "limited size");
        assertMethod(summary.getTopMethods().get(0), "sample.Target.run", 3, "limited method");
    }

    private static void returnEmptySummaryWhenNothingMatches() {
        JitSummaryAnalyzer analyzer = new JitSummaryAnalyzer();

        JitEventSummary summary = analyzer.analyze(events(), new JitEventFilter("missing"), 3);

        assertEquals(5, summary.getTotalEventCount(), "total count");
        assertEquals(0, summary.getMatchedEventCount(), "matched count");
        assertEquals("", summary.getLatestMethodName(), "latest method");
        assertEquals(0, summary.getTopMethods().size(), "top method size");
    }

    private static List<JitEvent> events() {
        return List.of(
                event(1, "sample.Target::run"),
                event(2, "sample.Target::run"),
                event(3, "sample.Other::work"),
                event(4, "sample.Target::run"),
                event(5, "sample.Target::compute"));
    }

    private static JitEvent event(int compileId, String methodName) {
        return new JitEvent(1000L + compileId, compileId, "3", methodName, methodName);
    }

    private static void assertMethod(JitMethodCount methodCount, String methodName, int count, String label) {
        assertEquals(methodName, methodCount.getMethodName(), label + " name");
        assertEquals(count, methodCount.getCount(), label + " count");
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
