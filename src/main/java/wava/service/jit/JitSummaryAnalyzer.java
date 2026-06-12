package wava.service.jit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wava.model.jit.JitEvent;
import wava.model.jit.JitEventSummary;
import wava.model.jit.JitMethodCount;

public class JitSummaryAnalyzer {
    private static final int DEFAULT_TOP_LIMIT = 3;

    public JitEventSummary analyze(List<JitEvent> events, JitEventFilter filter) {
        return analyze(events, filter, DEFAULT_TOP_LIMIT);
    }

    public JitEventSummary analyze(List<JitEvent> events, JitEventFilter filter, int topLimit) {
        Map<String, Integer> methodCounts = new HashMap<>();
        int matchedCount = 0;
        String latestMethodName = "";

        for (JitEvent event : events) {
            if (!filter.matches(event)) {
                continue;
            }
            matchedCount++;
            latestMethodName = formatMethodName(event.getMethodName());
            methodCounts.merge(latestMethodName, 1, Integer::sum);
        }

        return new JitEventSummary(
                events.size(),
                matchedCount,
                latestMethodName,
                createTopMethods(methodCounts, topLimit));
    }

    private List<JitMethodCount> createTopMethods(Map<String, Integer> methodCounts, int topLimit) {
        if (topLimit <= 0) {
            return List.of();
        }

        List<JitMethodCount> counts = new ArrayList<>();
        methodCounts.entrySet().stream()
                .sorted(Comparator
                        .comparing(Map.Entry<String, Integer>::getValue)
                        .reversed()
                        .thenComparing(Map.Entry::getKey))
                .limit(topLimit)
                .forEach(entry -> counts.add(new JitMethodCount(entry.getKey(), entry.getValue())));
        return counts;
    }

    private String formatMethodName(String methodName) {
        return methodName.replace("::", ".");
    }
}
