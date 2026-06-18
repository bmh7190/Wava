package wava.service.jit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import wava.model.jit.JitEvent;
import wava.model.jit.JitFilterSuggestion;
import wava.model.process.JavaProcessInfo;

public class JitFilterSuggestionBuilder {
    private static final List<String> KNOWN_PREFIXES = List.of("sample.", "java.", "jdk.", "sun.");

    public List<JitFilterSuggestion> build(JavaProcessInfo process, List<JitEvent> events, int maxSuggestions) {
        if (maxSuggestions <= 0) {
            return List.of();
        }

        List<JitFilterSuggestion> suggestions = new ArrayList<>();
        addCurrentTargetSuggestion(suggestions, process);
        addPackageSuggestions(suggestions, events);
        addTopMethodSuggestions(suggestions, events);
        return suggestions.stream()
                .limit(maxSuggestions)
                .toList();
    }

    private void addCurrentTargetSuggestion(List<JitFilterSuggestion> suggestions, JavaProcessInfo process) {
        if (process == null) {
            return;
        }
        String targetName = simpleClassName(process.getDisplayName());
        if (!targetName.isBlank()) {
            suggestions.add(new JitFilterSuggestion("[Current] " + targetName, targetName));
        }
    }

    private void addPackageSuggestions(List<JitFilterSuggestion> suggestions, List<JitEvent> events) {
        Set<String> discoveredPrefixes = new LinkedHashSet<>();
        for (String knownPrefix : KNOWN_PREFIXES) {
            for (JitEvent event : events) {
                if (event.getMethodName().startsWith(knownPrefix)) {
                    discoveredPrefixes.add(knownPrefix);
                    break;
                }
            }
        }
        for (String prefix : discoveredPrefixes) {
            suggestions.add(new JitFilterSuggestion("[Package] " + prefix, prefix));
        }
    }

    private void addTopMethodSuggestions(List<JitFilterSuggestion> suggestions, List<JitEvent> events) {
        Map<String, Integer> methodCounts = new HashMap<>();
        for (JitEvent event : events) {
            methodCounts.merge(event.getMethodName(), 1, Integer::sum);
        }
        methodCounts.entrySet().stream()
                .sorted(Comparator
                        .comparing(Map.Entry<String, Integer>::getValue)
                        .reversed()
                        .thenComparing(Map.Entry::getKey))
                .forEach(entry -> suggestions.add(new JitFilterSuggestion(
                        "[Top] " + entry.getKey().replace("::", ".") + " (" + entry.getValue() + ")",
                        entry.getKey())));
    }

    private String simpleClassName(String displayName) {
        int packageSeparator = displayName.lastIndexOf('.');
        if (packageSeparator >= 0 && packageSeparator + 1 < displayName.length()) {
            return displayName.substring(packageSeparator + 1);
        }
        return displayName;
    }
}
