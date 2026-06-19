package wava.service.jit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wava.model.jit.JitEvent;
import wava.model.jit.JitEventSummary;
import wava.model.jit.JitMethodCount;
import wava.model.jit.JitMethodSummary;

public class JitSummaryAnalyzer {
    private static final int DEFAULT_TOP_LIMIT = 3;

    public JitEventSummary analyze(List<JitEvent> events, JitEventFilter filter) {
        return analyze(events, filter, DEFAULT_TOP_LIMIT);
    }

    public JitEventSummary analyze(List<JitEvent> events, JitEventFilter filter, int topLimit) {
        Map<String, MethodAccumulator> methodSummaries = new HashMap<>();
        int matchedCount = 0;
        String latestMethodName = "";

        for (JitEvent event : events) {
            if (!filter.matches(event)) {
                continue;
            }
            matchedCount++;
            latestMethodName = formatMethodName(event.getMethodName());
            methodSummaries
                    .computeIfAbsent(latestMethodName, MethodAccumulator::new)
                    .add(event);
        }

        List<JitMethodSummary> topMethodSummaries = createTopMethodSummaries(methodSummaries, topLimit);
        return new JitEventSummary(
                events.size(),
                matchedCount,
                latestMethodName,
                createTopMethods(topMethodSummaries),
                topMethodSummaries);
    }

    private List<JitMethodSummary> createTopMethodSummaries(
            Map<String, MethodAccumulator> methodSummaries,
            int topLimit) {
        if (topLimit <= 0) {
            return List.of();
        }

        List<JitMethodSummary> summaries = new ArrayList<>();
        methodSummaries.values().stream()
                .sorted(Comparator
                        .comparing(MethodAccumulator::getEventCount)
                        .reversed()
                        .thenComparing(MethodAccumulator::getMethodName))
                .limit(topLimit)
                .forEach(accumulator -> summaries.add(accumulator.toSummary()));
        return summaries;
    }

    private List<JitMethodCount> createTopMethods(List<JitMethodSummary> methodSummaries) {
        List<JitMethodCount> counts = new ArrayList<>();
        for (JitMethodSummary summary : methodSummaries) {
            counts.add(new JitMethodCount(summary.getMethodName(), summary.getEventCount()));
        }
        return counts;
    }

    private String formatMethodName(String methodName) {
        return methodName.replace("::", ".");
    }

    private static class MethodAccumulator {
        private final String methodName;
        private int eventCount;
        private String latestLevel;
        private long firstJvmElapsedMillis;
        private long latestJvmElapsedMillis;
        private boolean madeNotEntrantObserved;

        private MethodAccumulator(String methodName) {
            this.methodName = methodName;
            eventCount = 0;
            latestLevel = "";
            firstJvmElapsedMillis = JitMethodSummary.unknownElapsedMillis();
            latestJvmElapsedMillis = JitMethodSummary.unknownElapsedMillis();
            madeNotEntrantObserved = false;
        }

        private void add(JitEvent event) {
            eventCount++;
            if (!event.getLevel().isBlank()) {
                latestLevel = event.getLevel();
            }
            if (event.hasJvmElapsedMillis()) {
                if (firstJvmElapsedMillis < 0L) {
                    firstJvmElapsedMillis = event.getJvmElapsedMillis();
                }
                latestJvmElapsedMillis = event.getJvmElapsedMillis();
            }
            madeNotEntrantObserved = madeNotEntrantObserved
                    || event.getRawLine().contains("made not entrant");
        }

        private String getMethodName() {
            return methodName;
        }

        private int getEventCount() {
            return eventCount;
        }

        private JitMethodSummary toSummary() {
            return new JitMethodSummary(
                    methodName,
                    eventCount,
                    latestLevel,
                    firstJvmElapsedMillis,
                    latestJvmElapsedMillis,
                    madeNotEntrantObserved);
        }
    }
}
