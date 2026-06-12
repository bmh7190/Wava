package wava.service;

import java.util.Optional;
import wava.model.JitEvent;

public class JitLogParser {
    public Optional<JitEvent> parse(String rawLine, long timestampMillis) {
        String line = rawLine.trim();
        if (line.isEmpty()) {
            return Optional.empty();
        }

        String[] tokens = line.split("\\s+");
        int methodIndex = findMethodIndex(tokens);
        if (tokens.length < 2 || methodIndex < 0) {
            return Optional.empty();
        }

        Optional<Integer> compileId = parseInteger(tokens[1]);
        if (compileId.isEmpty()) {
            return Optional.empty();
        }

        String level = findLevel(tokens, methodIndex);
        return Optional.of(new JitEvent(
                timestampMillis,
                compileId.get(),
                level,
                tokens[methodIndex],
                rawLine));
    }

    private int findMethodIndex(String[] tokens) {
        for (int index = 0; index < tokens.length; index++) {
            if (tokens[index].contains("::")) {
                return index;
            }
        }
        return -1;
    }

    private String findLevel(String[] tokens, int methodIndex) {
        for (int index = methodIndex - 1; index >= 2; index--) {
            if (isInteger(tokens[index])) {
                return tokens[index];
            }
        }
        return "";
    }

    private Optional<Integer> parseInteger(String value) {
        try {
            return Optional.of(Integer.parseInt(value));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    private boolean isInteger(String value) {
        return parseInteger(value).isPresent();
    }
}
