package wava.model.jit;

import java.util.List;

public class JitEventSummary {
    private final int totalEventCount;
    private final int matchedEventCount;
    private final String latestMethodName;
    private final List<JitMethodCount> topMethods;
    private final List<JitMethodSummary> topMethodSummaries;

    public JitEventSummary(
            int totalEventCount,
            int matchedEventCount,
            String latestMethodName,
            List<JitMethodCount> topMethods) {
        this(totalEventCount, matchedEventCount, latestMethodName, topMethods, List.of());
    }

    public JitEventSummary(
            int totalEventCount,
            int matchedEventCount,
            String latestMethodName,
            List<JitMethodCount> topMethods,
            List<JitMethodSummary> topMethodSummaries) {
        this.totalEventCount = totalEventCount;
        this.matchedEventCount = matchedEventCount;
        this.latestMethodName = latestMethodName;
        this.topMethods = List.copyOf(topMethods);
        this.topMethodSummaries = List.copyOf(topMethodSummaries);
    }

    public int getTotalEventCount() {
        return totalEventCount;
    }

    public int getMatchedEventCount() {
        return matchedEventCount;
    }

    public String getLatestMethodName() {
        return latestMethodName;
    }

    public List<JitMethodCount> getTopMethods() {
        return topMethods;
    }

    public List<JitMethodSummary> getTopMethodSummaries() {
        return topMethodSummaries;
    }

    public boolean hasMatchedEvents() {
        return matchedEventCount > 0;
    }
}
