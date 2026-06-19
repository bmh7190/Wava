package wava.service.jit;

import java.util.List;
import wava.model.jit.JitEvent;
import wava.model.jit.JitEventSummary;
import wava.model.jit.JitMethodCount;
import wava.model.jit.JitMethodSummary;

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
        assertEquals(2, summary.getTopMethodSummaries().size(), "method summary size");
        assertMethodSummary(
                summary.getTopMethodSummaries().get(0),
                "sample.Target.run",
                3,
                "4",
                "00:00:01.001 -> 00:00:01.004",
                true,
                "first summary");
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
        assertEquals(0, summary.getTopMethodSummaries().size(), "top summary size");
    }

    private static List<JitEvent> events() {
        return List.of(
                event(1, "3", "sample.Target::run", "sample.Target::run"),
                event(2, "4", "sample.Target::run", "sample.Target::run"),
                event(3, "sample.Other::work"),
                event(4, "4", "sample.Target::run", "sample.Target::run made not entrant"),
                event(5, "sample.Target::compute"));
    }

    private static JitEvent event(int compileId, String methodName) {
        return event(compileId, "3", methodName, methodName);
    }

    private static JitEvent event(int compileId, String level, String methodName, String rawLine) {
        return new JitEvent(
                1000L + compileId,
                1000L + compileId,
                compileId,
                level,
                methodName,
                rawLine);
    }

    private static void assertMethod(JitMethodCount methodCount, String methodName, int count, String label) {
        assertEquals(methodName, methodCount.getMethodName(), label + " name");
        assertEquals(count, methodCount.getCount(), label + " count");
    }

    private static void assertMethodSummary(
            JitMethodSummary summary,
            String methodName,
            int eventCount,
            String latestLevel,
            String elapsedRange,
            boolean madeNotEntrantObserved,
            String label) {
        assertEquals(methodName, summary.getMethodName(), label + " name");
        assertEquals(eventCount, summary.getEventCount(), label + " count");
        assertEquals(latestLevel, summary.getLatestLevel(), label + " latest level");
        assertEquals(elapsedRange, summary.formatElapsedRange(), label + " elapsed");
        assertEquals(madeNotEntrantObserved, summary.isMadeNotEntrantObserved(), label + " not entrant");
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

    private static void assertEquals(boolean expected, boolean actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }
}
