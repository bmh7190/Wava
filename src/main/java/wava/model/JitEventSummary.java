package wava.model;

import java.util.List;

public class JitEventSummary {
    private final int totalEventCount;
    private final int matchedEventCount;
    private final String latestMethodName;
    private final List<JitMethodCount> topMethods;

    public JitEventSummary(
            int totalEventCount,
            int matchedEventCount,
            String latestMethodName,
            List<JitMethodCount> topMethods) {
        this.totalEventCount = totalEventCount;
        this.matchedEventCount = matchedEventCount;
        this.latestMethodName = latestMethodName;
        this.topMethods = List.copyOf(topMethods);
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

    public boolean hasMatchedEvents() {
        return matchedEventCount > 0;
    }
}
